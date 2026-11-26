# FortPoint Properties — Engineering Standards

_Baseline established 2026-11-27. Scope: `web/` (React) and `backend/` (Spring Boot). `mobile/` is out of scope._

---

## 1. Codebase analysis (baseline)

### 1.1 Architecture

| Part | Stack | Structure |
|---|---|---|
| `web/` | React 19.2, Vite 7.3, React Router 7, Tailwind 4, axios, STOMP/SockJS | Feature folders `src/features/<feature>/{api,hooks,components,pages}` + `src/shared/{components,context,utils}`. JavaScript only. |
| `backend/` | Spring Boot 3.5.11, Java 17, Spring Security + OAuth2 (Google), JPA/PostgreSQL (Supabase), JJWT 0.11.5, WebSocket/STOMP | Vertical slices `<feature>/{controller,service,repository,entity,dto}` + `shared/{security,exception,dto,validation}`. |

**What's already good:**
- Clear feature-slice structure on both sides.
- Backend: constructor injection everywhere, DTOs on most endpoints, `ApiResponse`/`ErrorDetail` envelope,
  centralised `GlobalExceptionHandler` that hides internals (`SYS-500`), Bean Validation (`@Valid` on 16 endpoints),
  stateless JWT, BCrypt(12), explicit route-by-route authorisation in `SecurityConfig`, configurable CORS.
- Frontend: single axios instance with auth interceptor and 401 handling, `normalizeApiError` helper, hooks per feature.
- 40 existing backend tests (service + controller).

### 1.2 Issues found

Severity: 🔴 high · 🟠 medium · 🟡 low. "Status" says what this setup did about it.

| # | Sev | Area | Finding | Status |
|---|---|---|---|---|
| 1 | 🔴 | Backend tests | `@SpringBootTest` context tests connected to the **real database from `.env`** with `ddl-auto=update`, so `mvn test` could alter the Supabase schema. CI could not run them at all. | **Fixed** — `test` profile (H2 in Postgres mode, dummy secrets) activated by Surefire. |
| 2 | 🔴 | Web deps | `npm audit`: 12 vulns (1 critical `websocket-driver` via sockjs-client, 9 high incl. Vite dev-server file read). | **Fixed** — `npm audit fix` (in-range only, no major bumps). Now 0. |
| 3 | 🔴 | Web | `FavoritePage.jsx` called `useMemo` after an early `return` (Rules of Hooks) — crashes when login state flips. | **Fixed** — hook moved above the return, mapper hoisted to module scope. No behaviour change. |
| 4 | 🟠 | Backend security | `JwtUtil.getSigningKey()` used `secret.getBytes()` (platform charset), so a non-ASCII secret gave different keys on Windows/Java 17 vs Linux. | **Fixed** — `getBytes(StandardCharsets.UTF_8)`. No-op for production (Linux, already UTF-8). Covered by `JwtUtilTest`. |
| 5 | 🟡 | Backend | 5 upload services null-checked `getOriginalFilename()` but then called it a second time to use it, which SpotBugs can't prove non-null. | **Fixed** — single call via `Objects.requireNonNullElse(...)`. Covered by `ArticleStorageServiceTest`. |
| 6 | 🟠 | Backend layering | 4 controllers (`Auth`, `Favorite`, `Messaging`, `PropertyAdmin`) inject `UserRepository` and use `User` entities to resolve the current user — duplicated logic, breaks C-S-R. | Frozen in ArchUnit. Refactor into a `CurrentUserService`. |
| 7 | 🟠 | Backend security | OAuth success/failure handlers `sendRedirect` (FindSecBugs `UNVALIDATED_REDIRECT`). Targets come from config, but verify no request value can reach them. | Baselined; review. |
| 8 | 🟠 | Web security | Access + refresh tokens stored in `localStorage` (31 call sites) — readable by any XSS. | Documented. Longer-term: httpOnly refresh cookie + in-memory access token. |
| 9 | 🟠 | Web | 17 `react-hooks/set-state-in-effect`, 4 `preserve-manual-memoization`, 1 `immutability` warnings (React Compiler rules) — cascading re-renders. | Warnings with a CI budget of 25 (ratchet down). |
| 10 | 🟠 | Web | `PropertyDetailModal.jsx` (1205 lines) and `AdminPropertyCreateModal.jsx` (718) — too large to test or reason about. | Documented; split when next touched. |
| 11 | 🟡 | Web | `articleApi.js` creates a second axios instance for public calls, bypassing the shared client. | Documented. |
| 12 | 🟡 | Backend | DTOs use public fields (≈60 SpotBugs URF/UWF/PA hits); `JwtUtil` uses `@Value` field injection. | DTO noise excluded; new DTOs use records. Field injection frozen. |
| 13 | 🟡 | Backend | `spring.jpa.hibernate.ddl-auto=update` in the main config — schema drift in production. | Documented. Move to Flyway/Liquibase migrations (SQL files already exist). |
| 14 | 🟡 | Repo | `web/.github/workflows/playwright.yml` never ran (GitHub only reads root `.github/`); Playwright config pointed at playwright.dev. | **Fixed** — root `.github/workflows/ci.yml`, real smoke tests. |
| 15 | 🟡 | Repo | `.gitignore` ended in UTF-16 bytes, so `backend/uploads/` was never ignored. `web/.vite/deps` is committed. | `.gitignore` **fixed**. Untrack `.vite` with `git rm -r --cached web/.vite`. |
| 16 | 🟡 | Repo | 151 of ~155 web files not Prettier-formatted. | Prettier configured; CI check is informational until a one-time format commit. |
| 17 | 🟡 | Backend | 3 `if` without braces (`PropertyService`), 3 methods with > 8 params, 1 method > 80 lines. | Checkstyle warnings. |

