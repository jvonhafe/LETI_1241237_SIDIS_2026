package com.aisafe.controller;

import com.aisafe.application.route.*;
import com.aisafe.model.IataCode;
import com.aisafe.model.Route;
import com.aisafe.model.RouteHistory;
import com.aisafe.repository.RouteRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final CreateRouteUseCase createRouteUseCase;
    private final GetRouteByIdUseCase getRouteByIdUseCase;
    private final GetRoutesFromAirportUseCase getRoutesFromAirportUseCase;
    private final SearchRouteUseCase searchRoutesUseCase;
    private final UpdateRouteUseCase updateRouteUseCase;
    private final DeactivateRouteUseCase deactivateRouteUseCase;
    private final GetRouteHistoryUseCase getRouteHistoryUseCase;
    private final RouteRepository routeRepository;

    // Novos UseCases das US 214, 215 e 216
    private final GetActiveRoutesSortedUseCase getActiveRoutesSortedUseCase;
    private final CalculateTotalDistanceUseCase calculateTotalDistanceUseCase;
    private final FindAlternativeRoutesUseCase findAlternativeRoutesUseCase;

    public RouteController(CreateRouteUseCase createRouteUseCase,
                           GetRouteByIdUseCase getRouteByIdUseCase,
                           GetRoutesFromAirportUseCase getRoutesFromAirportUseCase,
                           SearchRouteUseCase searchRoutesUseCase,
                           UpdateRouteUseCase updateRouteUseCase,
                           DeactivateRouteUseCase deactivateRouteUseCase,
                           GetRouteHistoryUseCase getRouteHistoryUseCase,
                           RouteRepository routeRepository,
                           GetActiveRoutesSortedUseCase getActiveRoutesSortedUseCase,
                           CalculateTotalDistanceUseCase calculateTotalDistanceUseCase,
                           FindAlternativeRoutesUseCase findAlternativeRoutesUseCase) {
        this.createRouteUseCase = createRouteUseCase;
        this.getRouteByIdUseCase = getRouteByIdUseCase;
        this.getRoutesFromAirportUseCase = getRoutesFromAirportUseCase;
        this.searchRoutesUseCase = searchRoutesUseCase;
        this.updateRouteUseCase = updateRouteUseCase;
        this.deactivateRouteUseCase = deactivateRouteUseCase;
        this.getRouteHistoryUseCase = getRouteHistoryUseCase;
        this.routeRepository = routeRepository;
        this.getActiveRoutesSortedUseCase = getActiveRoutesSortedUseCase;
        this.calculateTotalDistanceUseCase = calculateTotalDistanceUseCase;
        this.findAlternativeRoutesUseCase = findAlternativeRoutesUseCase;
    }

    // US110: Criar Rota
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Route createRoute(@RequestBody CreateRouteRequest request) {
        return createRouteUseCase.execute(
                request.originAirport,
                request.destinationAirport,
                request.estimatedFlightTimeMinutes,
                request.minimumRange,
                request.minimumCapacity,
                request.distanceKm
        );
    }

    // US113: Obter Rota por ID
    @GetMapping("/{id}")
    public Route getRouteById(@PathVariable Long id) {
        return getRouteByIdUseCase.execute(id);
    }

    // US113: Listar Rotas por Aeroporto de Origem
    @GetMapping("/from/{iataCode}")
    public List<Route> getRoutesFromAirport(@PathVariable String iataCode) {
        return getRoutesFromAirportUseCase.execute(iataCode);
    }

    // US114: Pesquisar Rotas por Origem/Destino
    @GetMapping("/search")
    public List<Route> searchRoutes(@RequestParam(required = false) String origin,
                                    @RequestParam(required = false) String destination) {
        return searchRoutesUseCase.execute(origin, destination);
    }

    // US112: Atualizar Rota
    @PutMapping("/{id}")
    public Route updateRoute(@PathVariable Long id,
                             @RequestBody UpdateRouteRequest request) {
        return updateRouteUseCase.execute(
                id,
                request.estimatedFlightTimeMinutes,
                request.minimumRange,
                request.minimumCapacity,
                request.distanceKm
        );
    }

    // US112: Desativar Rota
    @PatchMapping("/{id}/deactivate")
    public Route deactivateRoute(@PathVariable Long id) {
        return deactivateRouteUseCase.execute(id);
    }

    // US111: Ver Histórico da Rota
    @GetMapping("/{id}/history")
    public List<RouteHistory> getRouteHistory(@PathVariable Long id) {
        return getRouteHistoryUseCase.execute(id);
    }

    // US209: Consultar rotas por Aeroporto (Partida ou Chegada)
    @GetMapping("/airport/{iata}")
    public List<Route> getRoutesByAirport(@PathVariable String iata) {
        return routeRepository.findRoutesByAirport(new IataCode(iata));
    }

    // ==============================================================
    // NOVOS ENDPOINTS: US 214, 215, 216
    // ==============================================================

    // US214: Listar rotas ativas ordenadas por popularidade ou distância
    @GetMapping("/active/sorted")
    public List<Route> getActiveRoutesSorted(@RequestParam(defaultValue = "distance") String sortBy) {
        return getActiveRoutesSortedUseCase.execute(sortBy);
    }

    // US215: Calcular distância total da rede de rotas
    @GetMapping("/total-distance")
    public Double getTotalNetworkDistance() {
        return calculateTotalDistanceUseCase.execute();
    }

    // US216: Pesquisar rotas alternativas (com 1 escala)
    @GetMapping("/alternative")
    public List<List<Route>> getAlternativeRoutes(@RequestParam String origin,
                                                  @RequestParam String destination) {
        return findAlternativeRoutesUseCase.execute(origin, destination);
    }

    // ==============================================================

    public static class CreateRouteRequest {
        public String originAirport;
        public String destinationAirport;
        public Integer estimatedFlightTimeMinutes;
        public Integer minimumRange;
        public Integer minimumCapacity;
        public Double distanceKm;
    }

    public static class UpdateRouteRequest {
        public Integer estimatedFlightTimeMinutes;
        public Integer minimumRange;
        public Integer minimumCapacity;
        public Double distanceKm;
    }
}