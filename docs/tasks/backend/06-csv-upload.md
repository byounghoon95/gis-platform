# backend TASK-06: Implement Admin CSV Uploads

## Status

todo

## Goal

Allow admins to upload GIS datasets from CSV.

## Source Requirement IDs

- T-601
- T-602
- T-603
- T-604
- T-605

## Scope

- Add CSV parsing utility
- Add validation result model
- Add row-level validation
- Add facilities upload endpoint
- Add competitors upload endpoint
- Add transit stops upload endpoint
- Add foot traffic upload endpoint
- Restrict upload APIs to admin users

## Out of Scope

- Do not build frontend upload UI.
- Do not add background jobs unless explicitly requested.

## Acceptance Criteria

- Invalid rows are reported.
- Valid rows are mapped to DTOs.
- Valid CSV inserts rows.
- Invalid CSV returns row-level errors.
- Competitor business type is required.
- Transit stop type must be `SUBWAY` or `BUS`.
- Foot traffic date, hour, latitude, longitude, and count are validated.

## Verification

- Backend test command
- CSV parser and validation unit tests
- Upload API smoke check
