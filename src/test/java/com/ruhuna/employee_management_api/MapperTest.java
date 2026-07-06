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

        assertEquals(employee.getId(), viewModel.id());
        assertEquals(employee.getName(), viewModel.name());
        assertEquals(employee.getEmail(), viewModel.email());
        assertEquals(employee.getDob(), viewModel.dob());
        assertEquals(1, viewModel.skills().size());
        assertEquals(skill.getId(), viewModel.skills().get(0).id());
        assertEquals(skill.getDescription(), viewModel.skills().get(0).description());
    }

    @Test
    public void testConvertToSkillViewModel() {
        Skill skill = new Skill("Java");
        skill.setId(10L);

        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        employee.setId(1L);
        skill.getEmployees().add(employee);

        SkillViewModel viewModel = mapper.convertToSkillViewModel(skill);

        assertEquals(skill.getId(), viewModel.id());
        assertEquals(skill.getDescription(), viewModel.description());
        assertEquals(1, viewModel.employees().size());
        assertEquals(employee.getId(), viewModel.employees().get(0).id());
        assertEquals(employee.getName(), viewModel.employees().get(0).name());
    }

    @Test
    public void testConvertToEmployee_NewEmployee() {
        SkillViewModel skillViewModel = new SkillViewModel("Spring");
        EmployeeViewModel viewModel = new EmployeeViewModel(
                null,
                "Jane Doe",
                "jane@example.com",
                new Date(),
                Collections.singletonList(skillViewModel)
        );

        Skill skill = new Skill("Spring");
        when(skillRepository.findByDescription("Spring")).thenReturn(skill);

        Employee employee = mapper.convertToEmployee(viewModel);

        assertNull(employee.getId());
        assertEquals(viewModel.name(), employee.getName());
        assertEquals(viewModel.email(), employee.getEmail());
        assertEquals(viewModel.dob(), employee.getDob());
        assertEquals(1, employee.getSkills().size());
        assertTrue(employee.getSkills().contains(skill));
    }

    @Test
    public void testConvertToEmployee_ExistingEmployee() {
        SkillViewModel skillViewModel = new SkillViewModel("Spring");
        EmployeeViewModel viewModel = new EmployeeViewModel(
                1L,
                "Jane Doe",
                "jane@example.com",
                new Date(),
                Collections.singletonList(skillViewModel)
        );

        Employee existingEmployee = new Employee("Old Name", "old@example.com", new Date());
        existingEmployee.setId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));

        Skill skill = new Skill("Spring");
        when(skillRepository.findByDescription("Spring")).thenReturn(skill);

        Employee employee = mapper.convertToEmployee(viewModel);

        assertNotNull(employee.getId());
        assertEquals(1L, (long) employee.getId());
        assertEquals(viewModel.name(), employee.getName());
        assertEquals(viewModel.email(), employee.getEmail());
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
        SkillViewModel viewModel = new SkillViewModel(10L, "React", List.of());

        Skill existingSkill = new Skill("Old React");
        existingSkill.setId(10L);

        when(skillRepository.findById(10L)).thenReturn(Optional.of(existingSkill));

        Skill skill = mapper.convertToSkill(viewModel);

        assertEquals(10L, (long) skill.getId());
        assertEquals("React", skill.getDescription());
    }
}
