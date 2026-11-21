package com.aisafe.cqrs.event;

import java.io.Serializable;
import java.time.LocalDate;

public record AircraftEvent(
        String eventType,
        String registration,
        String modelId,
        LocalDate manufacturingDate,
        String status
) implements Serializable {}