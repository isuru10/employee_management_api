# Agent Operating Manual (AGENTS.md)

This file provides guidance to AI coding agents (such as Claude Code, Antigravity, Cursor) when working with code in this repository.

## Project Overview
The **Employee Management API** is a Spring Boot application that manages employees and their skills. It uses Maven for building, PostgreSQL as the database, and JPA/Hibernate for persistence.

The current goal is to **modernize the repository to Java 21 and Spring Boot 3.x**.

---

## 1. Commands

Use the Maven Wrapper (`./mvnw`) to manage the application lifecycle:

```sh
# Clean the project build
./mvnw clean

# Compile the project classes
./mvnw compile

# Run all unit and integration tests
./mvnw test

# Package the application into a runnable jar (skipping tests for speed)
./mvnw package -DskipTests

# Run the application locally
./mvnw spring-boot:run
```

---

## 2. Repository Structure

- `pom.xml` — Maven project configuration, dependencies, and properties.
- `src/main/java/com/ruhuna/employee_management_api/`
  - `EmployeeManagementApiApplication.java` — Application entry point.
  - `model/` — JPA database entities (`Employee.java`, `Skill.java`).
  - `db/` — Database repositories and seed data (`DbSeeder.java`).
  - `viewModel/` — Data Transfer Objects (DTOs) used at the API boundary.
  - `api/` — REST controllers exposing endpoints.
  - `Mapper.java` — Utility class to map between entities and view models.
- `src/main/resources/`
  - `application.properties` — Spring application configurations.
- `src/test/java/com/ruhuna/employee_management_api/` — JUnit test classes.

---

## 3. Development Guidelines & Standards

When modernizing and making changes to the codebase, follow these rules:

1. **Inward Dependencies (Clean Architecture)**:
   - Core model classes and business rules must not depend on outer frameworks or web delivery code.
   - Use cases and mapping logic should be pure.

2. **Jakarta EE Namespace Migration**:
   - Change all old `javax.persistence.*` and `javax.validation.*` imports to `jakarta.persistence.*` and `jakarta.validation.*` once upgraded to Spring Boot 3.x.

3. **Immutable DTOs (Java Records)**:
   - Convert old mutable view model classes (e.g., `EmployeeViewModel.java`, `SkillViewModel.java`) to Java Records to reduce boilerplate and ensure immutability.

4. **Pure Mapping Logic**:
   - Decouple `Mapper.java` from repositories. Do not perform database calls (`findById`, etc.) inside the mapping methods.
   - Database reads and writes must be handled in the controller or service layer.

5. **Validation and Global Exception Handling**:
   - Use `@Valid` or `@Validated` annotations in controllers to validate input DTOs.
   - Avoid checking `BindingResult` manually in controllers. Handle validation errors globally using a `@RestControllerAdvice` class returning RFC 7807 `ProblemDetail` payloads.

6. **JUnit 5 Migration**:
   - Write all new tests using JUnit Jupiter (JUnit 5).
   - Migrate existing tests by removing `@RunWith(SpringRunner.class)` and updating imports from `org.junit.Test` to `org.junit.jupiter.api.Test`.

---

## 4. Agentic Workflow (Harness Engineering)

To work effectively and safely in this repository, agents must adhere to the following Augmented Coding Patterns:

1. **Check Alignment (No Unvalidated Leaps)**:
   - Before executing large changes, surface your understanding. Show the plan or architecture first.
   - Ask clarifying questions instead of guessing (avoid Compliance Bias / "Tell Me a Lie").
   - Act as an **Active Partner** — push back if a request doesn't make sense.

2. **Chain of Small Steps**:
   - Break down complex tasks into small, independently verifiable steps.
   - Workflow: Step → Verify → Commit. Do not pile assumptions on top of unverified assumptions.

3. **Fast Feedback Loop**:
   - The test runner is the signal. Keep the loop short (20-60s) to minimize drift.
   - Write failing tests first (Red → Green → Refactor). 
   - When using BDD/Gherkin, write declarative behavior, not UI-coupled clicks. AI drafts the tests; human reviews numbers and edge cases.

4. **Review Structure, Not Just Syntax**:
   - Protect the primary value of software: its *softness* (ability to change).
   - Ensure clean functions, inward-pointing dependencies, and use cases separated from the delivery mechanism.
   - **Defer Decisions**: Frameworks and Databases are details. Keep the domain pure.

5. **Apply the Four Quality Lenses**:
   - **Mutation Testing**: Ensure tests actually test behavior, not just coverage.
   - **CRAP Analysis**: Break down high-complexity/low-coverage functions.
   - **Property Testing (Take All Paths)**: Test invariants, not just single examples.
   - **DRY (Extract Knowledge)**: Save discovered rules and decisions to project knowledge documents.

6. **Navigate AI Obstacles & Anti-Patterns**:
   - **Obstacles**: Expect AI to have *Compliance Bias* ("Sure, boss") and *Cannot Learn* (no memory between sessions). Engineer around them using human review and knowledge docs.
   - **Anti-Patterns**: Avoid *Tell Me a Lie* (AI fabricating based on false premises), *Unvalidated Leaps* (assumption chains), and *Silent Misalignment*. Solve these by enforcing the *Check Alignment* pattern and running tests frequently.
