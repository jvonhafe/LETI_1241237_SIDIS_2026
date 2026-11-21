package com.aisafe.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteReadRepository extends JpaRepository<RouteReadModel, Long> {
    List<RouteReadModel> findByStatusOrderByDistanceKmAsc(String status);
    List<RouteReadModel> findByOriginIata(String originIata);
    List<RouteReadModel> findByDestinationIata(String destinationIata);
    List<RouteReadModel> findByOriginIataAndDestinationIata(String originIata, String destinationIata);
    List<RouteReadModel> findByOriginIataOrDestinationIata(String originIata, String destinationIata);
}