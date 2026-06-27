$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$driveName = "P"
$drivePath = "$driveName`:"
$createdDrive = $false

if (-not (Get-PSDrive -Name $driveName -ErrorAction SilentlyContinue)) {
    # Usamos una unidad temporal corta para evitar errores de resolucion con OneDrive.
    subst $drivePath $projectRoot.Path
    $createdDrive = $true
}

try {
    Push-Location "$drivePath\apps\web"
    npm run build -- --emptyOutDir
}
finally {
    Pop-Location -ErrorAction SilentlyContinue
    if ($createdDrive) {
        subst $drivePath /D
    }
}
