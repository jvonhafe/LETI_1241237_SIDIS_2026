package com.aisafe.application;

import com.aisafe.model.Capacity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterAircraftModelUseCaseTest {

    @Test
    void us101_quandoCapacidadeForValida_deveCriarComSucesso() {
        Capacity capacity = new Capacity(180);
        assertEquals(180, capacity.getValue());
    }

    @Test
    void us101_quandoCapacidadeForZero_deveLancarIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Capacity(0));
        assertEquals("A capacidade de lugares deve ser um valor positivo.", exception.getMessage());
    }

    @Test
    void us101_quandoCapacidadeForNegativa_deveLancarIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Capacity(-50));
        assertEquals("A capacidade de lugares deve ser um valor positivo.", exception.getMessage());
    }
}