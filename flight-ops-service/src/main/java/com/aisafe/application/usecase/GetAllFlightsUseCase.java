package com.aisafe.application.usecase;

import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllFlightsUseCase {
    private final ScheduledFlightRepository flightRepository;

    public GetAllFlightsUseCase(ScheduledFlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<ScheduledFlight> execute() {
        return flightRepository.findAll();
    }
}