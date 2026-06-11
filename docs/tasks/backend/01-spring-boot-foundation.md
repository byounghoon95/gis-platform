# backend TASK-01: Create Spring Boot Foundation

## Status

done

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

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added a Maven Spring Boot 3 backend foundation with health API, profile-based config, PostgreSQL/PostGIS datasource wiring, Flyway initial migration, common API error response handling, Springdoc Swagger UI, Docker image build, and Compose backend runtime wiring.
- Verification:
  - `docker run --rm -v "$PWD/backend":/workspace -w /workspace maven:3.9.9-eclipse-temurin-17 mvn -B test` -> passed; 2 tests, 0 failures.
  - `docker compose up -d --build postgres backend` -> passed; Postgres and backend containers started healthy.
  - `curl -fsS http://localhost:8080/api/health` -> passed; returned `{"status":"UP",...}`.
  - `curl -fsSI http://localhost:8080/swagger-ui.html` -> passed; returned `302` to `/swagger-ui/index.html`.
  - `docker compose exec -T postgres psql -U gis_user -d gis_platform -c "select version, description, success from flyway_schema_history order by installed_rank;"` -> passed; version `1`, description `init schema`, success `t`.
- Notes: Local host does not have `java` or `gradle` installed, so verification used Dockerized Maven. Smoke-check containers were stopped with `docker compose down` after verification.
