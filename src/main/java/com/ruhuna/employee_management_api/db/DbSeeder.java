package com.ruhuna.employee_management_api.db;

import com.ruhuna.employee_management_api.model.Employee;
import com.ruhuna.employee_management_api.model.Skill;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class DbSeeder implements CommandLineRunner {
    private EmployeeRepository employeeRepository;
    private SkillRepository skillRepository;

    public DbSeeder(EmployeeRepository employeeRepository, SkillRepository skillRepository){
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        this.employeeRepository.deleteAll();
        this.skillRepository.deleteAll();

        Skill skill1 = new Skill("Java");
        Skill skill2 = new Skill("Angular");
        Skill skill3 = new Skill("Postgres");

        Employee employee = new Employee("Isuru", "isuru@gmail.com", new SimpleDateFormat("dd/MM/yyyy").parse("18/08/1995"));
        Employee employee2 = new Employee("Bathiya", "bathiya@gmail.com", new SimpleDateFormat("dd/MM/yyyy").parse("18/01/1994"));

        employee.getSkills().add(skill1);
        employee.getSkills().add(skill2);
        employee.getSkills().add(skill3);

        employee2.getSkills().add(skill1);
        employee2.getSkills().add(skill2);
        employee2.getSkills().add(skill3);

        skill1.getEmployees().add(employee);
        skill2.getEmployees().add(employee);
        skill3.getEmployees().add(employee);

        skill1.getEmployees().add(employee2);
        skill2.getEmployees().add(employee2);
        skill3.getEmployees().add(employee2);

        employeeRepository.save(employee);
        employeeRepository.save(employee2);

        employee2.setName("Lochana");
        employeeRepository.save(employee2);
    }
}
