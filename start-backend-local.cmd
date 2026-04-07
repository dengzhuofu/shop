@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PS_SCRIPT=%SCRIPT_DIR%scripts\start-backend-local.ps1"

if not exist "%PS_SCRIPT%" (
  echo [shop-backend] Could not find "%PS_SCRIPT%".
  pause
  exit /b 1
)

start "Shop Backend Local" powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -Command ^
 "& '%PS_SCRIPT%'; $code = $LASTEXITCODE; if ($code -ne 0) { Write-Host ''; Write-Host '[shop-backend] Startup failed. Press any key to close...' -ForegroundColor Red; $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown') | Out-Null }; exit $code"
exit /b 0
