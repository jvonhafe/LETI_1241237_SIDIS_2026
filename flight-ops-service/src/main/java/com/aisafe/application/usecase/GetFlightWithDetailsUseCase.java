package com.aisafe.application.usecase;

import com.aisafe.cqrs.query.FlightReadModel;
import com.aisafe.cqrs.query.FlightReadModelRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GetFlightWithDetailsUseCase {
    private final FlightReadModelRepository flightReadRepository;
    private final RestTemplate restTemplate;

    public GetFlightWithDetailsUseCase(FlightReadModelRepository flightReadRepository, RestTemplate restTemplate) {
        this.flightReadRepository = flightReadRepository;
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> execute(String flightNumber, String authHeader) {
        FlightReadModel flight = flightReadRepository.findById(flightNumber)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado: " + flightNumber));

        String aircraftServiceUrl = "https://localhost:8081/api/aircraft/" + flight.getAircraftId();
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