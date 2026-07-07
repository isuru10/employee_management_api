package com.ruhuna.employee_management_api;

import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.dto.SkillDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public EmployeeDto convertToEmployeeDto(Employee employee){
        List<SkillDto> skills = employee.getSkills().stream()
                .map(skill -> new SkillDto(skill.getId(), skill.getDescription(), List.of()))
                .collect(Collectors.toList());

        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDob(),
                skills
        );
    }

    public SkillDto convertToSkillDto(Skill skill){
        Set<Employee> employees = skill.getEmployees();

        List<EmployeeDto> employeeDtos = employees.stream()
                .map(employee -> new EmployeeDto(
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getDob(),
                        List.of()
                ))
                .collect(Collectors.toList());

        return new SkillDto(
                skill.getId(),
                skill.getDescription(),
                employeeDtos
        );
    }

    public Employee updateEmployeeFromDto(Employee employee, EmployeeDto dto, Set<Skill> skills){
        employee.setName(dto.name());
        employee.setDob(dto.dob());
        employee.setEmail(dto.email());

        // Link skills bidirectionally
        skills.forEach(skill -> skill.getEmployees().add(employee));
        employee.setSkills(skills);

        return employee;
    }

    public Skill updateSkillFromDto(Skill skill, SkillDto dto){
        skill.setDescription(dto.description());
        return skill;
    }
}
