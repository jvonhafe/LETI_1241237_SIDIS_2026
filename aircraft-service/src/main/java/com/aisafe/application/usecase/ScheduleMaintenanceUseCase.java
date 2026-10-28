package com.aisafe.application.usecase;

import com.aisafe.model.MaintenanceRecord;
import com.aisafe.repository.MaintenanceRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class ScheduleMaintenanceUseCase {

    private final MaintenanceRecordRepository recordRepository;

    public ScheduleMaintenanceUseCase(MaintenanceRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public MaintenanceRecord execute(MaintenanceRecord record) {
        return recordRepository.save(record);
    }
}