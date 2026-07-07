package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.service.SkillService;
import com.ruhuna.employee_management_api.dto.SkillDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

    @Test
    public void testGetAll() {
        SkillDto dto = new SkillDto(10L, "Java", List.of());

        when(skillService.getAll()).thenReturn(Collections.singletonList(dto));

        List<SkillDto> result = skillController.getAll();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).description());
        verify(skillService, times(1)).getAll();
    }

    @Test
    public void testGetById() {
        SkillDto dto = new SkillDto(10L, "Java", List.of());

        when(skillService.getById(10L)).thenReturn(dto);

        SkillDto result = skillController.getById(10L);

        assertNotNull(result);
        assertEquals("Java", result.description());
        verify(skillService, times(1)).getById(10L);
    }

    @Test
    public void testSave() {
        SkillDto dto = new SkillDto(null, "Java", List.of());

        when(skillService.save(any(SkillDto.class))).thenReturn(dto);

        SkillDto result = skillController.save(dto);

        assertNotNull(result);
        assertEquals("Java", result.description());
        verify(skillService, times(1)).save(any(SkillDto.class));
    }

    @Test
    public void testDelete() {
        skillController.delete(10L);
        verify(skillService, times(1)).delete(10L);
    }
}
