package com.aisafe.application.dto;

import com.aisafe.messaging.DomainEventPublisher;
import com.aisafe.model.Airport;
import com.aisafe.repository.AirportRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterAirportUseCase {

    private final AirportRepository airportRepository;
    private final DomainEventPublisher eventPublisher;

    public RegisterAirportUseCase(AirportRepository airportRepository,
                                  DomainEventPublisher eventPublisher) {
        this.airportRepository = airportRepository;
        this.eventPublisher = eventPublisher;
    }

    public Airport execute(Airport airport) {
        if (airportRepository.existsById(airport.getIataCode())) {
            throw new IllegalArgumentException("Já existe um aeroporto com IATA: " + airport.getIataCode().getCode());
        }

        Airport saved = airportRepository.save(airport);

        eventPublisher.publishAirportEvent(
                "airport.registered",
                "REGISTERED",
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