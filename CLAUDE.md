# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Refer to the main [AGENTS.md](file:///Users/isuru/Documents/Workspace/employee_management_api/AGENTS.md) operating manual for detailed guidelines and architecture notes.

## Commands

```sh
# Clean compile
./mvnw clean compile

# Run tests
./mvnw test

# Package app
./mvnw package -DskipTests

# Run app
./mvnw spring-boot:run
```

## Agentic Workflow Checklist

When working on features or modernizing this repo, follow this loop:
1. **Start from a use case** — not a framework. Keep the domain pure.
2. **Have AI draft tests (e.g., Gherkin/BDD)** — but wait for human review on numbers and edge cases.
3. **Check Alignment** — Ask the human to "show the plan before acting" to avoid Silent Misalignment.
4. **Feedback Loop** — Iterate autonomously until tests are green. The test runner is your signal.
5. **Quality Pass** — Check Mutation (tests strong?), CRAP (functions clean?), Property (all paths covered?), and DRY.
6. **Extract Knowledge** — Save any understood implicit rules into a knowledge document since AI "Cannot Learn".
