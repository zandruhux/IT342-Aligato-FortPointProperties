# FortPoint Properties — Claude Code guide

Monorepo: `web/` (React 19 + Vite 7, JS migrating to TS), `backend/` (Spring Boot 3.5, Java 17),
`mobile/` (Android/Kotlin, out of scope for these tools). Full standards: `docs/ENGINEERING_STANDARDS.md`.

## Commands

| Where | Command | What |
|---|---|---|
| web | `npm run verify` | lint + typecheck + unit tests + build |
| web | `npm run test:e2e` | Playwright smoke tests (starts dev server) |
| web | `npm run format` | Prettier write |
| backend | `./mvnw -B verify` | tests (H2) + ArchUnit + JaCoCo + Checkstyle + SpotBugs |
| backend | `./mvnw test -Dtest=ClassName` | single test class |

Skills: `/quality-check`, `/backend-feature`, `/react-feature`.

## Non-negotiables

- Backend layering: controller → service → repository. Controllers never touch repositories or
  entities; APIs take and return DTOs. Constructor injection only. Errors via `AppException` +
  `GlobalExceptionHandler`. New routes must be added to `SecurityConfig` with the narrowest role.
- Frontend: feature folders (`api/hooks/components/pages`), API calls only via `shared/utils/api`,
  new files in TypeScript, Rules of Hooks always.
- Tests never use real infrastructure: backend tests run on the `test` profile (H2, dummy secrets).
- Never read or edit `.env` files or commit secrets. Use `.env.example` for new variables.
- Don't weaken a quality gate to get green: don't raise the ESLint `--max-warnings` budget, add
  SpotBugs exclusions without a reason, or refreeze ArchUnit violations. Fix or ask.
- Keep changes scoped; don't reformat files you aren't otherwise changing.

## Docs lookup

Use the Context7 MCP for library/framework docs (React, Vite, Vitest, Spring Boot, Spring Security...).
