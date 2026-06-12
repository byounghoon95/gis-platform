# frontend TASK-04: Implement Map UI

## Status

done

## Goal

Render candidate locations on Google Maps and support radius/location picking interactions.

## Source Requirement IDs

- T-801
- T-802
- T-803
- T-804

## Scope

- Add Google Maps loader
- Read Google Maps API key from environment variable
- Add map component
- Render location markers
- Color markers by score
- Support marker click selection
- Focus marker from list selection
- Add radius selector
- Add circle overlay
- Support map click coordinate picker

## Out of Scope

- Do not implement analysis chart UI.
- Do not hard-code API keys.

## Acceptance Criteria

- Map renders without layout shift.
- Missing API key shows clear error state.
- Clicking marker opens detail panel.
- List selection focuses marker.
- Changing radius updates circle.
- Clicked coordinates populate latitude and longitude fields.

## Verification

- Frontend lint/test/build commands as configured
- Desktop and mobile map smoke check

## Completion Notes

- Status: done
- Skills used: implement-task, browser:control-in-app-browser
- Changed: Replaced the dashboard map placeholder with a Google Maps loader keyed by `VITE_GOOGLE_MAPS_API_KEY`, authenticated admin location loading, score-colored marker/list/detail selection flow, radius-controlled circle overlay, and map-click latitude/longitude picker fields. Added a thin frontend locations API helper and preserved missing-key, loading, error, and empty states.
- Verification:
  - `npm run lint` -> blocked by local PowerShell execution policy for `npm.ps1`; reran as `npm.cmd run lint` -> passed.
  - `npm test` -> blocked by local PowerShell execution policy for `npm.ps1`; reran as `npm.cmd test` -> passed; 3 files, 5 tests.
  - `npm run build` -> blocked by local PowerShell execution policy for `npm.ps1`; reran as `npm.cmd run build` -> passed; Vite built `dist`.
  - Browser desktop smoke on built `dist` with injected local test session -> passed for dashboard shell, stable three-column layout, missing Google Maps API key state, coordinate picker presence, and no horizontal overflow.
  - Browser mobile smoke at 390x844 on built `dist` with injected local test session -> passed for stacked layout, missing Google Maps API key state, coordinate picker presence, and no horizontal overflow.
- Notes: Backend and a Google Maps API key were not running/configured in this environment, so live marker rendering, marker click selection, radius circle rendering, and map-click coordinate population could not be exercised against the real Google map. The UI paths are implemented and will activate when `/api/admin/locations` and `VITE_GOOGLE_MAPS_API_KEY` are available.
