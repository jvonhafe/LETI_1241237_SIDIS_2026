package com.aisafe.application.usecase;

import com.aisafe.model.IataCode;
import com.aisafe.readmodel.RouteReadModel;
import com.aisafe.readmodel.RouteReadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRoutesFromAirportUseCase {

    private final RouteReadRepository routeReadRepository;

    public GetRoutesFromAirportUseCase(RouteReadRepository routeReadRepository) {
        this.routeReadRepository = routeReadRepository;
    }

    public List<RouteReadModel> execute(String originCode) {
        IataCode code = new IataCode(originCode);
        return routeReadRepository.findByOriginIata(code.getCode());
    }
}