package com.ruhuna.employee_management_api.repository;

import com.ruhuna.employee_management_api.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
