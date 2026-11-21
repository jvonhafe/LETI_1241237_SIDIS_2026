package com.aisafe.application.usecase;

import com.aisafe.cqrs.event.FlightEvent;
import com.aisafe.cqrs.event.FlightEventPublisher;
import com.aisafe.cqrs.query.FlightReadModelRepository;
import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateFlightUseCase {
    private final ScheduledFlightRepository flightRepository;
    private final FlightReadModelRepository flightReadRepository;
    private final FlightEventPublisher eventPublisher;

    public UpdateFlightUseCase(ScheduledFlightRepository flightRepository,
                               FlightReadModelRepository flightReadRepository,
                               FlightEventPublisher eventPublisher) {
        this.flightRepository = flightRepository;
        this.flightReadRepository = flightReadRepository;
        this.eventPublisher = eventPublisher;
    }

    public ScheduledFlight execute(String flightNumber, ScheduledFlight updatedFlight) {
        if (!flightRepository.existsById(flightNumber) && !flightReadRepository.existsById(flightNumber)) {
            throw new RuntimeException("Voo não encontrado para atualização: " + flightNumber);
        }

        ScheduledFlight flight = flightRepository.findById(flightNumber).orElse(new ScheduledFlight());
        flight.setFlightNumber(flightNumber);
        flight.setAircraftId(updatedFlight.getAircraftId());
        flight.setOriginAirportId(updatedFlight.getOriginAirportId());
        flight.setDestinationAirportId(updatedFlight.getDestinationAirportId());
        flight.setDepartureTime(updatedFlight.getDepartureTime());
        flight.setArrivalTime(updatedFlight.getArrivalTime());
        flight.setStatus(updatedFlight.getStatus());

        ScheduledFlight saved = flightRepository.save(flight);

        eventPublisher.publish(new FlightEvent(
                "UPDATED",
                saved.getFlightNumber(),
                saved.getAircraftId(),
                saved.getOriginAirportId(),
                saved.getDestinationAirportId(),
                saved.getDepartureTime(),
                saved.getArrivalTime(),
                saved.getStatus()
        ));

        return saved;
    }
}