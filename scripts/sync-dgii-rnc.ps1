$ErrorActionPreference = "Stop"

$apiBaseUrl = $env:PERALTA_POS_API_URL

if ([string]::IsNullOrWhiteSpace($apiBaseUrl)) {
    $apiBaseUrl = "http://localhost:8080/api"
}

$url = "$apiBaseUrl/dgii/rnc/import"

Write-Host "Importando listado RNC desde DGII..."
Write-Host "Backend: $apiBaseUrl"

$response = Invoke-RestMethod -Method Post -Uri $url -TimeoutSec 900

if (-not $response.success) {
    throw $response.message
}

$summary = $response.data

Write-Host "Importacion completada."
Write-Host "Registros leidos: $($summary.read)"
Write-Host "Insertados: $($summary.inserted)"
Write-Host "Actualizados: $($summary.updated)"
Write-Host "Ignorados: $($summary.ignored)"
Write-Host "Fecha: $($summary.updatedAt)"
