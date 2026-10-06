package com.aisafe.application.route;

import com.aisafe.model.Airport;
import com.aisafe.model.IataCode;
import com.aisafe.model.Route;
import com.aisafe.model.RouteHistory;
import com.aisafe.repository.AirportRepository;
import com.aisafe.repository.RouteHistoryRepository;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Component;

@Component
public class RouteUseCaseHelper {

    private final AirportRepository airportRepository;
    private final RouteRepository routeRepository;
    private final RouteHistoryRepository routeHistoryRepository;

    public RouteUseCaseHelper(AirportRepository airportRepository,
                              RouteRepository routeRepository,
                              RouteHistoryRepository routeHistoryRepository) {
        this.airportRepository = airportRepository;
        this.routeRepository = routeRepository;
        this.routeHistoryRepository = routeHistoryRepository;
    }

    public Airport getAirport(String iataCode) {
        return airportRepository.findById(new IataCode(iataCode))
                // CORRIGIDO: Substituído por IllegalArgumentException
                .orElseThrow(() -> new IllegalArgumentException(
                        "Airport not found with IATA code: " + iataCode));
    }

    public Route getRoute(Long id) {
        return routeRepository.findById(id)
                // CORRIGIDO: Substituído por IllegalArgumentException
                .orElseThrow(() -> new IllegalArgumentException(
                        "Route not found with id: " + id));
    }

    public void validateRouteInput(String originCode,
                                   String destinationCode,
                                   Integer estimatedFlightTimeMinutes,
                                   Integer minimumRange,
                                   Integer minimumCapacity,
                                   Double distanceKm) {

        if (originCode == null || !originCode.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Origin airport must be a valid 3-letter IATA code.");
        }

        if (destinationCode == null || !destinationCode.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Destination airport must be a valid 3-letter IATA code.");
        }

        if (estimatedFlightTimeMinutes == null || estimatedFlightTimeMinutes <= 0) {
            throw new IllegalArgumentException("Estimated flight time must be greater than zero.");
        }

        if (minimumRange == null || minimumRange <= 0) {
            throw new IllegalArgumentException("Minimum range must be greater than zero.");
        }

        if (minimumCapacity == null || minimumCapacity <= 0) {
            throw new IllegalArgumentException("Minimum capacity must be greater than zero.");
        }

        if (distanceKm == null || distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero.");
        }
    }

    public void saveHistory(Route route, String action, String description) {
        RouteHistory history = new RouteHistory();
        history.setRoute(route);
        history.setAction(action);
        history.setDescription(description);
        routeHistoryRepository.save(history);
    }
}