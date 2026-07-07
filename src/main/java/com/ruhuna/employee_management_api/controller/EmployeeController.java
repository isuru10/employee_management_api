package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAll(){
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable long id){
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeDto dto) {
        // Enforce null ID on creation (server-generated IDs)
        EmployeeDto dtoToSave = new EmployeeDto(null, dto.name(), dto.email(), dto.dob(), dto.skills());
        EmployeeDto saved = employeeService.save(dtoToSave);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(@PathVariable long id, @Valid @RequestBody EmployeeDto dto) {
        // Enforce path variable ID consistency in the DTO payload
        EmployeeDto dtoToSave = new EmployeeDto(id, dto.name(), dto.email(), dto.dob(), dto.skills());
        EmployeeDto updated = employeeService.save(dtoToSave);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
