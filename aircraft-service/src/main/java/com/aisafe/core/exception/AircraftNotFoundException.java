package com.aisafe.core.exception;

public class AircraftNotFoundException extends RuntimeException {
    public AircraftNotFoundException(String registration) {
        super("Aeronave com a matrícula " + registration + " não encontrada.");
    }
}