package com.aisafe.application.airport;

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

    public Airport execute(String iata, UpdateDetailsRequest request) {
        IataCode iataCode = new IataCode(iata);

        Airport airport = airportRepository.findById(iataCode)
                // CORRIGIDO: Substituído por IllegalArgumentException
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto não encontrado: " + iata));

        if (request.contactInfo != null) {
            airport.updateContactInfo(request.contactInfo);
        }

        if (request.operatingHours != null) {
            airport.updateOperatingHours(request.operatingHours);
        }

        return airportRepository.save(airport);
    }

    public static class UpdateDetailsRequest {
        public ContactInfo contactInfo;
        public OperatingHours operatingHours;
    }
}