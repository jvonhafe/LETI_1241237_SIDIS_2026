# AISafe — Segurança (SIDIS Assignment 1)

Este documento descreve a parte de segurança do sistema distribuído AISafe: **autenticação (incluindo entre serviços), cifra em trânsito (TLS), controlo de acesso (RBAC) e audit logging**.

> Documento escrito com ajuda do Claude (Anthropic); o desenho foi revisto e testado por mim.

## 1. Arquitetura

```
                        ┌────────────────────────┐
   pessoa ── login ───▶ │ auth-service  :8084    │  único sítio com passwords (BCrypt)
                        │ emite JWT (HS256)      │  emite tokens de serviço (5 min)
                        └───────────┬────────────┘
                 JWT de utilizador  │  JWT de serviço
        ┌───────────────────────────┼───────────────────────────┐
        ▼                           ▼                           ▼
 aircraft-service :8081   airportRoutes-service     flight-ops-service :8083
 (valida o JWT)           (valida o JWT)            (valida o JWT)
        ▲                           ▲                           │
        └───────── chamadas entre serviços (HTTPS + token SERVICE) ┘
```

- Todos os serviços falam **HTTPS** (TLS).
- Só o `auth-service` conhece passwords. Os outros três **validam a assinatura do JWT** e leem a role; não guardam credenciais de utilizadores.
- Cada serviço tem as suas próprias regras de acesso (`@PreAuthorize`) e o seu próprio ficheiro de audit log.

## 2. Autenticação

| Quem | Como | Endpoint | Validade |
|---|---|---|---|
| Pessoa | username + password → JWT | `POST /api/auth/login` | 60 min |
| Serviço | serviceId + secret → JWT de serviço (role `SERVICE`) | `POST /api/auth/service-token` | 5 min |

- **JWT HS256 standard** (biblioteca Nimbus). Claims: `iss`, `sub`, `iat`, `exp`, `role`, `type` (`user` ou `service`).
- Passwords guardadas com **BCrypt**. Para username inexistente e password errada a resposta é idêntica (`Invalid credentials`) e gasta-se o mesmo tempo de BCrypt (anti-enumeração de utilizadores).
- Cada serviço valida o token com `spring-boot-starter-oauth2-resource-server` e o segredo partilhado `AISAFE_JWT_SECRET`.

### Autenticação entre serviços
Quando o `flight-ops-service` chama o `aircraft-service`, ou um `airportRoutes` chama um peer:
1. O serviço pede ao `auth-service` um token de serviço com o seu `serviceId` e `secret` (`ServiceTokenProvider`).
2. O token fica em cache e é renovado 30 s antes de expirar.
3. Um interceptor do `RestTemplate` junta `Authorization: Bearer <token>` a cada chamada.
4. O serviço de destino só aceita a role `SERVICE` nos endpoints de **leitura** usados entre serviços.

## 3. Autorização (RBAC)

Roles: `ADMIN`, `BACKOFFICE`, `ATCC`, `MAINTENANCE_TECH`, `MAINTENANCE_SUPERVISOR`, `SERVICE`.
A cadeia de segurança exige autenticação em tudo menos Swagger/OpenAPI; as permissões finas são `@PreAuthorize` nos controllers.

| Serviço | Recurso | Quem pode |
|---|---|---|
| auth-service | `POST /api/auth/users` | `ADMIN` |
| flight-ops-service | `/api/flights/**` (classe inteira) | `ADMIN`, `ATCC` |
| airportRoutes-service | `/api/airports/**` | `ADMIN`, `ATCC`, `BACKOFFICE`, `SERVICE` (leitura) |
| airportRoutes-service | criar/alterar aeroportos, estatísticas | `BACKOFFICE`, `ADMIN` |
| airportRoutes-service | `/api/routes/**` | `ADMIN`, `ATCC` (alterar/desativar também `BACKOFFICE`) |
| aircraft-service | `/api/aircraft/{id}/details` e `/specs` | roles de utilizador + `SERVICE` |
| aircraft-service | criar templates de manutenção | `ADMIN`, `MAINTENANCE_TECH` |

Respostas: **401** sem token ou token inválido/expirado; **403** com token válido mas role sem permissão.

## 4. Dados em trânsito (TLS)

- Cada serviço corre com `server.ssl.*` ativo, com keystore PKCS12 (`aisafe-keystore.p12`). HTTP simples é recusado.
- Chamadas entre serviços e ao `auth-service` usam `https://`. O cliente (`RestTemplate`) confia nos certificados através de uma **truststore** própria (`aisafe-truststore.p12`), em vez de desligar a validação.
- Em desenvolvimento o certificado é **auto-assinado** (SAN `localhost` e `127.0.0.1`).
- As chaves **não estão no Git** (`certs/` e `*.p12` estão no `.gitignore`). Gerar com:

```powershell
.\scripts\generate-certs.ps1
```
O script cria o certificado e copia o keystore e a truststore para `src/main/resources` de cada serviço. A password por omissão (`changeit-dev`) é só para desenvolvimento e pode ser mudada com `AISAFE_KEYSTORE_PASSWORD`.

