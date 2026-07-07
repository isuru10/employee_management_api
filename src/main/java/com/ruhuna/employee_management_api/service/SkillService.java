package com.ruhuna.employee_management_api.service;

import com.ruhuna.employee_management_api.dto.SkillDto;
import com.ruhuna.employee_management_api.mapper.SkillMapper;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SkillService {

    private static final Logger logger = LoggerFactory.getLogger(SkillService.class);

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getAll() {
        logger.debug("Retrieving all skills");
        return skillRepository.findAll().stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SkillDto getById(Long id) {
        logger.debug("Retrieving skill with ID: {}", id);
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Skill not found with ID: {}", id);
                    return new EntityNotFoundException("Skill not found");
                });
        return skillMapper.toDto(skill);
    }

    public SkillDto save(SkillDto dto) {
        logger.info("Saving skill: {}", dto);
        Skill skill;
        if (dto.id() != null) {
            skill = skillRepository.findById(dto.id())
                    .orElseThrow(() -> {
                        logger.warn("Skill not found with ID: {}", dto.id());
                        return new EntityNotFoundException("Skill not found");
                    });
        } else {
            skill = new Skill();
        }

        skillMapper.updateSkillFromDto(skill, dto);
        skillRepository.save(skill);
        logger.info("Skill saved successfully with ID: {}", skill.getId());
        return skillMapper.toDto(skill);
    }

    public void delete(Long id) {
        logger.info("Deleting skill with ID: {}", id);
        skillRepository.deleteById(id);
    }
}
