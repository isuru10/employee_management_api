package com.ruhuna.employee_management_api.integration;

import com.ruhuna.employee_management_api.dto.SkillDto;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import com.ruhuna.employee_management_api.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SkillControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SkillRepository skillRepository;

    @BeforeEach
    public void cleanDb() {
        employeeRepository.deleteAll();
        skillRepository.deleteAll();
    }

    @Test
    public void testCreateSkillSuccess() {
        SkillDto skillDto = new SkillDto(null, "Java", List.of());

        ResponseEntity<SkillDto> response = restTemplate.postForEntity("/api/skills", skillDto, SkillDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        SkillDto body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.id());
        assertEquals("Java", body.description());

        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.toString().contains("/api/skills/" + body.id()));
    }
}
