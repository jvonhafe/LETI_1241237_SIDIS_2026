package com.aisafe.application.usecase;

import com.aisafe.cqrs.query.FlightReadModel;
import com.aisafe.cqrs.query.FlightReadModelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllFlightsUseCase {
    private final FlightReadModelRepository flightReadRepository;

    public GetAllFlightsUseCase(FlightReadModelRepository flightReadRepository) {
        this.flightReadRepository = flightReadRepository;
    }

    public List<FlightReadModel> execute() {
        return flightReadRepository.findAll();
    }
}