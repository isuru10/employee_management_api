package com.ruhuna.employee_management_api.service;

import com.ruhuna.employee_management_api.mapper.SkillMapper;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.dto.SkillDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @Test
    public void testGetAll() {
        Skill skill = new Skill("Java");
        SkillDto dto = new SkillDto(10L, "Java", List.of());

        when(skillRepository.findAll()).thenReturn(Collections.singletonList(skill));
        when(skillMapper.toDto(skill)).thenReturn(dto);

        List<SkillDto> result = skillService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).description());
        verify(skillRepository, times(1)).findAll();
    }

    @Test
    public void testGetById_Success() {
        Skill skill = new Skill("Java");
        SkillDto dto = new SkillDto(10L, "Java", List.of());

        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(skillMapper.toDto(skill)).thenReturn(dto);

        SkillDto result = skillService.getById(10L);

        assertNotNull(result);
        assertEquals("Java", result.description());
        verify(skillRepository, times(1)).findById(10L);
    }

    @Test
    public void testGetById_NotFound() {
        when(skillRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> skillService.getById(10L));
    }

    @Test
    public void testSave_Success() {
        SkillDto dto = new SkillDto(null, "Java", List.of());
        Skill skill = new Skill("Java");

        doAnswer(invocation -> {
            Skill s = invocation.getArgument(0);
            s.setDescription("Java");
            return null;
        }).when(skillMapper).updateSkillFromDto(any(Skill.class), any(SkillDto.class));

        when(skillMapper.toDto(any(Skill.class))).thenReturn(dto);

        SkillDto result = skillService.save(dto);

        assertNotNull(result);
        assertEquals("Java", result.description());
        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    public void testDelete() {
        skillService.delete(10L);
        verify(skillRepository, times(1)).deleteById(10L);
    }
}
