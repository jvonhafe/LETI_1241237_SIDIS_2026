package com.aisafe.cqrs.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MaintenanceReadModelRepository extends JpaRepository<MaintenanceReadModel, String> {

    List<MaintenanceReadModel> findByAircraftRegistration(String aircraftRegistration);

    @Query("SELECT COALESCE(SUM(m.expectedDurationHours), 0.0) FROM MaintenanceReadModel m WHERE m.aircraftRegistration = :registration")
    Double totalMaintenanceHoursByAircraft(String registration);

    @Query("SELECT COALESCE(SUM(m.expectedDurationHours), 0.0) FROM MaintenanceReadModel m")
    Double totalMaintenanceHoursForFleet();
}