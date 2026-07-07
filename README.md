# Employee Management API

The **Employee Management API** is a Spring Boot application designed to manage employees and their professional skills. The project has been modernized to utilize **Java 21** and **Spring Boot 3.3.1**, following clean architecture, test-driven development (TDD), automated quality metrics, and production-grade containerization.

---

## 1. Features & Modernization Highlights

- **Modern Stack**: Targeting **Java 21** and **Spring Boot 3.3.1**.
- **Jakarta Namespace Migration**: Fully migrated from legacy `javax.*` imports to the modern `jakarta.*` packages for JPA and validation.
- **Service Layer & Transaction Boundaries**: Introduced a clean service layer to encapsulate transaction boundaries, mapper orchestration, and domain rules.
- **Automated Mapping via MapStruct**: Replaced custom mapper logic with compile-time generated **MapStruct** mappers.
- **Immutable DTOs**: Refactored view model structures to use native **Java Records** for guaranteed immutability.
- **Centralized Error Handling**: Integrated global exception advice formatting validation and entity errors using RFC 7807 / RFC 9457 standard **Problem Details** payloads.
- **Enterprise Configuration**: Refactored configurations to YAML (`application.yml`) with production-grade defaults (HikariCP pool tuning, validated schema execution, and no SQL print leaks).
- **Container-Native Logging**: Integrated SLF4J structured logging configured strictly for stdout streaming.
- **Interactive Documentation**: Integrated **Swagger UI** for API exploration and interactive testing.
- **Quality Gates**: 
  - Centralized **JaCoCo** code coverage reporting.
  - Hard gate **CRAP (Change Risk Anti-Patterns) Metrics** analysis checking method complexity and test coverage against default thresholds.
  - **PIT Mutation Testing** to assess and guarantee test suite strength.

---

## 2. API Endpoint Directory (Strict RESTful)

All APIs follow strict RESTful resource conventions and utilize proper HTTP response codes:

### Employee Resource (`/api/employees`)
- **`GET /api/employees`**: Retrieve all employees (HTTP 200).
- **`GET /api/employees/{id}`**: Retrieve a single employee by ID (HTTP 200, returns HTTP 404 if not found).
- **`POST /api/employees`**: Create a new employee (HTTP 201 with `Location` header pointing to new resource).
- **`PUT /api/employees/{id}`**: Update an existing employee (HTTP 200).
- **`DELETE /api/employees/{id}`**: Delete an employee (HTTP 204 No Content).

### Skill Resource (`/api/skills`)
- **`GET /api/skills`**: Retrieve all skills (HTTP 200).
- **`GET /api/skills/{id}`**: Retrieve a single skill by ID (HTTP 200, returns HTTP 404 if not found).
- **`POST /api/skills`**: Create a new skill (HTTP 201 with `Location` header).
- **`PUT /api/skills/{id}`**: Update an existing skill (HTTP 200).
- **`DELETE /api/skills/{id}`**: Delete a skill (HTTP 204 No Content).

### System Endpoints
- **Actuator Health Check**: `GET /actuator/health` (HTTP 200, returns `{"status":"UP"}`).
- **OpenAPI JSON Spec**: `GET /v3/api-docs`.
- **Swagger UI Console**: `GET /swagger-ui/index.html` (interactive documentation).

---

## 3. Container Stack & Execution

The database and application are containerized and orchestrated using **Docker Compose**.

### Running the Stack
Ensure you have Docker installed, configure your `.env` parameters using `.env.example` as a template, and run:
- **Build and Start Stack**: `docker compose up --build -d`
- **Stop Stack**: `docker compose down`

### Logs
Logs can be streamed directly from the container:
- **View Application logs**: `docker compose logs -f app`
- **View DB logs**: `docker compose logs -f db`

---

## 4. Development Commands

Always use the Maven Wrapper (`./mvnw`) to manage the project locally:

| Action | Command | Description |
|---|---|---|
| **Clean & Compile** | `./mvnw clean compile` | Compiles the main application source files. |
| **Run Tests** | `./mvnw test` | Executes the JUnit 5 unit and service test suites. |
| **Verify & Quality Gate** | `./mvnw clean verify` | Runs tests, generates JaCoCo coverage, and checks CRAP metrics. |
| **Mutation Testing** | `./mvnw pitest:mutationCoverage` | Runs mutation testing to calculate test suite strength. |
| **Package Application** | `./mvnw package -DskipTests` | Packages the application into an executable `.jar` file. |
| **Run Locally** | `./mvnw spring-boot:run` | Starts the Spring Boot application locally. |
| **Workspace Health Check**| `bash init.sh` | Performs a comprehensive environment, build, testing, and quality gate check. |

---

## 5. Documentation Files

- [AGENTS.md](AGENTS.md): Operating instructions and hard coding constraints (TDD, Architecture boundary, etc.).
- [CLAUDE.md](CLAUDE.md): Quick reference for build commands and naming rules.
- [domain_knowledge.md](knowledge/domain_knowledge.md): Authority document describing entities, database schemas, bidirectional associations, business rules, and validation logic.
