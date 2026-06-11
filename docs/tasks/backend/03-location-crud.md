# backend TASK-03: Implement Location CRUD

## Status

todo

## Goal

Manage candidate business locations with PostGIS point storage.

## Source Requirement IDs

- T-301
- T-302
- T-303

## Scope

- Create `locations` table
- Enable PostGIS extension through migration
- Store `geom` for every location
- Add spatial index
- Implement create, list, detail, update, and delete APIs
- Add business type filter
- Add score range filter placeholder
- Add keyword search
- Validate latitude and longitude

## Out of Scope

- Do not implement nearby analysis.
- Do not implement scoring persistence.

## Acceptance Criteria

- CRUD APIs work through Swagger.
- Create/update validates latitude and longitude.
- List API supports query parameters.
- Spatial index exists.

## Verification

- Backend test command
- Swagger or API smoke check for CRUD
