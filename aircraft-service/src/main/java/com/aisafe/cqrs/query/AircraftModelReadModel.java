package com.aisafe.cqrs.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "aircraft_model_read_model")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AircraftModelReadModel {
    @Id
    private String modelId;
    private String modelName;
    private String manufacturer;
    private int maxCapacity;
    private double fuelCapacity;
    private double maxRange;
    private double cruisingSpeed;
}