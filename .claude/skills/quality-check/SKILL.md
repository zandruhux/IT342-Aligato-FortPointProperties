---
name: quality-check
description: Run the full FortPoint quality gate (frontend lint/typecheck/unit tests/build and backend tests/ArchUnit/Checkstyle/SpotBugs) and report results. Use before committing, before opening a PR, or when asked to "check", "verify" or "run the quality gate".
---

# Quality check

Run both halves, then report a short pass/fail table. Do not "fix" failures
unless the user asks; report them with file:line.

## Frontend (from `web/`)

```bash
npm run lint:ci        # ESLint, fails above the warning budget (ratchet)
npm run typecheck      # tsc --noEmit
npm run test           # Vitest + React Testing Library
npm run build          # Vite production build
npm run format:check   # Prettier (report only: legacy files are not yet formatted)
```

Optional, slower: `npm run test:e2e` (Playwright, starts the dev server).

## Backend (from `backend/`)

```bash
./mvnw -B verify
```

`verify` runs: unit + integration tests (H2 `test` profile, never the real DB),
ArchUnit rules, JaCoCo coverage, Checkstyle, SpotBugs + FindSecBugs.

## Reading results

- ArchUnit "frozen" rules only fail on NEW violations. If a violation was fixed,
  the store in `backend/src/test/resources/archunit_store` shrinks: include it in the commit.
- SpotBugs known-issue baseline lives in `backend/config/spotbugs/exclude.xml`
  (section "KNOWN ISSUES BASELINE"). Never add a new exclusion without a reason comment.
- ESLint warning budget is `--max-warnings` in `web/package.json` `lint:ci`.
  Lower it when warnings are fixed; never raise it to make CI pass.

## Report format

| Check | Result | Notes |
|---|---|---|
| ESLint | ✅/❌ | errors / warnings |
| ... | | |
