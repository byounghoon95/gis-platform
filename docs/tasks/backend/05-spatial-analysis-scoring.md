# backend TASK-05: Implement Spatial Analysis and Scoring

## Status

done

## Goal

Calculate nearby data and explainable location scores for selected candidate locations.

## Source Requirement IDs

- T-501
- T-502
- T-503
- T-504

## Scope

- Add PostGIS radius query repository methods
- Expose `GET /api/locations/{locationId}/nearby?radius=500`
- Implement score formula
- Implement score normalization utilities
- Implement score explanation builder
- Add `location_scores` table
- Persist analysis results
- Expose analysis command API and latest score API

## Out of Scope

- Do not tune scoring weights dynamically.
- Do not add machine learning.

## Acceptance Criteria

- Nearby API returns counts and item lists.
- Radius query uses `ST_DWithin`.
- Score is always between 0 and 100.
- Sub-scores and total score are returned.
- Explanation includes concrete nearby data.
- `POST /api/locations/{locationId}/analysis?radius=500` calculates and stores score.
- `GET /api/locations/{locationId}/score` returns latest score.

## Verification

- Backend test command
- Focused scoring unit tests
- API smoke check for nearby and analysis endpoints

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added `location_scores` persistence, nearby-data API, analysis command API, latest score API, fixed-weight score calculation, score normalization, concrete explanation text, and focused scoring tests.
- Verification: `gradle.bat test --no-daemon` passed; app boot smoke check on `localhost:8082` passed with Flyway applying V5 and `/api/health` returning 200; API smoke check passed for `GET /api/locations/{locationId}/nearby?radius=500`, `POST /api/locations/{locationId}/analysis?radius=500`, and `GET /api/locations/{locationId}/score` using temporary PostGIS sample data that was cleaned up afterward.
- Notes: Scoring weights are fixed per spec. Dynamic tuning and machine learning were not added.
