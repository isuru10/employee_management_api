package com.ruhuna.employee_management_api.api;

import com.ruhuna.employee_management_api.Mapper;
import com.ruhuna.employee_management_api.db.SkillRepository;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.viewModel.SkillViewModel;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;
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
    public SkillViewModel save(@RequestBody SkillViewModel viewModel, BindingResult bindingResult) throws ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException("Skill");
        }

        Skill skill = this.mapper.convertToSkill(viewModel);
        this.skillRepository.save(skill);

        return this.mapper.convertToSkillViewModel(skill);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        this.skillRepository.deleteById(id);
    }
}
