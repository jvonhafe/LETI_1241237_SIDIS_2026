package com.aisafe.application.usecase;

import com.aisafe.model.MaintenanceRecord;
import com.aisafe.service.MaintenanceService;
import org.springframework.stereotype.Service;

@Service
public class ScheduleMaintenanceUseCase {

    private final MaintenanceService maintenanceService;

    public ScheduleMaintenanceUseCase(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    public MaintenanceRecord execute(MaintenanceRecord record) {
        return maintenanceService.createRecord(record);
    }
}