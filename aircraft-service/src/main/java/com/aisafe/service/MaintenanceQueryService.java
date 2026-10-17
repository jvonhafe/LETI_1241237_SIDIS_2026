package com.aisafe.service;

import com.aisafe.model.MaintenanceComponent;
import com.aisafe.model.MaintenanceRecord;
import com.aisafe.repository.MaintenanceRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceQueryService {

    private final MaintenanceRecordRepository recordRepository;

    public MaintenanceQueryService(MaintenanceRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public Page<MaintenanceRecord> searchRecords(String aircraft, MaintenanceComponent component, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return recordRepository.searchRecords(aircraft, component, startDate, endDate, pageable);
    }

    public Page<MaintenanceRecord> getOngoingActivities(Pageable pageable) {
        return recordRepository.findByStatus("IN_PROGRESS", pageable);
    }

    public List<Map<String, Object>> getCostsPerAircraft() {
        return recordRepository.getMaintenanceCostPerAircraft();
    }

    public List<Map<String, Object>> getCostsPerModel() {
        return recordRepository.getMaintenanceCostPerModel();
    }

    public List<Map<String, Object>> getAverageTurnaroundTime() {
        return recordRepository.getAverageTurnaroundTimePerModel();
    }
}