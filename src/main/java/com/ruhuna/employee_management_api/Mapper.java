package com.ruhuna.employee_management_api;

import com.ruhuna.employee_management_api.db.EmployeeRepository;
import com.ruhuna.employee_management_api.db.SkillRepository;
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
    private EmployeeRepository employeeRepository;
    private SkillRepository skillRepository;

    public Mapper(EmployeeRepository employeeRepository, SkillRepository skillRepository){
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
    }

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

    public Employee convertToEmployee(EmployeeViewModel viewModel){
        Employee employee;

        if(viewModel.id() != null){
            employee = employeeRepository.findById(viewModel.id()).get();
        }else{
            employee = new Employee();
        }

        employee.setName(viewModel.name());
        employee.setDob(viewModel.dob());
        employee.setEmail(viewModel.email());

        Set<Skill> skills = viewModel.skills().stream()
                .map(viewModel1 -> {
                    Skill skill = skillRepository.findByDescription(viewModel1.description());
                    skill.getEmployees().add(employee);
                    return skill;
                }).collect(Collectors.toSet());

        employee.setSkills(skills);
        return employee;
    }

    public Skill convertToSkill(SkillViewModel viewModel){
        Skill skill;

        if(viewModel.id() != null){
            skill = skillRepository.findById(viewModel.id()).get();
            skill.setDescription(viewModel.description());
        }else{
            skill = new Skill(viewModel.description());
        }

        return skill;
    }
}
