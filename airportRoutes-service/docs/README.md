# AISafe - Plataforma Distribuída de Gestão de Operações de Aviação

## 1. Visão Geral e Arquitetura do Sistema
O **AISafe** é um sistema distribuído baseado em microsserviços, desenvolvido em **Java com Spring Boot**, desenhado para gerir aeroportos, rotas e operações de voo de forma escalável e resiliente. O projeto implementa replicação de dados descentralizada, mensageria assíncrona orientada a eventos e propagação de pedidos ponto a ponto (peer-to-peer).

### Stack Tecnológica:
* **Backend:** Spring Boot (WebMVC, HATEOAS, Data JPA).
* **Base de Dados:** H2 (Persistência local relacional por instância).
* **Mensageria:** RabbitMQ (Versão 4, filas duráveis com `durable: true`).
* **Documentação de API:** SpringDoc OpenAPI (Swagger).

---

## 2. Decisões de Design e Modelos C4
A arquitetura está documentada seguindo o modelo C4 para garantir a legibilidade e separação de responsabilidades:
* **Nível 1 (Contexto):** Interação entre o Administrador/ATCC e a plataforma distribuída.
* **Nível 2 (Contentores):** Isolamento entre instâncias de microsserviços de aeroportos, base de dados H2 e o Message Broker (RabbitMQ).
* **Nível 3 (Componentes):** Divisão interna da aplicação por camadas (Controllers, Use Cases, Repositories, Producers e Consumers).

---

## 3. Modelo de Consistência e Tolerância a Falhas (HTTP Forwarding)
Para garantir que os dados replicados entre instâncias vizinhas (portas `8081` e `8082`) sejam consultados de forma transparente sem uma base de dados centralizada, implementou-se um mecanismo de **HTTP Forwarding**:

1. **Consulta Local:** Quando um cliente pede um aeroporto por código IATA (`GET /api/airports/{iata}`), a instância consulta primeiro a sua base de dados local.
2. **Propagação Ponto a Ponto:** Caso o recurso não exista localmente, a instância atua como proxy e reencaminha o pedido HTTP para as instâncias vizinhas registadas na lista de peers.
3. **Prevenção de Loops:** Para evitar ciclos infinitos de reencaminhamento entre instâncias, injeta-se o cabeçalho personalizado `X-Forwarded=true`. Se uma instância recebe um pedido com este cabeçalho e não encontra o dado localmente, aborta a pesquisa e devolve um erro controlado.
4. **Mensageria Assíncrona:** A criação de aeroportos despacha eventos de forma assíncrona através do RabbitMQ (`hello-queue`), garantindo o desacoplamento temporal e resiliência a falhas de rede.

---

## 4. Guia de Implementação e Deploy

### Pré-requisitos:
* Java JDK 17 ou superior.
* Apache Maven (ou utilização do Maven Wrapper incluído `./mvnw`).
* Servidor RabbitMQ a correr localmente na porta `5672` (com painel de gestão na porta `15672`).

### Instruções de Instalação e Execução:
1. **Compilar o projeto:**
   ```bash
   ./mvnw clean install
   ```
2. **Arrancar a Instância 1 (Porta 8081):**
   Execute o projeto a partir da classe principal `AisafeApplication.java` com a porta 8081.
    ```bash
   -Dserver.port=8081
   ```
3. **Arrancar a Instância 2 (Porta 8082):**
   Duplique a configuração de execução no IDE (ex: IntelliJ) e defina a opção de VM:
   ```bash
   -Dserver.port=8082
   ```

---

## 5. Guião de Testes Práticos (Postman)

### Teste 1: Registo Simples & RabbitMQ (Instância 1)
* **Método:** `POST`
* **URL:** `http://localhost:8081/api/airports`
* **Body (JSON):**
```json
{
    "iataCode": {
        "code": "OPO"
    },
    "name": "Aeroporto Francisco Sá Carneiro",
    "city": "Porto",
    "country": "Portugal",
    "timezone": "Europe/Lisbon",
    "status": "OPERATIONAL"
}
```

### Teste 2: Registo Detalhado (Instância 2)
* **Método:** `POST`
* **URL:** `http://localhost:8082/api/airports/detailed`
* **Body (JSON):**
```json
{
    "iataCode": "LIS",
    "name": "Aeroporto Humberto Delgado",
    "city": "Lisboa",
    "country": "Portugal",
    "timezone": "Europe/Lisbon",
    "facilities": [
        {
            "type": "Lounge",
            "capacity": 150,
            "description": "TAP Premium Lounge"
        }
    ]
}
```

### Teste 3: Teste de Ouro (HTTP Forwarding)
* **Método:** `GET`
* **URL:** `http://localhost:8081/api/airports/LIS`
* **Descrição:** Pede um aeroporto criado na Instância 2 (`8082`) diretamente à Instância 1 (`8081`). O sistema reencaminha o pedido internamente e devolve os dados com sucesso (`200`) e ao mesmo tempo parece no run a informação que foi buscar a outra instância.