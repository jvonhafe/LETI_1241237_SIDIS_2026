package com.aisafe.application.usecase;

import com.aisafe.model.Aircraft;
import com.aisafe.model.FleetAvailabilityDTO;
import com.aisafe.repository.AircraftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetFleetAvailabilityUseCase {

    private final AircraftRepository aircraftRepository;

    public GetFleetAvailabilityUseCase(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    public FleetAvailabilityDTO execute() {
        List<Aircraft> allAircraft = aircraftRepository.findAll();
        long total = allAircraft.size();

        long available = allAircraft.stream()
                .filter(a -> a.getStatus() != null &&
                        ("AVAILABLE".equalsIgnoreCase(a.getStatus().toString()) ||
                                "OPERATIONAL".equalsIgnoreCase(a.getStatus().toString())))
                .count();

        long maintenance = allAircraft.stream()
                .filter(a -> a.getStatus() != null &&
                        "MAINTENANCE".equalsIgnoreCase(a.getStatus().toString()))
                .count();

        double rate = total > 0 ? ((double) available / total) * 100 : 100.0;

        List<FleetAvailabilityDTO.AircraftStatusDTO> details = allAircraft.stream()
                .map(a -> new FleetAvailabilityDTO.AircraftStatusDTO(
                        a.getRegistrationNumber() != null ? a.getRegistrationNumber().getRegistration() : "UNKNOWN",
                        a.getModelId(),
                        a.getStatus() != null ? a.getStatus().toString() : "UNKNOWN"
                ))
                .collect(Collectors.toList());

        return new FleetAvailabilityDTO(total, available, maintenance, rate, details);
    }
}