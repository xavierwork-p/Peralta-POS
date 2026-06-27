$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$driveName = "P"
$drivePath = "$driveName`:"
$createdDrive = $false

if (-not (Get-PSDrive -Name $driveName -ErrorAction SilentlyContinue)) {
    subst $drivePath $projectRoot.Path
    $createdDrive = $true
}

try {
    Push-Location "$drivePath\apps\web"
    npm run build -- --emptyOutDir
    npx vite preview --host 127.0.0.1 --port 5174 --strictPort
}
finally {
    Pop-Location -ErrorAction SilentlyContinue
    if ($createdDrive) {
        subst $drivePath /D
    }
}
