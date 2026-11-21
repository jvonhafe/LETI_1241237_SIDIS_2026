package com.aisafe.cqrs.event;

import com.aisafe.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AircraftEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AircraftEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(AircraftEvent event) {
        // FanoutExchange ignora a routingKey (por isso vai "")
        rabbitTemplate.convertAndSend(RabbitMQConfig.AIRCRAFT_EXCHANGE, "", event);
    }

    public void publishModel(AircraftModelEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.MODEL_EXCHANGE, "", event);
    }

    public void publishMaintenance(MaintenanceEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.MAINTENANCE_EXCHANGE, "", event);
    }
}