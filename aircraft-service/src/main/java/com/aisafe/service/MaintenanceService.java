package com.aisafe.service;

import com.aisafe.core.exception.ResourceNotFoundException;
import com.aisafe.cqrs.event.AircraftEvent;
import com.aisafe.cqrs.event.AircraftEventPublisher;
import com.aisafe.cqrs.event.MaintenanceEvent;
import com.aisafe.cqrs.query.AircraftReadModel;
import com.aisafe.cqrs.query.AircraftReadModelRepository;
import com.aisafe.cqrs.query.MaintenanceReadModel;
import com.aisafe.cqrs.query.MaintenanceReadModelRepository;
import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftRegistration;
import com.aisafe.model.MaintenanceRecord;
import com.aisafe.model.MaintenanceTemplate;
import com.aisafe.repository.AircraftRepository;
import com.aisafe.repository.MaintenanceRecordRepository;
import com.aisafe.repository.MaintenanceTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class MaintenanceService {

    private final MaintenanceRecordRepository recordRepository;
    private final MaintenanceTemplateRepository templateRepository;
    private final AircraftRepository aircraftRepository;
    private final AircraftReadModelRepository aircraftReadRepository;
    private final MaintenanceReadModelRepository maintenanceReadRepository;
    private final AircraftEventPublisher eventPublisher;

    public MaintenanceService(
            MaintenanceRecordRepository recordRepository,
            MaintenanceTemplateRepository templateRepository,
            AircraftRepository aircraftRepository,
            AircraftReadModelRepository aircraftReadRepository,
            MaintenanceReadModelRepository maintenanceReadRepository,
            AircraftEventPublisher eventPublisher
    ) {
        this.recordRepository = recordRepository;
        this.templateRepository = templateRepository;
        this.aircraftRepository = aircraftRepository;
        this.aircraftReadRepository = aircraftReadRepository;
        this.maintenanceReadRepository = maintenanceReadRepository;
        this.eventPublisher = eventPublisher;
    }

    public MaintenanceTemplate createTemplate(MaintenanceTemplate template) {
        return templateRepository.save(template);
    }

    public MaintenanceRecord createRecord(MaintenanceRecord record) {
        String reg = record.getAircraftRegistration();
        AircraftRegistration registration = new AircraftRegistration(reg);

        Optional<Aircraft> localAircraft = aircraftRepository.findById(registration);
        Optional<AircraftReadModel> readAircraft = aircraftReadRepository.findById(reg);

        if (localAircraft.isEmpty() && readAircraft.isEmpty()) {
            throw new ResourceNotFoundException("Aircraft not found");
        }

        String templateIdStr = record.getTemplateId() != null ? String.valueOf(record.getTemplateId()) : null;
        MaintenanceTemplate template = templateRepository.findById(templateIdStr)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance template not found"));

        if (record.getChecklist() == null || record.getChecklist().isEmpty()) {
            if (template.getChecklist() != null) {
                record.setChecklist(new ArrayList<>(template.getChecklist()));
            }
        }

        MaintenanceRecord saved = recordRepository.save(record);

        if (localAircraft.isPresent()) {
            Aircraft aircraft = localAircraft.get();
            aircraft.setStatus("UNDER_MAINTENANCE");
            aircraftRepository.save(aircraft);

            eventPublisher.publish(new AircraftEvent(
                    "UPDATED",
                    aircraft.getRegistrationNumber().getRegistration(),
                    aircraft.getModelId(),
                    aircraft.getManufacturingDate(),
                    "UNDER_MAINTENANCE"
            ));
        } else {
            AircraftReadModel rm = readAircraft.get();
            eventPublisher.publish(new AircraftEvent(
                    "UPDATED",
                    rm.getRegistration(),
                    rm.getModelId(),
                    rm.getManufacturingDate(),
                    "UNDER_MAINTENANCE"
            ));
        }

        eventPublisher.publishMaintenance(new MaintenanceEvent(
                "CREATED",
                saved.getId(),
                saved.getAircraftRegistration(),
                saved.getTemplateId(),
                saved.getExpectedDurationHours(),
                saved.getStatus(),
                saved.getCompletionNotes(),
                saved.getComponent() != null ? saved.getComponent().name() : null,
                saved.getCost()
        ));

        return saved;
    }

    public List<MaintenanceReadModel> recordsForAircraft(String registration) {
        if (!aircraftReadRepository.existsById(registration)) {
            throw new ResourceNotFoundException("Aircraft not found");
        }
        return maintenanceReadRepository.findByAircraftRegistration(registration);
    }

    public Double totalHoursForAircraft(String registration) {
        if (!aircraftReadRepository.existsById(registration)) {
            throw new ResourceNotFoundException("Aircraft not found");
        }
        return maintenanceReadRepository.totalMaintenanceHoursByAircraft(registration);
    }

    public Double totalHoursForFleet() {
        return maintenanceReadRepository.totalMaintenanceHoursForFleet();
    }

    public MaintenanceRecord completeRecord(String recordId, String completionNotes) {
        MaintenanceRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found"));

        record.complete(completionNotes);

        String reg = record.getAircraftRegistration();
        AircraftRegistration registration = new AircraftRegistration(reg);

        Optional<Aircraft> localAircraft = aircraftRepository.findById(registration);
        Optional<AircraftReadModel> readAircraft = aircraftReadRepository.findById(reg);

        if (localAircraft.isEmpty() && readAircraft.isEmpty()) {
            throw new ResourceNotFoundException("Aircraft not found");
        }

        if (localAircraft.isPresent()) {
            Aircraft aircraft = localAircraft.get();
            aircraft.setStatus("ACTIVE");
            aircraftRepository.save(aircraft);

            eventPublisher.publish(new AircraftEvent(
                    "UPDATED",
                    aircraft.getRegistrationNumber().getRegistration(),
                    aircraft.getModelId(),
                    aircraft.getManufacturingDate(),
                    "ACTIVE"
            ));
        } else {
            AircraftReadModel rm = readAircraft.get();
            eventPublisher.publish(new AircraftEvent(
                    "UPDATED",
                    rm.getRegistration(),
                    rm.getModelId(),
                    rm.getManufacturingDate(),
                    "ACTIVE"
            ));
        }

        MaintenanceRecord saved = recordRepository.save(record);

        eventPublisher.publishMaintenance(new MaintenanceEvent(
                "COMPLETED",
                saved.getId(),
                saved.getAircraftRegistration(),
                saved.getTemplateId(),
                saved.getExpectedDurationHours(),
                saved.getStatus(),
                saved.getCompletionNotes(),
                saved.getComponent() != null ? saved.getComponent().name() : null,
                saved.getCost()
        ));

        return saved;
    }
}