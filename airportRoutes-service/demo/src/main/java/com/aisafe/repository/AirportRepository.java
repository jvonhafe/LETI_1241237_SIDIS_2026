package com.aisafe.repository;

import com.aisafe.model.Airport;
import com.aisafe.model.BusiestAirportDto;
import com.aisafe.model.IataCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport, IataCode> {

    List<Airport> findByCityContainingIgnoreCase(String city);

    @Query("SELECT a.iataCode.code AS iata, a.name AS name, " +
            "(SELECT COUNT(r) FROM Route r WHERE r.originAirport.iataCode = a.iataCode OR r.destinationAirport.iataCode = a.iataCode) AS totalRoutes " +
            "FROM Airport a " +
            "ORDER BY totalRoutes DESC")
    List<BusiestAirportDto> findBusiestAirports(Pageable pageable);
}