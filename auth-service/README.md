# auth-service

Serviço de identidade do AISafe (porta **8084**). Swagger: http://localhost:8084/swagger-ui.html

## Correr
```
mvn spring-boot:run
```

## Utilizadores iniciais (WP0)
| username | role | password (dev) |
|---|---|---|
| admin | ADMIN | `Admin#2026` |
| backoffice | BACKOFFICE | `Change#Me2026` |
| atcc_user | ATCC | `Change#Me2026` |
| tech | MAINTENANCE_TECH | `Change#Me2026` |
| supervisor | MAINTENANCE_SUPERVISOR | `Change#Me2026` |

## Exemplos
```
curl -X POST localhost:8084/api/auth/login -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"Admin#2026"}'

curl -X POST localhost:8084/api/auth/service-token -H "Content-Type: application/json" \
     -d '{"serviceId":"aircraft-service","secret":"dev-aircraft-service-secret"}'
```
Documentação: `docs/SECURITY.md`
