# TallerC4 UX/UI Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Modernize the existing React frontend with component-based structure, CSS-first motion, accessible interaction feedback, and genuine browser evidence while preserving all API behavior.

**Architecture:** Keep the existing React + Vite frontend and move the monolithic `App.tsx` presentation into focused components. App state and API handlers remain at the page level; child components receive typed props and callbacks. No backend, API contract, IcePanel diagram, or animation dependency changes.

**Tech Stack:** React, TypeScript, Vite, Vitest, Testing Library, Playwright/Edge evidence script, CSS keyframes and media queries.

**Spec:** Approved UX/UI request in the current conversation; baseline captured at `docs/evidence/screenshots/ux-before.png`.

## Global Constraints

- Preserve SRI/ANT functionality, API contracts, data provenance and all existing validations.
- Do not modify `backend/` or IcePanel diagrams.
- Add no new animation library or runtime dependency.
- Keep keyboard accessibility and `prefers-reduced-motion` support.
- Do not claim human usability results; only report automated browser checks.
- Do not commit `BITACORA_PRIVADA.md` or secrets.
- Commit and push only after frontend tests, build and browser checks pass.

## Review Focus

- A toast must announce success, warning and error without replacing inline validation; test visible toast copy and `role=status`/`role=alert` behavior.
- Loading buttons must be disabled, expose `aria-busy` and show an animated indicator while a real request is pending; test with a deferred fetch.
- Result cards must preserve accessible result labels and provenance while allowing details to collapse; test live/stale/error result content.
- Progress navigation must scroll/focus the correct query section and remain usable on mobile; test buttons and section IDs.
- Reduced-motion users must receive readable transitions without keyframe animation; verify the CSS media query and keep content visible.

---

### Task 1: Preserve baseline and add behavioral tests

**Files:**
- Copy: `docs/evidence/screenshots/01-initial-responsive-flow.png` → `docs/evidence/screenshots/ux-before.png`
- Modify: `frontend/src/App.test.tsx`
- Test: `frontend/src/App.test.tsx`

**Interfaces:**
- Existing API mocks remain unchanged.
- New UI expectations target progress navigation, toast notifications, loading affordance and expandable result cards.

- [ ] **Step 1: Preserve the original screenshot**

Use `Copy-Item` only; do not alter the existing baseline file.

- [ ] **Step 2: Write failing tests**

Add tests for:

- a `nav`/progress control exposing the three query labels;
- a successful ANT response producing a visible success toast;
- a deferred ANT request showing `aria-busy="true"`, disabled submit and spinner label;
- a result card exposing an accessible details toggle while preserving `Resultado ANT`.

- [ ] **Step 3: Run the focused frontend tests**

Run `npm test -- --run src/App.test.tsx --reporter=dot` from `frontend/`.

Expected: the new assertions fail because the current UI has no progress navigation, toast or loading component.

### Task 2: Extract component-based frontend structure

**Files:**
- Create: `frontend/src/components/Icons.tsx`
- Create: `frontend/src/components/ProgressNav.tsx`
- Create: `frontend/src/components/LoadingButton.tsx`
- Create: `frontend/src/components/ToastViewport.tsx`
- Create: `frontend/src/components/QuerySectionHeader.tsx`
- Create: `frontend/src/components/TaxpayerQueryCard.tsx`
- Create: `frontend/src/components/VehicleQueryCard.tsx`
- Create: `frontend/src/components/AntQueryCard.tsx`
- Create: `frontend/src/components/ResultCard.tsx`
- Modify: `frontend/src/App.tsx`

**Interfaces:**
- `ProgressNav` receives `{ activeStep, statuses, onNavigate }`.
- `LoadingButton` receives `{ loading, children, loadingLabel, ...buttonProps }` and exposes `aria-busy`.
- `ToastViewport` receives `{ toasts, onDismiss }`.
- Query cards receive their existing data/error/loading values and submit/input callbacks; no API calls move into components.
- `ResultCard` renders an accessible `<article>` with a native `<details>` disclosure and preserves existing `aria-label` values.

- [ ] **Step 1: Implement the smallest components to satisfy the failing tests**

Use inline SVG icons, semantic buttons, native details disclosure, and existing Spanish copy/provenance. Keep API handlers in `App.tsx`.

- [ ] **Step 2: Add toast state and event mapping in `App.tsx`**

Emit success, warning and error toasts from the existing taxpayer, vehicle and ANT handlers. Keep inline field/service errors in place.

- [ ] **Step 3: Run focused tests**

Run `npm test -- --run src/App.test.tsx --reporter=dot`.

Expected: all new behavioral tests pass.

### Task 3: Apply the CSS-first visual system

**Files:**
- Modify: `frontend/src/styles.css`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/components/*.tsx`

**Interfaces:**
- CSS classes are presentation-only; no API or domain types change.
- Animation tokens include entrance, hover lift, toast reveal, spinner and skeleton effects.

- [ ] **Step 1: Compact the header and hero**

Use a compact topbar, a shorter hero, three-step progress navigation and a clear “consulta en 3 pasos” context so query cards appear sooner.

- [ ] **Step 2: Add interaction states**

Style hover/focus-visible/disabled states, interactive result cards, toast variants, loading indicators, field focus rings and mobile stacking.

- [ ] **Step 3: Add reduced-motion rules**

Under `@media (prefers-reduced-motion: reduce)`, disable keyframe motion and set transitions to near-zero while preserving layout and status color.

- [ ] **Step 4: Run unit tests and build**

Run `npm test -- --run --reporter=dot` and `npm run build`.

### Task 4: Extend real browser evidence

**Files:**
- Modify: `frontend/scripts/automated-usability.mjs`
- Modify: `docs/evidence/automated-browser-report.md`
- Create: `docs/evidence/screenshots/ux-after-desktop.png`
- Create: `docs/evidence/screenshots/ux-after-mobile.png`
- Create: `docs/evidence/screenshots/ux-hover.png`
- Create: `docs/evidence/screenshots/ux-toast.png`
- Create: `docs/evidence/screenshots/ux-loading.png`
- Create: `docs/evidence/screenshots/ux-results.png`

**Interfaces:**
- Browser script continues to use the current local backend/frontend and synthetic ANT ID for screenshots.
- SRI RUC/plate checks remain real API checks but screenshots avoid personal identifiers.

- [ ] **Step 1: Add screenshot checkpoints**

Capture desktop, mobile, hovered control, toast, loading, and result states with Playwright/Edge; assert visible labels before each capture.

- [ ] **Step 2: Run the browser script**

Run `npm run e2e:usability` with backend and frontend active.

Expected: all existing tasks plus UX checkpoints PASS and all six named screenshots exist.

### Task 5: Document UX findings and verify the repository

**Files:**
- Create: `docs/usability/ux-improvement-report.md`
- Modify: `docs/usability/report-template.md`
- Modify: `BITACORA_PRIVADA.md` (ignored)

**Interfaces:**
- Documentation distinguishes prior usability observations/proposals from human participant results.
- Report includes before/after comparison, reason for each change and automated test evidence.

- [ ] **Step 1: Write the UX improvement report**

Document observed baseline issues from the existing UI, implemented changes, rationale, accessibility/reduced-motion behavior and remaining human-study work.

- [ ] **Step 2: Run final checks**

Run frontend unit tests, production build, browser E2E, `git diff --check`, and a sensitive-file check.

- [ ] **Step 3: Commit and push**

Use conventional commits and push `main` only after all checks pass. Never stage `BITACORA_PRIVADA.md`.
