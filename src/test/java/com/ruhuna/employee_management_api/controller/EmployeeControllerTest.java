package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.repository.EmployeeRepository;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.dto.SkillDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    private EmployeeController employeeController;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private Mapper mapper;

    @BeforeEach
    public void setUp() {
        employeeController = new EmployeeController(employeeRepository, skillRepository, mapper, null);
    }

    @Test
    public void testGetAll() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        EmployeeDto dto = new EmployeeDto(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));
        when(mapper.convertToEmployeeDto(employee)).thenReturn(dto);

        List<EmployeeDto> result = employeeController.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).name());
    }

    @Test
    public void testGetById_Success() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        EmployeeDto dto = new EmployeeDto(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.convertToEmployeeDto(employee)).thenReturn(dto);

        EmployeeDto result = employeeController.getById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
    }

    @Test
    public void testGetById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> employeeController.getById(1L));
    }

    @Test
    public void testSave_Success() {
        EmployeeDto dto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of());
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());

        when(mapper.updateEmployeeFromDto(any(Employee.class), any(EmployeeDto.class), anySet())).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            emp.setName("Jane Doe");
            emp.setEmail("jane@example.com");
            return emp;
        });
        when(mapper.convertToEmployeeDto(any(Employee.class))).thenReturn(dto);

        EmployeeDto result = employeeController.save(dto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.name());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_ExistingEmployee_Success() {
        EmployeeDto dto = new EmployeeDto(1L, "Jane Doe", "jane@example.com", new Date(), List.of());
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());
        employee.setId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.updateEmployeeFromDto(any(Employee.class), any(EmployeeDto.class), anySet())).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            return emp;
        });
        when(mapper.convertToEmployeeDto(any(Employee.class))).thenReturn(dto);

        EmployeeDto result = employeeController.save(dto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_ExistingEmployee_NotFound() {
        EmployeeDto dto = new EmployeeDto(1L, "Jane Doe", "jane@example.com", new Date(), List.of());

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeController.save(dto));
    }

    @Test
    public void testSave_WithSkills_Success() {
        SkillDto skillDto = new SkillDto(1L, "Java", List.of());
        EmployeeDto employeeDto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of(skillDto));
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());
        Skill skill = new Skill("Java");
        skill.setId(1L);

        when(skillRepository.findByDescription("Java")).thenReturn(skill);
        when(mapper.updateEmployeeFromDto(any(Employee.class), any(EmployeeDto.class), anySet())).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            return emp;
        });
        when(mapper.convertToEmployeeDto(any(Employee.class))).thenReturn(employeeDto);

        EmployeeDto result = employeeController.save(employeeDto);

        assertNotNull(result);
        verify(skillRepository, times(1)).findByDescription("Java");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_WithSkills_NotFound() {
        SkillDto skillDto = new SkillDto(1L, "Java", List.of());
        EmployeeDto employeeDto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of(skillDto));

        when(skillRepository.findByDescription("Java")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> employeeController.save(employeeDto));
    }

    @Test
    public void testDelete() {
        employeeController.delete(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
