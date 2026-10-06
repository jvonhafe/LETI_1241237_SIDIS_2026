package com.aisafe.application.airport;

import com.aisafe.model.Airport;
import com.aisafe.repository.AirportRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterAirportUseCase {

    private final AirportRepository airportRepository;

    public RegisterAirportUseCase(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public Airport execute(Airport airport) {
        if (airportRepository.existsById(airport.getIataCode())) {
            throw new IllegalArgumentException("Já existe um aeroporto com IATA: " + airport.getIataCode().getCode());
        }
        return airportRepository.save(airport);
    }
}