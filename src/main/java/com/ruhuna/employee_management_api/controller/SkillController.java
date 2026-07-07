package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.dto.SkillDto;
import com.ruhuna.employee_management_api.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService){
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<List<SkillDto>> getAll(){
        return ResponseEntity.ok(skillService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(skillService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SkillDto> create(@Valid @RequestBody SkillDto dto) {
        // Enforce null ID on creation
        SkillDto dtoToSave = new SkillDto(null, dto.description(), dto.employees());
        SkillDto saved = skillService.save(dtoToSave);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDto> update(@PathVariable Long id, @Valid @RequestBody SkillDto dto) {
        // Enforce path variable ID consistency in the DTO payload
        SkillDto dtoToSave = new SkillDto(id, dto.description(), dto.employees());
        SkillDto updated = skillService.save(dtoToSave);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
