# frontend TASK-02: Implement Login and Auth Guard

## Status

done

## Goal

Allow admin login and protect authenticated routes.

## Source Requirement IDs

- T-702

## Scope

- Add `/login`
- Store access token
- Add auth guard
- Add login error handling
- Redirect successful login to dashboard

## Out of Scope

- Do not add signup.
- Do not add user management.

## Acceptance Criteria

- Login redirects to dashboard.
- Invalid login shows error.
- Unauthorized dashboard access redirects or blocks.

## Verification

- Frontend lint/test/build commands as configured
- Login flow smoke check

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added `/login`, admin login API client, local token/session storage with Zustand, protected dashboard routing, login error display, sign-out action, README login notes, and focused auth/client tests.
- Verification: `npm run lint` passed; `npm test` passed with 5 tests; `npm run build` passed with Vite reporting 34.66s build time.
- Login flow smoke check: not rerun before PR because no backend containers were running and `curl -fsS http://localhost:8080/api/health` could not connect.
- Notes: Signup and user management were not added.
