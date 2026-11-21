package com.aisafe.application.usecase;

import com.aisafe.messaging.DomainEventPublisher;
import com.aisafe.model.Route;
import com.aisafe.model.RouteStatus;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class DeactivateRouteUseCase {

    private final RouteRepository routeRepository;
    private final RouteUseCaseHelper helper;
    private final DomainEventPublisher eventPublisher;

    public DeactivateRouteUseCase(RouteRepository routeRepository,
                                  RouteUseCaseHelper helper,
                                  DomainEventPublisher eventPublisher) {
        this.routeRepository = routeRepository;
        this.helper = helper;
        this.eventPublisher = eventPublisher;
    }

    public Route execute(Long id) {
        Route route = helper.getRoute(id);

        if (route.getStatus() == RouteStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Route is already deactivated.");
        }

        route.setStatus(RouteStatus.DEACTIVATED);

        Route updatedRoute = routeRepository.save(route);
        helper.saveHistory(updatedRoute, "DEACTIVATED", "Route deactivated.");

        eventPublisher.publishRouteEvent(
                "route.deactivated",
                "DEACTIVATED",
                updatedRoute.getId(),
                updatedRoute.getOriginAirport().getIataCode().getCode(),
                updatedRoute.getOriginAirport().getName(),
                updatedRoute.getDestinationAirport().getIataCode().getCode(),
                updatedRoute.getDestinationAirport().getName(),
                updatedRoute.getEstimatedFlightTimeMinutes(),
                updatedRoute.getMinimumRange(),
                updatedRoute.getMinimumCapacity(),
                updatedRoute.getDistanceKm(),
                updatedRoute.getStatus().name()
        );

        return updatedRoute;
    }
}