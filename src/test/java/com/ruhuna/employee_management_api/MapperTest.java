package com.ruhuna.employee_management_api;

import com.ruhuna.employee_management_api.db.EmployeeRepository;
import com.ruhuna.employee_management_api.db.SkillRepository;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.viewModel.EmployeeViewModel;
import com.ruhuna.employee_management_api.viewModel.SkillViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MapperTest {

    private Mapper mapper;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    public void setUp() {
        mapper = new Mapper(employeeRepository, skillRepository);
    }

    @Test
    public void testConvertToEmployeeViewModel() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        employee.setId(1L);

        Skill skill = new Skill("Java");
        skill.setId(10L);
        employee.getSkills().add(skill);

        EmployeeViewModel viewModel = mapper.convertToEmployeeViewModel(employee);

        assertEquals(employee.getId(), viewModel.getId());
        assertEquals(employee.getName(), viewModel.getName());
        assertEquals(employee.getEmail(), viewModel.getEmail());
        assertEquals(employee.getDob(), viewModel.getDob());
        assertEquals(1, viewModel.getSkills().size());
        assertEquals(skill.getId(), viewModel.getSkills().get(0).getId());
        assertEquals(skill.getDescription(), viewModel.getSkills().get(0).getDescription());
    }

    @Test
    public void testConvertToSkillViewModel() {
        Skill skill = new Skill("Java");
        skill.setId(10L);

        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        employee.setId(1L);
        skill.getEmployees().add(employee);

        SkillViewModel viewModel = mapper.convertToSkillViewModel(skill);

        assertEquals(skill.getId(), viewModel.getId());
        assertEquals(skill.getDescription(), viewModel.getDescription());
        assertEquals(1, viewModel.getEmployees().size());
        assertEquals(employee.getId(), viewModel.getEmployees().get(0).getId());
        assertEquals(employee.getName(), viewModel.getEmployees().get(0).getName());
    }

    @Test
    public void testConvertToEmployee_NewEmployee() {
        EmployeeViewModel viewModel = new EmployeeViewModel();
        viewModel.setName("Jane Doe");
        viewModel.setEmail("jane@example.com");
        viewModel.setDob(new Date());

        SkillViewModel skillViewModel = new SkillViewModel("Spring");
        viewModel.setSkills(Collections.singletonList(skillViewModel));

        Skill skill = new Skill("Spring");
        when(skillRepository.findByDescription("Spring")).thenReturn(skill);

        Employee employee = mapper.convertToEmployee(viewModel);

        assertNull(employee.getId());
        assertEquals(viewModel.getName(), employee.getName());
        assertEquals(viewModel.getEmail(), employee.getEmail());
        assertEquals(viewModel.getDob(), employee.getDob());
        assertEquals(1, employee.getSkills().size());
        assertTrue(employee.getSkills().contains(skill));
    }

    @Test
    public void testConvertToEmployee_ExistingEmployee() {
        EmployeeViewModel viewModel = new EmployeeViewModel();
        viewModel.setId(1L);
        viewModel.setName("Jane Doe");
        viewModel.setEmail("jane@example.com");
        viewModel.setDob(new Date());

        Employee existingEmployee = new Employee("Old Name", "old@example.com", new Date());
        existingEmployee.setId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));

        SkillViewModel skillViewModel = new SkillViewModel("Spring");
        viewModel.setSkills(Collections.singletonList(skillViewModel));

        Skill skill = new Skill("Spring");
        when(skillRepository.findByDescription("Spring")).thenReturn(skill);

        Employee employee = mapper.convertToEmployee(viewModel);

        assertNotNull(employee.getId());
        assertEquals(1L, (long) employee.getId());
        assertEquals(viewModel.getName(), employee.getName());
        assertEquals(viewModel.getEmail(), employee.getEmail());
        assertEquals(1, employee.getSkills().size());
    }

    @Test
    public void testConvertToSkill_NewSkill() {
        SkillViewModel viewModel = new SkillViewModel("React");

        Skill skill = mapper.convertToSkill(viewModel);

        assertNull(skill.getId());
        assertEquals("React", skill.getDescription());
    }

    @Test
    public void testConvertToSkill_ExistingSkill() {
        SkillViewModel viewModel = new SkillViewModel("React");
        viewModel.setId(10L);

        Skill existingSkill = new Skill("Old React");
        existingSkill.setId(10L);

        when(skillRepository.findById(10L)).thenReturn(Optional.of(existingSkill));

        Skill skill = mapper.convertToSkill(viewModel);

        assertEquals(10L, (long) skill.getId());
        assertEquals("React", skill.getDescription());
    }
}
