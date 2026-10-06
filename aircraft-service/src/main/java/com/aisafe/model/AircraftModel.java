package com.aisafe.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AircraftModel {

    @Id
    private String modelId;
    private String modelName;
    private String manufacturer;

    @Embedded
    private Capacity maxCapacity;

    private double fuelCapacity;
    private double maxRange;
    private double cruisingSpeed;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private MediaImage image;

    public void updateSpecifications(Capacity maxCapacity, Double fuelCapacity, Double maxRange, Double cruisingSpeed) {
        if (fuelCapacity != null && fuelCapacity <= 0) {
            throw new IllegalArgumentException("A capacidade de combustível tem de ser positiva.");
        }
        if (maxRange != null && maxRange <= 0) {
            throw new IllegalArgumentException("O alcance máximo tem de ser positivo.");
        }
        if (cruisingSpeed != null && cruisingSpeed <= 0) {
            throw new IllegalArgumentException("A velocidade de cruzeiro tem de ser positiva.");
        }

        this.maxCapacity = maxCapacity;
        this.fuelCapacity = fuelCapacity;
        this.maxRange = maxRange;
        this.cruisingSpeed = cruisingSpeed;
    }
}