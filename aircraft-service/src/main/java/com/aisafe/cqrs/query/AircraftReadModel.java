package com.aisafe.cqrs.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "aircraft_read_model")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AircraftReadModel {

    @Id
    private String registration;
    private String modelId;
    private LocalDate manufacturingDate;
    private String status;
}