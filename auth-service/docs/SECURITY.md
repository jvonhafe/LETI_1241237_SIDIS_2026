# AISafe — Segurança (Assignment 1 SIDIS)

## 1. Visão geral
- `auth-service` (porta 8084) é o único sítio onde há passwords. Valida credenciais (BCrypt) e emite **JWT HS256 standard** (Nimbus).
- Claims: `iss`, `sub`, `iat`, `exp`, `role` (ADMIN, BACKOFFICE, ATCC, MAINTENANCE_TECH, MAINTENANCE_SUPERVISOR, SERVICE), `type` (`user` | `service`).
- Os restantes serviços **não guardam passwords**: só validam a assinatura e leem a role.

## 2. Autenticação
| Quem | Como | Endpoint |
|---|---|---|
| Pessoa | username + password → JWT (60 min) | `POST /api/auth/login` |
| Serviço / peer | serviceId + secret → JWT de serviço (5 min, role SERVICE) | `POST /api/auth/service-token` |

Respostas de falha são idênticas (`Invalid credentials`) para username inexistente e password errada; é gasto o mesmo tempo de BCrypt (anti-enumeração).

## 3. Autorização (RBAC)
`@PreAuthorize("hasRole(...)")` nos controllers; tudo o que não é login/Swagger exige autenticação.
Criação de utilizadores: apenas ADMIN (`POST /api/auth/users`).

## 4. Dados em trânsito
TLS (HTTPS) em todos os serviços e chamadas peer-to-peer por `https://` (ver secção de TLS no README do módulo).

## 5. Audit logging
Logger `AUDIT`, uma linha `key=value` por evento (LOGIN, SERVICE_TOKEN, USER_CREATE) com actor, resultado e IP. Nunca regista passwords nem tokens; valores do cliente são sanitizados contra log injection.

## 6. Segredos
Nada hardcoded para produção: `AISAFE_JWT_SECRET` (≥ 32 bytes), `AISAFE_ADMIN_PASSWORD`, `AISAFE_DEFAULT_PASSWORD`, `AISAFE_SVC_*_SECRET`.
Os valores por omissão no `application.properties` servem **só** para desenvolvimento local.

## 7. Limitações conhecidas
- Segredo HS256 partilhado entre serviços (simples; alternativa: RS256 com chave pública/JWKS).
- Sem revogação de tokens antes do `exp` (TTLs curtos mitigam).
- Sem rate limiting no login.
