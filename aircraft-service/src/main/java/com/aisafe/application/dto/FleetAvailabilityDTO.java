package com.aisafe.application.dto;

import java.util.List;

public class FleetAvailabilityDTO {
    private long totalAircraft;
    private long availableAircraft;
    private long underMaintenanceAircraft;
    private double availabilityRate;
    private List<AircraftStatusDTO> aircraftList;

    public FleetAvailabilityDTO(long totalAircraft, long availableAircraft, long underMaintenanceAircraft, double availabilityRate, List<AircraftStatusDTO> aircraftList) {
        this.totalAircraft = totalAircraft;
        this.availableAircraft = availableAircraft;
        this.underMaintenanceAircraft = underMaintenanceAircraft;
        this.availabilityRate = availabilityRate;
        this.aircraftList = aircraftList;
    }

    public long getTotalAircraft() { return totalAircraft; }
    public long getAvailableAircraft() { return availableAircraft; }
    public long getUnderMaintenanceAircraft() { return underMaintenanceAircraft; }
    public double getAvailabilityRate() { return availabilityRate; }
    public List<AircraftStatusDTO> getAircraftList() { return aircraftList; }

    public static class AircraftStatusDTO {
        private String registration;
        private String modelId;
        private String status;

        public AircraftStatusDTO(String registration, String modelId, String status) {
            this.registration = registration;
            this.modelId = modelId;
            this.status = status;
        }

        public String getRegistration() { return registration; }
        public String getModelId() { return modelId; }
        public String getStatus() { return status; }
    }
}