---

## 2. Coding standards

### 2.1 Frontend (React)

**Structure**
- Feature folders: `features/<feature>/{api,hooks,components,pages,index.ts}`. Cross-feature code → `shared/`.
- `pages/` compose; `components/` are presentational and props-driven; `hooks/` own data fetching and state; `api/` owns HTTP.

**TypeScript**
- New files are `.ts`/`.tsx` (strict mode). Existing JS migrates when a file is meaningfully changed — no mass renames.
- Type API DTOs in the feature's `api/types.ts`, mirroring backend DTO names.

**Components & hooks**
- One component per file; keep files < ~250 lines — extract subcomponents/hooks beyond that.
- Rules of Hooks always (no hooks after early returns/conditions). ESLint enforces it as an error.
- Don't `setState` synchronously in `useEffect`; derive during render, use event handlers, or a data hook.
- Reuse before writing: check `shared/components/ui`, `shared/utils`, and the feature's `hooks/`.
- Memoise (`useMemo`/`useCallback`/`memo`) only for measured cost or a needed stable reference.

**State**
- Local UI state → `useState`. Server data → feature hook (`useXxx`) that exposes `{ data, loading, error, refetch }`.
- Global state only for genuinely global concerns (auth = `AuthContext`). No prop-drilling beyond 2 levels — lift to a hook or context.

**HTTP & security**
- Only `shared/utils/api` (`axiosInstance`). Errors via `normalizeApiError`. No new axios instances.
- Never `dangerouslySetInnerHTML` with API data. No tokens/PII in URLs or logs.

### 2.2 Backend (Spring Boot)

**Layering (ArchUnit-enforced)**
- `controller → service → repository`. Controllers: validation + HTTP mapping only. Services: business rules,
  ownership checks, `@Transactional` on writes. Repositories: data access only.
- Entities never cross the controller boundary; requests and responses are DTOs. New DTOs: Java `record`s.

**SOLID / clean code**
- Constructor injection with `private final` fields; no field injection.
- One responsibility per service; extract collaborators (e.g. storage, current-user lookup) instead of growing services past ~300 lines.
- Methods ≤ 80 lines, ≤ 8 parameters (use a request object), cyclomatic complexity ≤ 15 (Checkstyle warns).

**Errors & validation**
- Throw `AppException(code, message, status)` from services. Codes follow `AREA-NNN` (`AUTH-002`, `VAL-001`).
- `GlobalExceptionHandler` is the only `@RestControllerAdvice`. Never leak exception messages or stack traces for 5xx.
- Validate every `@RequestBody` with `@Valid` + Bean Validation; validate path/query params with `@Validated` constraints.

**Secure REST API design**
- Every route listed in `SecurityConfig` with the narrowest role; fallback stays `authenticated()`.
- Ownership checks in services (users only access their own favorites/applications/conversations).
- Correct status codes: 200/201/204, 400 validation, 401 unauthenticated, 403 forbidden, 404, 409 conflict.
- No secrets in code or `application.properties` — env vars only (`backend/.env.example` lists them).
- `StandardCharsets.UTF_8` for all byte conversions; uploads validated by type and size.
- Log with SLF4J; never log tokens, passwords, or full payloads.

**Testing**
- Services: JUnit 5 + Mockito, one test per business rule. Controllers: `@WebMvcTest` + `@MockitoBean`.
- Integration: `@SpringBootTest` on the `test` profile only. Names: `method_scenario_expected`.

---

## 3. Tooling

### 3.1 Frontend (`web/`, Node ≥ 20.19)

