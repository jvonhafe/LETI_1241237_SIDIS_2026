package com.aisafe.application.usecase;

import com.aisafe.cqrs.event.FlightEvent;
import com.aisafe.cqrs.event.FlightEventPublisher;
import com.aisafe.cqrs.query.FlightReadModelRepository;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteFlightUseCase {
    private final ScheduledFlightRepository flightRepository;
    private final FlightReadModelRepository flightReadRepository;
    private final FlightEventPublisher eventPublisher;

    public DeleteFlightUseCase(ScheduledFlightRepository flightRepository,
                               FlightReadModelRepository flightReadRepository,
                               FlightEventPublisher eventPublisher) {
        this.flightRepository = flightRepository;
        this.flightReadRepository = flightReadRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(String flightNumber) {
        if (!flightRepository.existsById(flightNumber) && !flightReadRepository.existsById(flightNumber)) {
            throw new RuntimeException("Voo não encontrado para apagar: " + flightNumber);
        }

        if (flightRepository.existsById(flightNumber)) {
            flightRepository.deleteById(flightNumber);
        }

        eventPublisher.publish(new FlightEvent(
                "DELETED",
                flightNumber,
                null, null, null, null, null, null
        ));
    }
}