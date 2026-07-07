package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.dto.SkillDto;
import com.ruhuna.employee_management_api.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService){
        this.skillService = skillService;
    }

    @GetMapping("/all")
    public List<SkillDto> getAll(){
        return skillService.getAll();
    }

    @GetMapping("/{id}")
    public SkillDto getById(@PathVariable Long id){
        return skillService.getById(id);
    }

    @PostMapping
    public SkillDto save(@Valid @RequestBody SkillDto dto) {
        return skillService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        skillService.delete(id);
    }
}
