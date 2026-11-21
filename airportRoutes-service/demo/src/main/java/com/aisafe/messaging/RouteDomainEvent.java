package com.aisafe.messaging;

import java.time.Instant;

public record RouteDomainEvent(
        String eventId,
        String sourcePort,
        String eventType,
        Long routeId,
        String originIata,
        String originName,
        String destinationIata,
        String destinationName,
        Integer estimatedFlightTimeMinutes,
        Integer minimumRange,
        Integer minimumCapacity,
        Double distanceKm,
        String status,
        Instant occurredAt
) {}