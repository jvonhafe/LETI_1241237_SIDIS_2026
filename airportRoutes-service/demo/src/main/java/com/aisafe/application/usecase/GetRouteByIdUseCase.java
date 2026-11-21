package com.aisafe.application.usecase;

import com.aisafe.readmodel.RouteReadModel;
import com.aisafe.readmodel.RouteReadRepository;
import org.springframework.stereotype.Service;

@Service
public class GetRouteByIdUseCase {

    private final RouteReadRepository routeReadRepository;

    public GetRouteByIdUseCase(RouteReadRepository routeReadRepository) {
        this.routeReadRepository = routeReadRepository;
    }

    public RouteReadModel execute(Long id) {
        return routeReadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found with id: " + id));
    }
}