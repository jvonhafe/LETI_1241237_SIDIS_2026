package com.aisafe.application.route;

import com.aisafe.model.Route;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class CalculateTotalDistanceUseCase {
    private final RouteRepository repository;

    public CalculateTotalDistanceUseCase(RouteRepository repository) {
        this.repository = repository;
    }

    public Double execute() {
        return repository.findAll().stream()
                .filter(route -> route.getDistanceKm() != null)
                .mapToDouble(Route::getDistanceKm)
                .sum();
    }
}