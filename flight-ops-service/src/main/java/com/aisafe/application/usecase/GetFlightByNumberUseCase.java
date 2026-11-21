package com.aisafe.application.usecase;

import com.aisafe.cqrs.query.FlightReadModel;
import com.aisafe.cqrs.query.FlightReadModelRepository;
import org.springframework.stereotype.Service;

@Service
public class GetFlightByNumberUseCase {
    private final FlightReadModelRepository flightReadRepository;

    public GetFlightByNumberUseCase(FlightReadModelRepository flightReadRepository) {
        this.flightReadRepository = flightReadRepository;
    }

    public FlightReadModel execute(String flightNumber) {
        return flightReadRepository.findById(flightNumber)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado com o número: " + flightNumber));
    }
}