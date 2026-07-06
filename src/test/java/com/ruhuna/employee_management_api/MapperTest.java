package com.ruhuna.employee_management_api;

import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.viewModel.EmployeeViewModel;
import com.ruhuna.employee_management_api.viewModel.SkillViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MapperTest {

    private Mapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new Mapper();
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
    public void testUpdateEmployeeFromViewModel_NewEmployee() {
        SkillViewModel skillViewModel = new SkillViewModel("Spring");
        EmployeeViewModel viewModel = new EmployeeViewModel(
                null,
                "Jane Doe",
                "jane@example.com",
                new Date(),
                Collections.singletonList(skillViewModel)
        );

        Skill skill = new Skill("Spring");
        Employee employee = new Employee();

        mapper.updateEmployeeFromViewModel(employee, viewModel, Set.of(skill));

        assertNull(employee.getId());
        assertEquals(viewModel.name(), employee.getName());
        assertEquals(viewModel.email(), employee.getEmail());
        assertEquals(viewModel.dob(), employee.getDob());
        assertEquals(1, employee.getSkills().size());
        assertTrue(employee.getSkills().contains(skill));
    }

    @Test
    public void testUpdateEmployeeFromViewModel_ExistingEmployee() {
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
        Skill skill = new Skill("Spring");

        mapper.updateEmployeeFromViewModel(existingEmployee, viewModel, Set.of(skill));

        assertNotNull(existingEmployee.getId());
        assertEquals(1L, (long) existingEmployee.getId());
        assertEquals(viewModel.name(), existingEmployee.getName());
        assertEquals(viewModel.email(), existingEmployee.getEmail());
        assertEquals(1, existingEmployee.getSkills().size());
    }

    @Test
    public void testUpdateSkillFromViewModel_NewSkill() {
        SkillViewModel viewModel = new SkillViewModel("React");
        Skill skill = new Skill();

        mapper.updateSkillFromViewModel(skill, viewModel);

        assertNull(skill.getId());
        assertEquals("React", skill.getDescription());
    }

    @Test
    public void testUpdateSkillFromViewModel_ExistingSkill() {
        SkillViewModel viewModel = new SkillViewModel(10L, "React", List.of());
        Skill existingSkill = new Skill("Old React");
        existingSkill.setId(10L);

        mapper.updateSkillFromViewModel(existingSkill, viewModel);

        assertEquals(10L, (long) existingSkill.getId());
        assertEquals("React", existingSkill.getDescription());
    }
}
