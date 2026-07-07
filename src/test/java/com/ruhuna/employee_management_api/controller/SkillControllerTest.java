package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.service.SkillService;
import com.ruhuna.employee_management_api.dto.SkillDto;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillService skillService;

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
        SkillDto dto = new SkillDto(10L, "Java", List.of());

        when(skillService.getAll()).thenReturn(Collections.singletonList(dto));

        ResponseEntity<List<SkillDto>> response = skillController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<SkillDto> result = response.getBody();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).description());
        verify(skillService, times(1)).getAll();
    }

    @Test
    public void testGetById() {
        SkillDto dto = new SkillDto(10L, "Java", List.of());

        when(skillService.getById(10L)).thenReturn(dto);

        ResponseEntity<SkillDto> response = skillController.getById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        SkillDto result = response.getBody();
        assertNotNull(result);
        assertEquals("Java", result.description());
        verify(skillService, times(1)).getById(10L);
    }

    @Test
    public void testCreate() {
        SkillDto inputDto = new SkillDto(null, "Java", List.of());
        SkillDto savedDto = new SkillDto(10L, "Java", List.of());

        when(skillService.save(any(SkillDto.class))).thenReturn(savedDto);

        ResponseEntity<SkillDto> response = skillController.create(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        SkillDto result = response.getBody();
        assertNotNull(result);
        assertEquals("Java", result.description());
        assertEquals(10L, result.id());

        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.toString().endsWith("/10"));

        verify(skillService, times(1)).save(any(SkillDto.class));
    }

    @Test
    public void testUpdate() {
        SkillDto inputDto = new SkillDto(null, "Java", List.of());
        SkillDto savedDto = new SkillDto(10L, "Java", List.of());

        when(skillService.save(any(SkillDto.class))).thenReturn(savedDto);

        ResponseEntity<SkillDto> response = skillController.update(10L, inputDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        SkillDto result = response.getBody();
        assertNotNull(result);
        assertEquals("Java", result.description());
        assertEquals(10L, result.id());

        verify(skillService, times(1)).save(any(SkillDto.class));
    }

    @Test
    public void testDelete() {
        ResponseEntity<Void> response = skillController.delete(10L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(skillService, times(1)).delete(10L);
    }
}
