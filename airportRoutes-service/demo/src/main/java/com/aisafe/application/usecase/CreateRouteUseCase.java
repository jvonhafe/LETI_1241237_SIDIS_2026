package com.aisafe.application.usecase;

import com.aisafe.messaging.DomainEventPublisher;
import com.aisafe.model.Airport;
import com.aisafe.model.Route;
import com.aisafe.model.RouteStatus;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateRouteUseCase {

    private final RouteRepository routeRepository;
    private final RouteUseCaseHelper helper;
    private final DomainEventPublisher eventPublisher;

    public CreateRouteUseCase(RouteRepository routeRepository,
                              RouteUseCaseHelper helper,
                              DomainEventPublisher eventPublisher) {
        this.routeRepository = routeRepository;
        this.helper = helper;
        this.eventPublisher = eventPublisher;
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

        // Publica evento assíncrono para sincronizar o Read Model das duas instâncias
        eventPublisher.publishRouteEvent(
                "route.created",
                "CREATED",
                savedRoute.getId(),
                origin.getIataCode().getCode(),
                origin.getName(),
                destination.getIataCode().getCode(),
                destination.getName(),
                savedRoute.getEstimatedFlightTimeMinutes(),
                savedRoute.getMinimumRange(),
                savedRoute.getMinimumCapacity(),
                savedRoute.getDistanceKm(),
                savedRoute.getStatus().name()
        );

        return savedRoute;
    }
}