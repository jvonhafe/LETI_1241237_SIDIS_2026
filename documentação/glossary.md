# Glossário - AISafe Flight Management System

Este glossário está rigorosamente sincronizado com o Diagrama de Modelo de Domínio (`diagram.puml`) e com a arquitetura do sistema, englobando todas as funcionalidades desenvolvidas nas séries US 100 e US 200.

---

## 1. Aircraft Domain (Domínio de Aeronaves)
*(Baseado no diagrama aircraft_DM.puml)*

### Aircraft
*(Aggregate Root)* O agregado principal que representa uma instância física e real de um avião pertencente à frota. Mantém o registo do tempo de utilização através da propriedade `totalFlightHours` (Horas Totais de Voo).

### AircraftRegistration
*(Value Object)* Identificador único da aeronave no sistema, encapsulado na propriedade `code` (ex: a matrícula do avião, como `CS-TTO`). Cada `Aircraft` é identificada estritamente por este objeto.

### AircraftStatus
*(Value Object / Enum)* Define de forma rígida o estado operacional da aeronave física. Os estados permitidos são:
- `ACTIVE`: Aeronave operacional e disponível para voos.
- `INACTIVE`: Aeronave temporariamente indisponível.
- `IN_MAINTENANCE`: Aeronave retida para intervenções técnicas.
- `RETIRED`: Aeronave abatida permanentemente da frota.

---

## 2. Aircraft Model Domain (Domínio de Modelos de Aeronave)
*(Baseado no diagrama aircraft_DM.puml)*

### AircraftModel
*(Entity)* Representa o modelo de construção partilhado por várias aeronaves. Cada aeronave física (`Aircraft`) está ligada a um `AircraftModel` (is of type). Define as caraterísticas estruturais:
- **manufacturer (Fabricante):** A empresa construtora (ex: Airbus, Boeing).
- **maxRange (Alcance Máximo):** A distância máxima de operação do modelo.
- **maxWeight (Peso Máximo):** O limite de peso estrutural suportado.

### ModelId
*(Value Object)* Identificador único utilizado para distinguir diferentes modelos de aeronaves no sistema (propriedade `id`). Serve como chave primária deste domínio.

### Capacity
*(Value Object)* Objeto que encapsula a lotação do modelo de aeronave. Define o limite legal e físico de lugares através da propriedade `maxSeats` (Lotação Máxima). recursos visuais, como diagramas técnicos (`fileUrl`) ou manuais operacionais (`diagramType`).

---

## 3. Airport Domain (Domínio de Aeroportos)
*(Reflete o diagrama de domínio de aeroportos e as regras das US 106 a 109, e série 200)*

### Airport
*(Entity, Aggregate Root)* O agregado principal responsável por representar um aeroporto. Gere de forma atómica a sua infraestrutura física (pistas), o seu estado operacional, a sua localização geográfica e as certificações de modelos de aeronaves que lá podem aterrar.

### AirportCode
*(Value Object, Id)* Identificador único do aeroporto (ex: código IATA `OPO`, `LIS`). Serve de chave primária inalterável para o agregado e é a referência usada por outros domínios (como a `Route`) para apontar para um aeroporto sem o duplicar.

### Location
*(Value Object)* Agrupa de forma coesa e imutável as coordenadas e a morada do aeroporto. Contém:
- **city:** Cidade onde se insere.
- **country:** País.
- **latitude / longitude:** Coordenadas geográficas exatas.

### Timezone & AirportType
*(Value Objects)*
- **Timezone:** Fuso horário oficial de operação do aeroporto.
- **AirportType:** Classificação estrutural do aeroporto (ex: Comercial, Militar, Doméstico).

### AirportStatus
*(Value Object)* Indica a disponibilidade atual da infraestrutura para receber e enviar voos (ex: `OPERATIONAL`, `CLOSED`, `UNDER_MAINTENANCE`).

### Runway
*(Entity)* Entidade local e dependente do `Airport`. Representa uma pista de aterragem/descolagem individual, identificada pelo seu nome ou designador (`designatorName`), possuindo medidas físicas de comprimento (`length`) e a sua orientação magnética (`orientation`).

### AircraftCertification
*(Entity)* Entidade local que funciona como uma licença. Garante que um determinado modelo de avião (guardando o seu `AircraftModelId`) está oficialmente autorizado e é tecnicamente compatível para operar nas pistas deste aeroporto. Esta licença é válida durante uma janela temporal definida por `validFrom` e `validTo`.

### MediaImage
*(Entity)* Representação visual fotográfica, diagramas de terminais ou renderizações do aeroporto, detalhando o seu endereço web (`imageUrl`) e a sua descrição textual (`description`).

---

## 4. Route Domain (Domínio de Rotas)
*(Reflete o diagrama de domínio de rotas detalhado e ligações a Aeroportos)*

### Route
*(Entity, Aggregate Root)* Representa uma ligação de voo comercial predefinida. É o agregado principal que gere as regras operacionais, os horários planeados, e mantém o registo histórico da sua evolução no tempo. A rota não guarda os objetos do aeroporto inteiros, mas sim as referências rígidas `originId` e `destinationId` mapeadas para o *AirportCode* correspondente.

### RouteId
*(Value Object, Id)* Identificador único gerado para a rota (neste contexto mapeado para um `Long` sequencial).

