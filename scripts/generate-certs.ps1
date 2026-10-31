# Gera um certificado autoassinado (APENAS para desenvolvimento) e copia os keystores para cada serviço.
# Uso (na raiz do projeto): powershell -ExecutionPolicy Bypass -File .\scripts\generate-certs.ps1
param([string]$Password = "changeit-dev")

$keytool = (Get-Command keytool -ErrorAction SilentlyContinue).Source
if (-not $keytool) {
    $candidates = @("$env:JAVA_HOME\bin\keytool.exe") +
        (Get-ChildItem "C:\Program Files" -Recurse -Filter keytool.exe -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName)
    $keytool = $candidates | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
}
if (-not $keytool) { throw "keytool nao encontrado. Instala um JDK ou define JAVA_HOME." }

$root  = Split-Path -Parent $PSScriptRoot
$certs = Join-Path $root "certs"
New-Item -ItemType Directory -Force $certs | Out-Null
Remove-Item "$certs\*" -Force -ErrorAction SilentlyContinue

& $keytool -genkeypair -alias aisafe -keyalg RSA -keysize 2048 -validity 365 -storetype PKCS12 `
    -keystore "$certs\aisafe-keystore.p12" -storepass $Password `
    -dname "CN=localhost, OU=AISafe, O=AISafe, C=PT" -ext "SAN=dns:localhost,ip:127.0.0.1"

& $keytool -exportcert -alias aisafe -keystore "$certs\aisafe-keystore.p12" -storepass $Password `
    -rfc -file "$certs\aisafe-cert.pem"

& $keytool -importcert -alias aisafe -file "$certs\aisafe-cert.pem" -storetype PKCS12 `
    -keystore "$certs\aisafe-truststore.p12" -storepass $Password -noprompt

$targets = @(
    "auth-service\src\main\resources",
    "aircraft-service\src\main\resources",
    "flight-ops-service\src\main\resources",
    "airportRoutes-service\demo\src\main\resources"
)
foreach ($t in $targets) {
    Copy-Item "$certs\aisafe-keystore.p12", "$certs\aisafe-truststore.p12" (Join-Path $root $t) -Force
}
Write-Host "Certificados gerados em $certs e copiados para os 4 servicos."