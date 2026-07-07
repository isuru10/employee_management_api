package com.ruhuna.employee_management_api.service;

import com.ruhuna.employee_management_api.mapper.EmployeeMapper;
import com.ruhuna.employee_management_api.repository.EmployeeRepository;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.dto.SkillDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    public void setupLogger() {
        logger = (Logger) LoggerFactory.getLogger(EmployeeService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    public void teardownLogger() {
        logger.detachAppender(listAppender);
    }

    @Test
    public void testGetAll() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        EmployeeDto dto = new EmployeeDto(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));
        when(employeeMapper.toDto(employee)).thenReturn(dto);

        List<EmployeeDto> result = employeeService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).name());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testGetById_Success() {
        Employee employee = new Employee("John Doe", "john@example.com", new Date());
        EmployeeDto dto = new EmployeeDto(1L, "John Doe", "john@example.com", new Date(), List.of());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(dto);

        EmployeeDto result = employeeService.getById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> employeeService.getById(1L));
    }

    @Test
    public void testSave_NewEmployee_Success() {
        EmployeeDto dto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of());
        Employee employee = new Employee("Jane Doe", "jane@example.com", new Date());

        doAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            emp.setName("Jane Doe");
            emp.setEmail("jane@example.com");
            return null;
        }).when(employeeMapper).updateEmployeeFromDto(any(Employee.class), any(EmployeeDto.class), anySet());

        when(employeeMapper.toDto(any(Employee.class))).thenReturn(dto);

        EmployeeDto result = employeeService.save(dto);

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
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(dto);

        EmployeeDto result = employeeService.save(dto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_ExistingEmployee_NotFound() {
        EmployeeDto dto = new EmployeeDto(1L, "Jane Doe", "jane@example.com", new Date(), List.of());

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.save(dto));
    }

    @Test
    public void testDelete() {
        employeeService.delete(1L);
        verify(employeeRepository, times(1)).deleteById(1L);

        // Verify log output
        List<ILoggingEvent> logs = listAppender.list;
        assertFalse(logs.isEmpty());
        assertEquals(Level.INFO, logs.get(0).getLevel());
        assertTrue(logs.get(0).getFormattedMessage().contains("Deleting employee with ID: 1"));
    }

    @Test
    public void testSave_WithSkills_Success() {
        SkillDto skillDto = new SkillDto(10L, "Java", List.of());
        EmployeeDto employeeDto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of(skillDto));
        Skill skill = new Skill();
        skill.setId(10L);
        skill.setDescription("Java");

        when(skillRepository.findAllById(anyList())).thenReturn(List.of(skill));
        
        doAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            emp.setName("Jane Doe");
            return null;
        }).when(employeeMapper).updateEmployeeFromDto(any(Employee.class), any(EmployeeDto.class), anySet());

        when(employeeMapper.toDto(any(Employee.class))).thenReturn(employeeDto);

        EmployeeDto result = employeeService.save(employeeDto);

        assertNotNull(result);
        verify(skillRepository, times(1)).findAllById(anyList());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testSave_WithSkills_NullId() {
        SkillDto skillDto = new SkillDto(null, "Java", List.of());
        EmployeeDto employeeDto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of(skillDto));

        assertThrows(EntityNotFoundException.class, () -> employeeService.save(employeeDto));
    }

    @Test
    public void testSave_WithSkills_NotFoundId() {
        SkillDto skillDto = new SkillDto(10L, "Java", List.of());
        EmployeeDto employeeDto = new EmployeeDto(null, "Jane Doe", "jane@example.com", new Date(), List.of(skillDto));

        when(skillRepository.findAllById(anyList())).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> employeeService.save(employeeDto));
    }
}
