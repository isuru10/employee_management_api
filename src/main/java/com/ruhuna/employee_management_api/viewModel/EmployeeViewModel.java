package com.ruhuna.employee_management_api.viewModel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public record EmployeeViewModel(
    Long id,
    @NotNull String name,
    @Email String email,
    Date dob,
    List<SkillViewModel> skills
) {
    public EmployeeViewModel {
        if (skills == null) {
            skills = List.of();
        }
    }

    public EmployeeViewModel() {
        this(null, null, null, null, List.of());
    }

    public EmployeeViewModel(String name, Date dob) {
        this(null, name, null, dob, List.of());
    }
}
