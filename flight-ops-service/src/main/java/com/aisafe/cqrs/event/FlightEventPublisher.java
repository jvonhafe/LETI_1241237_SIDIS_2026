package com.aisafe.cqrs.event;

import com.aisafe.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class FlightEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public FlightEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(FlightEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.FLIGHT_EXCHANGE, "", event);
    }
}