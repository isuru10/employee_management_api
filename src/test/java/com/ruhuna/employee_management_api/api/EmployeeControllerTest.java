package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.db.EmployeeRepository;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.viewModel.EmployeeViewModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmployeeControllerTest {

    private EmployeeController employeeController;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private Mapper mapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        employeeController = new EmployeeController(employeeRepository, mapper, null);
    }

    @Test
    public void testGetAll() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        EmployeeViewModel viewModel = new EmployeeViewModel("John Doe", new Date());

        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));
        when(mapper.convertToEmployeeViewModel(employee)).thenReturn(viewModel);

        List<EmployeeViewModel> result = employeeController.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    public void testGetById_Success() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        EmployeeViewModel viewModel = new EmployeeViewModel("John Doe", new Date());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.convertToEmployeeViewModel(employee)).thenReturn(viewModel);

        EmployeeViewModel result = employeeController.getById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        employeeController.getById(1L);
    }

    @Test
    public void testSave_Success() throws ValidationException {
        EmployeeViewModel viewModel = new EmployeeViewModel("Jane Doe", new Date());
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(mapper.convertToEmployee(viewModel)).thenReturn(employee);
        when(mapper.convertToEmployeeViewModel(employee)).thenReturn(viewModel);

        EmployeeViewModel result = employeeController.save(viewModel, bindingResult);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test(expected = ValidationException.class)
    public void testSave_ValidationError() throws ValidationException {
        EmployeeViewModel viewModel = new EmployeeViewModel("Jane Doe", new Date());
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);

        employeeController.save(viewModel, bindingResult);
    }

    @Test
    public void testDelete() {
        employeeController.delete(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
