package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.dto.SkillDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    private SkillController skillController;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private Mapper mapper;

    @BeforeEach
    public void setUp() {
        skillController = new SkillController(skillRepository, mapper);
    }

    @Test
    public void testGetAll() {
        Skill skill = new Skill("Java");
        SkillDto dto = new SkillDto(10L, "Java", List.of());

        when(skillRepository.findAll()).thenReturn(Collections.singletonList(skill));
        when(mapper.convertToSkillDto(skill)).thenReturn(dto);

        List<SkillDto> result = skillController.getAll();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).description());
    }

    @Test
    public void testGetById_Success() {
        Skill skill = new Skill("Java");
        SkillDto dto = new SkillDto(10L, "Java", List.of());

        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(mapper.convertToSkillDto(skill)).thenReturn(dto);

        SkillDto result = skillController.getById(10L);

        assertNotNull(result);
        assertEquals("Java", result.description());
    }

    @Test
    public void testGetById_NotFound() {
        when(skillRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> skillController.getById(10L));
    }

    @Test
    public void testSave_Success() {
        SkillDto dto = new SkillDto(null, "Java", List.of());
        Skill skill = new Skill("Java");

        when(mapper.updateSkillFromDto(any(Skill.class), any(SkillDto.class))).thenAnswer(invocation -> {
            Skill s = invocation.getArgument(0);
            s.setDescription("Java");
            return s;
        });
        when(mapper.convertToSkillDto(any(Skill.class))).thenReturn(dto);

        SkillDto result = skillController.save(dto);

        assertNotNull(result);
        assertEquals("Java", result.description());
        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    public void testDelete() {
        skillController.delete(10L);
        verify(skillRepository, times(1)).deleteById(10L);
    }
}
