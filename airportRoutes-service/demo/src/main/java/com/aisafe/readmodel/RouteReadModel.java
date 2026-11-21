package com.aisafe.readmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "route_read_model")
public class RouteReadModel {

    @Id
    private Long id;
    private String originIata;
    private String originName;
    private String destinationIata;
    private String destinationName;
    private Integer estimatedFlightTimeMinutes;
    private Integer minimumRange;
    private Integer minimumCapacity;
    private Double distanceKm;
    private String status;
    private String lastEventId;

    public RouteReadModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginIata() { return originIata; }
    public void setOriginIata(String originIata) { this.originIata = originIata; }
    public String getOriginName() { return originName; }
    public void setOriginName(String originName) { this.originName = originName; }
    public String getDestinationIata() { return destinationIata; }
    public void setDestinationIata(String destinationIata) { this.destinationIata = destinationIata; }
    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }
    public Integer getEstimatedFlightTimeMinutes() { return estimatedFlightTimeMinutes; }
    public void setEstimatedFlightTimeMinutes(Integer estimatedFlightTimeMinutes) { this.estimatedFlightTimeMinutes = estimatedFlightTimeMinutes; }
    public Integer getMinimumRange() { return minimumRange; }
    public void setMinimumRange(Integer minimumRange) { this.minimumRange = minimumRange; }
    public Integer getMinimumCapacity() { return minimumCapacity; }
    public void setMinimumCapacity(Integer minimumCapacity) { this.minimumCapacity = minimumCapacity; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLastEventId() { return lastEventId; }
    public void setLastEventId(String lastEventId) { this.lastEventId = lastEventId; }
}