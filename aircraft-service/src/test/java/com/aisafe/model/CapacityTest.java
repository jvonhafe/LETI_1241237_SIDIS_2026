package com.aisafe.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CapacityTest {

    @Test
    void quandoValorForPositivo_deveCriarComSucesso() {
        Capacity capacity = new Capacity(180);
        assertEquals(180, capacity.getValue());
    }

    @Test
    void quandoValorForZero_deveLancarIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Capacity(0));
        assertEquals("A capacidade de lugares deve ser um valor positivo.", exception.getMessage());
    }

    @Test
    void quandoValorForNegativo_deveLancarIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Capacity(-10));
        assertEquals("A capacidade de lugares deve ser um valor positivo.", exception.getMessage());
    }
}