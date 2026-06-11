# Codex Project Guide

## Project

This repository is a portfolio project for a GIS-based B2B location analysis platform.

Before implementing any task, read:

- `docs/SPEC.md`
- `docs/tasks/README.md`
- The requested task file under `docs/tasks/`
- The track-local `AGENTS.md` for the area you are editing:
  - `backend/AGENTS.md` for backend work
  - `frontend/AGENTS.md` for frontend work
  - `infra/AGENTS.md` for Docker, Compose, deployment, and runtime work
- Any task-specific spec referenced by the user

Track-local `AGENTS.md` files hold stack, coding standards, and verification rules for that area.

## Behavioral Guidelines

### 1. Think Before Coding

- State assumptions explicitly when scope is unclear.
- If multiple interpretations exist, clarify the tradeoff before changing code.
- Prefer a small, verifiable implementation over broad speculative work.

### 2. Simplicity First

- No features beyond the requested task and acceptance criteria.
- No abstractions for one-off code.
- Match the existing project shape before introducing new structure.

### 3. Surgical Changes

- Touch only files required by the task.
- Do not refactor adjacent code unless the task requires it.
- Remove only dead code introduced by your own changes.

### 4. Goal-Driven Execution

For each task:

- Update `docs/tasks/README.md` status when work starts and finishes.
- Update the matching task file's `## Status`.
- Append or update `## Completion Notes` when the task is done.
- Record actual verification commands and results. If a check cannot run, state why.

## Status Values

- `todo`: not started
- `doing`: currently being implemented
- `done`: completed and verified, or completed with verification notes
- `blocked`: cannot continue without a decision or external dependency

## Git Conventions

Use predictable branch names and commit messages once this directory is initialized as a Git repository.

### Worktree Workflow

For new implementation or PR work, use a separate Git worktree outside the repository directory instead of changing branches in the main checkout.

Preferred location pattern:

```text
../gis-platform-worktrees/<branch-name>
```

Example:

```sh
mkdir -p ../gis-platform-worktrees
git worktree add ../gis-platform-worktrees/feature-infra-docker-compose-base -b feature/infra-docker-compose-base main
```

Branch names:

```text
<type>/<scope>-<short-description>
```

Allowed `type` values:

- `feature`
- `fix`
- `docs`
- `chore`
- `refactor`
- `test`

Commit messages:

```text
<type>(<scope>): <imperative summary>
```

Examples:

- `feature(backend): add location crud api`
- `feature(frontend): add map dashboard`
- `feature(compose): add postgis runtime`
- `docs(tasks): record repository structure task`

Pull request titles:

- The `<type>` must always be one of the Git convention types above. Do not use track names such as `backend`, `frontend`, or `infra` as the type.

- For task-based work, use:

```text
<type>(<track>-task-<number>): <imperative summary>
```

Examples:

- `feature(backend-task-01): create spring boot foundation`
- `feature(frontend-task-03): add dashboard layout`
- `feature(infra-task-01): add compose base`
- `docs(foundation-task-01): record verification notes`

- For non-task work, omit the scope:

```text
<type>: <imperative summary>
```

Examples:

- `docs: update contribution guide`
- `fix: correct typo in readme`
- `chore: update ignore rules`

## Scope Guardrails

- Keep the MVP focused on candidate location management, map visualization, PostGIS nearby analysis, explainable scoring, CSV upload, and portfolio documentation.
- Do not add payments, multi-tenant billing, complex BI dashboards, or unrelated admin features unless explicitly requested.
- Update docs when a task changes architecture, APIs, infrastructure, or development workflow.
