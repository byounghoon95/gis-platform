# frontend TASK-05: Implement Analysis UI

## Status

done

## Goal

Show selected location summary, score, chart, explanation, and nearby evidence.

## Source Requirement IDs

- T-901
- T-902
- T-903
- T-904

## Scope

- Add location detail panel summary section
- Add score badge
- Add analyze button
- Show latest analyzed time
- Add bar or radar score chart
- Add facilities table
- Add competitors table
- Add transit stops table
- Add `/locations/:id` detail page
- Show loading and error states

## Out of Scope

- Do not implement CSV upload UI.
- Do not change backend scoring formula.

## Acceptance Criteria

- Analysis can be triggered from dashboard.
- Loading and error states are visible.
- All sub-scores are visible.
- Tables update when radius changes.
- Detail page can be opened directly by URL.

## Verification

- Frontend lint/test/build commands as configured
- Analysis flow smoke check

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added authenticated analysis API helpers, reusable analysis panel with score badge, analyze action, sub-score bars, explanation, nearby counts, and facilities/competitors/transit evidence tables; wired the panel into the dashboard; added direct `/locations/:id` detail page with radius-driven table refresh.
- Verification: `npm.cmd run lint` passed; `npm.cmd test` passed with 3 files and 5 tests; `npm.cmd run build` passed. `npm run ...` was not usable directly in PowerShell because local execution policy blocks `npm.ps1`, so `npm.cmd` was used. Browser smoke check was attempted, and Vite starts in foreground on `http://localhost:5173/`, but this tool session could not keep the dev server alive as a background process long enough for the in-app browser to connect.
- Notes: CSV upload UI and backend scoring logic were not changed.
