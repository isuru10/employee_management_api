package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.db.EmployeeRepository;
import com.ruhuna.employee_management_api.db.SkillRepository;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.viewModel.EmployeeViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
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
    public void testSave_Success() throws ValidationException {
        EmployeeViewModel viewModel = new EmployeeViewModel(null, "Jane Doe", "jane@example.com", new Date(), List.of());
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(mapper.updateEmployeeFromViewModel(any(Employee.class), any(EmployeeViewModel.class), anySet())).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            emp.setName("Jane Doe");
            emp.setEmail("jane@example.com");
            return emp;
        });
        when(mapper.convertToEmployeeViewModel(any(Employee.class))).thenReturn(viewModel);

        EmployeeViewModel result = employeeController.save(viewModel, bindingResult);

        assertNotNull(result);
        assertEquals("Jane Doe", result.name());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_ValidationError() {
        EmployeeViewModel viewModel = new EmployeeViewModel(null, "Jane Doe", "jane@example.com", new Date(), List.of());
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(ValidationException.class, () -> employeeController.save(viewModel, bindingResult));
    }

    @Test
    public void testDelete() {
        employeeController.delete(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
