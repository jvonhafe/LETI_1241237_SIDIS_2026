package com.aisafe.application.usecase;

import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateFlightUseCase {
    private final ScheduledFlightRepository flightRepository;

    public CreateFlightUseCase(ScheduledFlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public ScheduledFlight execute(ScheduledFlight flight) {
        return flightRepository.save(flight);
    }
}