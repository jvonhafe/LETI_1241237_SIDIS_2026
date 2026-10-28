package com.aisafe.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledFlightTest {

    @Test
    void deveCriarVooComConstrutorCompleto() {
        LocalDateTime dep = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime arr = LocalDateTime.of(2026, 6, 1, 14, 0);

        ScheduledFlight voo = new ScheduledFlight("TP123", "AC-01", "LIS", "OPO", dep, arr, "SCHEDULED");

        assertNotNull(voo);
        assertEquals("TP123", voo.getFlightNumber());
        assertEquals("AC-01", voo.getAircraftId());
        assertEquals("LIS", voo.getOriginAirportId());
        assertEquals("OPO", voo.getDestinationAirportId());
        assertEquals(dep, voo.getDepartureTime());
        assertEquals(arr, voo.getArrivalTime());
        assertEquals("SCHEDULED", voo.getStatus());
    }

    @Test
    void deveTestarSettersEGetters() {
        ScheduledFlight voo = new ScheduledFlight();
        voo.setFlightNumber("TP999");
        voo.setAircraftId("AC-02");
        voo.setOriginAirportId("OPO");
        voo.setDestinationAirportId("MAD");
        voo.setStatus("IN_FLIGHT");

        assertEquals("TP999", voo.getFlightNumber());
        assertEquals("AC-02", voo.getAircraftId());
        assertEquals("OPO", voo.getOriginAirportId());
        assertEquals("MAD", voo.getDestinationAirportId());
        assertEquals("IN_FLIGHT", voo.getStatus());
    }
}