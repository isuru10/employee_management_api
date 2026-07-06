# Agent Operating Manual (AGENTS.md)

Welcome, coding agent. This file describes the development environment, guidelines, and execution workflows for the **Employee Management API** modernization project.

---

## 1. Project Overview
The **Employee Management API** is a Spring Boot application that manages employees and their skills. It uses Maven for building, PostgreSQL as the database, and JPA/Hibernate for persistence.

The current goal is to **modernize the repository to Java 21 and Spring Boot 3.x**.

---

## 2. Directory Layout
Ensure you maintain this structure. Do not create ad-hoc files in the root folder.
```
.
├── AGENTS.md               # This operating manual
├── CLAUDE.md               # Standard command definitions (build/test/style)
├── init.sh                 # Environment setup and health check script
├── feature_list.json       # Structured progress and scope tracker
├── claude-progress.md      # Chronological session and handoff log
├── pom.xml                 # Maven project configuration
├── src/
│   ├── main/
│   │   ├── java/com/ruhuna/employee_management_api/
│   │   │   ├── EmployeeManagementApiApplication.java # App entry point
│   │   │   ├── Mapper.java                           # DTO Mapping utility
│   │   │   ├── api/                                  # REST Controllers
│   │   │   ├── db/                                   # Repository & Seeders
│   │   │   ├── model/                                # JPA Entities (Employee, Skill)
│   │   │   └── viewModel/                            # DTOs / View Models
│   │   └── resources/
│   │       └── application.properties                # Spring configs
│   └── test/
│       └── java/com/ruhuna/employee_management_api/   # JUnit Tests
```

---

## 3. Development Workflow & Commands
Always use the Maven Wrapper (`./mvnw`) to manage the application lifecycle:

- **Clean and Compile:** `./mvnw clean compile`
- **Run Tests:** `./mvnw test`
- **Package Application:** `./mvnw package -DskipTests`
- **Run Locally:** `./mvnw spring-boot:run`
- **Initialize & Health Check:** `bash init.sh`

---

## 4. Hard Constraints & Coding Rules
1. **Scope Boundaries:** Work on exactly one feature from `feature_list.json` at a time.
2. **Inward Dependencies (Clean Architecture):** Core model classes and business rules must not depend on outer frameworks or web delivery code. Use cases and mapping logic should be pure.
3. **Jakarta EE Namespace Migration:** Change all old `javax.persistence.*` and `javax.validation.*` imports to `jakarta.persistence.*` and `jakarta.validation.*` once upgraded to Spring Boot 3.x.
4. **Immutable DTOs (Java Records):** Convert old mutable view model classes (e.g., `EmployeeViewModel.java`, `SkillViewModel.java`) to Java Records to reduce boilerplate and ensure immutability.
5. **Pure Mapping Logic:** Decouple `Mapper.java` from repositories. Do not perform database calls (`findById`, etc.) inside the mapping methods. Database reads/writes must be handled in the controller/service layer.
6. **Validation and Global Exception Handling:** Use `@Valid` or `@Validated` annotations in controllers to validate DTOs. Avoid checking `BindingResult` manually. Handle validation errors globally using a `@RestControllerAdvice` class returning RFC 7807 `ProblemDetail` payloads.
7. **JUnit 5 Migration:** Write all new tests using JUnit Jupiter (JUnit 5). Migrate existing tests by removing `@RunWith(SpringRunner.class)` and updating imports from `org.junit.Test` to `org.junit.jupiter.api.Test`.
8. **No Placeholders:** Write fully functional code. Do not use comments like `// TODO: implement this later`.
9. **Verification Gate:** Every session code modification must be verified by running `./mvnw test`. Never declare victory early.
10. **State Updates:** Before finishing your turn, update `feature_list.json` and `claude-progress.md` with your progress and current state.
