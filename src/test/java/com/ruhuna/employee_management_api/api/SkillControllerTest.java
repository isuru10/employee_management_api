package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.db.SkillRepository;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.viewModel.SkillViewModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SkillControllerTest {

    private SkillController skillController;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private Mapper mapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        skillController = new SkillController(skillRepository, mapper);
    }

    @Test
    public void testGetAll() {
        Skill skill = new Skill("Java");
        SkillViewModel viewModel = new SkillViewModel("Java");

        when(skillRepository.findAll()).thenReturn(Collections.singletonList(skill));
        when(mapper.convertToSkillViewModel(skill)).thenReturn(viewModel);

        List<SkillViewModel> result = skillController.getAll();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getDescription());
    }

    @Test
    public void testGetById_Success() {
        Skill skill = new Skill("Java");
        SkillViewModel viewModel = new SkillViewModel("Java");

        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(mapper.convertToSkillViewModel(skill)).thenReturn(viewModel);

        SkillViewModel result = skillController.getById(10L);

        assertNotNull(result);
        assertEquals("Java", result.getDescription());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetById_NotFound() {
        when(skillRepository.findById(10L)).thenReturn(Optional.empty());
        skillController.getById(10L);
    }

    @Test
    public void testSave_Success() throws ValidationException {
        SkillViewModel viewModel = new SkillViewModel("Java");
        Skill skill = new Skill("Java");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(mapper.convertToSkill(viewModel)).thenReturn(skill);
        when(mapper.convertToSkillViewModel(skill)).thenReturn(viewModel);

        SkillViewModel result = skillController.save(viewModel, bindingResult);

        assertNotNull(result);
        assertEquals("Java", result.getDescription());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test(expected = ValidationException.class)
    public void testSave_ValidationError() throws ValidationException {
        SkillViewModel viewModel = new SkillViewModel("Java");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);

        skillController.save(viewModel, bindingResult);
    }

    @Test
    public void testDelete() {
        skillController.delete(10L);
        verify(skillRepository, times(1)).deleteById(10L);
    }
}
