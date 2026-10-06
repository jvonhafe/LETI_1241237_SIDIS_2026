package com.aisafe.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Version;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Aircraft {

    @EmbeddedId
    private AircraftRegistration registrationNumber;

    private String modelId;
    private LocalDate manufacturingDate;
    private String status;

    @Version
    private Long version;
}