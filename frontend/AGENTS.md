# Frontend Agent Guide

Track-local rules for `frontend/`. Root rules live in the repo-root `AGENTS.md`.

## Stack

- Language: JavaScript
- Framework: React
- Bundler / dev server: Vite
- Server state: TanStack Query
- Client state: Zustand
- Map: Google Maps JavaScript API
- Styling: Tailwind CSS
- Charts: Recharts

## Coding Standards

- Build the dashboard as the primary experience, not a marketing landing page.
- Handle loading, error, empty, unauthorized, and missing API key states.
- Keep API client code thin and typed.
- Keep map, list, detail panel, and upload flows in clear module boundaries.
- Do not hard-code sensitive values. Google Maps API key must come from environment variables.
- Make desktop layouts dense and scannable.

## Verification

Run before declaring a frontend task complete:

- lint, for example `npm run lint`
- test, for example `npm test` if configured
- build, for example `npm run build`

Record the command output in the task's `Completion Notes`. If a check cannot run, state why.
