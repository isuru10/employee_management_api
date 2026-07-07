package com.ruhuna.employee_management_api.controller;

import com.ruhuna.employee_management_api.mapper.SkillMapper;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.dto.SkillDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin
public class SkillController {
    private SkillRepository skillRepository;
    private SkillMapper skillMapper;

    public SkillController(SkillRepository skillRepository, SkillMapper skillMapper){
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    @GetMapping("/all")
    public List<SkillDto> getAll(){
        return this.skillRepository.findAll().stream()
                .map(skill -> this.skillMapper.toDto(skill))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SkillDto getById(@PathVariable Long id){
        Skill skill = this.skillRepository.findById(id).orElse(null);

        if(skill == null){
            throw new EntityNotFoundException();
        }

        return this.skillMapper.toDto(skill);
    }

    @PostMapping
    public SkillDto save(@Valid @RequestBody SkillDto dto) {

        Skill skill;
        if(dto.id() != null){
            skill = this.skillRepository.findById(dto.id()).orElse(null);
            if(skill == null){
                throw new EntityNotFoundException();
            }
        }else{
            skill = new Skill();
        }

        this.skillMapper.updateSkillFromDto(skill, dto);
        this.skillRepository.save(skill);

        return this.skillMapper.toDto(skill);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        this.skillRepository.deleteById(id);
    }
}
