package com.ruhuna.employee_management_api.integration;

import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.dto.SkillDto;
import com.ruhuna.employee_management_api.repository.EmployeeRepository;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import com.ruhuna.employee_management_api.model.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EmployeeControllerIntegrationTest {

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
    public void testCreateEmployeeWithSkillSuccess() {
        // 1. Create a skill first
        Skill skill = new Skill("Java");
        skill = skillRepository.save(skill);

        // 2. Build employee dto linking skill by ID only
        SkillDto skillDto = new SkillDto(skill.getId(), null, List.of());
        EmployeeDto employeeDto = new EmployeeDto(null, "Isuru", "isuru@example.com", new Date(), List.of(skillDto));

        // 3. Post to create employee
        ResponseEntity<EmployeeDto> response = restTemplate.postForEntity("/api/employees", employeeDto, EmployeeDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        EmployeeDto body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.id());
        assertEquals("Isuru", body.name());
        assertEquals(1, body.skills().size());
        assertEquals("Java", body.skills().get(0).description());
        assertEquals(skill.getId(), body.skills().get(0).id());

        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.toString().contains("/api/employees/" + body.id()));
    }

    @Test
    public void testCreateEmployeeWithInvalidSkillNotFound() {
        SkillDto skillDto = new SkillDto(9999L, null, List.of());
        EmployeeDto employeeDto = new EmployeeDto(null, "Isuru", "isuru@example.com", new Date(), List.of(skillDto));

        ResponseEntity<String> response = restTemplate.postForEntity("/api/employees", employeeDto, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Skill(s) not found with ID(s)"));
    }
}
