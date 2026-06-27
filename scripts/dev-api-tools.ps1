$ErrorActionPreference = "Stop"

function Get-PeraltaMavenCommand {
    $mavenFromPath = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($mavenFromPath) {
        return $mavenFromPath.Source
    }

    $mavenFromPath = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mavenFromPath) {
        return $mavenFromPath.Source
    }

    $netBeansMaven = "C:\Program Files\NetBeans-24\netbeans\java\maven\bin\mvn.cmd"
    if (Test-Path $netBeansMaven) {
        return $netBeansMaven
    }

    throw "No se encontro Maven. Instala Maven o NetBeans, o agrega mvn al PATH."
}

function Test-PeraltaApi {
    param(
        [int] $Port = 8080
    )

    try {
        $response = Invoke-RestMethod -Uri "http://127.0.0.1:$Port/api/health" -TimeoutSec 2
        return $response.success -eq $true
    }
    catch {
        return $false
    }
}

function Start-PeraltaApi {
    param(
        [Parameter(Mandatory = $true)]
        [string] $ProjectRoot,
        [int] $Port = 8080
    )

    if (Test-PeraltaApi -Port $Port) {
        Write-Host "Backend ya esta corriendo en http://127.0.0.1:$Port"
        return [pscustomobject]@{
            Started = $false
            Job = $null
            Port = $Port
        }
    }

    $maven = Get-PeraltaMavenCommand
    $logDir = Join-Path $ProjectRoot "tmp\api"
    New-Item -ItemType Directory -Force -Path $logDir | Out-Null

    $stdoutLog = Join-Path $logDir "backend.out.log"
    $stderrLog = Join-Path $logDir "backend.err.log"
    if (Test-Path $stdoutLog) { Remove-Item -LiteralPath $stdoutLog -Force }
    if (Test-Path $stderrLog) { Remove-Item -LiteralPath $stderrLog -Force }

    Write-Host "Arrancando backend automatico en http://127.0.0.1:$Port ..."

    $job = Start-Job -Name "peralta-pos-api-$Port" -ScriptBlock {
        param($Root, $MavenCommand, $ApiPort, $OutLog, $ErrLog)

        Set-Location $Root

        $runArguments = "--server.port=$ApiPort --spring.security.user.password=local-dev-only --app.dgii.rnc.auto-sync-enabled=false"
        & $MavenCommand -f "services/api/pom.xml" spring-boot:run "-Dspring-boot.run.arguments=$runArguments" > $OutLog 2> $ErrLog
    } -ArgumentList $ProjectRoot, $maven, $Port, $stdoutLog, $stderrLog

    $started = $false
    for ($attempt = 0; $attempt -lt 90; $attempt++) {
        if (Test-PeraltaApi -Port $Port) {
            $started = $true
            break
        }

        if ($job.State -in @("Completed", "Failed", "Stopped")) {
            break
        }

        Start-Sleep -Seconds 1
    }

    if (-not $started) {
        $tail = @()
        if (Test-Path $stderrLog) {
            $tail += Get-Content -LiteralPath $stderrLog -Tail 30 -ErrorAction SilentlyContinue
        }
        if (Test-Path $stdoutLog) {
            $tail += Get-Content -LiteralPath $stdoutLog -Tail 30 -ErrorAction SilentlyContinue
        }

        Stop-Job -Job $job -ErrorAction SilentlyContinue | Out-Null
        Remove-Job -Job $job -Force -ErrorAction SilentlyContinue | Out-Null

        $details = ($tail -join [Environment]::NewLine)
        if ($details.Trim()) {
            throw "El backend no pudo arrancar. Ultimas lineas del log:$([Environment]::NewLine)$details"
        }

        throw "El backend no pudo arrancar. Verifica que MySQL este encendido y que el puerto $Port este libre."
    }

    Write-Host "Backend listo en http://127.0.0.1:$Port"

    return [pscustomobject]@{
        Started = $true
        Job = $job
        Port = $Port
    }
}

function Stop-PeraltaApi {
    param(
        $Handle
    )

    if ($null -eq $Handle -or -not $Handle.Started -or $null -eq $Handle.Job) {
        return
    }

    Write-Host "Deteniendo backend automatico..."
    Stop-Job -Job $Handle.Job -ErrorAction SilentlyContinue | Out-Null
    Remove-Job -Job $Handle.Job -Force -ErrorAction SilentlyContinue | Out-Null
}
