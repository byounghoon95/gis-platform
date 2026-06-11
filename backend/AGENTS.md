# Backend Agent Guide

Track-local rules for `backend/`. Root rules live in the repo-root `AGENTS.md`.

## Stack

- Language: Java 17
- Framework: Spring Boot 3
- API: Spring Web
- Persistence: Spring Data JPA
- Security: Spring Security + JWT
- Database: PostgreSQL + PostGIS
- Migration: Flyway or Liquibase
- API docs: Springdoc OpenAPI

## Coding Standards

- Keep controllers thin. Controllers own HTTP details and delegate use cases to services.
- Keep service logic framework-light where practical, especially scoring and CSV validation.
- Put PostGIS-specific SQL in repositories or focused query components, not controllers.
- Validate latitude and longitude at the API boundary.
- Use DTOs for request/response contracts. Do not expose entities directly.
- Keep API error responses consistent through a global exception handler.

## Architecture

Use a 3-tier architecture:

```text
Controller/API layer -> Service layer -> Repository/Data access layer
```

- Controller/API layer: owns HTTP routes, request validation, authentication context, status codes, and request/response DTO mapping.
- Service layer: owns use cases, transactions, domain rules, scoring orchestration, CSV validation orchestration, and coordinates repositories.
- Repository/Data access layer: owns JPA repositories, persistence details, PostGIS queries, spatial indexes, and database-specific SQL.

Do not skip the service layer for business workflows. Controllers must not call PostGIS queries directly, and repositories must not return public API response DTOs.

## Verification

Run before declaring a backend task complete:

- Backend test command, for example `./gradlew test` or `./mvnw test`
- Application boot or focused API smoke check when the task changes runtime wiring

Record the command output in the task's `Completion Notes`. If a check cannot run, state why.
