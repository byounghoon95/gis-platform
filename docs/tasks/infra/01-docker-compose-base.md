# infra TASK-01: Add Docker Compose Base

## Status

todo

## Goal

Run PostgreSQL/PostGIS, backend, and frontend with Docker Compose.

## Source Requirement IDs

- T-002

## Scope

- Add `compose.yaml` or `infra/docker/compose.yaml`
- Add `.env.example`
- Add PostGIS database container
- Add backend service placeholder
- Add frontend service placeholder
- Document local startup command

## Out of Scope

- Do not add production deployment manifests.
- Do not optimize production Docker images.

## Acceptance Criteria

- Docker Compose config is valid.
- Database exposes port `5432`.
- Backend and frontend services can read environment variables.

## Verification

- `docker compose config`
