# infra TASK-02: Add Sample Data and Demo Import Flow

## Status

done

## Goal

Make the project demoable without external GIS datasets.

## Source Requirement IDs

- T-1101
- T-1102

## Scope

- Add sample locations CSV
- Add sample facilities CSV
- Add sample competitors CSV
- Add sample transit stops CSV
- Add sample foot traffic CSV
- Cover at least one Seoul district
- Add backend seed command or documented upload flow
- Document demo preparation steps

## Out of Scope

- Do not add large real-world datasets.
- Do not commit private or licensed data.

## Acceptance Criteria

- Sample data covers at least one Seoul district.
- Demo environment can be prepared in under 5 minutes.

## Verification

- Documented import command or upload flow smoke check

## Completion Notes

- Status: done
- Skills used: implement-task
- Changed: added small synthetic Gangnam-gu sample CSVs for locations, facilities, competitors, transit stops, and foot traffic; added `infra/demo-data/import-demo-data.ps1`; documented the demo import flow in `docs/demo-data.md` and linked it from `README.md`.
- Verification: `Get-ChildItem infra\demo-data\*.csv | ForEach-Object { Import-Csv ... }` passed with 3 locations, 6 facilities, 5 competitors, 8 transit stops, and 9 foot traffic rows; PowerShell parser check for `infra\demo-data\import-demo-data.ps1` passed.
- Verification: `docker compose config` could not run because `docker` is not installed or not available on PATH in this environment.
- Notes: data is synthetic, intentionally small, and centered around Gangnam-gu, Seoul so the local demo can be prepared quickly without external GIS datasets.
