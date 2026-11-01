package com.aisafe.application.usecase;

import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteFlightUseCase {
    private final ScheduledFlightRepository flightRepository;

    public DeleteFlightUseCase(ScheduledFlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public void execute(String flightNumber) {
        if (!flightRepository.existsById(flightNumber)) {
            throw new RuntimeException("Voo não encontrado para apagar: " + flightNumber);
        }
        flightRepository.deleteById(flightNumber);
    }
}