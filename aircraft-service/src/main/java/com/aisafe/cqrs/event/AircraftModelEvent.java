package com.aisafe.cqrs.event;

import java.io.Serializable;

public record AircraftModelEvent(
        String eventType,
        String modelId,
        String modelName,
        String manufacturer,
        int maxCapacity,
        double fuelCapacity,
        double maxRange,
        double cruisingSpeed
) implements Serializable {}