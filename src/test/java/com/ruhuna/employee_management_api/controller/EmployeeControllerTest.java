package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.service.EmployeeService;
import com.ruhuna.employee_management_api.dto.EmployeeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @Test
    public void testGetAll() {
        EmployeeDto dto = new EmployeeDto(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeService.getAll()).thenReturn(Collections.singletonList(dto));

        List<EmployeeDto> result = employeeController.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).name());
        verify(employeeService, times(1)).getAll();
    }

    @Test
    public void testGetById() {
        EmployeeDto dto = new EmployeeDto(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeService.getById(1L)).thenReturn(dto);

        EmployeeDto result = employeeController.getById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(employeeService, times(1)).getById(1L);
    }

    @Test
    public void testSave() {
        EmployeeDto dto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of());

        when(employeeService.save(any(EmployeeDto.class))).thenReturn(dto);

        EmployeeDto result = employeeController.save(dto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.name());
        verify(employeeService, times(1)).save(any(EmployeeDto.class));
    }

    @Test
    public void testDelete() {
        employeeController.delete(1L);
        verify(employeeService, times(1)).delete(1L);
    }
}