### RouteRequirements
*(Value Object)* Objeto que encapsula de forma atómica os requisitos mínimos e restrições que uma aeronave deve cumprir para ser elegível para esta rota:
- **minimumRange:** Autonomia de voo mínima exigida.
- **minimumCapacity:** Limite mínimo de lugares/passageiros exigido para garantir a viabilidade comercial do percurso.

### FlightDuration
*(Value Object)* Encapsula a duração estimada para a conclusão do percurso entre a origem e o destino.

### RouteSchedule
*(Value Object)* Define a janela de planeamento da rota. Contém:
- **operatingDays:** Os dias específicos da semana em que a rota está ativa e planeada para operar.
- **timeWindow:** A janela temporal diária atribuída para a realização dos voos desta rota.

### RouteType
*(Value Object)* Classificação estrutural ou categoria da rota (ex: Doméstica, Internacional, Comercial, Carga), definida pela propriedade `type`.

### RouteStatus
*(Value Object)* Estado lógico de ativação da rota. Indica se a rota se encontra operacional (`ACTIVE`) ou suspensa/desativada (`DEACTIVATED`), impactando a capacidade de agendar novos voos para a mesma.

### RouteHistory
*(Entity)* Entidade local contida dentro do agregado `Route` que serve como log de auditoria. Sempre que a rota sofre alterações, é criado um registo inalterável que contém a ação (`action`), uma descrição textual (`description`) e a data/hora do evento (`timestamp`).

## 5. Scheduled Flight Domain (Domínio de Voos Planeados)
*(Cobre operações de agendamento de frota)*

### ScheduledFlight
*(Entity, Aggregate Root)* Representa a execução física e temporal de uma `Route`. Associa uma aeronave específica (`AircraftRegistration`) a uma rota (`RouteId`) num determinado momento.

### FlightId
*(Value Object, Id)* Identificador único do voo no sistema de reservas e controlo de tráfego.

### FlightSchedule
*(Value Object)* Define a janela cronológica do voo, englobando a data/hora exata de partida (`departureLDT`) e chegada (`arrivalLDT`).

---

## 6. Maintenance Domain (Domínio de Manutenção)
*(Cobre as US 115 a 119 e lógicas avançadas US 217 a 222, como Alertas e Cálculos de Frota)*

### MaintenanceTemplate
*(Entity, Aggregate Root)* Define um plano/molde de manutenção reutilizável (US115_Template) que descreve o tipo de procedimento (`TemplateType`) e a `Checklist` rigorosa de tarefas a realizar. Associa-se aos modelos de aeronaves compatíveis.

### MaintenanceRecord
*(Entity, Aggregate Root)* Representa uma instância real de intervenção de manutenção numa aeronave específica (US115_Record a US119). Monitoriza descrições, progresso, consumo temporal e relatórios pós-manutenção.

### RecordId
*(Value Object, Id)* Identificador unívoco do registo de intervenção.

### MaintenanceSchedule / MaintenancePeriod
*(Value Object)* Define o planeamento temporal da manutenção, incluindo a data de início oficial e a estimativa ou total de horas consumidas (`expectedDuration`), crucial para gerar o total de horas da frota (US117).

### MaintenanceStatus
*(Value Object / Enum)* Estado de ciclo de vida da reparação (ex: Agendado, Em Curso, Concluído).

### Notas de Conclusão (Completion Notes)
Relatório ou observações finais submetidas obrigatoriamente para possibilitar o fecho legal do registo de manutenção.

---

## Relações e Práticas de Domain-Driven Design (DDD)

A arquitetura do sistema assegura o isolamento de domínios (Baixo Acoplamento) e a proteção das invariantes através da **Regra de Ouro do DDD**: ligações entre *Aggregate Roots* diferentes são feitas **exclusivamente por Identificadores (IDs)**, enquanto elementos internos do mesmo agregado utilizam composição direta.

**1. Relações Inter-Agregados (Baixo Acoplamento por IDs):**
- **Airport ⇄ Route:** Uma `Route` não duplica nem carrega os dados pesados dos aeroportos; referencia rigidamente os identificadores `originId` e `destinationId` (usando o `AirportCode` / IATA).
- **Aircraft ⇄ AircraftModel:** Um `Aircraft` físico está acoplado ao seu modelo guardando apenas o identificador `modelId` (`AircraftModelId`).
- **ScheduledFlight ⇄ Aircraft & Route:** Um `ScheduledFlight` é estritamente a fusão temporal por ID de um avião (usando a `AircraftRegistration`) e de uma rota (usando o `RouteId`).
- **MaintenanceRecord ⇄ Aircraft & Template:** O `MaintenanceRecord` liga o plano de manutenção ao avião operado utilizando respetivamente o `TemplateId` e a `AircraftRegistration`.
- **MaintenanceTemplate ⇄ AircraftModel:** Um `MaintenanceTemplate` define a que modelos de avião se aplica guardando uma lista de identificadores (`AircraftModelId`), evitando acoplamento estrutural.
- **Airport ⇄ AircraftModel (Certificações):** A entidade local `AircraftCertification` dentro do Aeroporto define que modelos estão autorizados a aterrar guardando apenas a referência ao `AircraftModelId`.

**2. Relações Intra-Agregados (Composição Forte):**
- Sempre que as entidades pertencem ao mesmo limite de consistência (*Aggregate Root*), a relação é feita por composição direta de objetos.
- Exemplos do projeto incluem: O `Airport` contém diretamente a lista de objetos `Runway` e `Facility`; A `Route` possui diretamente a lista de `RouteHistory`. Apenas o agregado principal pode modificar estas entidades locais.