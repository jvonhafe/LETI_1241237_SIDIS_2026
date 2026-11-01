# Arquitetura e Design - Aircraft & Maintenance Service

## 1. Segregação Dirigida pelo Domínio (Domain-Driven Segregation)
No contexto da arquitetura distribuída do AISafe, este microsserviço (a correr primariamente na porta 8081) atua como a única fonte de verdade para o domínio de engenharia e frota. Encapsula as seguintes operações:
*   **Gestão de Frota:** Catálogo de modelos de aeronaves e registo de instâncias ativas.
*   **Manutenção:** Definição de templates de inspeção e acompanhamento de registos (records) de manutenção.
    Esta segregação garante um baixo acoplamento com o `flight-ops-service`, permitindo que as regras de negócio de manutenções evoluam de forma totalmente isolada das operações de rotas e voos.

## 2. Tolerância a Falhas e Replicação
Para evitar pontos únicos de falha (Single Point of Failure) no acesso aos dados técnicos, o `aircraft-service` suporta replicação horizontal:
*   **Arquitetura Stateless:** A autenticação e o processamento de pedidos não dependem de sessões locais. Qualquer instância do serviço pode responder a um pedido, permitindo que a carga seja distribuída.
*   **Disponibilidade Operacional:** Se uma instância do serviço cair, outras réplicas podem assumir os pedidos (ex: quando o `flight-ops-service` necessita de agregar os detalhes da aeronave para apresentar o painel de voo).

## 3. Segurança de Dados e Autenticação Inter-Serviços
Devido ao caráter crítico e sensível dos dados aeronáuticos e de manutenção, a segurança foi integrada diretamente ao nível da infraestrutura do serviço:
*   **Validação Descentralizada JWT:** O serviço não depende de chamadas constantes a um servidor de autenticação central. Através do `JwtAuthenticationFilter` e do `JwtService` no pacote `core.security`, a integridade do token é validada localmente via assinatura criptográfica.
*   **Zero Trust Architecture:** O `aircraft-service` rejeita pedidos internos (como as agregações vindas do `flight-ops-service`) que não apresentem um token `Bearer` válido, devolvendo HTTP 403 Forbidden. O `SecurityConfig` assegura que apenas chamadas inter-serviços devidamente autenticadas acedem às rotas operacionais.

## 4. Instruções de Deployment
Para provisionar o serviço e simular a replicação local:
