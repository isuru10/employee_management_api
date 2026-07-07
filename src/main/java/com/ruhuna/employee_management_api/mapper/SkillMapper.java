package com.ruhuna.employee_management_api.mapper;

import com.ruhuna.employee_management_api.dto.SkillDto;
import com.ruhuna.employee_management_api.dto.EmployeeDto;
import com.ruhuna.employee_management_api.model.Skill;
import com.ruhuna.employee_management_api.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(target = "employees", expression = "java(mapEmployeesToDtos(skill.getEmployees()))")
    SkillDto toDto(Skill skill);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Skill toEntity(SkillDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntityFromDto(SkillDto dto, @MappingTarget Skill skill);

    default void updateSkillFromDto(Skill skill, SkillDto dto) {
        updateEntityFromDto(dto, skill);
    }

    default List<EmployeeDto> mapEmployeesToDtos(Set<Employee> employees) {
        if (employees == null) return List.of();
        return employees.stream()
                .map(employee -> new EmployeeDto(
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getDob(),
                        List.of()
                ))
                .collect(Collectors.toList());
    }
}
