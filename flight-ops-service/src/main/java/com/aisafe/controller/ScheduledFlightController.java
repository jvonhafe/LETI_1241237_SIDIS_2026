package com.aisafe.controller;

import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
public class ScheduledFlightController {

    private final ScheduledFlightRepository flightRepository;
    private final RestTemplate restTemplate;

    public ScheduledFlightController(ScheduledFlightRepository flightRepository, RestTemplate restTemplate) {
        this.flightRepository = flightRepository;
        this.restTemplate = restTemplate;
    }

    // 1. Criar um Voo novo (POST)
    @PostMapping
    public ResponseEntity<ScheduledFlight> createFlight(@RequestBody ScheduledFlight flight) {
        ScheduledFlight savedFlight = flightRepository.save(flight);
        return ResponseEntity.ok(savedFlight);
    }

    // 2. Listar todos os voos (GET)
    @GetMapping
    public List<ScheduledFlight> getAllFlights() {
        return flightRepository.findAll();
    }

    // 3. Consultar um voo específico por número (GET)
    @GetMapping("/{flightNumber}")
    public ResponseEntity<ScheduledFlight> getFlightByNumber(@PathVariable String flightNumber) {
        ScheduledFlight flight = flightRepository.findById(flightNumber)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado com o número: " + flightNumber));
        return ResponseEntity.ok(flight);
    }

    // 4. Atualizar um voo existente (PUT)
    @PutMapping("/{flightNumber}")
    public ResponseEntity<ScheduledFlight> updateFlight(@PathVariable String flightNumber, @RequestBody ScheduledFlight updatedFlight) {
        ScheduledFlight flight = flightRepository.findById(flightNumber)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado para atualização: " + flightNumber));

        flight.setAircraftId(updatedFlight.getAircraftId());
        flight.setOriginAirportId(updatedFlight.getOriginAirportId());
        flight.setDestinationAirportId(updatedFlight.getDestinationAirportId());
        flight.setDepartureTime(updatedFlight.getDepartureTime());
        flight.setArrivalTime(updatedFlight.getArrivalTime());
        flight.setStatus(updatedFlight.getStatus());

        ScheduledFlight saved = flightRepository.save(flight);
        return ResponseEntity.ok(saved);
    }

    // 5. Apagar um voo (DELETE)
    @DeleteMapping("/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        if (!flightRepository.existsById(flightNumber)) {
            throw new RuntimeException("Voo não encontrado para apagar: " + flightNumber);
        }
        flightRepository.deleteById(flightNumber);
        return ResponseEntity.noContent().build();
    }

    // 6. A AGREGAÇÃO: Ver o Voo e ir buscar os dados da Aeronave ao aircraft-service (Porta 8081)
    @GetMapping("/{flightNumber}/details")
    public ResponseEntity<Map<String, Object>> getFlightWithAircraftDetails(@PathVariable String flightNumber) {
        ScheduledFlight flight = flightRepository.findById(flightNumber)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado: " + flightNumber));

        String aircraftServiceUrl = "http://localhost:8081/api/aircraft/" + flight.getAircraftId();

        Object aircraftDetails = null;
        try {
            aircraftDetails = restTemplate.getForObject(aircraftServiceUrl, Object.class);
        } catch (Exception e) {
            aircraftDetails = "Erro ao contactar o aircraft-service: " + e.getMessage();
        }

        Map<String, Object> response = Map.of(
                "flight", flight,
                "aircraftDetails", aircraftDetails
        );

        return ResponseEntity.ok(response);
    }
}