| Tool | Version | Config | Command |
|---|---|---|---|
| ESLint + react-hooks + react-refresh + typescript-eslint | 9.39 / 7.1 / 8.74 | `eslint.config.js` | `npm run lint` · `npm run lint:ci` (warning budget) |
| Prettier (+ eslint-config-prettier) | 3.x | `.prettierrc.json`, `.prettierignore` | `npm run format` · `format:check` |
| TypeScript | 5.9 (typescript-eslint supports < 6.1) | `tsconfig.json` (`allowJs`, strict for TS) | `npm run typecheck` |
| Vitest + React Testing Library + jest-dom + user-event | 4.1 / 16.3 | `vite.config.js` `test`, `src/test/setup.js` | `npm test` · `test:coverage` |
| Playwright | 1.65 | `playwright.config.js`, `e2e/` | `npm run test:e2e` |
| Husky + lint-staged | 9 / 16 (17 needs Node 22) | `.husky/pre-commit`, `package.json` | runs on `git commit` |

### 3.2 Backend (`backend/`, Java 17)

| Tool | Version | Config | Runs in |
|---|---|---|---|
| JUnit 5 + Mockito + Spring Boot Test | managed by Boot 3.5.11 | — | `test` |
| H2 (test DB) | managed by Boot | `src/test/resources/application-test.properties` | `test` |
| ArchUnit | 1.4.1 | `architecture/ArchitectureTest.java`, `archunit.properties`, `archunit_store/` | `test` |
| JaCoCo | 0.8.13 | `pom.xml` | `verify` → `target/site/jacoco/` |
| Checkstyle | plugin 3.6.0 / engine 10.26.1 | `config/checkstyle/` | `verify` |
| SpotBugs + FindSecBugs | 4.9.3.0 / 1.14.0 | `config/spotbugs/exclude.xml` | `verify` |
| Sonar Maven plugin | 5.1 | `pom.xml` | `mvn sonar:sonar` (optional) |

### 3.3 Baselines (how legacy debt is handled without blocking work)

- **ESLint:** React Compiler rules are warnings; CI fails above `--max-warnings=25`. Lower the number as warnings are fixed.
- **ArchUnit:** `FreezingArchRule` records today's violations in `archunit_store/`; only new violations fail. Fixed ones drop out automatically — commit the store.
- **SpotBugs:** pre-existing findings are excluded by *exact class + pattern* under "KNOWN ISSUES BASELINE", so the same bug elsewhere still fails. Delete the entry when fixed.
- **Checkstyle:** correctness rules are errors; size/complexity budgets are warnings.

---

## 4. Automated quality checks

| Where | What runs |
|---|---|
| **Pre-commit** (Husky) | lint-staged: `eslint --fix` + `prettier --write` on staged web files |
| **Claude Code hooks** | Prettier on every file Claude edits in `web/`; block reading/editing `.env`, keys and credential files |
| **CI** (`.github/workflows/ci.yml`, on push/PR to `main`/`dev`) | Frontend: lint budget, typecheck, unit tests + coverage, build, Prettier (informational), `npm audit --omit=dev --audit-level=high` · E2E: Playwright (Chromium) · Backend: `mvn verify` (tests, ArchUnit, JaCoCo, Checkstyle, SpotBugs) · Sonar: when `SONAR_TOKEN` secret exists |
| **SonarQube Cloud** | Maintainability, security hotspots, duplication and coverage for both apps (`sonar-project.properties`) |

Local one-liners: `cd web && npm run verify` · `cd backend && ./mvnw -B verify`.

---

## 5. AI development environment (Claude Code)

| Piece | File | Notes |
|---|---|---|
| Project instructions | `CLAUDE.md` | Commands + non-negotiables, loaded every session |
| MCP: Context7 | `.mcp.json` | Live library docs. Optional `CONTEXT7_API_KEY` env var for higher limits |
| MCP: GitHub | `.mcp.json` | Needs `GITHUB_PERSONAL_ACCESS_TOKEN` env var (fine-grained PAT, this repo only) |
| MCP: SonarQube | `.mcp.json` | Needs Docker + `SONAR_TOKEN`, `SONAR_ORGANIZATION` env vars |
| Skills | `.claude/skills/{quality-check,backend-feature,react-feature}` | `/quality-check`, `/backend-feature`, `/react-feature` |
| Hooks | `.claude/hooks/*.mjs` + `docs/ai-dev/claude-settings.json` | Copy the JSON to `.claude/settings.json` to activate (see README there) |

Tokens are referenced as `${VAR}` and never stored in the repo.

---

## 6. Recommended next steps (in order)

1. ~~Fix findings #4 and #5~~ — done; their SpotBugs baseline entries are removed.
2. Extract a `CurrentUserService` and remove `UserRepository` from the 4 controllers (#6) — the ArchUnit store shrinks.
3. One-time `npm run format` commit for `web/`, then make the Prettier CI step blocking.
4. Work the ESLint warning budget down (#9) and lower `--max-warnings` each time.
5. Introduce Flyway with the existing SQL files and set `ddl-auto=validate` (#13).
6. Move refresh tokens to an httpOnly cookie (#8).
