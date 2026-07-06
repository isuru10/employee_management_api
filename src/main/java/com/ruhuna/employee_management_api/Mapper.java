package com.ruhuna.employee_management_api;

import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.viewModel.EmployeeViewModel;
import com.ruhuna.employee_management_api.viewModel.SkillViewModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public EmployeeViewModel convertToEmployeeViewModel(Employee employee){
        List<SkillViewModel> skills = employee.getSkills().stream()
                .map(skill -> new SkillViewModel(skill.getId(), skill.getDescription(), List.of()))
                .collect(Collectors.toList());

        return new EmployeeViewModel(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDob(),
                skills
        );
    }

    public SkillViewModel convertToSkillViewModel(Skill skill){
        Set<Employee> employees = skill.getEmployees();

        List<EmployeeViewModel> employeeViewModels = employees.stream()
                .map(employee -> new EmployeeViewModel(
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getDob(),
                        List.of()
                ))
                .collect(Collectors.toList());

        return new SkillViewModel(
                skill.getId(),
                skill.getDescription(),
                employeeViewModels
        );
    }

    public Employee updateEmployeeFromViewModel(Employee employee, EmployeeViewModel viewModel, Set<Skill> skills){
        employee.setName(viewModel.name());
        employee.setDob(viewModel.dob());
        employee.setEmail(viewModel.email());

        // Link skills bidirectionally
        skills.forEach(skill -> skill.getEmployees().add(employee));
        employee.setSkills(skills);

        return employee;
    }

    public Skill updateSkillFromViewModel(Skill skill, SkillViewModel viewModel){
        skill.setDescription(viewModel.description());
        return skill;
    }
}
