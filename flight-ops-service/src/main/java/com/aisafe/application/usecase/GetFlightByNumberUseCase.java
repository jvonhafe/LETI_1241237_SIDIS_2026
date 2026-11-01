package com.aisafe.application.usecase;

import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.stereotype.Service;

@Service
public class GetFlightByNumberUseCase {
    private final ScheduledFlightRepository flightRepository;

    public GetFlightByNumberUseCase(ScheduledFlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public ScheduledFlight execute(String flightNumber) {
        return flightRepository.findById(flightNumber)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado com o número: " + flightNumber));
    }
}