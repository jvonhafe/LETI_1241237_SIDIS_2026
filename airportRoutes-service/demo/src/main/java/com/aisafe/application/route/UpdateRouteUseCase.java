package com.aisafe.application.route;

import com.aisafe.model.Route;
import com.aisafe.model.RouteStatus;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateRouteUseCase {

    private final RouteRepository routeRepository;
    private final RouteUseCaseHelper helper;

    public UpdateRouteUseCase(RouteRepository routeRepository,
                              RouteUseCaseHelper helper) {
        this.routeRepository = routeRepository;
        this.helper = helper;
    }

    public Route execute(Long id,
                         Integer estimatedFlightTimeMinutes,
                         Integer minimumRange,
                         Integer minimumCapacity,
                         Double distanceKm) {

        Route route = helper.getRoute(id);

        if (route.getStatus() == RouteStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Cannot update a deactivated route.");
        }

        if (estimatedFlightTimeMinutes != null && estimatedFlightTimeMinutes <= 0) {
            throw new IllegalArgumentException("Estimated flight time must be greater than zero.");
        }

        if (minimumRange != null && minimumRange <= 0) {
            throw new IllegalArgumentException("Minimum range must be greater than zero.");
        }

        if (minimumCapacity != null && minimumCapacity <= 0) {
            throw new IllegalArgumentException("Minimum capacity must be greater than zero.");
        }

        if (distanceKm != null && distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero.");
        }

        if (estimatedFlightTimeMinutes != null) {
            route.setEstimatedFlightTimeMinutes(estimatedFlightTimeMinutes);
        }

        if (minimumRange != null) {
            route.setMinimumRange(minimumRange);
        }

        if (minimumCapacity != null) {
            route.setMinimumCapacity(minimumCapacity);
        }

        if (distanceKm != null) {
            route.setDistanceKm(distanceKm);
        }

        Route updatedRoute = routeRepository.save(route);
        helper.saveHistory(updatedRoute, "UPDATED", "Route updated.");

        return updatedRoute;
    }
}