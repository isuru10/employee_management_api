# Employee Management API

The **Employee Management API** is a Spring Boot application designed to manage employees and their professional skills. The project has been modernized to utilize **Java 21** and **Spring Boot 3.x**, following clean architecture principles, TDD, and automated quality metrics.

---

## 1. Features & Modernization Highlights

- **Modern Stack:** Upgraded to target **Java 21** and **Spring Boot 3.3.1**.
- **Jakarta Namespace Migration:** Fully migrated from legacy `javax.*` imports to the modern `jakarta.*` packages for JPA and validation.
- **Immutable DTOs:** Refactored view model structures to use native **Java Records** for guaranteed immutability.
- **Pure Mapping Layer:** Decoupled `Mapper.java` from repositories. Database lookups are handled strictly in the controller layer to maintain a pure, side-effect-free translation layer.
- **Centralized Error Handling:** Integrated global exception advice formatting validation and entity errors using RFC 7807 / RFC 9457 standard **Problem Details** payloads.
- **Quality Gates:** 
  - Centralized **JaCoCo** code coverage reporting.
  - Hard gate **CRAP (Change Risk Anti-Patterns) Metrics** analysis checking method complexity and test coverage against default thresholds.
  - **PIT Mutation Testing** to assess and guarantee test suite strength.

---

## 2. Prerequisites

- **Java Development Kit (JDK):** Version 21 or higher.
- **Database:** PostgreSQL (configured in `src/main/resources/application.properties` for local runtime execution; unit tests run in-memory using mocks).

---

## 3. Development Commands

Always use the Maven Wrapper (`./mvnw`) to manage the project:

| Action | Command | Description |
|---|---|---|
| **Clean & Compile** | `./mvnw clean compile` | Compiles the main application source files. |
| **Run Tests** | `./mvnw test` | Executes unit and controller test suites. |
| **Verify & Quality Gate** | `./mvnw clean verify` | Runs tests, generates JaCoCo coverage, and checks CRAP metrics. |
| **Mutation Testing** | `./mvnw pitest:mutationCoverage` | Runs mutation testing to calculate test suite strength. |
| **Package Application** | `./mvnw package -DskipTests` | Packages the application into an executable `.jar` file. |
| **Run Locally** | `./mvnw spring-boot:run` | Starts the Spring Boot application locally. |
| **Health Check** | `bash init.sh` | Performs a comprehensive environment, build, testing, and quality gate check. |

---

## 4. Documentation Files

- [AGENTS.md](AGENTS.md): Operating instructions and hard coding constraints (TDD, Architecture boundary, etc.).
- [CLAUDE.md](CLAUDE.md): Quick reference for build commands and naming rules.
- [domain_knowledge.md](knowledge/domain_knowledge.md): Authority document describing entities, database schemas, bidirectional associations, business rules, and validation logic.
