package com.aisafe.application.usecase;

import com.aisafe.core.exception.ResourceNotFoundException;
import com.aisafe.model.AircraftModel;
import com.aisafe.model.Capacity;
import com.aisafe.model.UpdateAircraftModelDTO;
import com.aisafe.repository.AircraftModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateAircraftModelUseCase {

    private final AircraftModelRepository aircraftModelRepository;

    public UpdateAircraftModelUseCase(AircraftModelRepository aircraftModelRepository) {
        this.aircraftModelRepository = aircraftModelRepository;
    }

    @Transactional
    public AircraftModel execute(String modelId, UpdateAircraftModelDTO dto) {
        AircraftModel model = aircraftModelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de aeronave " + modelId + " não encontrado."));

        Capacity newCapacity = new Capacity(dto.getMaxCapacity());

        model.updateSpecifications(
                newCapacity,
                dto.getFuelCapacity(),
                dto.getMaxRange(),
                dto.getCruisingSpeed()
        );

        return aircraftModelRepository.save(model);
    }
}