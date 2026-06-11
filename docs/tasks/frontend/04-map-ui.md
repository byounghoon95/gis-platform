# frontend TASK-04: Implement Map UI

## Status

todo

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
