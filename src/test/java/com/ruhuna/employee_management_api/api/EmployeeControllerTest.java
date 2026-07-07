package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
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
        EmployeeViewModel viewModel = new EmployeeViewModel(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));
        when(mapper.convertToEmployeeViewModel(employee)).thenReturn(viewModel);

        List<EmployeeViewModel> result = employeeController.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).name());
    }

    @Test
    public void testGetById_Success() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        EmployeeViewModel viewModel = new EmployeeViewModel(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.convertToEmployeeViewModel(employee)).thenReturn(viewModel);

        EmployeeViewModel result = employeeController.getById(1L);

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
        EmployeeViewModel viewModel = new EmployeeViewModel(null, "Jane Doe", "jane@example.com", new Date(), List.of());
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());

        when(mapper.updateEmployeeFromViewModel(any(Employee.class), any(EmployeeViewModel.class), anySet())).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            emp.setName("Jane Doe");
            emp.setEmail("jane@example.com");
            return emp;
        });
        when(mapper.convertToEmployeeViewModel(any(Employee.class))).thenReturn(viewModel);

        EmployeeViewModel result = employeeController.save(viewModel);

        assertNotNull(result);
        assertEquals("Jane Doe", result.name());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_ExistingEmployee_Success() {
        EmployeeViewModel viewModel = new EmployeeViewModel(1L, "Jane Doe", "jane@example.com", new Date(), List.of());
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());
        employee.setId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.updateEmployeeFromViewModel(any(Employee.class), any(EmployeeViewModel.class), anySet())).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            return emp;
        });
        when(mapper.convertToEmployeeViewModel(any(Employee.class))).thenReturn(viewModel);

        EmployeeViewModel result = employeeController.save(viewModel);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_ExistingEmployee_NotFound() {
        EmployeeViewModel viewModel = new EmployeeViewModel(1L, "Jane Doe", "jane@example.com", new Date(), List.of());

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeController.save(viewModel));
    }

    @Test
    public void testSave_WithSkills_Success() {
        SkillViewModel skillViewModel = new SkillViewModel(1L, "Java", List.of());
        EmployeeViewModel viewModel = new EmployeeViewModel(null, "Jane Doe", "jane@example.com", new Date(), List.of(skillViewModel));
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());
        Skill skill = new Skill("Java");
        skill.setId(1L);

        when(skillRepository.findByDescription("Java")).thenReturn(skill);
        when(mapper.updateEmployeeFromViewModel(any(Employee.class), any(EmployeeViewModel.class), anySet())).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            return emp;
        });
        when(mapper.convertToEmployeeViewModel(any(Employee.class))).thenReturn(viewModel);

        EmployeeViewModel result = employeeController.save(viewModel);

        assertNotNull(result);
        verify(skillRepository, times(1)).findByDescription("Java");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_WithSkills_NotFound() {
        SkillViewModel skillViewModel = new SkillViewModel(1L, "Java", List.of());
        EmployeeViewModel viewModel = new EmployeeViewModel(null, "Jane Doe", "jane@example.com", new Date(), List.of(skillViewModel));

        when(skillRepository.findByDescription("Java")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> employeeController.save(viewModel));
    }

    @Test
    public void testDelete() {
        employeeController.delete(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
