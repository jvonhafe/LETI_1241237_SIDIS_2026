package com.aisafe.messaging;

import com.aisafe.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${server.port:8091}")
    private String serverPort;

    public DomainEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAirportEvent(String routingKey, String eventType, String iataCode,
                                    String name, String city, String country,
                                    String timezone, String status) {
        AirportDomainEvent event = new AirportDomainEvent(
                UUID.randomUUID().toString(),
                serverPort,
                eventType,
                iataCode,
                name,
                city,
                country,
                timezone,
                status,
                Instant.now()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
    }

    public void publishRouteEvent(String routingKey, String eventType, Long routeId,
                                  String originIata, String originName,
                                  String destinationIata, String destinationName,
                                  Integer estimatedFlightTimeMinutes, Integer minimumRange,
                                  Integer minimumCapacity, Double distanceKm, String status) {
        RouteDomainEvent event = new RouteDomainEvent(
                UUID.randomUUID().toString(),
                serverPort,
                eventType,
                routeId,
                originIata,
                originName,
                destinationIata,
                destinationName,
                estimatedFlightTimeMinutes,
                minimumRange,
                minimumCapacity,
                distanceKm,
                status,
                Instant.now()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
    }
}