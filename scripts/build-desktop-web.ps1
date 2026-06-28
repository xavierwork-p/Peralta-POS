$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$driveName = "P"
$drivePath = "$driveName`:"
$createdDrive = $false
$previousClientChannel = $env:VITE_CLIENT_CHANNEL

if (-not (Get-PSDrive -Name $driveName -ErrorAction SilentlyContinue)) {
    # Usamos una unidad temporal corta para evitar errores de resolucion con OneDrive.
    subst $drivePath $projectRoot.Path
    $createdDrive = $true
}

try {
    $env:VITE_CLIENT_CHANNEL = "DESKTOP"

    Push-Location "$drivePath\apps\web"
    npm run build -- --mode desktop --emptyOutDir
}
finally {
    Pop-Location -ErrorAction SilentlyContinue

    if ($null -eq $previousClientChannel) {
        Remove-Item Env:\VITE_CLIENT_CHANNEL -ErrorAction SilentlyContinue
    }
    else {
        $env:VITE_CLIENT_CHANNEL = $previousClientChannel
    }

    if ($createdDrive) {
        subst $drivePath /D
    }
}
