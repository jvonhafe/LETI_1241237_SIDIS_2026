# Arquitetura e Design - Aircraft & Maintenance Service

## 1. Segregação Dirigida pelo Domínio (Domain-Driven Segregation)
No contexto da arquitetura distribuída do AISafe, o `aircraft-service` atua como a fonte de verdade para o domínio de engenharia, frota e manutenção, encapsulando:
* **Gestão de Frota:** Catálogo de modelos de aeronaves (`AircraftModel`) e registo de instâncias ativas (`Aircraft`).
* **Manutenção:** Definição de templates de inspeção (`MaintenanceTemplate`) e acompanhamento de registos de manutenção (`MaintenanceRecord`).

Esta segregação garante baixo acoplamento com os restantes serviços (`flight-ops-service` e `airportRoutes-service`), permitindo que a lógica de frota e manutenção evolua de forma independente.

## 2. Arquitetura CQRS, RabbitMQ e Database-per-Service
Para suportar escalabilidade horizontal com isolamento total de dados (**Database-per-Service**, onde cada instância do serviço tem a sua própria base de dados H2), o serviço adota **CQRS** com propagação assíncrona via **RabbitMQ**:
* **Separação Write / Read (CQRS):**
    * **Comandos (`POST`, `PUT`, `DELETE`):** Persistem alterações nos modelos de escrita (`Aircraft`, `AircraftModel`, `MaintenanceRecord`) e publicam eventos de domínio através do `AircraftEventPublisher` (`AircraftEvent`, `AircraftModelEvent`, `MaintenanceEvent`).
    * **Consultas (`GET`):** Consultam os modelos de leitura locais (`AircraftReadModel`, `AircraftModelReadModel`, `MaintenanceReadModel`) através de serviços/repositórios dedicados (ex.: `MaintenanceQueryService`), inicializados no arranque pelo `ReadModelInitializer`.
* **Comunicação Assíncrona (AMQP / RabbitMQ):**
    * Configurado em `RabbitMQConfig`, o serviço publica eventos no broker e cada instância consome-os através do `AircraftEventListener` para sincronizar os seus modelos de leitura locais.
* **Modelo de Consistência:**
    * **Consistência eventual:** após uma escrita numa instância (ex.: `8081`), existe uma pequena janela temporal até o evento ser entregue via RabbitMQ e refletido nos *read models* da outra instância (ex.: `8086`).

## 3. Tolerância a Falhas e Replicação
* **Arquitetura Stateless:** A autenticação JWT é validada localmente, permitindo que qualquer instância responda a pedidos sem dependência de sessões.
* **Disponibilidade Operacional:** Se uma instância cair, a outra continua a servir comandos e leituras com a sua base de dados local (incluindo pedidos de agregação vindos do `flight-ops-service`).
* **Desacoplamento via Broker:** Falhas temporárias numa réplica não bloqueiam as operações de escrita nas restantes instâncias.

## 4. Segurança de Dados e Autenticação Inter-Serviços
* **Validação Descentralizada JWT:** Através do `JwtAuthenticationFilter` e `JwtService` (`core.security`), os tokens JWT são validados localmente sem chamadas constantes ao `auth-service`.
* **RBAC e Zero Trust:** O `SecurityConfig` protege os endpoints sobre HTTPS com controlo de acesso por papéis (`ADMIN`, `MAINTENANCE_TECH`, `MAINTENANCE_SUPERVISOR`, `ATCC`, `SERVICE`), rejeitando pedidos sem token válido ou sem permissões adequadas (`401` / `403`).

## 5. Limitações e Alternativas Consideradas
* **H2 em memória vs. BD persistente (ex.: PostgreSQL):** A H2 isolada por instância simplifica o ambiente de desenvolvimento, mas perde dados ao reiniciar. Em produção usar-se-ia uma BD persistente por instância ou replicação gerida.
* **Comunicação assíncrona (RabbitMQ) vs. replicação síncrona P2P:** A abordagem assíncrona desacopla as instâncias e melhora a resiliência, trocando consistência forte por consistência eventual.
* **Perda de eventos com broker em baixo:** Se o RabbitMQ estiver indisponível no momento da escrita, o evento não é entregue às outras réplicas. Como alternativa robusta em produção, aplicar-se-ia o padrão **Transactional Outbox**.

## 6. Instruções de Deployment
1. Iniciar o **RabbitMQ** (`localhost:5672`).
2. Iniciar o `auth-service` (`8084`).
3. Em dois terminais, dentro de `aircraft-service`, arrancar duas instâncias com bases de dados isoladas (`8081` e `8086`):
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
   mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8086"