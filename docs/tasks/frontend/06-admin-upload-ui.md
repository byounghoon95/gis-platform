# frontend TASK-06: Implement Admin Upload UI

## Status

done

## Goal

Allow admins to upload GIS datasets from CSV and review validation results.

## Source Requirement IDs

- T-1001
- T-1002

## Scope

- Add `/admin/uploads`
- Add dataset type selector
- Add file input
- Add upload button
- Show inserted row count
- Show failed row count
- Show row-level error table
- Block or redirect unauthorized users

## Out of Scope

- Do not implement backend upload endpoints.
- Do not add background upload progress unless explicitly requested.

## Acceptance Criteria

- Admin can upload a CSV file.
- Unauthorized user is redirected or blocked.
- Invalid rows are understandable to the user.

## Verification

- Frontend lint/test/build commands as configured
- Upload UI smoke check

## Completion Notes

- Status: done
- Skills used: implement-task, browser:control-in-app-browser
- Changed: Added `/admin/uploads`, admin-only route blocking, CSV dataset upload client, upload result summary, row-level validation table, and dashboard navigation for admins.
- Verification:
  - `npm.cmd run lint` - passed.
  - `npm.cmd test` - passed, 4 test files and 8 tests.
  - `npm.cmd run build` - passed.
  - Built-app smoke check - unauthenticated `/admin/uploads` redirects to `/login`; seeded admin session renders dataset selector, CSV file input, upload button, and import summary; seeded non-admin session is blocked.
- Notes: Backend upload endpoints already existed, so this task only connected the frontend multipart request and validation-result display.
