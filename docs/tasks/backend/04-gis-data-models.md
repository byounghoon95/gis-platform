# backend TASK-04: Implement GIS Data Models

## Status

todo

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
