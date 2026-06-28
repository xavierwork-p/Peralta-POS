param(
    [Parameter(Mandatory = $true)]
    [string]$Url
)

$ErrorActionPreference = "Stop"

if ($Url -notmatch '^https?://') {
    throw "La URL debe empezar por http:// o https://"
}

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$outputDir = Join-Path $projectRoot "output\desktop-launcher"

New-Item -ItemType Directory -Force -Path $outputDir | Out-Null

$urlShortcutPath = Join-Path $outputDir "Peralta POS Web.url"
$cmdLauncherPath = Join-Path $outputDir "Abrir Peralta POS.cmd"

$urlShortcut = @(
    "[InternetShortcut]",
    "URL=$Url",
    "IconIndex=0"
)

$cmdLauncher = @(
    "@echo off",
    "start """" ""$Url"""
)

Set-Content -Path $urlShortcutPath -Value $urlShortcut -Encoding ASCII
Set-Content -Path $cmdLauncherPath -Value $cmdLauncher -Encoding ASCII

Write-Host "Lanzadores creados en: $outputDir"
Write-Host "Puedes enviar el archivo .url o el .cmd para abrir Peralta POS desde Windows."
