# Arquitetura e Design - Flight Operations Service

## 1. Segregação Dirigida pelo Domínio (Domain-Driven Segregation)
O `flight-ops-service` atua como o orquestrador central das operações de voo no ecossistema AISafe. A sua principal responsabilidade é a gestão da entidade `ScheduledFlight`. Este microsserviço isola a lógica de negócio do agendamento operacional, não mantendo réplicas locais de aeroportos ou aeronaves, garantindo assim um baixo acoplamento.

## 2. Agregação e Comunicação Peer-to-Peer (P2P)
Enquanto outros serviços atuam principalmente como fornecedores de dados, este serviço atua frequentemente como **cliente P2P**:
*   **Agregação Dinâmica:** Através do `RestTemplateConfig`, o serviço efetua chamadas síncronas (REST) ao `aircraft-service` e ao `airportRoutes-service` para compor os detalhes completos de um voo antes de os devolver ao cliente.
*   **Resiliência:** O serviço está desenhado para processar falhas parciais (ex: caso o serviço de rotas esteja em baixo, o voo é retornado com dados parciais ou com um aviso adequado).

## 3. Segurança e Propagação de Contexto (JWT)
O `flight-ops-service` desempenha um papel crítico na cadeia de segurança (Zero Trust):
*   **Validação de Entrada:** Interceta os pedidos via `JwtAuthenticationFilter` para garantir que quem agenda um voo (ex: ADMIN ou ATCC) tem privilégios válidos.
*   **Propagação de Identidade:** Sempre que precisa de consultar um serviço externo (como a validação de uma aeronave), o serviço extrai o JWT do cabeçalho original e repassa-o através do `RestTemplate`. Isto garante que o serviço final (ex: `aircraft-service`) sabe a identidade real do utilizador que iniciou a cadeia de pedidos.

## 4. Instruções de Deployment
O serviço corre por defeito na porta 8083 e exige que os restantes serviços de domínio estejam operacionais para as US de agregação.
```bash