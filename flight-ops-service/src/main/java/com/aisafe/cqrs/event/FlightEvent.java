package com.aisafe.cqrs.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record FlightEvent(
        String eventType, // "CREATED", "UPDATED", "DELETED"
        String flightNumber,
        String aircraftId,
        String originAirportId,
        String destinationAirportId,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String status
) implements Serializable {}