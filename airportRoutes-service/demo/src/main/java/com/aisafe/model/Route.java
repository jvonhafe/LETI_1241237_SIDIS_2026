package com.aisafe.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Airport originAirport;

    @ManyToOne(optional = false)
    private Airport destinationAirport;

    private Integer estimatedFlightTimeMinutes;

    private Integer minimumRange;

    private Integer minimumCapacity;

    private Double distanceKm;

    @Enumerated(EnumType.STRING)
    private RouteStatus status = RouteStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}