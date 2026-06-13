# backend TASK-06: Implement Admin CSV Uploads

## Status

done

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

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added admin CSV upload endpoints for facilities, competitors, transit stops, and foot traffic; added CSV parsing, row DTO mapping, row-level validation results, and repository-backed inserts for fully valid files.
- Verification: `gradle test` passed using the repo-local Gradle 8.10.2 and JDK 17 toolchain; coverage includes CSV parser tests, validation/service tests, and multipart upload controller smoke tests.
- Notes: Upload APIs live under `/api/admin/uploads/**`, so existing `/api/admin/**` security restricts them to admin users. Invalid CSV responses return `400` with row-level errors and do not insert partial data.
