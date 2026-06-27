$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$cloudflared = Join-Path $projectRoot "tools\cloudflared.exe"
$logPath = Join-Path $projectRoot "tmp\cloudflared-demo.log"

if (-not (Test-Path $cloudflared)) {
    throw "No se encontro tools\cloudflared.exe. Ejecuta primero la preparacion del enlace publico."
}

New-Item -ItemType Directory -Path (Split-Path $logPath) -Force | Out-Null
Remove-Item -LiteralPath $logPath -ErrorAction SilentlyContinue

Write-Host "Creando enlace publico temporal para Peralta POS..."
Write-Host "La web, el backend y MySQL deben permanecer encendidos."

& $cloudflared tunnel --url http://127.0.0.1:5174 --no-autoupdate 2>&1 |
    Tee-Object -FilePath $logPath
