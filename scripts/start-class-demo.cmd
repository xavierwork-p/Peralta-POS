@echo off
setlocal
cd /d "%~dp0.."

if not exist "tools\cloudflared.exe" (
  echo Falta tools\cloudflared.exe
  exit /b 1
)

start "Peralta POS Web Demo" /min powershell.exe -NoProfile -ExecutionPolicy Bypass -File "scripts\dev-share-web.ps1"

timeout /t 10 /nobreak >nul

start "Peralta POS Public Link" /min powershell.exe -NoProfile -ExecutionPolicy Bypass -File "scripts\share-demo.ps1"

echo Peralta POS se esta compartiendo.
echo Revisa tmp\cloudflared-demo.log para ver el enlace publico.
