package com.aisafe.cqrs.event;

import java.io.Serializable;

public record MaintenanceEvent(
        String eventType,
        String id,
        String aircraftRegistration,
        Long templateId,
        Double expectedDurationHours,
        String status,
        String completionNotes,
        String component,
        Double cost
) implements Serializable {}