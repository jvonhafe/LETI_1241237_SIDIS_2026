package com.aisafe.application.usecase;

import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateFlightUseCase {
    private final ScheduledFlightRepository flightRepository;

    public UpdateFlightUseCase(ScheduledFlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public ScheduledFlight execute(String flightNumber, ScheduledFlight updatedFlight) {
        ScheduledFlight flight = flightRepository.findById(flightNumber)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado para atualização: " + flightNumber));

        flight.setAircraftId(updatedFlight.getAircraftId());
        flight.setOriginAirportId(updatedFlight.getOriginAirportId());
        flight.setDestinationAirportId(updatedFlight.getDestinationAirportId());
        flight.setDepartureTime(updatedFlight.getDepartureTime());
        flight.setArrivalTime(updatedFlight.getArrivalTime());
        flight.setStatus(updatedFlight.getStatus());

        return flightRepository.save(flight);
    }
}