# frontend TASK-07: Add Frontend Smoke Tests

## Status

done

## Goal

Catch major UI failures with focused frontend smoke tests.

## Source Requirement IDs

- T-1203

## Scope

- Add login smoke test
- Add dashboard smoke test
- Add map error state test

## Out of Scope

- Do not add full end-to-end coverage for every workflow.
- Do not test Google Maps internals.

## Acceptance Criteria

- Frontend test command passes.
- Major route-level UI failures are covered.

## Verification

- Frontend test command
- Frontend build command

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added route smoke tests for login, authenticated dashboard rendering, and the Google Maps missing-key state.
- Verification:
  - `npm.cmd test` passed: 5 test files, 11 tests. React Router emitted expected server-render `useLayoutEffect` warnings from `renderToString`.
  - `npm.cmd run lint` passed.
  - `npm.cmd run build` passed.
- Notes: Smoke tests use React server rendering with a scoped auth-store mock to avoid adding browser-testing dependencies or testing Google Maps internals.
