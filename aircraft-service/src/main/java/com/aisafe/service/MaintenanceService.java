package com.aisafe.service;

import com.aisafe.core.exception.ResourceNotFoundException;
import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftRegistration;
import com.aisafe.model.MaintenanceRecord;
import com.aisafe.model.MaintenanceTemplate;
import com.aisafe.repository.AircraftRepository;
import com.aisafe.repository.MaintenanceRecordRepository;
import com.aisafe.repository.MaintenanceTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceService {

    private final MaintenanceRecordRepository recordRepository;
    private final MaintenanceTemplateRepository templateRepository;
    private final AircraftRepository aircraftRepository;

    public MaintenanceService(
            MaintenanceRecordRepository recordRepository,
            MaintenanceTemplateRepository templateRepository,
            AircraftRepository aircraftRepository
    ) {
        this.recordRepository = recordRepository;
        this.templateRepository = templateRepository;
        this.aircraftRepository = aircraftRepository;
    }

    public MaintenanceTemplate createTemplate(MaintenanceTemplate template) {
        return templateRepository.save(template);
    }

    public MaintenanceRecord createRecord(MaintenanceRecord record) {
        AircraftRegistration registration = new AircraftRegistration(record.getAircraftRegistration());

        Aircraft aircraft = aircraftRepository.findById(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found"));

        String templateIdStr = record.getTemplateId() != null ? String.valueOf(record.getTemplateId()) : null;

        MaintenanceTemplate template = templateRepository.findById(templateIdStr)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance template not found"));

        if (record.getChecklist() == null || record.getChecklist().isEmpty()) {
            record.setChecklist(template.getChecklist());
        }

        aircraft.setStatus("UNDER_MAINTENANCE");
        aircraftRepository.save(aircraft);

        return recordRepository.save(record);
    }

    public List<MaintenanceRecord> recordsForAircraft(String registration) {
        AircraftRegistration regVo = new AircraftRegistration(registration);
        if (!aircraftRepository.existsById(regVo)) {
            throw new ResourceNotFoundException("Aircraft not found");
        }
        return recordRepository.findByAircraftRegistration(registration);
    }

    public Double totalHoursForAircraft(String registration) {
        AircraftRegistration regVo = new AircraftRegistration(registration);
        if (!aircraftRepository.existsById(regVo)) {
            throw new ResourceNotFoundException("Aircraft not found");
        }
        return recordRepository.totalMaintenanceHoursByAircraft(registration);
    }

    public Double totalHoursForFleet() {
        return recordRepository.totalMaintenanceHoursForFleet();
    }

    public MaintenanceRecord completeRecord(String recordId, String completionNotes) {
        MaintenanceRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found"));

        record.complete(completionNotes);

        AircraftRegistration registration = new AircraftRegistration(record.getAircraftRegistration());
        Aircraft aircraft = aircraftRepository.findById(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found"));

        aircraft.setStatus("ACTIVE");
        aircraftRepository.save(aircraft);

        return recordRepository.save(record);
    }
}