package com.aisafe.application.route;

import com.aisafe.model.RouteHistory;
import com.aisafe.repository.RouteHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRouteHistoryUseCase {

    private final RouteHistoryRepository routeHistoryRepository;
    private final RouteUseCaseHelper helper;

    public GetRouteHistoryUseCase(RouteHistoryRepository routeHistoryRepository,
                                  RouteUseCaseHelper helper) {
        this.routeHistoryRepository = routeHistoryRepository;
        this.helper = helper;
    }

    public List<RouteHistory> execute(Long routeId) {
        helper.getRoute(routeId);
        return routeHistoryRepository.findByRouteId(routeId);
    }
}