package com.aisafe.service;

import com.aisafe.model.MaintenanceRecord;
import com.aisafe.repository.MaintenanceRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaintenanceQueryServiceTest {

    @Mock
    private MaintenanceRecordRepository recordRepository;

    @InjectMocks
    private MaintenanceQueryService queryService;

    // US 218: Teste de Pesquisa com Paginação
    @Test
    void testSearchRecords_US218() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MaintenanceRecord> page = new PageImpl<>(Collections.emptyList());

        when(recordRepository.searchRecords(any(), any(), any(), any(), eq(pageable)))
                .thenReturn(page);

        Page<MaintenanceRecord> result = queryService.searchRecords(null, null, null, null, pageable);

        assertNotNull(result);
        verify(recordRepository, times(1)).searchRecords(any(), any(), any(), any(), eq(pageable));
    }

    // US 219: Teste de Atividades em Curso
    @Test
    void testGetOngoingActivities_US219() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MaintenanceRecord> page = new PageImpl<>(Collections.emptyList());

        when(recordRepository.findByStatus("IN_PROGRESS", pageable))
                .thenReturn(page);

        Page<MaintenanceRecord> result = queryService.getOngoingActivities(pageable);

        assertNotNull(result);
        verify(recordRepository, times(1)).findByStatus("IN_PROGRESS", pageable);
    }

    // US 220: Teste de Custo por Modelo
    @Test
    void testGetMaintenanceCostPerModel_US220() {
        Map<String, Object> mockMap = new java.util.HashMap<>();
        mockMap.put("model", "A320");
        mockMap.put("totalCost", 5000.0);
        List<Map<String, Object>> mockList = new java.util.ArrayList<>();
        mockList.add(mockMap);

        when(recordRepository.getMaintenanceCostPerModel()).thenReturn(mockList);

        List<Map<String, Object>> result = queryService.getCostsPerModel();

        assertNotNull(result);
        assertEquals("A320", result.get(0).get("model"));
        assertEquals(5000.0, (Double) result.get(0).get("totalCost"), 0.001);
        verify(recordRepository, times(1)).getMaintenanceCostPerModel();
    }

    // US 221: Teste de Tempo Turnaround
    @Test
    void testGetAverageTurnaroundTime_US221() {
        when(recordRepository.getAverageTurnaroundTimePerModel())
                .thenReturn(List.of(Map.of("model", "A320", "avgTurnaroundDays", 4.0)));

        List<Map<String, Object>> result = queryService.getAverageTurnaroundTime();

        assertNotNull(result);
        assertEquals(4.0, result.get(0).get("avgTurnaroundDays"));
        verify(recordRepository, times(1)).getAverageTurnaroundTimePerModel();
    }
}