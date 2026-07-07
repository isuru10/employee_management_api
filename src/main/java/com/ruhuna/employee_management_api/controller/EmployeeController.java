package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @GetMapping("/all")
    public List<EmployeeDto> getAll(){
        return employeeService.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeDto getById(@PathVariable long id){
        return employeeService.getById(id);
    }

    @PostMapping
    public EmployeeDto save(@Valid @RequestBody EmployeeDto dto) {
        return employeeService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        employeeService.delete(id);
    }
}
