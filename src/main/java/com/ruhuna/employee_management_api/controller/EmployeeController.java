package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.mapper.EmployeeMapper;
import com.ruhuna.employee_management_api.repository.EmployeeRepository;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.dto.EmployeeDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin
public class EmployeeController {
    private EmployeeRepository employeeRepository;
    private SkillRepository skillRepository;
    private EmployeeMapper employeeMapper;
    private EntityManager em;

    public EmployeeController(EmployeeRepository employeeRepository, SkillRepository skillRepository, EmployeeMapper employeeMapper, EntityManager em){
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.employeeMapper = employeeMapper;
        this.em = em;
    }

    @GetMapping("/all")
    public List<EmployeeDto> getAll(){
        return employeeRepository.findAll()
                .stream()
                .map(employee -> this.employeeMapper.toDto(employee))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EmployeeDto getById(@PathVariable long id){
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null){
            throw new EntityNotFoundException();
        }
        return this.employeeMapper.toDto(employee);
    }

    @PostMapping
    public EmployeeDto save(@Valid @RequestBody EmployeeDto dto) {

        Employee employee;
        if(dto.id() != null){
            employee = employeeRepository.findById(dto.id()).orElse(null);
            if(employee == null){
                throw new EntityNotFoundException();
            }
        }else{
            employee = new Employee();
        }

        Set<Skill> skills = dto.skills().stream()
                .map(skillDto -> {
                    Skill skill = skillRepository.findByDescription(skillDto.description());
                    if (skill == null) {
                        throw new EntityNotFoundException();
                    }
                    return skill;
                }).collect(Collectors.toSet());

        this.employeeMapper.updateEmployeeFromDto(employee, dto, skills);
        this.employeeRepository.save(employee);

        return this.employeeMapper.toDto(employee);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        this.employeeRepository.deleteById(id);
    }
}
