package com.aisafe.application.route;

import com.aisafe.model.IataCode;
import com.aisafe.model.Route;
import com.aisafe.model.RouteStatus;
import com.aisafe.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FindAlternativeRoutesUseCase {
    private final RouteRepository repository;

    public FindAlternativeRoutesUseCase(RouteRepository repository) {
        this.repository = repository;
    }

    public List<List<Route>> execute(String originStr, String destinationStr) {
        IataCode origin = new IataCode(originStr);
        IataCode destination = new IataCode(destinationStr);

        List<List<Route>> alternatives = new ArrayList<>();
        List<Route> allActive = repository.findAll().stream()
                .filter(r -> r.getStatus() == RouteStatus.ACTIVE)
                .collect(Collectors.toList());
        List<Route> fromOrigin = allActive.stream()
                .filter(r -> r.getOriginAirport().getIataCode().equals(origin)).toList();
        List<Route> toDestination = allActive.stream()
                .filter(r -> r.getDestinationAirport().getIataCode().equals(destination)).toList();

        for (Route firstLeg : fromOrigin) {
            for (Route secondLeg : toDestination) {
                // Se o aeroporto de chegada do 1º voo for igual ao aeroporto de partida do 2º voo... Encontrámos ligação!
                if (firstLeg.getDestinationAirport().getIataCode().equals(secondLeg.getOriginAirport().getIataCode())) {
                    alternatives.add(List.of(firstLeg, secondLeg));
                }
            }
        }
        return alternatives;
    }
}