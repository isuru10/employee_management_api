package com.ruhuna.employee_management_api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SkillDto(
    Long id,
    @NotNull String description,
    List<EmployeeDto> employees
) {
    public SkillDto {
        if (employees == null) {
            employees = List.of();
        }
    }

    public SkillDto() {
        this(null, null, List.of());
    }

    public SkillDto(String description) {
        this(null, description, List.of());
    }
}
