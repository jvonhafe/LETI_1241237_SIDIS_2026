package com.aisafe.repository;

import com.aisafe.model.MaintenanceComponent;
import com.aisafe.model.MaintenanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, String> {

    List<MaintenanceRecord> findByAircraftRegistration(String registration);

    @Query("SELECT SUM(m.expectedDurationHours) FROM MaintenanceRecord m WHERE m.aircraftRegistration = :registration")
    Double totalMaintenanceHoursByAircraft(@Param("registration") String registration);

    @Query("SELECT SUM(m.expectedDurationHours) FROM MaintenanceRecord m")
    Double totalMaintenanceHoursForFleet();

    // US218: Pesquisa avançada com Paginação
    @Query("SELECT m FROM MaintenanceRecord m WHERE " +
            "(:aircraft IS NULL OR m.aircraftRegistration = :aircraft) AND " +
            "(:component IS NULL OR m.component = :component) AND " +
            "(CAST(:startDate AS date) IS NULL OR m.period.startDate >= :startDate) AND " +
            "(CAST(:endDate AS date) IS NULL OR m.period.endDate <= :endDate)")
    Page<MaintenanceRecord> searchRecords(
            @Param("aircraft") String aircraft,
            @Param("component") MaintenanceComponent component,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // US219: Atividades em curso com Paginação
    Page<MaintenanceRecord> findByStatus(String status, Pageable pageable);

    // US220: Custo por aeronave
    @Query("SELECT m.aircraftRegistration as aircraft, SUM(m.cost) as totalCost FROM MaintenanceRecord m GROUP BY m.aircraftRegistration")
    List<Map<String, Object>> getMaintenanceCostPerAircraft();

    // US220: Custo por modelo de avião (Corrigido para usar registrationNumber)
    @Query("SELECT a.modelId as model, SUM(m.cost) as totalCost " +
            "FROM MaintenanceRecord m JOIN Aircraft a ON m.aircraftRegistration = a.registrationNumber.registration " +
            "GROUP BY a.modelId")
    List<Map<String, Object>> getMaintenanceCostPerModel();

    // US221: Tempo turnaround (Corrigido para usar registrationNumber)
    @Query("SELECT a.modelId as model, AVG(DATEDIFF(DAY, m.period.startDate, m.period.endDate)) as avgTurnaroundDays " +
            "FROM MaintenanceRecord m JOIN Aircraft a ON m.aircraftRegistration = a.registrationNumber.registration " +
            "WHERE m.status = 'COMPLETED' " +
            "GROUP BY a.modelId")
    List<Map<String, Object>> getAverageTurnaroundTimePerModel();
}