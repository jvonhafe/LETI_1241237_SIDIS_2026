package com.aisafe.service;

import com.aisafe.cqrs.event.AircraftEventPublisher;
import com.aisafe.cqrs.query.AircraftReadModelRepository;
import com.aisafe.cqrs.query.MaintenanceReadModel;
import com.aisafe.cqrs.query.MaintenanceReadModelRepository;
import com.aisafe.repository.AircraftRepository;
import com.aisafe.repository.MaintenanceRecordRepository;
import com.aisafe.repository.MaintenanceTemplateRepository;
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
    private MaintenanceTemplateRepository templateRepository;

    @Mock
    private AircraftRepository aircraftRepository;

    @Mock
    private AircraftReadModelRepository aircraftReadRepository;

    @Mock
    private MaintenanceReadModelRepository maintenanceReadRepository;

    @Mock
    private AircraftEventPublisher eventPublisher;

    @InjectMocks
    private MaintenanceService maintenanceService;

    @Test
    void recordsForAircraft_ReturnsList() {
        when(aircraftReadRepository.existsById("CS-TPA")).thenReturn(true);
        when(maintenanceReadRepository.findByAircraftRegistration("CS-TPA"))
                .thenReturn(List.of(new MaintenanceReadModel()));

        List<MaintenanceReadModel> result = maintenanceService.recordsForAircraft("CS-TPA");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(maintenanceReadRepository, times(1)).findByAircraftRegistration("CS-TPA");
    }

    @Test
    void totalHoursForAircraft_ReturnsTotal() {
        when(aircraftReadRepository.existsById("CS-TPA")).thenReturn(true);
        when(maintenanceReadRepository.totalMaintenanceHoursByAircraft("CS-TPA"))
                .thenReturn(10.0);

        Double total = maintenanceService.totalHoursForAircraft("CS-TPA");

        assertEquals(10.0, total);
        verify(maintenanceReadRepository, times(1)).totalMaintenanceHoursByAircraft("CS-TPA");
    }
}