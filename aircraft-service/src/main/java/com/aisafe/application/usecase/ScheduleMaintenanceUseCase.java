package com.aisafe.application.maintence;

import com.aisafe.model.MaintenanceRecord;
import com.aisafe.repository.MaintenanceRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class ScheduleMaintenanceUseCase {

    private final MaintenanceRecordRepository recordRepository;

    // Apenas o repositório é necessário aqui
    public ScheduleMaintenanceUseCase(MaintenanceRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public MaintenanceRecord execute(MaintenanceRecord record) {
        // Agora, como não chamamos o maintenanceService, o erro de NullPointerException desaparece
        return recordRepository.save(record);
    }
}