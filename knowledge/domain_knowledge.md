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

## 3. Transactional & Save Endpoint Rules

### A. Employee Save Operations (`POST /api/employees`)
- **New Employee:** If the request payload contains a `null` ID, a new `Employee` instance is instantiated.
- **Existing Employee:** If the request payload contains an ID:
  - The application attempts to resolve the employee by ID.
  - If the ID does not exist in the database, the operation aborts and throws an `EntityNotFoundException` (resulting in a HTTP 404 response).
- **Skill Reference Resolution:**
  - Skills cannot be created implicitly or dynamically through the employee creation endpoint.
  - Every skill listed in the employee DTO must already exist in the database (resolved via description lookup: `skillRepository.findByDescription(skillDescription)`).
  - If any skill description in the request cannot be found in the database, the transaction aborts and throws an `EntityNotFoundException` (resulting in a HTTP 404 response).

### B. Skill Save Operations (`POST /api/skills`)
- **New Skill:** If the request payload contains a `null` ID, a new `Skill` instance is instantiated.
- **Existing Skill:** If the request payload contains an ID:
  - The application attempts to resolve the skill by ID.
  - If the ID does not exist in the database, the operation aborts and throws an `EntityNotFoundException` (resulting in a HTTP 404 response).

---

## 4. DTO Validation Rules (Request Layer)

All request payloads are validated at the controller boundary using the Spring `@Valid` annotation.

### `EmployeeViewModel` (Record)
- **`name`:** Must not be null (`@NotNull`).
- **`email`:** Must be a valid email format (`@Email`).
- **`skills`:** Default initialization ensures it is never null (defaults to `List.of()`).

### `SkillViewModel` (Record)
- **`description`:** Must not be null (`@NotNull`).
- **`employees`:** Default initialization ensures it is never null (defaults to `List.of()`).

---

## 5. Centralized Exception Handling & API Contract

Centralized error handling is configured using `@RestControllerAdvice` (`GlobalExceptionHandler.java`) to adhere to the RFC 7807/RFC 9457 Problem Details standard.

- **Validation Errors (`HTTP 400 Bad Request`):**
  - Triggered by invalid DTO inputs (e.g. missing name, invalid email format).
  - The response payload utilizes the Spring `ProblemDetail` structure with properties outlining specific field-level validation errors.
- **Entity Missing Errors (`HTTP 404 Not Found`):**
  - Triggered when lookup by ID fails or when a skill reference description does not exist.
  - Returns a standard `ProblemDetail` payload with a 404 status.
