package com.aisafe.application.dto;

public class UpdateAircraftModelDTO {
    private Integer maxCapacity;
    private Double fuelCapacity;
    private Double maxRange;
    private Double cruisingSpeed;

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Double getFuelCapacity() { return fuelCapacity; }
    public void setFuelCapacity(Double fuelCapacity) { this.fuelCapacity = fuelCapacity; }

    public Double getMaxRange() { return maxRange; }
    public void setMaxRange(Double maxRange) { this.maxRange = maxRange; }

    public Double getCruisingSpeed() { return cruisingSpeed; }
    public void setCruisingSpeed(Double cruisingSpeed) { this.cruisingSpeed = cruisingSpeed; }
}