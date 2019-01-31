package com.ruhuna.employee_management_api.db;

import com.ruhuna.employee_management_api.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SkillRepository extends JpaRepository<Skill, Long> {
    Skill findByDescription(String description);
}
