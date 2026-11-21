package com.aisafe.cqrs.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight_read_model")
public class FlightReadModel {

    @Id
    private String flightNumber;
    private String aircraftId;
    private String originAirportId;
    private String destinationAirportId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;

    public FlightReadModel() {}

    public FlightReadModel(String flightNumber, String aircraftId, String originAirportId,
                           String destinationAirportId, LocalDateTime departureTime,
                           LocalDateTime arrivalTime, String status) {
        this.flightNumber = flightNumber;
        this.aircraftId = aircraftId;
        this.originAirportId = originAirportId;
        this.destinationAirportId = destinationAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

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