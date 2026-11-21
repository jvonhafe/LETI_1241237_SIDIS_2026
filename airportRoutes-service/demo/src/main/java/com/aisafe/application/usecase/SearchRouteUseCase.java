package com.aisafe.application.usecase;

import com.aisafe.model.IataCode;
import com.aisafe.readmodel.RouteReadModel;
import com.aisafe.readmodel.RouteReadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchRouteUseCase {

    private final RouteReadRepository routeReadRepository;

    public SearchRouteUseCase(RouteReadRepository routeReadRepository) {
        this.routeReadRepository = routeReadRepository;
    }

    public List<RouteReadModel> execute(String originCode, String destinationCode) {
        boolean hasOrigin = originCode != null && !originCode.isBlank();
        boolean hasDestination = destinationCode != null && !destinationCode.isBlank();

        if (hasOrigin && hasDestination) {
            return routeReadRepository.findByOriginIataAndDestinationIata(
                    new IataCode(originCode).getCode(),
                    new IataCode(destinationCode).getCode()
            );
        }

        if (hasOrigin) {
            return routeReadRepository.findByOriginIata(new IataCode(originCode).getCode());
        }

        if (hasDestination) {
            return routeReadRepository.findByDestinationIata(new IataCode(destinationCode).getCode());
        }

        return routeReadRepository.findAll();
    }
}