package com.aisafe.cqrs.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftReadModelRepository extends JpaRepository<AircraftReadModel, String> {
}