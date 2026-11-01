package com.aisafe.application.dto;

import java.time.LocalDateTime;

public class ScheduledFlightDTO {
    public String aircraftId;
    public String originAirportId;
    public String destinationAirportId;
    public LocalDateTime departureTime;
    public LocalDateTime arrivalTime;
    public String status;
}