# backend TASK-04: Implement GIS Data Models

## Status

done

## Goal

Store facilities, competitors, transit stops, and foot traffic samples for analysis.

## Source Requirement IDs

- T-401
- T-402
- T-403
- T-404

## Scope

- Add `facilities` table and entity/repository
- Add `competitors` table and entity/repository
- Add `transit_stops` table and entity/repository
- Add `foot_traffic_samples` table and entity/repository
- Store geometry points for spatial entities
- Add radius query support where required by the model

## Out of Scope

- Do not add CSV upload endpoints.
- Do not add scoring formula yet.

## Acceptance Criteria

- Facilities store geometry points.
- Competitors can be queried by radius and business type.
- Transit stops can be queried by radius and type.
- Foot traffic can be aggregated by radius.

## Verification

- Backend test command
- Repository/query smoke checks where available

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added GIS data model migration, JPA entities, repositories, and native PostGIS radius/aggregate queries for facilities, competitors, transit stops, and foot traffic samples.
- Verification: `gradle.bat test --no-daemon` passed; app boot smoke check on `localhost:8081` passed with Flyway applying V4 and `/api/health` returning 200; PostGIS SQL smoke check passed for geometry triggers, radius counts, business/type filters, and foot traffic average inside a rolled-back transaction.
- Notes: CSV upload endpoints and scoring formula were intentionally left for later tasks.
