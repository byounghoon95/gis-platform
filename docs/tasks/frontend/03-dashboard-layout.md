# frontend TASK-03: Implement Dashboard Layout

## Status

done

## Goal

Create the main B2B map workspace shell.

## Source Requirement IDs

- T-703

## Scope

- Add `/dashboard`
- Add map area placeholder
- Add side list
- Add detail panel placeholder
- Add filter bar
- Handle desktop and mobile widths

## Out of Scope

- Do not integrate Google Maps.
- Do not implement scoring charts.

## Acceptance Criteria

- Layout works on desktop and mobile width.
- Empty/loading/error states are represented where data will load.

## Verification

- Frontend lint/test/build commands as configured
- Visual smoke check

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: Added protected `/dashboard` routing, a responsive dashboard workspace shell, filter bar, map placeholder, side candidate list, detail panel placeholder, and represented loading/error/empty data states.
- Verification: `npm run lint`, `npm test`, and `npm run build` were blocked by PowerShell execution policy for `npm.ps1`; reran successfully with `npm.cmd run lint`, `npm.cmd test` (3 files, 5 tests passed), and `npm.cmd run build`. Visual smoke check used Playwright with system Edge against `http://localhost:5173/dashboard`; desktop rendered three columns (`290px 730px 340px`), mobile rendered one column (`358px`), and both had no horizontal overflow.
- Notes: Google Maps and scoring charts remain out of scope for later frontend tasks.
