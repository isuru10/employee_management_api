package com.ruhuna.employee_management_api.service;

import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.mapper.EmployeeMapper;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.repository.EmployeeRepository;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

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
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeDto getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        return employeeMapper.toDto(employee);
    }

    public EmployeeDto save(EmployeeDto dto) {
        Employee employee;
        if (dto.id() != null) {
            employee = employeeRepository.findById(dto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        } else {
            employee = new Employee();
        }

        Set<Skill> skills = dto.skills().stream()
                .map(skillDto -> {
                    Skill skill = skillRepository.findByDescription(skillDto.description());
                    if (skill == null) {
                        throw new EntityNotFoundException("Skill not found: " + skillDto.description());
                    }
                    return skill;
                }).collect(Collectors.toSet());

        employeeMapper.updateEmployeeFromDto(employee, dto, skills);
        employeeRepository.save(employee);
        return employeeMapper.toDto(employee);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
