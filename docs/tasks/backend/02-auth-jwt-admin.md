# backend TASK-02: Implement Auth, JWT, and Admin Guard

## Status

done

## Goal

Support seeded admin login and protect admin-only APIs.

## Source Requirement IDs

- T-201
- T-202
- T-203

## Scope

- Add `users` table
- Add user entity
- Seed admin account
- Encode passwords
- Add login request/response DTOs
- Add authentication service
- Add JWT provider
- Add JWT authentication filter
- Add role-based authorization for admin APIs

## Out of Scope

- Do not add user signup.
- Do not add refresh tokens unless explicitly requested.

## Acceptance Criteria

- Admin user exists after application startup.
- Valid credentials return access token.
- Invalid credentials return `401`.
- Anonymous users cannot call admin APIs.
- Admin token can call admin APIs.

## Verification

- Backend test command
- Login API smoke check

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added Spring Security and JWT dependencies, users table migration, user entity/repository, seeded admin startup runner, login DTO/controller/service, JWT provider/filter, JSON security errors, `/api/admin/me` guard endpoint, auth/admin tests, local auth environment variables, and Gradle/Docker cache settings for faster repeated verification.
- Verification:
  - `docker run --rm -v gis-gradle-cache:/home/gradle/.gradle -v "$PWD/backend":/workspace -w /workspace gradle:8.10.2-jdk17 gradle test` -> passed; 8 tests, 0 failures. Warm run reused configuration cache and completed in 21 seconds locally.
  - `docker compose up -d --build postgres backend` -> passed; postgres became healthy and backend started.
  - `docker compose build backend` -> passed; warm build reused Docker layers and completed in 6 seconds locally.
  - `curl -fsS http://localhost:8080/api/health` -> passed; returned `{"status":"UP",...}`.
  - `curl -fsS -X POST http://localhost:8080/api/auth/login ...` with `admin@example.com` / `admin1234` -> passed; returned Bearer access token and admin user payload.
  - Invalid login smoke check -> passed; returned HTTP `401`.
  - Anonymous `GET /api/admin/me` -> passed; returned HTTP `401`.
  - Token-authenticated `GET /api/admin/me` -> passed; returned admin user payload.
  - `docker compose exec -T postgres psql -U gis_user -d gis_platform -c "select email, name, role, password like '\$2%' as bcrypt_encoded from users order by id;"` -> passed; seeded admin row exists with BCrypt-encoded password.
- Notes: Admin seed values are configurable with `ADMIN_EMAIL`, `ADMIN_PASSWORD`, and `ADMIN_NAME`; local defaults are `admin@example.com` / `admin1234`.
