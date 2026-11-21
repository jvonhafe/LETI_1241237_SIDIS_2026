package com.aisafe.cqrs.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftModelReadModelRepository extends JpaRepository<AircraftModelReadModel, String> {
}