package com.ruhuna.employee_management_api.service;

import com.ruhuna.employee_management_api.dto.SkillDto;
import com.ruhuna.employee_management_api.mapper.SkillMapper;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getAll() {
        return skillRepository.findAll().stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SkillDto getById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found"));
        return skillMapper.toDto(skill);
    }

    public SkillDto save(SkillDto dto) {
        Skill skill;
        if (dto.id() != null) {
            skill = skillRepository.findById(dto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Skill not found"));
        } else {
            skill = new Skill();
        }

        skillMapper.updateSkillFromDto(skill, dto);
        skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    public void delete(Long id) {
        skillRepository.deleteById(id);
    }
}
