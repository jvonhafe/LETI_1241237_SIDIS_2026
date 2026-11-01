package com.aisafe.controller;

import com.aisafe.application.usecase.*;
import com.aisafe.model.ScheduledFlight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ScheduledFlightControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private CreateFlightUseCase createFlightUseCase;

    @MockitoBean
    private GetAllFlightsUseCase getAllFlightsUseCase;

    @MockitoBean
    private GetFlightByNumberUseCase getFlightByNumberUseCase;

    @MockitoBean
    private UpdateFlightUseCase updateFlightUseCase;

    @MockitoBean
    private DeleteFlightUseCase deleteFlightUseCase;

    @MockitoBean
    private GetFlightWithDetailsUseCase getFlightWithDetailsUseCase;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void deveCriarVooComSucesso() throws Exception {
        ScheduledFlight voo = new ScheduledFlight("TP123", "AC-01", "LIS", "OPO", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "SCHEDULED");

        when(createFlightUseCase.execute(any(ScheduledFlight.class))).thenReturn(voo);

        String jsonBody = """
                {
                    "flightNumber": "TP123",
                    "aircraftId": "AC-01",
                    "originAirportId": "LIS",
                    "destinationAirportId": "OPO",
                    "status": "SCHEDULED"
                }
                """;

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TP123"));
    }

    @Test
    void deveListarTodosOsVoos() throws Exception {
        ScheduledFlight voo1 = new ScheduledFlight("TP123", "AC-01", "LIS", "OPO", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "SCHEDULED");

        when(getAllFlightsUseCase.execute()).thenReturn(Arrays.asList(voo1));

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void deveRetornarVooPorNumero() throws Exception {
        ScheduledFlight voo = new ScheduledFlight("TP123", "AC-01", "LIS", "OPO", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "SCHEDULED");

        when(getFlightByNumberUseCase.execute("TP123")).thenReturn(voo);

        mockMvc.perform(get("/api/flights/TP123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TP123"));
    }

    @Test
    void deveAtualizarVooComSucesso() throws Exception {
        ScheduledFlight vooExistente = new ScheduledFlight("TP123", "AC-02", "LIS", "MAD", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "IN_FLIGHT");

        when(updateFlightUseCase.execute(eq("TP123"), any(ScheduledFlight.class))).thenReturn(vooExistente);

        String jsonBody = """
                {
                    "aircraftId": "AC-02",
                    "originAirportId": "LIS",
                    "destinationAirportId": "MAD",
                    "status": "IN_FLIGHT"
                }
                """;

        mockMvc.perform(put("/api/flights/TP123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aircraftId").value("AC-02"));
    }

    @Test
    void deveApagarVooComSucesso() throws Exception {
        doNothing().when(deleteFlightUseCase).execute("TP123");

        mockMvc.perform(delete("/api/flights/TP123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarDetalhesDoVooComAeronave() throws Exception {
        ScheduledFlight voo = new ScheduledFlight("TP123", "AC-01", "LIS", "OPO", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "SCHEDULED");
        Object mockAircraftDetails = Map.of("id", "AC-01", "model", "Boeing 737");

        Map<String, Object> mockResponse = Map.of(
                "flight", voo,
                "aircraftDetails", mockAircraftDetails
        );

        when(getFlightWithDetailsUseCase.execute(eq("TP123"), any())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/flights/TP123/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flight.flightNumber").value("TP123"))
                .andExpect(jsonPath("$.aircraftDetails.model").value("Boeing 737"));
    }
}