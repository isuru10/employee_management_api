package com.ruhuna.employee_management_api.service;

import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.mapper.EmployeeMapper;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.repository.EmployeeRepository;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, SkillRepository skillRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.employeeMapper = employeeMapper;
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAll() {
        logger.debug("Retrieving all employees");
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeDto getById(Long id) {
        logger.debug("Retrieving employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Employee not found with ID: {}", id);
                    return new EntityNotFoundException("Employee not found");
                });
        return employeeMapper.toDto(employee);
    }

    public EmployeeDto save(EmployeeDto dto) {
        logger.info("Saving employee: {}", dto);
        Employee employee;
        if (dto.id() != null) {
            employee = employeeRepository.findById(dto.id())
                    .orElseThrow(() -> {
                        logger.warn("Employee not found with ID: {}", dto.id());
                        return new EntityNotFoundException("Employee not found");
                    });
        } else {
            employee = new Employee();
        }

        List<Long> skillIds = dto.skills().stream()
                .map(skillDto -> {
                    if (skillDto.id() == null) {
                        logger.warn("Skill ID is null");
                        throw new EntityNotFoundException("Skill ID must not be null");
                    }
                    return skillDto.id();
                })
                .collect(Collectors.toList());

        List<Skill> foundSkills = skillRepository.findAllById(skillIds);

        if (foundSkills.size() < skillIds.size()) {
            List<Long> foundIds = foundSkills.stream().map(Skill::getId).toList();
            List<Long> missingIds = skillIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            logger.warn("Skill(s) not found with ID(s): {}", missingIds);
            throw new EntityNotFoundException("Skill(s) not found with ID(s): " + missingIds);
        }

        Set<Skill> skills = foundSkills.stream().collect(Collectors.toSet());

        employeeMapper.updateEmployeeFromDto(employee, dto, skills);
        employeeRepository.save(employee);
        logger.info("Employee saved successfully with ID: {}", employee.getId());
        return employeeMapper.toDto(employee);
    }

    public void delete(Long id) {
        logger.info("Deleting employee with ID: {}", id);
        employeeRepository.deleteById(id);
    }
}
