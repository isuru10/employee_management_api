package com.ruhuna.employee_management_api;

import com.ruhuna.employee_management_api.db.EmployeeRepository;
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

    public Mapper(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public EmployeeViewModel convertToEmployeeViewModel(Employee employee){

        EmployeeViewModel viewModel = new EmployeeViewModel();

        viewModel.setId(employee.getId());
        viewModel.setName(employee.getName());
        viewModel.setDob(employee.getDob());
        viewModel.setEmail(employee.getEmail());

        List<SkillViewModel> skills = employee.getSkills().stream().map(skill -> convertToSkillViewModel(skill)).collect(Collectors.toList());
        viewModel.setSkills(skills);

        return viewModel;
    }

    public SkillViewModel convertToSkillViewModel(Skill skill){
        SkillViewModel viewModel = new SkillViewModel(skill.getDescription());
        viewModel.setId(skill.getId());

        return viewModel;
    }

    public Employee convertToEmployee(EmployeeViewModel viewModel){
        Employee employee = new Employee();

        employee.setName(viewModel.getName());
        employee.setDob(viewModel.getDob());
        employee.setEmail(viewModel.getEmail());

        Set<Skill> skills = viewModel.getSkills().stream().map(viewModel1 -> convertToSkill(viewModel1)).collect(Collectors.toSet());

        employee.setSkills(skills);

        return employee;
    }

    public Skill convertToSkill(SkillViewModel viewModel){
        Skill skill = new Skill();

        skill.setDescription(viewModel.getDescription());
        if(viewModel.getId() != null){
            skill.setId(viewModel.getId());
        }


        return skill;
    }
}
