package com.aisafe.application.route;

import com.aisafe.model.Airport;
import com.aisafe.model.Route;
import com.aisafe.model.RouteStatus;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateRouteUseCase {

    private final RouteRepository routeRepository;
    private final RouteUseCaseHelper helper;

    public CreateRouteUseCase(RouteRepository routeRepository,
                              RouteUseCaseHelper helper) {
        this.routeRepository = routeRepository;
        this.helper = helper;
    }

    public Route execute(String originCode,
                         String destinationCode,
                         Integer estimatedFlightTimeMinutes,
                         Integer minimumRange,
                         Integer minimumCapacity,
                         Double distanceKm) {

        helper.validateRouteInput(originCode, destinationCode,
                estimatedFlightTimeMinutes,
                minimumRange,
                minimumCapacity,
                distanceKm);

        Airport origin = helper.getAirport(originCode);
        Airport destination = helper.getAirport(destinationCode);

        if (origin.getIataCode().equals(destination.getIataCode())) {
            throw new IllegalArgumentException("Origin and destination airports cannot be the same.");
        }

        if (routeRepository.existsByOriginAirportAndDestinationAirportAndStatus(
                origin, destination, RouteStatus.ACTIVE)) {
            // CORRIGIDO AQUI: Utiliza uma exceção nativa do Java
            throw new IllegalStateException("An active route already exists between these airports.");
        }

        Route route = new Route();
        route.setOriginAirport(origin);
        route.setDestinationAirport(destination);
        route.setEstimatedFlightTimeMinutes(estimatedFlightTimeMinutes);
        route.setMinimumRange(minimumRange);
        route.setMinimumCapacity(minimumCapacity);
        route.setDistanceKm(distanceKm);
        route.setStatus(RouteStatus.ACTIVE);

        Route savedRoute = routeRepository.save(route);
        helper.saveHistory(savedRoute, "CREATED", "Route created.");

        return savedRoute;
    }
}