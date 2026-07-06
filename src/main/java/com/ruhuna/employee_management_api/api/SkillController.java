package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.db.SkillRepository;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.viewModel.SkillViewModel;
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
    private Mapper mapper;

    public SkillController(SkillRepository skillRepository, Mapper mapper){
        this.skillRepository = skillRepository;
        this.mapper = mapper;
    }

    @GetMapping("/all")
    public List<SkillViewModel> getAll(){
        return this.skillRepository.findAll().stream()
                .map(skill -> this.mapper.convertToSkillViewModel(skill))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SkillViewModel getById(@PathVariable Long id){
        Skill skill = this.skillRepository.findById(id).orElse(null);

        if(skill == null){
            throw new EntityNotFoundException();
        }

        return this.mapper.convertToSkillViewModel(skill);
    }

    @PostMapping
    public SkillViewModel save(@Valid @RequestBody SkillViewModel viewModel) {

        Skill skill;
        if(viewModel.id() != null){
            skill = this.skillRepository.findById(viewModel.id()).orElse(null);
            if(skill == null){
                throw new EntityNotFoundException();
            }
        }else{
            skill = new Skill();
        }

        this.mapper.updateSkillFromViewModel(skill, viewModel);
        this.skillRepository.save(skill);

        return this.mapper.convertToSkillViewModel(skill);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        this.skillRepository.deleteById(id);
    }
}
