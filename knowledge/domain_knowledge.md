# Domain Knowledge & Business Rules

This document serves as the authoritative source of truth for the domain model, business rules, constraints, and validation logic in the **Employee Management API**.

---

## 1. Core Domain Entities & Database Mappings

The application centers around two main entities: `Employee` and `Skill`.

### A. Employee Entity
Represents an employee within the organization.
- **ID (`id`):** `Long`. Primary key. Automatically generated using the `IDENTITY` strategy.
- **Name (`name`):** `String`. Represents the full name of the employee.
- **Email (`email`):** `String`. Must be a valid email format.
- **Date of Birth (`dob`):** `java.util.Date`. Optional date of birth.
- **Skills (`skills`):** A collection of `Skill` entities. Lazily fetched, mapped via a many-to-many join table.

### B. Skill Entity
Represents a professional skill that employees can possess.
- **ID (`id`):** `Long`. Primary key. Automatically generated.
- **Description (`description`):** `String`. The description/name of the skill (e.g., "Java", "Spring Boot").
- **Employees (`employees`):** A collection of `Employee` entities possessing this skill. Lazily fetched.

---

## 2. Many-to-Many Relationship Rules

The relationship between `Employee` and `Skill` is a **bidirectional Many-to-Many** relationship.

- **Join Table Details:**
  - Name: `employee_skills`
  - Columns: `employee_id` (foreign key pointing to `Employee`) and `skill_id` (foreign key pointing to `Skill`).
- **Cascade Behavior:**
  - Set to `CascadeType.PERSIST` and `CascadeType.MERGE` on both sides.
  - Deleting an `Employee` does not delete the associated `Skill` entities, and deleting a `Skill` does not delete the `Employee` entities.
- **Link Management (Bidirectional Consistency):**
  - Relationships must be linked bidirectionally in memory before persisting.
  - When assigning skills to an employee, the code must add the `Employee` instance to each `Skill`'s employee collection:
    ```java
    skills.forEach(skill -> skill.getEmployees().add(employee));
    employee.setSkills(skills);
    ```

---

## 3. Transactional & Endpoint Rules

### A. Employee Operations
- **Creation (`POST /api/employees`):** Used to create new employees. Enforces a `null` ID on the resource to guarantee server-generated database keys. Returns `201 Created` with a `Location` header.
- **Update (`PUT /api/employees/{id}`):** Used to update existing employees.
  - If the ID does not exist in the database, the operation aborts and throws an `EntityNotFoundException` (resulting in a HTTP 404 response).
- **Skill Reference Resolution:**
  - Skills cannot be created implicitly or dynamically through the employee creation or update endpoints.
  - Every associated skill must be linked strictly by its unique `id`.
  - The application performs an optimized **batch fetch** lookup (`skillRepository.findAllById(skillIds)`) to retrieve and associate existing skills.
  - If any skill ID in the request cannot be found in the database, the transaction aborts and throws an `EntityNotFoundException` (resulting in an HTTP 404 response) indicating exactly which ID(s) are missing.

### B. Skill Operations
- **Creation (`POST /api/skills`):** Used to create new skills. Enforces a `null` ID. Returns `201 Created` with a `Location` header.
- **Update (`PUT /api/skills/{id}`):** Used to update existing skills.
  - If the ID does not exist in the database, the operation aborts and throws an `EntityNotFoundException` (resulting in a HTTP 404 response).

---

## 4. DTO Validation Rules (Request Layer)

All request payloads are validated at the controller boundary using the Spring `@Valid` annotation.

### `EmployeeDto` (Record)
- **`name`:** Must not be null (`@NotNull`).
- **`email`:** Must be a valid email format (`@Email`).
- **`skills`:** Default initialization ensures it is never null (defaults to `List.of()`).

### `SkillDto` (Record)
- **`description`:** Must not be null (`@NotNull`).
- **`employees`:** Default initialization ensures it is never null (defaults to `List.of()`).

---

## 5. Centralized Exception Handling & API Contract

Centralized error handling is configured using `@RestControllerAdvice` (`GlobalExceptionHandler.java`) to adhere to the RFC 7807/RFC 9457 Problem Details standard.

- **Validation Errors (`HTTP 400 Bad Request`):**
  - Triggered by invalid DTO inputs (e.g. missing name, invalid email format).
  - The response payload utilizes the Spring `ProblemDetail` structure with properties outlining specific field-level validation errors.
- **Entity Missing Errors (`HTTP 404 Not Found`):**
  - Triggered when lookup by ID fails or when a skill reference ID does not exist.
  - Returns a standard `ProblemDetail` payload with a 404 status.
