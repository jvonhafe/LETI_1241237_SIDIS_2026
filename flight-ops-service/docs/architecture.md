# Arquitetura e Design - Flight Operations Service

## 1. Segregação Dirigida pelo Domínio (Domain-Driven Segregation)
O `flight-ops-service` atua como o orquestrador central das operações de voo no ecossistema AISafe. A sua principal responsabilidade é a gestão da entidade `ScheduledFlight`. Este microsserviço isola a lógica de negócio do agendamento operacional, não mantendo réplicas locais de aeroportos ou aeronaves, garantindo assim um baixo acoplamento.

## 2. Arquitetura CQRS e Sincronização Assíncrona via RabbitMQ
Para suportar múltiplas instâncias com bases de dados H2 isoladas (**Database-per-Service**), o serviço utiliza **CQRS** com eventos assíncronos no **RabbitMQ**:
- **Escrita vs. Leitura (CQRS):** Operações de escrita (`POST`, `PUT`, `DELETE`) atualizam o modelo de escrita (`ScheduledFlight`) e o modelo de leitura local (`FlightReadModel`), publicando um `FlightEvent` (`CREATED`, `UPDATED`, `DELETED`). As leituras (`GET`) consultam o `FlightReadModel` local da instância.
- **RabbitMQ (`FanoutExchange`):** Os eventos são enviados para o `FanoutExchange` (`flight.events.exchange`). Cada instância declara uma fila exclusiva, não-durável e `auto-delete` com sufixo único (`UUID`), evitando conflitos entre réplicas e sem usar o `queue_master_locator` depreciado no RabbitMQ 4.x. O `FlightEventListener` atualiza o `FlightReadModel` local ao receber o evento.
- **Consistência Eventual e Tolerância a Falhas:** A sincronização entre instâncias (ex.: portas `8083` e `8085`) é eventualmente consistente. Cada instância mantém autonomia sobre a sua base H2 local e reconecta automaticamente ao broker (`localhost:5672`) se houver falha temporária de ligação.

## 3. Agregação Externa e Comunicação Peer-to-Peer (P2P)
Para compor vistas detalhadas de voos com dados externos, o serviço mantém comunicação P2P com outros microsserviços:
- **Agregação Dinâmica:** Através do `RestTemplateConfig`, efetua chamadas ao `aircraft-service` e ao `airportRoutes-service` para enriquecer os detalhes do voo.
- **Resiliência:** Trata falhas parciais nos serviços externos, permitindo devolver informação parcial quando algum serviço externo não responde.

## 4. Segurança e Propagação de Contexto (JWT + RBAC)
- **Validação e RBAC:** Protege os endpoints sobre HTTPS com `JwtAuthenticationFilter` e controlo por perfis (`ADMIN`, `ATCC`, `SERVICE`).
- **Propagação de Identidade:** Repassa o token JWT no `RestTemplate` nas chamadas a serviços externos para manter o contexto de autenticação.

## 5. Deployment
1. Iniciar o **RabbitMQ** (`localhost:5672`).
2. Iniciar o `auth-service` (`8084`).
3. Iniciar a primeira instância do `flight-ops-service` (`8083`):
   `mvn spring-boot:run`
4. Iniciar a segunda instância (`8085`) com base de dados H2 isolada:
   `mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8085"`