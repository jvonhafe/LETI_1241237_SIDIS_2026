package com.aisafe.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftRegistrationTest {

    @Test
    void quandoMatriculaValidaComHifen_deveCriarComSucesso() {
        AircraftRegistration registration = new AircraftRegistration("CS-TTO");
        assertEquals("CS-TTO", registration.getRegistration());
    }

    @Test
    void quandoMatriculaValidaMinuscula_deveConverterParaMaiusculas() {
        AircraftRegistration registration = new AircraftRegistration("cs-tto");
        assertEquals("CS-TTO", registration.getRegistration());
    }

    @Test
    void quandoMatriculaSemHifen_deveLancarIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("CSTTO"));
        assertEquals("Matrícula inválida. Deve seguir o formato internacional (ex: CS-TTO).", exception.getMessage());
    }

    @Test
    void quandoMatriculaVazia_deveLancarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
    }
}