package com.aisafe.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AircraftTest {

    @Test
    void us102_us103_deveCriarERetornarDetalhesDaAeronaveComSucesso() {
        AircraftRegistration reg = new AircraftRegistration("CS-TTO");
        Aircraft aircraft = new Aircraft();

        aircraft.setRegistrationNumber(reg);
        aircraft.setModelId("A320");
        aircraft.setManufacturingDate(LocalDate.of(2024, 5, 15));
        aircraft.setStatus("ACTIVE");

        assertEquals("CS-TTO", aircraft.getRegistrationNumber().getRegistration());
        assertEquals("A320", aircraft.getModelId());
        assertEquals(LocalDate.of(2024, 5, 15), aircraft.getManufacturingDate());
        assertEquals("ACTIVE", aircraft.getStatus());
    }

    @Test
    void us104_deveValidarSeAeronaveCorrespondeAosFiltrosDePesquisa() {
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistrationNumber(new AircraftRegistration("CS-TTO"));
        aircraft.setModelId("A320");
        aircraft.setManufacturingDate(LocalDate.of(2024, 5, 15));
        aircraft.setStatus("ACTIVE");

        boolean matchesModel = "A320".equals(aircraft.getModelId());
        boolean matchesStatus = "ACTIVE".equals(aircraft.getStatus());
        boolean matchesYear = Integer.valueOf(2024).equals(aircraft.getManufacturingDate().getYear());

        assertTrue(matchesModel, "O modelo deve coincidir com o filtro.");
        assertTrue(matchesStatus, "O estado deve coincidir com o filtro.");
        assertTrue(matchesYear, "O ano de fabrico deve coincidir com o filtro.");
    }

    @Test
    void us105_deveAlterarOEstadoOperacionalParaMaiusculasComSucesso() {
        Aircraft aircraft = new Aircraft();
        aircraft.setStatus("ACTIVE");

        String novoEstadoDoPedido = "under_maintenance";
        aircraft.setStatus(novoEstadoDoPedido.toUpperCase());

        assertEquals("UNDER_MAINTENANCE", aircraft.getStatus(), "O estado devia ter sido alterado e normalizado para maiúsculas.");
    }

    @Test
    void us105_deveManterEstadoSeForIgual() {
        Aircraft aircraft = new Aircraft();
        aircraft.setStatus("UNDER_MAINTENANCE");

        String mesmoEstado = "UNDER_MAINTENANCE";
        aircraft.setStatus(mesmoEstado);

        assertEquals("UNDER_MAINTENANCE", aircraft.getStatus());
    }
}