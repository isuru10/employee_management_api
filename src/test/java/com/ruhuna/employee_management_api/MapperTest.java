package com.ruhuna.employee_management_api;

import com.ruhuna.employee_management_api.mapper.EmployeeMapper;
import com.ruhuna.employee_management_api.mapper.EmployeeMapperImpl;
import com.ruhuna.employee_management_api.mapper.SkillMapper;
import com.ruhuna.employee_management_api.mapper.SkillMapperImpl;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.dto.SkillDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MapperTest {

    private EmployeeMapper employeeMapper;
    private SkillMapper skillMapper;

    @BeforeEach
    public void setUp() {
        employeeMapper = new EmployeeMapperImpl();
        skillMapper = new SkillMapperImpl();
    }

    @Test
    public void testConvertToEmployeeDto() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        employee.setId(1L);

        Skill skill = new Skill("Java");
        skill.setId(10L);
        employee.getSkills().add(skill);

        EmployeeDto dto = employeeMapper.toDto(employee);

        assertEquals(employee.getId(), dto.id());
        assertEquals(employee.getName(), dto.name());
        assertEquals(employee.getEmail(), dto.email());
        assertEquals(employee.getDob(), dto.dob());
        assertEquals(1, dto.skills().size());
        assertEquals(skill.getId(), dto.skills().get(0).id());
        assertEquals(skill.getDescription(), dto.skills().get(0).description());
    }

    @Test
    public void testConvertToSkillDto() {
        Skill skill = new Skill("Java");
        skill.setId(10L);

        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        employee.setId(1L);
        skill.getEmployees().add(employee);

        SkillDto dto = skillMapper.toDto(skill);

        assertEquals(skill.getId(), dto.id());
        assertEquals(skill.getDescription(), dto.description());
        assertEquals(1, dto.employees().size());
        assertEquals(employee.getId(), dto.employees().get(0).id());
        assertEquals(employee.getName(), dto.employees().get(0).name());
    }

    @Test
    public void testUpdateEmployeeFromDto_NewEmployee() {
        SkillDto skillDto = new SkillDto("Spring");
        EmployeeDto dto = new EmployeeDto(
                null,
                "Jane Doe",
                "jane@example.com",
                new Date(),
                Collections.singletonList(skillDto)
        );

        Skill skill = new Skill("Spring");
        Employee employee = new Employee();

        employeeMapper.updateEmployeeFromDto(employee, dto, Set.of(skill));

        assertNull(employee.getId());
        assertEquals(dto.name(), employee.getName());
        assertEquals(dto.email(), employee.getEmail());
        assertEquals(dto.dob(), employee.getDob());
        assertEquals(1, employee.getSkills().size());
        assertTrue(employee.getSkills().contains(skill));
    }

    @Test
    public void testUpdateEmployeeFromDto_ExistingEmployee() {
        SkillDto skillDto = new SkillDto("Spring");
        EmployeeDto dto = new EmployeeDto(
                1L,
                "Jane Doe",
                "jane@example.com",
                new Date(),
                Collections.singletonList(skillDto)
        );

        Employee existingEmployee = new Employee("Old Name", "old@example.com", new Date());
        existingEmployee.setId(1L);
        Skill skill = new Skill("Spring");

        employeeMapper.updateEmployeeFromDto(existingEmployee, dto, Set.of(skill));

        assertNotNull(existingEmployee.getId());
        assertEquals(1L, (long) existingEmployee.getId());
        assertEquals(dto.name(), existingEmployee.getName());
        assertEquals(dto.email(), existingEmployee.getEmail());
        assertEquals(1, existingEmployee.getSkills().size());
    }

    @Test
    public void testUpdateSkillFromDto_NewSkill() {
        SkillDto dto = new SkillDto("React");
        Skill skill = new Skill();

        skillMapper.updateSkillFromDto(skill, dto);

        assertNull(skill.getId());
        assertEquals("React", skill.getDescription());
    }

    @Test
    public void testUpdateSkillFromDto_ExistingSkill() {
        SkillDto dto = new SkillDto(10L, "React", List.of());
        Skill existingSkill = new Skill("Old React");
        existingSkill.setId(10L);

        skillMapper.updateSkillFromDto(existingSkill, dto);

        assertEquals(10L, (long) existingSkill.getId());
        assertEquals("React", existingSkill.getDescription());
    }
}
