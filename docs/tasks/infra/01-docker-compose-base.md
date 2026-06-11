# infra TASK-01: Add Docker Compose Base

## Status

done

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

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added root `compose.yaml`, `.env.example`, PostGIS init SQL, and README local runtime instructions for the base Docker Compose stack.
- Verification: `docker compose config` passed; config includes `postgres` on published port `5432`, backend placeholder env values, and frontend placeholder env values.
- Notes: Backend and frontend services intentionally use lightweight placeholders until their application implementation tasks provide real Docker build targets.
