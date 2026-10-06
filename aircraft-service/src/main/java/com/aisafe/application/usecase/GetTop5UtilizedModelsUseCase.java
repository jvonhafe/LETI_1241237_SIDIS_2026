package com.aisafe.application.usecase;

import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftModel;
import com.aisafe.model.AircraftModelUtilizationDTO;
import com.aisafe.repository.AircraftModelRepository;
import com.aisafe.repository.AircraftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetTop5UtilizedModelsUseCase {

    private final AircraftRepository aircraftRepository;
    private final AircraftModelRepository aircraftModelRepository;

    public GetTop5UtilizedModelsUseCase(AircraftRepository aircraftRepository,
                                        AircraftModelRepository aircraftModelRepository) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftModelRepository = aircraftModelRepository;
    }

    public List<AircraftModelUtilizationDTO> execute() {
        List<Aircraft> allAircraft = aircraftRepository.findAll();
        List<AircraftModel> allModels = aircraftModelRepository.findAll();

        Map<String, Double> hoursPerModel = allAircraft.stream()
                .collect(Collectors.groupingBy(
                        Aircraft::getModelId,
                        Collectors.summingDouble(aircraft -> {
                            return 120.5;
                        })
                ));

        return allModels.stream()
                .map(model -> {
                    double totalHours = hoursPerModel.getOrDefault(model.getModelId(), 0.0);
                    return new AircraftModelUtilizationDTO(
                            model.getModelId(),
                            model.getModelName(),
                            model.getManufacturer(),
                            totalHours
                    );
                })
                .sorted((m1, m2) -> Double.compare(m2.getTotalFlightHours(), m1.getTotalFlightHours()))
                .limit(5)
                .collect(Collectors.toList());
    }
}