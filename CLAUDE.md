# CLAUDE.md

## Build & Test Setup
- **Java Version:** Java 21 (Modernization Target)
- **Build Tool:** Maven Wrapper (`./mvnw`)
- **Compilation:** `./mvnw clean compile`
- **Execution & Verification:** `./mvnw test`
- **CRAP Metrics & Coverage Check:** `./mvnw clean verify`
- **Mutation Testing:** `./mvnw pitest:mutationCoverage`
- **Run Locally:** `./mvnw spring-boot:run`
- **Initialize & Health Check:** `bash init.sh`

## Code Style & Formatting
- **Naming Standards:** Standard Java camelCase for methods/variables, PascalCase for classes/interfaces, and UPPER_SNAKE_CASE for constants.
- **Jakarta Namespace:** Use `jakarta.persistence.*` and `jakarta.validation.*` instead of `javax.*` (post Spring Boot 3.x upgrade).
- **Immutability:** DTOs in `dto/` must be Java Records.
- **Clean Architecture:** Keep mapping layers pure. Do not inject repositories or do database calls in the mapper.
- **Validation:** Use `@Valid` on controller requests and handle validation exceptions globally using a `@RestControllerAdvice` class returning RFC 7807 `ProblemDetail` payloads.
- **Tests:** Use JUnit 5 (JUnit Jupiter) annotations only. Standard unit tests mock database access. Request-to-persistence integration tests are placed in the `integration/` test package and run in the `test` profile using H2 Database.
- **No Mocking / Placeholders:** Write fully functional code without placeholder comments.
- **TDD Requirement:** Always write a failing test first before implementing any production code changes, and verify the test fails and subsequently passes.
