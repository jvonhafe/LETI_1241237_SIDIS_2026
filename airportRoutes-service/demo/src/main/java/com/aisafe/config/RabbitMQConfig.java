package com.aisafe.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "aisafe.domain.exchange";

    @Value("${server.port:8091}")
    private String serverPort;

    // 1. Mantém a tua fila original intacta para o HelloConsumer
    @Bean
    public Queue helloQueue() {
        return new Queue("hello-queue", true);
    }

    // 2. Exchange principal para eventos de domínio (CQRS e comunicação entre serviços)
    @Bean
    public TopicExchange domainExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    // 3. Fila exclusiva por instância (ex: airport-routes.sync.8091 e airport-routes.sync.8092)
    @Bean
    public Queue instanceSyncQueue() {
        return QueueBuilder.durable("airport-routes.sync." + serverPort).build();
    }

    @Bean
    public Binding routeBinding(Queue instanceSyncQueue, TopicExchange domainExchange) {
        return BindingBuilder.bind(instanceSyncQueue).to(domainExchange).with("route.#");
    }

    @Bean
    public Binding airportBinding(Queue instanceSyncQueue, TopicExchange domainExchange) {
        return BindingBuilder.bind(instanceSyncQueue).to(domainExchange).with("airport.#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}