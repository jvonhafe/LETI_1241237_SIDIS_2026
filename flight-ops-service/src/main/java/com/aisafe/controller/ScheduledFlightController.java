package com.aisafe.controller;

import com.aisafe.application.usecase.*;
import com.aisafe.model.ScheduledFlight;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
public class ScheduledFlightController {

    private final CreateFlightUseCase createFlightUseCase;
    private final GetAllFlightsUseCase getAllFlightsUseCase;
    private final GetFlightByNumberUseCase getFlightByNumberUseCase;
    private final UpdateFlightUseCase updateFlightUseCase;
    private final DeleteFlightUseCase deleteFlightUseCase;
    private final GetFlightWithDetailsUseCase getFlightWithDetailsUseCase;

    public ScheduledFlightController(CreateFlightUseCase createFlightUseCase,
                                     GetAllFlightsUseCase getAllFlightsUseCase,
                                     GetFlightByNumberUseCase getFlightByNumberUseCase,
                                     UpdateFlightUseCase updateFlightUseCase,
                                     DeleteFlightUseCase deleteFlightUseCase,
                                     GetFlightWithDetailsUseCase getFlightWithDetailsUseCase) {
        this.createFlightUseCase = createFlightUseCase;
        this.getAllFlightsUseCase = getAllFlightsUseCase;
        this.getFlightByNumberUseCase = getFlightByNumberUseCase;
        this.updateFlightUseCase = updateFlightUseCase;
        this.deleteFlightUseCase = deleteFlightUseCase;
        this.getFlightWithDetailsUseCase = getFlightWithDetailsUseCase;
    }

    @PostMapping
    public ResponseEntity<ScheduledFlight> createFlight(@RequestBody ScheduledFlight flight) {
        return ResponseEntity.ok(createFlightUseCase.execute(flight));
    }

    @GetMapping
    public ResponseEntity<List<ScheduledFlight>> getAllFlights() {
        return ResponseEntity.ok(getAllFlightsUseCase.execute());
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<ScheduledFlight> getFlightByNumber(@PathVariable String flightNumber) {
        return ResponseEntity.ok(getFlightByNumberUseCase.execute(flightNumber));
    }

    @PutMapping("/{flightNumber}")
    public ResponseEntity<ScheduledFlight> updateFlight(@PathVariable String flightNumber, @RequestBody ScheduledFlight updatedFlight) {
        return ResponseEntity.ok(updateFlightUseCase.execute(flightNumber, updatedFlight));
    }

    @DeleteMapping("/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        deleteFlightUseCase.execute(flightNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{flightNumber}/details")
    public ResponseEntity<?> getFlightWithAircraftDetails(
            @PathVariable String flightNumber,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        return ResponseEntity.ok(getFlightWithDetailsUseCase.execute(flightNumber, authHeader));
    }
}