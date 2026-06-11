# frontend TASK-06: Implement Admin Upload UI

## Status

todo

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
