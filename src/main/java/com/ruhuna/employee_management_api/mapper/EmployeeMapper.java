package com.ruhuna.employee_management_api.mapper;

import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.dto.SkillDto;
import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "skills", expression = "java(mapSkillsToDtos(employee.getSkills()))")
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "id", ignore = true)
    Employee toEntity(EmployeeDto dto);

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(EmployeeDto dto, @MappingTarget Employee employee);

    default void updateEmployeeFromDto(Employee employee, EmployeeDto dto, Set<Skill> skills) {
        updateEntityFromDto(dto, employee);
        if (skills != null) {
            // Link skills bidirectionally
            skills.forEach(skill -> skill.getEmployees().add(employee));
            employee.setSkills(skills);
        }
    }

    default List<SkillDto> mapSkillsToDtos(Set<Skill> skills) {
        if (skills == null) return List.of();
        return skills.stream()
                .map(skill -> new SkillDto(skill.getId(), skill.getDescription(), List.of()))
                .collect(Collectors.toList());
    }
}
