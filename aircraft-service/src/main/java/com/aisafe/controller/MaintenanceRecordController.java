package com.aisafe.controller;

import com.aisafe.application.maintence.CloseMaintenanceRecordUseCase;
import com.aisafe.application.maintence.ScheduleMaintenanceUseCase;
import com.aisafe.model.MaintenanceComponent;
import com.aisafe.model.MaintenanceRecord;
import com.aisafe.service.MaintenanceService;
import com.aisafe.service.MaintenanceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Maintenance Records",
        description = "Gestão de Registos de Manutenção (Fases 1 e 2)"
)
@RestController
@RequestMapping("/api/maintenance-records")
public class MaintenanceRecordController {

    private final MaintenanceService maintenanceService;
    private final ScheduleMaintenanceUseCase scheduleMaintenanceUseCase;
    private final CloseMaintenanceRecordUseCase closeMaintenanceRecordUseCase;
    private final MaintenanceQueryService queryService;

    public MaintenanceRecordController(
            MaintenanceService maintenanceService,
            ScheduleMaintenanceUseCase scheduleMaintenanceUseCase,
            CloseMaintenanceRecordUseCase closeMaintenanceRecordUseCase,
            MaintenanceQueryService queryService
    ) {
        this.maintenanceService = maintenanceService;
        this.scheduleMaintenanceUseCase = scheduleMaintenanceUseCase;
        this.closeMaintenanceRecordUseCase = closeMaintenanceRecordUseCase;
        this.queryService = queryService;
    }

    @Operation(summary = "US217 - Criar e Categorizar Registro de Manutenção")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTENANCE_TECH')")
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceRecord createRecord(@Valid @RequestBody MaintenanceRecord record) {
        return scheduleMaintenanceUseCase.execute(record);
    }

    @Operation(summary = "Listar Registos de Manutenção por Aeronave")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTENANCE_TECH')")
    @GetMapping("/aircraft/{registration}")
    public List<MaintenanceRecord> recordsForAircraft(@PathVariable String registration) {
        return maintenanceService.recordsForAircraft(registration);
    }

    @Operation(summary = "Ver Total de Horas de Manutenção de uma Aeronave")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    @GetMapping("/aircraft/{registration}/hours")
    public Map<String, Object> totalHoursForAircraft(@PathVariable String registration) {
        return Map.of(
                "aircraftRegistration", registration,
                "totalMaintenanceHours", maintenanceService.totalHoursForAircraft(registration)
        );
    }

    @Operation(summary = "Ver Total de Horas de Manutenção da Frota")
    @GetMapping("/fleet/hours")
    public Map<String, Object> totalHoursForFleet() {
        return Map.of(
                "totalMaintenanceHours",
                maintenanceService.totalHoursForFleet()
        );
    }

    @Operation(summary = "Completar Registo de Manutenção")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTENANCE_TECH')")
    @PatchMapping("/{recordId}/complete")
    public MaintenanceRecord completeRecord(
            @PathVariable String recordId,
            @Valid @RequestBody CompleteMaintenanceRequest request
    ) {
        return closeMaintenanceRecordUseCase.execute(
                recordId,
                request.getCompletionNotes()
        );
    }

    // ==========================================
    // ENDPOINTS DA FASE 2 (US218 A US222)
    // ==========================================

    @Operation(summary = "US218 - Pesquisar Registos (Paginação)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTENANCE_TECH')")
    @GetMapping("/search")
    public Page<MaintenanceRecord> searchRecords(
            @RequestParam(required = false) String aircraft,
            @RequestParam(required = false) MaintenanceComponent component,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable) {
        return queryService.searchRecords(aircraft, component, startDate, endDate, pageable);
    }

    @Operation(summary = "US219 - Ver Manutenções em Curso (Paginação)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTENANCE_SUPERVISOR')")
    @GetMapping("/ongoing")
    public Page<MaintenanceRecord> getOngoingActivities(Pageable pageable) {
        return queryService.getOngoingActivities(pageable);
    }

    @Operation(summary = "US220 - Custos de Manutenção por Aeronave")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    @GetMapping("/reports/costs/aircraft")
    public List<Map<String, Object>> getCostsPerAircraft() {
        return queryService.getCostsPerAircraft();
    }

    @Operation(summary = "US220 - Custos de Manutenção por Modelo")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    @GetMapping("/reports/costs/model")
    public List<Map<String, Object>> getCostsPerModel() {
        return queryService.getCostsPerModel();
    }

    @Operation(summary = "US221 - Tempo Médio de Resolução (Turnaround)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTENANCE_SUPERVISOR')")
    @GetMapping("/reports/turnaround-time")
    public List<Map<String, Object>> getTurnaroundTime() {
        return queryService.getAverageTurnaroundTime();
    }

    @Operation(summary = "US222 - Alertas de Manutenção Planeada")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    @GetMapping("/alerts")
    public List<Map<String, Object>> getMaintenanceAlerts() {
        // Retorna um mock de alertas pois necessitaria cruzar com o histórico de voos real da Fase 1/2.
        return List.of(Map.of(
                "alert", "Aircraft CS-TPA exceeds scheduled maintenance threshold based on flight hours."
        ));
    }

    public static class CompleteMaintenanceRequest {

        @NotBlank
        private String completionNotes;

        public String getCompletionNotes() {
            return completionNotes;
        }

        public void setCompletionNotes(String completionNotes) {
            this.completionNotes = completionNotes;
        }
    }
}