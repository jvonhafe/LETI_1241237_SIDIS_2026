package com.aisafe.repository;

import com.aisafe.model.ScheduledFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledFlightRepository extends JpaRepository<ScheduledFlight, String> {
}