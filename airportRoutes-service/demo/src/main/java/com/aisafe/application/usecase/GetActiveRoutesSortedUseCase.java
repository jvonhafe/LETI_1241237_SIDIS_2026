package com.aisafe.application.usecase;

import com.aisafe.readmodel.RouteReadModel;
import com.aisafe.readmodel.RouteReadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetActiveRoutesSortedUseCase {

    private final RouteReadRepository routeReadRepository;

    public GetActiveRoutesSortedUseCase(RouteReadRepository routeReadRepository) {
        this.routeReadRepository = routeReadRepository;
    }

    public List<RouteReadModel> execute(String sortBy) {
        if ("popularity".equalsIgnoreCase(sortBy)) {
            throw new UnsupportedOperationException("A ordenação por popularidade requer projeção de eventos do Flight Operations Service.");
        }
        return routeReadRepository.findByStatusOrderByDistanceKmAsc("ACTIVE");
    }
}