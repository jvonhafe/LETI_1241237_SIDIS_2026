package com.aisafe.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FLIGHT_EXCHANGE = "flight.events.exchange";

    @Bean
    public FanoutExchange flightExchange() {
        return new FanoutExchange(FLIGHT_EXCHANGE);
    }

    @Bean
    public Queue flightSyncQueue() {
        return new Queue("flight.sync." + java.util.UUID.randomUUID(), false, true, true);
    }

    @Bean
    public Binding flightBinding(FanoutExchange flightExchange, Queue flightSyncQueue) {
        return BindingBuilder.bind(flightSyncQueue).to(flightExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}