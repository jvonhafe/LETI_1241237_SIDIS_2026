package com.aisafe.messaging;

import java.time.Instant;

public record AirportDomainEvent(
        String eventId,
        String sourcePort,
        String eventType,
        String iataCode,
        String name,
        String city,
        String country,
        String timezone,
        String status,
        Instant occurredAt
) {}