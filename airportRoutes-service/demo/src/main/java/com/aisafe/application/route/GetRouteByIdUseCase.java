package com.aisafe.application.route;

import com.aisafe.model.Route;
import org.springframework.stereotype.Service;

@Service
public class GetRouteByIdUseCase {

    private final RouteUseCaseHelper helper;

    public GetRouteByIdUseCase(RouteUseCaseHelper helper) {
        this.helper = helper;
    }

    public Route execute(Long id) {
        return helper.getRoute(id);
    }
}