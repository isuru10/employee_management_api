package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.db.EmployeeRepository;
import com.ruhuna.employee_management_api.db.SkillRepository;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.viewModel.EmployeeViewModel;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private EmployeeRepository employeeRepository;
    private SkillRepository skillRepository;
    private Mapper mapper;

    public EmployeeController(EmployeeRepository employeeRepository, SkillRepository skillRepository, Mapper mapper){
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.mapper = mapper;
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
            return null;
        }
        return this.mapper.convertToEmployeeViewModel(employee);
    }

    @PostMapping
    public Employee save(@RequestBody EmployeeViewModel viewModel, BindingResult bindingResult) throws ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException("Employee");
        }

        Employee employee = this.mapper.convertToEmployee(viewModel);
        this.employeeRepository.save(employee);

        return employee;
    }

}
