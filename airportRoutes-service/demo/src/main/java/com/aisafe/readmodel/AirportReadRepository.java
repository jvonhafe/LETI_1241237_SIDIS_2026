package com.aisafe.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AirportReadRepository extends JpaRepository<AirportReadModel, String> {
    List<AirportReadModel> findByStatus(String status);
    List<AirportReadModel> findByCountryIgnoreCase(String country);
    List<AirportReadModel> findByCityContainingIgnoreCase(String city);
}