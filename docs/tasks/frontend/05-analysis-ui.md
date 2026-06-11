# frontend TASK-05: Implement Analysis UI

## Status

todo

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
