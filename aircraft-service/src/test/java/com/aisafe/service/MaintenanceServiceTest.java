package com.aisafe.service;

import com.aisafe.model.MaintenanceRecord;
import com.aisafe.repository.AircraftRepository;
import com.aisafe.repository.MaintenanceRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaintenanceServiceTest {

    @Mock
    private MaintenanceRecordRepository recordRepository;

    @Mock
    private AircraftRepository aircraftRepository;

    @InjectMocks
    private MaintenanceService maintenanceService;

    private MaintenanceRecord record1;

    @BeforeEach
    void setUp() {
        record1 = new MaintenanceRecord();
        record1.setId("REC-1");
        record1.setAircraftRegistration("CS-TPA");
    }

    @Test
    void recordsForAircraft_ReturnsList() {
        when(aircraftRepository.existsById(any())).thenReturn(true);
        when(recordRepository.findByAircraftRegistration("CS-TPA"))
                .thenReturn(List.of(record1));

        List<MaintenanceRecord> result = maintenanceService.recordsForAircraft("CS-TPA");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recordRepository, times(1)).findByAircraftRegistration("CS-TPA");
    }

    @Test
    void totalHoursForAircraft_ReturnsTotal() {
        when(aircraftRepository.existsById(any())).thenReturn(true);
        when(recordRepository.totalMaintenanceHoursByAircraft("CS-TPA")).thenReturn(10.0);

        Double total = maintenanceService.totalHoursForAircraft("CS-TPA");

        assertEquals(10.0, total);
        verify(recordRepository, times(1)).totalMaintenanceHoursByAircraft("CS-TPA");
    }
}