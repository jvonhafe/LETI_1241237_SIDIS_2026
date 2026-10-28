package com.aisafe.controller;

import com.aisafe.model.MaintenanceTemplate;
import com.aisafe.repository.MaintenanceTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaintenanceTemplateControllerTest {

    @Mock
    private MaintenanceTemplateRepository templateRepository;

    @InjectMocks
    private MaintenanceTemplateController templateController;

    @Test
    void createTemplate_ReturnsSavedTemplate() {
        MaintenanceTemplate template = new MaintenanceTemplate();
        // Usar os setters corretos de acordo com a tua classe
        template.setTemplateName("Check A");
        template.setTemplateType("INSPECTION");

        when(templateRepository.save(any(MaintenanceTemplate.class))).thenReturn(template);

        MaintenanceTemplate savedTemplate = templateRepository.save(template);

        assertNotNull(savedTemplate);
        // Usar os getters corretos de acordo com a tua classe
        assertEquals("Check A", savedTemplate.getTemplateName());
        assertEquals("INSPECTION", savedTemplate.getTemplateType());

        verify(templateRepository, times(1)).save(template);
    }
}