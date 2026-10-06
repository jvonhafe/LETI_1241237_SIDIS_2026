package com.aisafe.application.route;

import com.aisafe.model.Route;
import com.aisafe.model.RouteStatus;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetActiveRoutesSortedUseCase {

    private final RouteRepository routeRepository;
    // APAGADO: private final ScheduledFlightRepository flightRepository;

    public GetActiveRoutesSortedUseCase(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> execute(String sortBy) {
        List<Route> activeRoutes = routeRepository.findAll().stream()
                .filter(r -> r.getStatus() == RouteStatus.ACTIVE)
                .collect(Collectors.toList());

        if ("distance".equalsIgnoreCase(sortBy)) {
            activeRoutes.sort(Comparator.comparing(r -> r.getDistanceKm() == null ? 0.0 : r.getDistanceKm()));
        } else if ("popularity".equalsIgnoreCase(sortBy)) {
            // NOTA PARA O FUTURO: Num sistema distribuído, terás de pedir ao Flight Operations Service
            // a quantidade de voos por rota através de um pedido HTTP.
            throw new UnsupportedOperationException("A ordenação por popularidade requer comunicação com o serviço de Voos (ficará para a Semana 3).");
        }
        return activeRoutes;
    }
}