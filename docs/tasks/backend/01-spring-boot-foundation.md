# backend TASK-01: Create Spring Boot Foundation

## Status

todo

## Goal

Initialize the Java 17 Spring Boot 3 API project and baseline backend runtime.

## Source Requirement IDs

- T-101
- T-102
- T-103
- T-104

## Scope

- Create Gradle or Maven Spring Boot project
- Add health check API
- Add profile-based config
- Configure PostgreSQL/PostGIS datasource
- Add migration tool setup and initial schema migration
- Add global exception handling and common API error shape
- Add Springdoc OpenAPI and Swagger UI

## Out of Scope

- Do not implement auth.
- Do not implement location CRUD.
- Do not add scoring logic.

## Acceptance Criteria

- `GET /api/health` returns `200 OK`.
- Backend can run locally.
- Backend starts with database connection.
- Schema migration runs automatically.
- Swagger UI is available from browser.
- Invalid request errors use a consistent JSON shape.

## Verification

- Backend test command
- Backend boot or health endpoint smoke check
