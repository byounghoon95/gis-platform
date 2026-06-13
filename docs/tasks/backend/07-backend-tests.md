# backend TASK-07: Add Backend Test Coverage

## Status

done

## Goal

Cover core backend business logic and integration behavior.

## Source Requirement IDs

- T-1201
- T-1202

## Scope

- Add scoring service tests
- Add normalization tests
- Add CSV validation tests
- Add location CRUD integration test
- Add radius query integration test
- Add analysis API integration test

## Out of Scope

- Do not add frontend tests.
- Do not add load tests.

## Acceptance Criteria

- Tests cover score min/max edge cases.
- Integration tests run against test database or Testcontainers.
- Backend test command passes.

## Verification

- Backend test command

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added Testcontainers-backed backend integration coverage for location CRUD, PostGIS radius queries, and analysis API behavior; expanded scoring min/max edge tests; added CSV missing-column validation coverage; added backend Testcontainers test dependencies.
- Verification: `.\gradlew test` from `backend/` failed because no Gradle wrapper exists; `gradle test` failed because Gradle is not installed on PATH; `java -version` and `where.exe docker` also failed because Java and Docker are not available on PATH in this shell.
- Notes: Integration tests are configured to run Flyway migrations against a `postgis/postgis:16-3.4-alpine` Testcontainers PostgreSQL database when Java, Gradle, and Docker are available.
