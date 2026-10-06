package com.aisafe.application.dto;

public class AircraftOperationalHoursDTO {
    private String registrationNumber;
    private String modelId;
    private double totalOperationalHours;

    public AircraftOperationalHoursDTO(String registrationNumber, String modelId, double totalOperationalHours) {
        this.registrationNumber = registrationNumber;
        this.modelId = modelId;
        this.totalOperationalHours = totalOperationalHours;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public String getModelId() { return modelId; }
    public double getTotalOperationalHours() { return totalOperationalHours; }
}