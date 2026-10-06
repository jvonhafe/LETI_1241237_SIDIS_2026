package com.aisafe.controller;

import com.aisafe.application.route.*;
import com.aisafe.model.IataCode;
import com.aisafe.model.Route;
import com.aisafe.model.RouteHistory;
import com.aisafe.repository.RouteRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(
        name = "Flight Routes",
        description = "Gestão de Rotas de Voo (WP#3A e WP#3B completos)"
)
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

    @Operation(summary = "US110: Criar Rota")
    @PreAuthorize("hasAnyRole('ATCC', 'ADMIN')") // Corrigido para ATCC (req. do PDF)
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

    @Operation(summary = "US113: Obter Rota por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC')")
    @GetMapping("/{id}")
    public Route getRouteById(@PathVariable Long id) {
        return getRouteByIdUseCase.execute(id);
    }

    @Operation(summary = "US113: Listar Rotas por Aeroporto de Origem")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC')")
    @GetMapping("/from/{iataCode}")
    public List<Route> getRoutesFromAirport(@PathVariable String iataCode) {
        return getRoutesFromAirportUseCase.execute(iataCode);
    }

    @Operation(summary = "US114: Pesquisar Rotas por Origem/Destino")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC')")
    @GetMapping("/search")
    public List<Route> searchRoutes(@RequestParam(required = false) String origin,
                                    @RequestParam(required = false) String destination) {
        return searchRoutesUseCase.execute(origin, destination);
    }

    @Operation(summary = "US112: Atualizar Rota")
    @PreAuthorize("hasAnyRole('ATCC', 'BACKOFFICE', 'ADMIN')") // ATCC adicionado
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

    @Operation(summary = "US112: Desativar Rota")
    @PreAuthorize("hasAnyRole('ATCC', 'BACKOFFICE', 'ADMIN')") // ATCC adicionado
    @PatchMapping("/{id}/deactivate")
    public Route deactivateRoute(@PathVariable Long id) {
        return deactivateRouteUseCase.execute(id);
    }

    @Operation(summary = "US111: Ver Histórico da Rota")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC')")
    @GetMapping("/{id}/history")
    public List<RouteHistory> getRouteHistory(@PathVariable Long id) {
        return getRouteHistoryUseCase.execute(id);
    }

    @Operation(summary = "US209: Consultar rotas por Aeroporto (Partida ou Chegada)")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC')")
    @GetMapping("/airport/{iata}")
    public List<Route> getRoutesByAirport(@PathVariable String iata) {
        return routeRepository.findRoutesByAirport(new IataCode(iata));
    }

    // ==============================================================
    // NOVOS ENDPOINTS: US 214, 215, 216
    // ==============================================================

    @Operation(summary = "US214: Listar rotas ativas ordenadas por popularidade ou distância")
    @PreAuthorize("hasAnyRole('ATCC', 'ADMIN')")
    @GetMapping("/active/sorted")
    public List<Route> getActiveRoutesSorted(@RequestParam(defaultValue = "distance") String sortBy) {
        return getActiveRoutesSortedUseCase.execute(sortBy);
    }

    @Operation(summary = "US215: Calcular distância total da rede de rotas")
    @PreAuthorize("hasAnyRole('ATCC', 'ADMIN')")
    @GetMapping("/total-distance")
    public Double getTotalNetworkDistance() {
        return calculateTotalDistanceUseCase.execute();
    }

    @Operation(summary = "US216: Pesquisar rotas alternativas (com 1 escala)")
    @PreAuthorize("hasAnyRole('ATCC', 'ADMIN')")
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