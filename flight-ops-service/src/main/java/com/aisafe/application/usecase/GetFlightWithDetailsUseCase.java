package com.aisafe.application.usecase;

import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GetFlightWithDetailsUseCase {
    private final ScheduledFlightRepository flightRepository;
    private final RestTemplate restTemplate;

    public GetFlightWithDetailsUseCase(ScheduledFlightRepository flightRepository, RestTemplate restTemplate) {
        this.flightRepository = flightRepository;
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> execute(String flightNumber, String authHeader) {
        ScheduledFlight flight = flightRepository.findById(flightNumber)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado: " + flightNumber));

        String aircraftServiceUrl = "http://localhost:8081/api/aircraft/" + flight.getAircraftId();
        Object aircraftDetails;

        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> aircraftResponse = restTemplate.exchange(
                    aircraftServiceUrl,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );
            aircraftDetails = aircraftResponse.getBody();
        } catch (Exception e) {
            aircraftDetails = "Erro ao contactar o aircraft-service: " + e.getMessage();
        }

        return Map.of("flight", flight, "aircraftDetails", aircraftDetails);
    }
}