package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.service.EmployeeService;
import com.ruhuna.employee_management_api.dto.EmployeeDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
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

    @BeforeEach
    public void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    public void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void testGetAll() {
        EmployeeDto dto = new EmployeeDto(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeService.getAll()).thenReturn(Collections.singletonList(dto));

        ResponseEntity<List<EmployeeDto>> response = employeeController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<EmployeeDto> result = response.getBody();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).name());
        verify(employeeService, times(1)).getAll();
    }

    @Test
    public void testGetById() {
        EmployeeDto dto = new EmployeeDto(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeService.getById(1L)).thenReturn(dto);

        ResponseEntity<EmployeeDto> response = employeeController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        EmployeeDto result = response.getBody();
        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(employeeService, times(1)).getById(1L);
    }

    @Test
    public void testCreate() {
        EmployeeDto inputDto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of());
        EmployeeDto savedDto = new EmployeeDto(1L, "Jane Doe", "jane@example.com", new Date(), List.of());

        when(employeeService.save(any(EmployeeDto.class))).thenReturn(savedDto);

        ResponseEntity<EmployeeDto> response = employeeController.create(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        EmployeeDto result = response.getBody();
        assertNotNull(result);
        assertEquals("Jane Doe", result.name());
        assertEquals(1L, result.id());
        
        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.toString().endsWith("/1"));

        verify(employeeService, times(1)).save(any(EmployeeDto.class));
    }

    @Test
    public void testUpdate() {
        EmployeeDto inputDto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of());
        EmployeeDto savedDto = new EmployeeDto(1L, "Jane Doe", "jane@example.com", new Date(), List.of());

        when(employeeService.save(any(EmployeeDto.class))).thenReturn(savedDto);

        ResponseEntity<EmployeeDto> response = employeeController.update(1L, inputDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        EmployeeDto result = response.getBody();
        assertNotNull(result);
        assertEquals("Jane Doe", result.name());
        assertEquals(1L, result.id());

        verify(employeeService, times(1)).save(any(EmployeeDto.class));
    }

    @Test
    public void testDelete() {
        ResponseEntity<Void> response = employeeController.delete(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(employeeService, times(1)).delete(1L);
    }
}
