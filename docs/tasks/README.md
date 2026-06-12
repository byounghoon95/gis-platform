# Tasks

Task specs are split by track so backend, frontend, and infrastructure work can move in parallel when dependencies allow it.

Use:

```text
backend TASK-01
frontend TASK-01
infra TASK-01
portfolio TASK-01
```

## Status Values

Each row in the Index uses a leading icon plus the status word so the table scans quickly.

- ⬜ `todo`: not started
- 🟡 `doing`: currently being implemented
- ✅ `done`: completed and verified, or completed with verification notes
- ⛔ `blocked`: cannot continue without a decision or external dependency

## Defaults

When a task is finished, update the task file with:

```md
## Completion Notes

- Status: done
- Changed: short summary of changed areas
- Verification: command and result, or reason not run
- Notes: important decisions or follow-up tasks
```

## Index

Status values come from `## Status Values` above. Update this column and the matching task file's `## Status` before considering a task complete.

| Track Task | Status | Depends On | File |
| --- | --- | --- | --- |
| foundation TASK-01 | ✅ done | none | [01-repository-structure.md](foundation/01-repository-structure.md) |
| infra TASK-01 | ✅ done | foundation TASK-01 | [01-docker-compose-base.md](infra/01-docker-compose-base.md) |
| backend TASK-01 | ✅ done | foundation TASK-01, infra TASK-01 | [01-spring-boot-foundation.md](backend/01-spring-boot-foundation.md) |
| backend TASK-02 | ✅ done | backend TASK-01 | [02-auth-jwt-admin.md](backend/02-auth-jwt-admin.md) |
| backend TASK-03 | ✅ done | backend TASK-01 | [03-location-crud.md](backend/03-location-crud.md) |
| backend TASK-04 | ⬜ todo | backend TASK-03 | [04-gis-data-models.md](backend/04-gis-data-models.md) |
| backend TASK-05 | ⬜ todo | backend TASK-04 | [05-spatial-analysis-scoring.md](backend/05-spatial-analysis-scoring.md) |
| backend TASK-06 | ⬜ todo | backend TASK-02, backend TASK-04 | [06-csv-upload.md](backend/06-csv-upload.md) |
| backend TASK-07 | ⬜ todo | backend TASK-05, backend TASK-06 | [07-backend-tests.md](backend/07-backend-tests.md) |
| frontend TASK-01 | ✅ done | foundation TASK-01 | [01-react-vite-foundation.md](frontend/01-react-vite-foundation.md) |
| frontend TASK-02 | ✅ done | frontend TASK-01, backend TASK-02 | [02-login-auth.md](frontend/02-login-auth.md) |
| frontend TASK-03 | ✅ done | frontend TASK-01 | [03-dashboard-layout.md](frontend/03-dashboard-layout.md) |
| frontend TASK-04 | ⬜ todo | frontend TASK-03, backend TASK-03 | [04-map-ui.md](frontend/04-map-ui.md) |
| frontend TASK-05 | ⬜ todo | frontend TASK-04, backend TASK-05 | [05-analysis-ui.md](frontend/05-analysis-ui.md) |
| frontend TASK-06 | ⬜ todo | frontend TASK-02, backend TASK-06 | [06-admin-upload-ui.md](frontend/06-admin-upload-ui.md) |
| frontend TASK-07 | ⬜ todo | frontend TASK-05, frontend TASK-06 | [07-frontend-smoke-tests.md](frontend/07-frontend-smoke-tests.md) |
| infra TASK-02 | ⬜ todo | backend TASK-06, frontend TASK-06 | [02-sample-data-demo.md](infra/02-sample-data-demo.md) |
| portfolio TASK-01 | ⬜ todo | backend TASK-07, frontend TASK-07, infra TASK-02 | [01-final-readme-portfolio-docs.md](portfolio/01-final-readme-portfolio-docs.md) |

## Parallel Work

After `foundation TASK-01`, these tracks can move independently:

- Infra: `infra TASK-01 -> infra TASK-02`
- Backend: `backend TASK-01 -> backend TASK-02/backend TASK-03 -> backend TASK-04 -> backend TASK-05/backend TASK-06 -> backend TASK-07`
- Frontend: `frontend TASK-01 -> frontend TASK-03`, then map and analysis work follow backend APIs
- Portfolio: `portfolio TASK-01` after the app is demoable

## Suggested Flow

```text
foundation TASK-01
  |
  +--> infra:    TASK-01 -------------------------------------------> TASK-02
  +--> backend:  TASK-01 -> TASK-02
  |                    \-> TASK-03 -> TASK-04 -> TASK-05 -> TASK-07
  |                                      \-----> TASK-06 ----^
  +--> frontend: TASK-01 -> TASK-02
                       \-> TASK-03 -> TASK-04 -> TASK-05 -> TASK-07
                                             \-> TASK-06 ----^

portfolio TASK-01 follows backend, frontend, and demo data completion.
```
