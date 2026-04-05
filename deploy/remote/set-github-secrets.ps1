param(
  [string]$Repo = "dengzhuofu/shop",
  [string]$ServerHost = "101.200.239.103",
  [string]$ServerPort = "22",
  [string]$ServerUser = "root",
  [string]$ProjectDir = "/opt/shop",
  [string]$SshPrivateKeyPath,
  [string]$GhcrUsername,
  [string]$GhcrReadToken
)

$ErrorActionPreference = "Stop"

function Set-EnvSecret {
  param(
    [string]$Name,
    [string]$Value
  )

  if ([string]::IsNullOrWhiteSpace($Value)) {
    throw "Secret '$Name' is empty."
  }

  $tempFile = [System.IO.Path]::GetTempFileName()
  try {
    [System.IO.File]::WriteAllText($tempFile, $Value)
    gh secret set $Name --repo $Repo --body-file $tempFile | Out-Null
    Write-Host "Set secret $Name"
  } finally {
    Remove-Item -LiteralPath $tempFile -Force -ErrorAction SilentlyContinue
  }
}

if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
  throw "GitHub CLI 'gh' is not installed or not on PATH."
}

gh auth status | Out-Null

if (-not $SshPrivateKeyPath) {
  throw "Please provide -SshPrivateKeyPath."
}

if (-not (Test-Path -LiteralPath $SshPrivateKeyPath)) {
  throw "SSH private key file not found: $SshPrivateKeyPath"
}

if ([string]::IsNullOrWhiteSpace($GhcrUsername)) {
  throw "Please provide -GhcrUsername."
}

if ([string]::IsNullOrWhiteSpace($GhcrReadToken)) {
  throw "Please provide -GhcrReadToken."
}

$sshPrivateKey = Get-Content -LiteralPath $SshPrivateKeyPath -Raw

Set-EnvSecret -Name "PROD_SERVER_HOST" -Value $ServerHost
Set-EnvSecret -Name "PROD_SERVER_PORT" -Value $ServerPort
Set-EnvSecret -Name "PROD_SERVER_USER" -Value $ServerUser
Set-EnvSecret -Name "PROD_PROJECT_DIR" -Value $ProjectDir
Set-EnvSecret -Name "PROD_SSH_PRIVATE_KEY" -Value $sshPrivateKey
Set-EnvSecret -Name "PROD_GHCR_USERNAME" -Value $GhcrUsername
Set-EnvSecret -Name "PROD_GHCR_READ_TOKEN" -Value $GhcrReadToken

Write-Host "All production environment secrets have been configured for $Repo."
