# AISafe Flight Management System — Airport & Routes Service (Assignment 2)

## 1. Visão Geral da Arquitetura
No âmbito do **Assignment 2** de Sistemas Distribuídos, o microsserviço `airport-routes-service` evoluiu de uma arquitetura baseada em comunicação síncrona (HTTP P2P *forwarding*) para uma arquitetura orientada a eventos com **CQRS**, **mensageria assíncrona via AMQP (RabbitMQ)** e **isolamento total de dados por instância (Database-per-Service)**.

### Objetivos Alcançados
* **Separação CQRS (Command-Query Responsibility Segregation):** Modelos, repositórios e tabelas independentes para operações de escrita (*Commands*) e leitura (*Queries*).
* **Comunicação Assíncrona (AMQP):** Substituição de chamadas REST internas por publicação e consumo de eventos de domínio através do *Topic Exchange* `aisafe.domain.exchange`.
* **API Externa REST Segura:** Todos os endpoints externos (`/api/airports` e `/api/routes`) mantêm-se sobre HTTPS/TLS com autenticação JWT e controlo de acessos RBAC (`ADMIN`, `ATCC`, `BACKOFFICE`, `SERVICE`).
* **Database-per-Service e Múltiplas Instâncias:** Execução simultânea de duas instâncias independentes (portas `8091` e `8092`), cada uma com a sua própria base de dados isolada (`airport_routes_8091` e `airport_routes_8092`).

---

## 2. Decisões de Design e Alternativas Analisadas

### 2.1. CQRS (Write Model vs. Read Model)
* **Decisão:** Os casos de uso de escrita (`CreateRouteUseCase`, `UpdateRouteUseCase`, `DeactivateRouteUseCase`, `RegisterAirportUseCase`, `UpdateAirportStatusUseCase`, `UpdateAirportDetailsUseCase`) validam invariantes de domínio e persistem nas entidades normalizadas (`Airport`, `Route`, `RouteHistory`). Os casos de uso de leitura (`GetRouteByIdUseCase`, `SearchRouteUseCase`, `GetRoutesFromAirportUseCase`, `GetActiveRoutesSortedUseCase`) consultam exclusivamente projeções desnormalizadas (`AirportReadModel`, `RouteReadModel`).
* **Vantagem:** Elimina *joins* em tempo de leitura (ex.: `RouteReadModel` já armazena `originName` e `destinationName` diretamente) e permite escalar consultas independentemente das escritas.
* **Alternativa considerada:** Manter um único modelo relacional partilhado para leitura e escrita. Rejeitada por acoplar o esquema de consulta às regras transacionais de escrita e dificultar a replicação entre instâncias isoladas.

### 2.2. Comunicação Assíncrona e Topologia RabbitMQ
* **Decisão:** Utilização de um `TopicExchange` (`aisafe.domain.exchange`) onde cada instância do serviço declara a sua própria fila durável exclusiva (`airport-routes.sync.8091` e `airport-routes.sync.8092`) associada às *routing keys* `airport.#` e `route.#`.
* **Vantagem:** Ao contrário de uma fila partilhada (*Competing Consumers*), onde apenas uma instância receberia cada evento, o padrão *Publish/Subscribe* com filas por instância garante que **todas as réplicas** recebem os eventos de domínio e mantêm os seus *Read Models* sincronizados. Permite também que o `flight-service` subscreva os mesmos eventos sem acoplamento temporal.
* **Alternativa considerada:** *HTTP Forwarding* síncrono entre pares (implementado na Fase 1) ou chamadas REST entre microsserviços. Rejeitada porque degrada a disponibilidade quando uma instância ou serviço vizinho está lento ou inacessível (*cascading failures*).

### 2.3. Modelo de Consistência (Teorema CAP)
* **Decisão:** Adoção de **Consistência Eventual (Eventual Consistency)** em detrimento de transações distribuídas síncronas.
* **Justificação:** Num sistema distribuído sujeito a partições de rede (CAP), privilegiou-se a **Disponibilidade (AP)** e o desacoplamento: uma escrita na Instância 1 (`8091`) é confirmada imediatamente após persistência local e envio para o broker, propagando-se assincronamente para a Instância 2 (`8092`) em milissegundos.
* **Alternativa considerada:** *Two-Phase Commit (2PC)* ou Sagas síncronas bloqueantes. Rejeitadas pela elevada latência e por introduzirem pontos únicos de bloqueio.

### 2.4. Mecanismos de Tolerância a Falhas
* **Filas Duráveis:** As filas `airport-routes.sync.<port>` e o *exchange* são declarados como `durable`, evitando perda de mensagens em caso de reinício do broker.
* **Idempotência nos Consumidores:** Cada evento transporta um `eventId` (UUID). O `DomainEventConsumer` compara o `eventId` recebido com o campo `lastEventId` persistido no *Read Model*, ignorando entregas duplicadas (*at-least-once delivery*).

---

## 3. Contrato de Eventos AMQP

* **Exchange:** `aisafe.domain.exchange` (`TopicExchange`, durável)
* **Formato das Mensagens:** JSON (`JacksonJsonMessageConverter`)

| Evento | Routing Key | Payload Principal | Consumidores |
| :--- | :--- | :--- | :--- |
| `AirportDomainEvent` | `airport.registered`, `airport.updated`, `airport.status.changed` | `eventId`, `sourcePort`, `eventType`, `iataCode`, `name`, `city`, `country`, `timezone`, `status` | Instâncias `airport-routes` (sync CQRS) e `flight-service` |
| `RouteDomainEvent` | `route.created`, `route.updated`, `route.deactivated` | `eventId`, `sourcePort`, `eventType`, `routeId`, `originIata`, `originName`, `destinationIata`, `destinationName`, `distanceKm`, `status` | Instâncias `airport-routes` (sync CQRS) e `flight-service` |

---

## 4. Instruções de Compilação, Execução e Testes

### Pré-requisitos
* Java 17+
* Maven (`./mvnw`)
* Docker (para executar o RabbitMQ)

### Passo 1: Iniciar o Message Broker (RabbitMQ)
```bash
docker run -d --name aisafe-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management