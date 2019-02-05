package com.ruhuna.employee_management_api.viewModel;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SkillViewModel {
    private Long id;

    @NotNull
    private String description;

    private List<EmployeeViewModel> employees;

    public SkillViewModel(){
        this.employees = new ArrayList<>();
    }

    public SkillViewModel(String description){
        this();
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<EmployeeViewModel> getEmployees() {
        return employees;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmployees(List<EmployeeViewModel> employees) {
        this.employees = employees;
    }
}
