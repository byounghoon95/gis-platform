# Infra Agent Guide

Track-local rules for `infra/`. Root rules live in the repo-root `AGENTS.md`.

## Stack

- Local runtime: Docker Compose
- Database: PostgreSQL with PostGIS extension
- Optional reverse proxy: Nginx
- Deployment target: to be decided when portfolio deployment starts

## Coding Standards

- Keep Compose service names stable: `postgres`, `backend`, `frontend`.
- Keep environment variable names consistent across Compose, backend config, and frontend config.
- Document local run commands whenever runtime files change.
- Do not commit secrets. Use `.env.example` for required variables.
- Prefer simple local demo setup over production-only complexity.

## Verification

Run the checks relevant to what changed:

- `docker compose config` for Compose changes
- Container smoke test for changed runtime services when available
- Manifest/config validation for deployment files if added later

Record the command output in the task's `Completion Notes`. If a check cannot run, state why.
