package com.aisafe.application.route;

import com.aisafe.model.Airport;
import com.aisafe.model.Route;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRoutesFromAirportUseCase {

    private final RouteRepository routeRepository;
    private final RouteUseCaseHelper helper;

    public GetRoutesFromAirportUseCase(RouteRepository routeRepository,
                                       RouteUseCaseHelper helper) {
        this.routeRepository = routeRepository;
        this.helper = helper;
    }

    public List<Route> execute(String originCode) {
        Airport origin = helper.getAirport(originCode);
        return routeRepository.findByOriginAirport(origin);
    }
}