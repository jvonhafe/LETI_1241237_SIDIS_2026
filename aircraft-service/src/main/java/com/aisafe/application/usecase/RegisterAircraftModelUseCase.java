package com.aisafe.application.usecase;

import com.aisafe.core.exception.DuplicateResourceException;
import com.aisafe.cqrs.event.AircraftEventPublisher;
import com.aisafe.cqrs.event.AircraftModelEvent;
import com.aisafe.model.AircraftModel;
import com.aisafe.model.Capacity;
import com.aisafe.model.MediaImage;
import com.aisafe.repository.AircraftModelRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterAircraftModelUseCase {

    private final AircraftModelRepository repository;
    private final AircraftEventPublisher eventPublisher;

    public RegisterAircraftModelUseCase(AircraftModelRepository repository,
                                        AircraftEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public AircraftModel execute(String modelId, String modelName, String manufacturer, int maxCapacity,
                                 double fuelCapacity, double maxRange, double cruisingSpeed,
                                 String imageUrl, String imageDescription) {

        if (repository.findById(modelId).isPresent()) {
            throw new DuplicateResourceException("O modelo com ID " + modelId + " já existe no sistema!");
        }

        AircraftModel model = new AircraftModel();
        model.setModelId(modelId);
        model.setModelName(modelName);
        model.setManufacturer(manufacturer);
        model.setMaxCapacity(new Capacity(maxCapacity));
        model.setFuelCapacity(fuelCapacity);
        model.setMaxRange(maxRange);
        model.setCruisingSpeed(cruisingSpeed);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            MediaImage image = new MediaImage();
            image.setImageUrl(imageUrl);
            image.setDescription(imageDescription);
            model.setImage(image);
        }

        AircraftModel saved = repository.save(model);

        eventPublisher.publishModel(new AircraftModelEvent(
                "CREATED",
                saved.getModelId(),
                saved.getModelName(),
                saved.getManufacturer(),
                maxCapacity,
                saved.getFuelCapacity(),
                saved.getMaxRange(),
                saved.getCruisingSpeed()
        ));

        return saved;
    }
}