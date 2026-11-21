package com.aisafe.application.dto;

import com.aisafe.messaging.DomainEventPublisher;
import com.aisafe.model.Airport;
import com.aisafe.model.IataCode;
import com.aisafe.repository.AirportRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateAirportStatusUseCase {

    private final AirportRepository airportRepository;
    private final DomainEventPublisher eventPublisher;

    public UpdateAirportStatusUseCase(AirportRepository airportRepository,
                                      DomainEventPublisher eventPublisher) {
        this.airportRepository = airportRepository;
        this.eventPublisher = eventPublisher;
    }

    public Airport execute(String iata, String newStatus) {
        IataCode searchId = new IataCode(iata);
        Airport airport = airportRepository.findById(searchId)
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto não encontrado: " + iata));

        airport.updateStatus(newStatus);
        Airport saved = airportRepository.save(airport);

        eventPublisher.publishAirportEvent(
                "airport.status.changed",
                "STATUS_CHANGED",
                saved.getIataCode().getCode(),
                saved.getName(),
                saved.getCity(),
                saved.getCountry(),
                saved.getTimezone(),
                saved.getStatus()
        );

        return saved;
    }
}