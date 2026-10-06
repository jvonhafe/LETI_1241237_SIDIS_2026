package com.aisafe.repository;

import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, AircraftRegistration> {

    @Query("SELECT a FROM Aircraft a WHERE " +
            "(:modelId IS NULL OR a.modelId = :modelId) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:year IS NULL OR YEAR(a.manufacturingDate) = :year)")
    List<Aircraft> searchAircrafts(@Param("modelId") String modelId,
                                   @Param("status") String status,
                                   @Param("year") Integer year);
}