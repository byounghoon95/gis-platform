# backend TASK-02: Implement Auth, JWT, and Admin Guard

## Status

todo

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
