package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.db.EmployeeRepository;
import com.ruhuna.employee_management_api.db.SkillRepository;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.viewModel.EmployeeViewModel;
import org.springframework.validation.BindingResult;
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
    private Mapper mapper;
    private EntityManager em;

    public EmployeeController(EmployeeRepository employeeRepository, SkillRepository skillRepository, Mapper mapper, EntityManager em){
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.mapper = mapper;
        this.em = em;
    }

    @GetMapping("/all")
    public List<EmployeeViewModel> getAll(){
        return employeeRepository.findAll()
                .stream()
                .map(employee -> this.mapper.convertToEmployeeViewModel(employee))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EmployeeViewModel getById(@PathVariable long id){
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null){
            throw new EntityNotFoundException();
        }
        return this.mapper.convertToEmployeeViewModel(employee);
    }

    @PostMapping
    public EmployeeViewModel save(@RequestBody EmployeeViewModel viewModel, BindingResult bindingResult) throws ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException("Employee");
        }

        Employee employee;
        if(viewModel.id() != null){
            employee = employeeRepository.findById(viewModel.id()).orElse(null);
            if(employee == null){
                throw new EntityNotFoundException();
            }
        }else{
            employee = new Employee();
        }

        Set<Skill> skills = viewModel.skills().stream()
                .map(viewModel1 -> {
                    Skill skill = skillRepository.findByDescription(viewModel1.description());
                    if (skill == null) {
                        throw new EntityNotFoundException();
                    }
                    return skill;
                }).collect(Collectors.toSet());

        this.mapper.updateEmployeeFromViewModel(employee, viewModel, skills);
        this.employeeRepository.save(employee);

        return this.mapper.convertToEmployeeViewModel(employee);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        this.employeeRepository.deleteById(id);
    }
}
