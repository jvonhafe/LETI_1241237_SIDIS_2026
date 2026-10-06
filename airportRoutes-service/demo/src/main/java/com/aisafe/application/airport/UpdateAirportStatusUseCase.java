package com.aisafe.application.airport;

import com.aisafe.model.Airport;
import com.aisafe.model.IataCode;
import com.aisafe.repository.AirportRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateAirportStatusUseCase {

    private final AirportRepository airportRepository;

    public UpdateAirportStatusUseCase(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public Airport execute(String iata, String newStatus) {
        IataCode searchId = new IataCode(iata);
        Airport airport = airportRepository.findById(searchId)
                // CORRIGIDO: Substituído por IllegalArgumentException
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto não encontrado: " + iata));

        airport.updateStatus(newStatus);
        return airportRepository.save(airport);
    }
}