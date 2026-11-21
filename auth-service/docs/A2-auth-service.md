# Assignment 2 - auth-service

## Papel no sistema
O auth-service emite JWT (HS256) para utilizadores e para os outros servicos. Nao faz parte dos dominios
CQRS (aeronaves, aeroportos, voos); mantem um modelo simples e sincrono, porque o login tem de responder logo.
Os endpoints externos continuam HTTP REST: POST /api/auth/login, POST /api/auth/service-token, POST /api/auth/users.

## Duas instancias e base de dados por instancia
O servico corre em duas instancias (8084 e 8087), cada uma com a sua base de dados H2 (Database-per-Service).
Os utilizadores iniciais sao semeados em cada instancia no arranque. O JWT e sem estado e todas as instancias
partilham o segredo de assinatura, por isso um token emitido por uma instancia e aceite pelas outras e
pelos restantes servicos (provado na pasta 7 do Postman).

## Comunicacao assincrona (AMQP)
Quando um ADMIN cria um utilizador (POST /api/auth/users) a instancia grava-o na sua BD e publica o evento
UserCreated na exchange fanout "aisafe.auth.users". Cada instancia tem uma fila duravel propria
("auth.users.<instance-id>"), consome o evento e grava o utilizador na SUA base de dados.
O evento transporta o hash BCrypt, nunca a password em claro.

## Modelo de consistencia
Consistencia eventual: ha uma pequena janela em que o utilizador existe so na instancia onde foi criado.
O consumo e idempotente (se o utilizador ja existe, ignora) e nao volta a publicar, por isso nao ha ciclos.

## Tolerancia a falhas
- Broker indisponivel: o utilizador fica criado localmente e a falha de publicacao e registada no audit log
  (USER_EVENT_PUBLISH ERROR). O login continua a funcionar nas duas instancias para os utilizadores existentes.
- Instancia em baixo: a fila duravel guarda os eventos e entrega-os quando a instancia volta.
- Mensagem invalida: e descartada e auditada (evita reentrega em ciclo).
- Auth-service em baixo: os outros servicos continuam a validar tokens ja emitidos (so precisam do segredo);
  ninguem consegue novos tokens ate voltar.

## Limitacoes e alternativas
- H2 em memoria: uma instancia que reinicia perde os utilizadores criados por API (os iniciais sao semeados de novo).
- Evento perdido (broker em baixo no momento da publicacao) nao e reenviado. Alternativa: outbox pattern.
- O evento leva o hash BCrypt: em producao o broker teria TLS e credenciais proprias (nao guest).
- Alternativa a replicar: uma base de dados de identidade partilhada (quebra o Database-per-Service).
- Alternativa ao segredo partilhado HS256: RS256 com chave publica (JWKS), sem partilhar o segredo.

## Como correr
RabbitMQ ligado e, em dois terminais, dentro de auth-service:

    mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8084"
    mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8087"

Testes: pasta "7. Auth - 2 instancias + AMQP (A2)" da colecao docs/aisafe-security.postman_collection.json.