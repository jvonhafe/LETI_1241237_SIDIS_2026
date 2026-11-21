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

    public static final String AIRCRAFT_EXCHANGE = "aircraft.events.exchange";

    @Bean
    public FanoutExchange aircraftExchange() {
        return new FanoutExchange(AIRCRAFT_EXCHANGE);
    }

    @Bean
    public Queue aircraftSyncQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding binding(FanoutExchange aircraftExchange, Queue aircraftSyncQueue) {
        return BindingBuilder.bind(aircraftSyncQueue).to(aircraftExchange);
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

    public static final String MODEL_EXCHANGE = "aircraft.model.events.exchange";
    public static final String MAINTENANCE_EXCHANGE = "maintenance.events.exchange";

    //aircraft model
    @Bean
    public FanoutExchange modelExchange() {
        return new FanoutExchange(MODEL_EXCHANGE);
    }

    @Bean
    public Queue modelSyncQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding modelBinding(FanoutExchange modelExchange, Queue modelSyncQueue) {
        return BindingBuilder.bind(modelSyncQueue).to(modelExchange);
    }

    //maintenance
    @Bean
    public FanoutExchange maintenanceExchange() {
        return new FanoutExchange(MAINTENANCE_EXCHANGE);
    }

    @Bean
    public Queue maintenanceSyncQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding maintenanceBinding(FanoutExchange maintenanceExchange, Queue maintenanceSyncQueue) {
        return BindingBuilder.bind(maintenanceSyncQueue).to(maintenanceExchange);
    }
}