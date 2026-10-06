package com.aisafe.application.route;

import com.aisafe.model.Route;
import com.aisafe.model.RouteStatus;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class DeactivateRouteUseCase {

    private final RouteRepository routeRepository;
    private final RouteUseCaseHelper helper;

    public DeactivateRouteUseCase(RouteRepository routeRepository,
                                  RouteUseCaseHelper helper) {
        this.routeRepository = routeRepository;
        this.helper = helper;
    }

    public Route execute(Long id) {
        Route route = helper.getRoute(id);

        if (route.getStatus() == RouteStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Route is already deactivated.");
        }

        route.setStatus(RouteStatus.DEACTIVATED);

        Route updatedRoute = routeRepository.save(route);
        helper.saveHistory(updatedRoute, "DEACTIVATED", "Route deactivated.");

        return updatedRoute;
    }
}