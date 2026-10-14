package com.aisafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class AircraftRegistration {

    @Column(name = "registration_number")
    private String registration;

    protected AircraftRegistration() {}

    public AircraftRegistration(String registration) {
        if (registration == null) {
            throw new IllegalArgumentException("A matrícula não pode ser nula.");
        }

        String upperReg = registration.toUpperCase().trim();

        if (!upperReg.matches("^[A-Z0-9]{1,5}-[A-Z0-9]{1,5}$")) {
            throw new IllegalArgumentException("Matrícula inválida. Deve seguir o formato internacional (ex: CS-TTO).");
        }

        this.registration = upperReg;
    }
}