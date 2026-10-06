package com.aisafe.application.route;

import com.aisafe.model.Airport;
import com.aisafe.model.Route;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchRouteUseCase {

    private final RouteRepository routeRepository;
    private final RouteUseCaseHelper helper;

    public SearchRouteUseCase(RouteRepository routeRepository,
                              RouteUseCaseHelper helper) {
        this.routeRepository = routeRepository;
        this.helper = helper;
    }

    public List<Route> execute(String originCode, String destinationCode) {

        if (originCode != null && destinationCode != null) {
            Airport origin = helper.getAirport(originCode);
            Airport destination = helper.getAirport(destinationCode);
            return routeRepository.findByOriginAirportAndDestinationAirport(origin, destination);
        }

        if (originCode != null) {
            Airport origin = helper.getAirport(originCode);
            return routeRepository.findByOriginAirport(origin);
        }

        if (destinationCode != null) {
            Airport destination = helper.getAirport(destinationCode);
            return routeRepository.findByDestinationAirport(destination);
        }

        return routeRepository.findAll();
    }
}