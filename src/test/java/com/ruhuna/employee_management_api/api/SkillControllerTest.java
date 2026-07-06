package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.db.SkillRepository;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.viewModel.SkillViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
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
        SkillViewModel viewModel = new SkillViewModel(10L, "Java", List.of());

        when(skillRepository.findAll()).thenReturn(Collections.singletonList(skill));
        when(mapper.convertToSkillViewModel(skill)).thenReturn(viewModel);

        List<SkillViewModel> result = skillController.getAll();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).description());
    }

    @Test
    public void testGetById_Success() {
        Skill skill = new Skill("Java");
        SkillViewModel viewModel = new SkillViewModel(10L, "Java", List.of());

        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(mapper.convertToSkillViewModel(skill)).thenReturn(viewModel);

        SkillViewModel result = skillController.getById(10L);

        assertNotNull(result);
        assertEquals("Java", result.description());
    }

    @Test
    public void testGetById_NotFound() {
        when(skillRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> skillController.getById(10L));
    }

    @Test
    public void testSave_Success() throws ValidationException {
        SkillViewModel viewModel = new SkillViewModel(null, "Java", List.of());
        Skill skill = new Skill("Java");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(mapper.convertToSkill(viewModel)).thenReturn(skill);
        when(mapper.convertToSkillViewModel(skill)).thenReturn(viewModel);

        SkillViewModel result = skillController.save(viewModel, bindingResult);

        assertNotNull(result);
        assertEquals("Java", result.description());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    public void testSave_ValidationError() {
        SkillViewModel viewModel = new SkillViewModel(null, "Java", List.of());
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(ValidationException.class, () -> skillController.save(viewModel, bindingResult));
    }

    @Test
    public void testDelete() {
        skillController.delete(10L);
        verify(skillRepository, times(1)).deleteById(10L);
    }
}
