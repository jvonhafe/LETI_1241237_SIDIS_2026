package com.aisafe.application.usecase;

import com.aisafe.cqrs.event.FlightEvent;
import com.aisafe.cqrs.event.FlightEventPublisher;
import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateFlightUseCase {
    private final ScheduledFlightRepository flightRepository;
    private final FlightEventPublisher eventPublisher;

    public CreateFlightUseCase(ScheduledFlightRepository flightRepository,
                               FlightEventPublisher eventPublisher) {
        this.flightRepository = flightRepository;
        this.eventPublisher = eventPublisher;
    }

    public ScheduledFlight execute(ScheduledFlight flight) {
        ScheduledFlight saved = flightRepository.save(flight);

        eventPublisher.publish(new FlightEvent(
                "CREATED",
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