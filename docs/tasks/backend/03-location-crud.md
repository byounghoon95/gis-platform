# backend TASK-03: Implement Location CRUD

## Status

done

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

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added `locations` migration with PostGIS geography point storage, geometry trigger, spatial/text/business indexes, location entity/repository/specifications, admin CRUD controller/service/DTOs, latitude/longitude validation, business type and keyword filters, score range placeholder parameters, and focused controller/service tests.
- Verification:
  - `docker run --rm -v gis-gradle-cache:/home/gradle/.gradle -v "$PWD/backend":/workspace -w /workspace gradle:8.10.2-jdk17 gradle test` -> passed; 20 tests, 0 failures.
  - `docker compose up -d --build postgres backend` -> passed; postgres became healthy and backend started.
  - `curl -fsS http://localhost:8080/api/health` -> passed; returned `{"status":"UP",...}`.
  - Login API smoke with `admin@example.com` / `admin1234` -> passed; returned Bearer token.
  - CRUD API smoke for `POST/GET list/GET detail/PUT/DELETE /api/admin/locations` -> passed; create/list/detail/update returned expected JSON, delete returned `204`, deleted detail returned `404`.
  - `docker compose exec -T postgres psql -U gis_user -d gis_platform -c "select id, ST_AsText(geom::geometry) as geom_text, geom is not null as has_geom from locations where id = ...;"` -> passed; `geom` stored as `POINT(127 37.5)` and `has_geom` was `t`.
  - `docker compose exec -T postgres psql -U gis_user -d gis_platform -c "select indexname, indexdef from pg_indexes where tablename = 'locations' and indexname = 'idx_locations_geom';"` -> passed; `idx_locations_geom` exists using GiST.
- Notes: Score range query parameters are accepted and validated as placeholders for future scoring work, but they do not filter results until scoring persistence is introduced.
