package com.aisafe.application.dto;

import com.aisafe.messaging.DomainEventPublisher;
import com.aisafe.model.Airport;
import com.aisafe.model.ContactInfo;
import com.aisafe.model.IataCode;
import com.aisafe.model.OperatingHours;
import com.aisafe.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateAirportDetailsUseCase {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private DomainEventPublisher eventPublisher;

    public Airport execute(String iata, UpdateDetailsRequest request) {
        IataCode iataCode = new IataCode(iata);

        Airport airport = airportRepository.findById(iataCode)
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto não encontrado: " + iata));

        if (request.contactInfo != null) {
            airport.updateContactInfo(request.contactInfo);
        }

        if (request.operatingHours != null) {
            airport.updateOperatingHours(request.operatingHours);
        }

        Airport saved = airportRepository.save(airport);

        eventPublisher.publishAirportEvent(
                "airport.updated",
                "DETAILS_UPDATED",
                saved.getIataCode().getCode(),
                saved.getName(),
                saved.getCity(),
                saved.getCountry(),
                saved.getTimezone(),
                saved.getStatus()
        );

        return saved;
    }

    public static class UpdateDetailsRequest {
        public ContactInfo contactInfo;
        public OperatingHours operatingHours;
    }
}