package com.aisafe.application;

import com.aisafe.model.AircraftRegistration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterAircraftUseCaseTest {

    @Test
    void us102_quandoMatriculaValidaComHifen_devePassar() {
        AircraftRegistration reg = new AircraftRegistration("CS-TTO");
        assertEquals("CS-TTO", reg.getRegistration());
    }

    @Test
    void us102_quandoMatriculaMinuscula_deveConverterParaMaiusculas() {
        AircraftRegistration reg = new AircraftRegistration("cs-tto");
        assertEquals("CS-TTO", reg.getRegistration());
    }

    @Test
    void us102_quandoMatriculaNaoSeguePadrao_deveLancarIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("CSTTO"));
        assertEquals("Matrícula inválida. Deve seguir o formato internacional (ex: CS-TTO).", exception.getMessage());
    }
}