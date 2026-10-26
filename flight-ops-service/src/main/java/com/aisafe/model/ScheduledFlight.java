package com.aisafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_flights")
public class ScheduledFlight {

    @Id
    private String flightNumber; // Ex: "TP123"

    private String aircraftId;   // ID do avião (que vai buscar ao aircraft-service)
    private String originAirportId; // ID do aeroporto de origem
    private String destinationAirportId; // ID do aeroporto de destino

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status; // Ex: SCHEDULED, IN_FLIGHT, LANDED

    // Construtores vazios e com argumentos do Lombok ou manuais
    public ScheduledFlight() {}

    public ScheduledFlight(String flightNumber, String aircraftId, String originAirportId, String destinationAirportId, LocalDateTime departureTime, LocalDateTime arrivalTime, String status) {
        this.flightNumber = flightNumber;
        this.aircraftId = aircraftId;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    // Getters e Setters
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getAircraftId() { return aircraftId; }
    public void setAircraftId(String aircraftId) { this.aircraftId = aircraftId; }

    public String getOriginAirportId() { return originAirportId; }
    public void setOriginAirportId(String originAirportId) { this.originAirportId = originAirportId; }

    public String getDestinationAirportId() { return destinationAirportId; }
    public void setDestinationAirportId(String destinationAirportId) { this.destinationAirportId = destinationAirportId; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}