package com.aisafe.cqrs.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightReadModelRepository extends JpaRepository<FlightReadModel, String> {
}