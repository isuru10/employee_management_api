package com.ruhuna.employee_management_api.viewModel;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SkillViewModel(
    Long id,
    @NotNull String description,
    List<EmployeeViewModel> employees
) {
    public SkillViewModel {
        if (employees == null) {
            employees = List.of();
        }
    }

    public SkillViewModel() {
        this(null, null, List.of());
    }

    public SkillViewModel(String description) {
        this(null, description, List.of());
    }
}
