package com.aisafe.application.usecase;

import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftOperationalHoursDTO;
import com.aisafe.repository.AircraftRepository;
import com.aisafe.repository.MaintenanceRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalculateFleetOperationalHoursUseCase {

    private final AircraftRepository aircraftRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;

    public CalculateFleetOperationalHoursUseCase(AircraftRepository aircraftRepository,
                                                 MaintenanceRecordRepository maintenanceRecordRepository) {
        this.aircraftRepository = aircraftRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
    }

    public List<AircraftOperationalHoursDTO> execute() {
        List<Aircraft> allAircraft = aircraftRepository.findAll();

        return allAircraft.stream().map(aircraft -> {

            String registration = aircraft.getRegistrationNumber() != null ?
                    aircraft.getRegistrationNumber().getRegistration() : "UNKNOWN";

            Double maintenanceHours = maintenanceRecordRepository.totalMaintenanceHoursByAircraft(registration);
            if (maintenanceHours == null) maintenanceHours = 0.0;

            double totalLifeHours = 0;
            if (aircraft.getManufacturingDate() != null) {
                long daysSinceManufacture = ChronoUnit.DAYS.between(aircraft.getManufacturingDate(), LocalDate.now());
                totalLifeHours = daysSinceManufacture * 24.0;
            }

            double operationalHours = Math.max(0, totalLifeHours - maintenanceHours);

            return new AircraftOperationalHoursDTO(
                    registration,
                    aircraft.getModelId(),
                    operationalHours
            );
        }).collect(Collectors.toList());
    }
}