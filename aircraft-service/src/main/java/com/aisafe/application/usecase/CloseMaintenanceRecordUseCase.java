package com.aisafe.application.maintence;

import com.aisafe.model.MaintenanceRecord;
import com.aisafe.service.MaintenanceService;
import org.springframework.stereotype.Service;

@Service
public class CloseMaintenanceRecordUseCase {

    private final MaintenanceService maintenanceService;

    public CloseMaintenanceRecordUseCase(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    public MaintenanceRecord execute(String recordId, String completionNotes) {
        return maintenanceService.completeRecord(recordId, completionNotes);
    }
}