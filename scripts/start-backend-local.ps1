$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Write-Step {
    param([string]$Message)
    Write-Host "[shop-backend] $Message" -ForegroundColor Cyan
}

function Assert-Command {
    param(
        [string]$Name,
        [string]$HelpMessage
    )

    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw $HelpMessage
    }
}

function Wait-ForPostgres {
    param(
        [string]$PgIsReadyPath,
        [int]$Port
    )

    for ($i = 0; $i -lt 30; $i++) {
        & $PgIsReadyPath -h localhost -p $Port | Out-Null
        if ($LASTEXITCODE -eq 0) {
            return
        }
        Start-Sleep -Seconds 1
    }

    throw "PostgreSQL did not become ready on localhost:$Port in time."
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir
$backendDir = Join-Path $projectRoot 'backend'
$schemaPath = Join-Path $backendDir 'src\main\resources\schema.sql'
$localAppConfigPath = Join-Path $backendDir 'src\main\resources\application-local.yml'
$localPgRoot = Join-Path $backendDir '.local-postgres'
$localPgData = Join-Path $localPgRoot 'data'
$localPgLogDir = Join-Path $localPgRoot 'logs'
$runLogDir = Join-Path $backendDir '.run-logs'
$pgPasswordFile = Join-Path $localPgRoot 'pgpass.txt'
$backendPort = 8081
$dbPort = 5433
$dbName = 'shop'
$dbUser = 'postgres'
$dbPassword = 'postgres'
$pgBinDir = 'C:\Program Files\PostgreSQL\17\bin'
$initDbPath = Join-Path $pgBinDir 'initdb.exe'
$pgCtlPath = Join-Path $pgBinDir 'pg_ctl.exe'
$pgIsReadyPath = Join-Path $pgBinDir 'pg_isready.exe'
$psqlPath = Join-Path $pgBinDir 'psql.exe'
$createdbPath = Join-Path $pgBinDir 'createdb.exe'

Write-Step "Preparing local backend startup..."

Assert-Command -Name 'mvn.cmd' -HelpMessage 'Maven was not found in PATH. Please make sure mvn.cmd is available.'
Assert-Command -Name 'java.exe' -HelpMessage 'Java was not found in PATH. Please make sure JDK 21 is installed and on PATH.'

if (-not (Test-Path $pgBinDir)) {
    throw "PostgreSQL 17 was not found at $pgBinDir."
}

New-Item -ItemType Directory -Force -Path $localPgRoot, $localPgLogDir, $runLogDir | Out-Null

if (-not (Test-Path (Join-Path $localPgData 'PG_VERSION'))) {
    Write-Step "Initializing project-local PostgreSQL data directory..."
    Set-Content -Path $pgPasswordFile -Value $dbPassword -NoNewline
    & $initDbPath -D $localPgData -U $dbUser --pwfile=$pgPasswordFile --auth-host=scram-sha-256 --auth-local=trust -E UTF8
    if ($LASTEXITCODE -ne 0) {
        throw 'initdb failed.'
    }

    $configPath = Join-Path $localPgData 'postgresql.conf'
    $config = Get-Content $configPath -Raw
    $config = [regex]::Replace($config, "#?listen_addresses\s*=.*", "listen_addresses = 'localhost'")
    $config = [regex]::Replace($config, "#?port\s*=\s*\d+", "port = $dbPort")
    Set-Content -Path $configPath -Value $config
}

Write-Step "Ensuring project-local PostgreSQL is running on port $dbPort..."
& $pgIsReadyPath -h localhost -p $dbPort | Out-Null
if ($LASTEXITCODE -ne 0) {
    $postgresLog = Join-Path $localPgLogDir 'postgres.log'
    & $pgCtlPath -D $localPgData -l $postgresLog start
    if ($LASTEXITCODE -ne 0) {
        throw 'pg_ctl start failed.'
    }
}

Wait-ForPostgres -PgIsReadyPath $pgIsReadyPath -Port $dbPort

$env:PGPASSWORD = $dbPassword
$dbExists = (& $psqlPath -h localhost -p $dbPort -U $dbUser -d postgres -t -A -c "SELECT datname FROM pg_database WHERE datname='$dbName';" | Out-String).Trim()
if ($dbExists -ne $dbName) {
    Write-Step "Creating database '$dbName'..."
    & $createdbPath -h localhost -p $dbPort -U $dbUser $dbName
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to create database '$dbName'."
    }
}

$tableExists = (& $psqlPath -h localhost -p $dbPort -U $dbUser -d $dbName -t -A -c "SELECT to_regclass('public.pms_product');" | Out-String).Trim()
if ($tableExists -ne 'pms_product') {
    Write-Step "Importing schema and seed data into '$dbName'..."
    & $psqlPath -h localhost -p $dbPort -U $dbUser -d $dbName -f $schemaPath
    if ($LASTEXITCODE -ne 0) {
        throw 'Failed to import schema.sql.'
    }
}

$backendProcess = Get-NetTCPConnection -LocalPort $backendPort -State Listen -ErrorAction SilentlyContinue |
    Select-Object -First 1 -ExpandProperty OwningProcess
if ($backendProcess) {
    Write-Step "Backend is already running on http://localhost:$backendPort (PID: $backendProcess)."
    Write-Step "If you want to restart it, stop that process first and rerun this script."
    exit 0
}

Write-Step "Starting Spring Boot backend on http://localhost:$backendPort ..."
Write-Step "Database: postgres@localhost:$dbPort/$dbName"
Write-Step "Close this window to stop the backend process. The project-local PostgreSQL instance stays running for faster next startup."

$env:DB_HOST = 'localhost'
$env:DB_PORT = "$dbPort"
$env:DB_NAME = $dbName
$env:DB_USERNAME = $dbUser
$env:DB_PASSWORD = $dbPassword
if (Test-Path $localAppConfigPath) {
    $env:SPRING_PROFILES_ACTIVE = 'local'
    Write-Step "Loaded Spring profile: local"
}

Push-Location $backendDir
try {
    & mvn.cmd spring-boot:run
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
