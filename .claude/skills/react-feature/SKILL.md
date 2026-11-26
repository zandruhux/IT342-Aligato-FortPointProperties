---
name: react-feature
description: Build or extend a React feature in web/ following the project's feature-folder layout (api / hooks / components / pages), shared axios client, error normalisation, TypeScript-first new code, and Vitest + React Testing Library tests. Use when adding a page, component, hook, or API call to the frontend.
---

# React feature (React 19 + Vite 7 + Tailwind 4)

Layout under `web/src/features/<feature>/`:

```
api/         <feature>Api.ts      calls via shared/utils/api (axiosInstance); throw normalizeApiError(...)
hooks/       use<Thing>.ts        data fetching + state; components stay presentational
components/  <Thing>.tsx          reusable, props-driven, no direct API calls
pages/       <Thing>Page.tsx      route-level composition only
index.ts                          public exports of the feature
```

Shared, cross-feature code goes in `web/src/shared/` (ui components, context, utils).

## Rules

1. **New files are `.ts` / `.tsx`.** Type props and API DTOs explicitly; no `any`.
   Existing `.js/.jsx` can stay until touched for a real change.
2. **API access only through `shared/utils/api` (`axiosInstance`)** so auth headers and 401 handling
   stay in one place. Do not create new axios instances. Do not read `localStorage` tokens in components.
3. **Hooks rules:** never call a hook after an early `return` or inside a condition.
   Avoid `setState` directly in `useEffect` bodies; derive values during render or use
   event handlers. Subscribe/cleanup pattern for sockets and timers.
4. **Memoisation:** don't add `useMemo`/`useCallback` by default; add when profiling or a
   dependency array needs a stable reference.
5. **No duplicated logic:** before writing a helper, search `shared/utils` and the feature's `hooks/`.
6. **Security:** never use `dangerouslySetInnerHTML` with API data; never put tokens or PII in URLs.
7. **Styling:** Tailwind utility classes; extract repeated class sets into a component, not a string constant.
8. **Accessibility:** buttons are `<button>`, inputs have labels, images have `alt`.

## Tests

- Co-locate `<Thing>.test.tsx` next to the file. Query by role/label (`getByRole`, `getByLabelText`), use
  `@testing-library/user-event` for interaction, mock the feature's `api/` module with `vi.mock`.
- E2E smoke flows go in `web/e2e/*.spec.js` (Playwright).

Finish by running from `web/`: `npm run lint && npm run typecheck && npm run test`.
