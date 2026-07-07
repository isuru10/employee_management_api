package com.ruhuna.employee_management_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public record EmployeeDto(
    Long id,
    @NotNull String name,
    @Email String email,
    Date dob,
    List<SkillDto> skills
) {
    public EmployeeDto {
        if (skills == null) {
            skills = List.of();
        }
    }

    public EmployeeDto() {
        this(null, null, null, null, List.of());
    }

    public EmployeeDto(String name, Date dob) {
        this(null, name, null, dob, List.of());
    }
}
