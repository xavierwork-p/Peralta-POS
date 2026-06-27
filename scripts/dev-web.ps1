$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$driveName = "P"
$drivePath = "$driveName`:"
$createdDrive = $false
$apiHandle = $null

. (Join-Path $PSScriptRoot "dev-api-tools.ps1")

if (-not (Get-PSDrive -Name $driveName -ErrorAction SilentlyContinue)) {
    # Vite/esbuild puede fallar con rutas largas dentro de OneDrive.
    # Montamos una unidad temporal corta mientras corre el servidor web.
    subst $drivePath $projectRoot.Path
    $createdDrive = $true
}

try {
    $apiHandle = Start-PeraltaApi -ProjectRoot $projectRoot.Path -Port 8080

    Push-Location "$drivePath\apps\web"
    # En esta ruta de OneDrive, el servidor dev de Vite puede servir TSX crudo.
    # Compilamos primero y luego servimos el build estable para evitar pantalla en blanco.
    npm exec -- tsc --noEmit
    npm exec -- vite build --configLoader runner --emptyOutDir
    npm exec -- vite preview --host 127.0.0.1 --port 5173 --strictPort
}
finally {
    Pop-Location -ErrorAction SilentlyContinue
    Stop-PeraltaApi -Handle $apiHandle
    if ($createdDrive) {
        subst $drivePath /D
    }
}
