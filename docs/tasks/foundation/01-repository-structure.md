# foundation TASK-01: Create Repository Structure and Task System

## Status

done

## Goal

Create the baseline repository structure and split project tasks by track.

## Source Requirement IDs

- T-001

## Scope

- Create `backend/`, `frontend/`, `infra/`, and `docs/`
- Add root `README.md`
- Add root `.gitignore`
- Add root and track-local `AGENTS.md`
- Split task tracking under `docs/tasks/`
- Add a task index with status, dependencies, and file links

## Out of Scope

- Do not scaffold Spring Boot.
- Do not scaffold React.
- Do not add Docker Compose runtime yet.

## Acceptance Criteria

- Repository structure is clear.
- README explains the project in one paragraph.
- Task progress can be checked from `docs/tasks/README.md`.
- Each implementation track has an `AGENTS.md`.

## Verification

- `find . -maxdepth 3 -type f | sort`
- `rg '^#|^###|TASK-' docs/tasks README.md AGENTS.md backend/AGENTS.md frontend/AGENTS.md infra/AGENTS.md`

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: added backend, frontend, infra, root README, root `.gitignore`, root/track `AGENTS.md`, and task index with per-track task files
- Verification: `find . -maxdepth 3 -type f | sort` passed; `rg '^#|^###|TASK-' docs/tasks README.md AGENTS.md backend/AGENTS.md frontend/AGENTS.md infra/AGENTS.md` passed
- Notes: application scaffolds and Docker Compose are intentionally left for later tasks