> Os testes de integração carregam o contexto Spring completo, por isso também precisam das chaves: correr o script **antes** de `mvn test` numa máquina nova.

## 5. Audit logging

Logger `AUDIT`, uma linha `chave=valor` por evento:

```
2026-10-31T15:17:26.694Z service=aircraft-service action=HTTP actor=tech role=ROLE_MAINTENANCE_TECH method=GET path=/api/aircraft-models status=403 outcome=DENIED ip=0:0:0:0:0:0:0:1 durationMs=17
```

- Cada serviço tem um `AuditLoggingFilter` **dentro da cadeia de segurança, antes da validação do token**, por isso regista também os pedidos recusados (401/403).
- `outcome`: `SUCCESS` (2xx/3xx), `DENIED` (401/403), `REJECTED` (outros 4xx), `ERROR` (5xx ou exceção).
- O `auth-service` regista ainda eventos de negócio: `LOGIN`, `SERVICE_TOKEN`, `USER_CREATE`.
- **Nunca** se registam tokens, passwords, cabeçalhos, corpos nem *query strings*. Valores controlados pelo cliente são sanitizados (sem quebras de linha nem aspas) contra *log injection*.
- Saída: consola e ficheiro próprio com rotação (10 MB / dia, 30 dias): `logs/audit-<serviço>.log`, relativo à pasta de onde o serviço é arrancado. A pasta `logs/` está no `.gitignore`.

## 6. Segredos e configuração

| Variável | Para quê |
|---|---|
| `AISAFE_JWT_SECRET` | segredo HS256 (≥ 32 bytes), igual em todos os serviços |
| `AISAFE_SVC_AIRCRAFT_SECRET`, `AISAFE_SVC_AIRPORTS_SECRET`, `AISAFE_SVC_FLIGHTS_SECRET` | segredo de cada conta de serviço |
| `AISAFE_ADMIN_PASSWORD`, `AISAFE_DEFAULT_PASSWORD` | passwords iniciais dos utilizadores (auth-service) |
| `AISAFE_KEYSTORE_PASSWORD` | password do keystore/truststore |

Os valores por omissão do `application.properties` existem **só para desenvolvimento local**. Em produção vêm de variáveis de ambiente ou de um gestor de segredos.

## 7. Como correr e testar

1. `.\scripts\generate-certs.ps1` (uma vez).
2. Arrancar por esta ordem: `auth-service` (8084), `aircraft-service` (8081), `flight-ops-service` (8083) e `airportRoutes-service`.
3. Utilizadores de desenvolvimento: `admin` (`Admin#2026`) e `backoffice`, `atcc_user`, `tech`, `supervisor` (`Change#Me2026`).
4. Exemplo (PowerShell; o certificado é auto-assinado, por isso `curl.exe -k` ou aceitar o certificado):

```powershell
$r = Invoke-RestMethod -Method Post -Uri https://localhost:8084/api/auth/login -ContentType "application/json" -Body '{"username":"atcc_user","password":"Change#Me2026"}'
$t = $r.token
Invoke-RestMethod -Uri https://localhost:8081/api/aircraft-models -Headers @{Authorization="Bearer $t"}
```
5. Swagger de cada serviço (`https://localhost:<porta>/swagger-ui.html`) tem o botão **Authorize**: cola o token (sem a palavra `Bearer`).
6. Coleção do Postman: `docs/aisafe-security.postman_collection.json`.
7. Testes automáticos: `mvn test` em cada serviço (RBAC com a cadeia de segurança real, filtro de auditoria, token JWT).

Resultados esperados: sem token → 401; role errada → 403; role certa → 200; `http://` em vez de `https://` → recusado.

## 8. Decisões e consistência

- **Um emissor, vários validadores:** só o `auth-service` emite tokens; os outros validam offline, sem chamadas ao auth em cada pedido (disponibilidade e latência).
- **Tokens de serviço curtos (5 min) e só de leitura** limitam o estrago se um segredo vazar.
- **Segurança no serviço de destino:** cada serviço decide o acesso aos seus dados; não se confia no que o chamador diz ser.
- A segurança não altera os contratos de dados dos colegas: só adiciona cabeçalho `Authorization`, respostas 401/403 e `https://`.

## 9. Limitações conhecidas

- Segredo HS256 partilhado entre serviços (alternativa: RS256 com chave pública/JWKS).
- Sem revogação de tokens antes do `exp` (TTL curtos mitigam) e sem *rate limiting* no login.
- Certificado auto-assinado em desenvolvimento; em produção usar uma CA e, idealmente, mTLS entre serviços.
- Bases de dados H2 em memória; consola H2 desativada.

## 10. Problemas comuns

| Sintoma | Causa |
|---|---|
| 401 em tudo, "malformed token" | `auth-service` não está a correr ou `$t` vazio |
| `Bad authority` ao arrancar | espaços no fim de `aisafe.auth.url` |
| 400 "requires TLS" | pedido `http://` a um serviço HTTPS |
| Testes falham a carregar o contexto | faltam as chaves: correr `scripts/generate-certs.ps1` |