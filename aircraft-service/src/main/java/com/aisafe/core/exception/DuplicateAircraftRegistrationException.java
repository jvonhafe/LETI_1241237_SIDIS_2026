package com.aisafe.core.exception;

public class DuplicateAircraftRegistrationException extends RuntimeException {
    public DuplicateAircraftRegistrationException(String registration) {
        super("Já existe uma aeronave registada com a matrícula " + registration + ".");
    }
}