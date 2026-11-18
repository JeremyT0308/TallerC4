# TallerC4 Complete Web Application Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build and verify the complete TallerC4 React/Spring Boot application with SRI integrations, ANT simulation/adapter boundary, Redis cache resilience, documentation and reproducible evidence steps.

**Architecture:** A modular Spring Boot monolith exposes a small REST API and depends on provider ports. SRI adapters call the verified official REST paths; ANT has real and deterministic simulated adapters behind the same port; Redis is an optional cache-aside adapter with stale fallback. A Vite React frontend consumes only the internal API.

**Tech Stack:** Java 21, Spring Boot 3.5.x, Maven, JUnit 5, WireMock, Redis client/Spring Data Redis, React 19, Vite, TypeScript, Vitest, Testing Library.

**Spec:** `docs/superpowers/specs/2026-11-17-tallerc4-design.md`

## Global Constraints

- The external SRI URLs and response fields documented in the design are the only verified provider contracts.
- ANT must never silently turn simulated or cached data into official data.
- Cache only successful ANT results; stale responses include provenance and timestamps.
- Do not commit `.env`, credentials, personal data, generated builds, or `BITACORA_PRIVADA.md`.
- Do not create or edit C4 diagrams; document C4 inventory as text.
- Spanish-language UI and structured JSON errors are required.

## Review Focus

- RUC checksum-valid but non-existing: return a clear not-found state without attempting detail mapping.
- SRI returns malformed JSON or an empty list: surface provider-unavailable/empty distinctly, never fabricate a taxpayer.
- ANT real adapter returns 401/HTML: classify as unavailable and preserve a valid cached result if eligible.
- Redis is down during an ANT success: return live result and keep the failure observable without failing the whole request.
- Stale cache is older than the maximum: return 503 with an actionable structured error, not stale data.

### Task 1: Bootstrap repository, design documents and test harness

**Files:**
- Create: `README.md`, `.gitignore`, `.env.example`, `.gitattributes` updates as needed
- Create: `backend/pom.xml`, `backend/src/main/java/.../TallerC4Application.java`, `backend/src/test/.../BootstrapTest.java`
- Create: `frontend/package.json`, `frontend/vite.config.ts`, `frontend/src/main.tsx`, `frontend/src/App.test.tsx`
- Modify: `docs/superpowers/specs/2026-11-17-tallerc4-design.md`

**Interfaces:** Produces runnable backend/frontend skeletons and test commands used by later tasks.

- [ ] Write smoke tests for Spring context and frontend render.
- [ ] Run them and observe the expected missing-scaffold failures.
- [ ] Add minimal Maven/Vite scaffolds and Spanish app shell.
- [ ] Run backend and frontend tests; require green.
- [ ] Commit `chore: bootstrap tallerc4 monorepo`.

### Task 2: Backend domain, validation, errors and internal REST contract

**Files:**
- Create: backend domain records for taxpayer, vehicle, ANT result, provenance and errors
- Create: validation utilities, application service ports, controllers and global exception handler
- Create: unit tests for RUC checksum, email/plate validation, error mapping and controller JSON
- Create: `docs/contracts/openapi.yaml`, `docs/contracts/examples/`, `docs/contracts/ant-contract.md`

**Interfaces:** `TaxpayerService`, `VehicleService`, `AntPointsService`; endpoints `GET /api/v1/taxpayers/{ruc}`, `GET /api/v1/vehicles/{plate}`, `GET /api/v1/ant/points?identification=...`.

- [ ] Write failing tests for valid/invalid inputs and the exact error envelope.
- [ ] Implement domain and REST boundary minimally.
- [ ] Run focused tests then the complete backend suite.
- [ ] Commit `feat: define internal contracts and validation`.

### Task 3: SRI adapters and mapping tests

**Files:**
- Create: `SriTaxpayerAdapter`, `SriVehicleAdapter`, HTTP client configuration and mappers
- Create: WireMock/MockWebServer contract fixtures matching observed SRI responses
- Create: adapter tests for 200, empty, 4xx/5xx, timeout and malformed payloads
- Modify: application services to use provider ports

**Interfaces:** SRI ports return stable internal DTOs and typed provider failures; controllers remain provider-agnostic.

- [ ] Write failing adapter contract tests from the observed JSON fields.
- [ ] Implement HTTP adapters with configured connect/read timeouts and safe logs.
- [ ] Run adapter and service tests; require green.
- [ ] Commit `feat: integrate sri taxpayer and vehicle services`.

### Task 4: ANT real/simulated adapters and resilient application flow

**Files:**
- Create: ANT port, `RealAntAdapter`, `SimulatedAntAdapter`, scenario selector and parser
- Create: tests for success, no-results, 502, timeout, invalid response, 401 and cache eligibility
- Modify: application service and controller for source/simulated/stale metadata

**Interfaces:** Both adapters implement `AntProvider.queryPoints(String identification) -> AntProviderResult`; application returns `AntResultResponse` or a structured 503.

- [ ] Write failing tests for each deterministic simulation and real-provider failure classification.
- [ ] Implement adapters and configuration selection; keep assumed ANT contract isolated and documented.
- [ ] Run focused and full backend tests.
- [ ] Commit `feat: add ant provider boundary and simulation modes`.

### Task 5: Redis cache-aside and stale fallback

**Files:**
- Create: Redis configuration, cache record/repository port, Redis adapter and in-memory fallback used only for tests
- Create: tests for fresh hit, live miss, stale fallback, expired cache, unsuccessful response exclusion and Redis outage
- Modify: ANT service/configuration and `.env.example`

**Interfaces:** `AntCache` supports `find`, `saveSuccessful`, and freshness/max-stale evaluation; service exposes `stale`, `retrievedAt`, `staleSince`, `source`.

- [ ] Write failing cache/resilience tests first.
- [ ] Implement cache-aside with TTL/freshness policy and graceful Redis failure.
- [ ] Run full backend suite plus a Redis Testcontainers/integration profile where available.
- [ ] Commit `feat: add redis ant cache resilience`.

### Task 6: React application and user-facing state model

**Files:**
- Create: `frontend/src/api/`, components, hooks/state, styles and accessibility helpers
- Create: frontend tests for validation, loading, success, empty, unavailable and stale states
- Modify: app shell and package scripts

**Interfaces:** Frontend calls only internal REST endpoints and renders provenance badges; supports an explicit demo scenario selector.

- [ ] Write failing component tests for the complete workflow.
- [ ] Implement responsive Spanish UI without hardcoded successful production data.
- [ ] Run frontend tests and production build.
- [ ] Commit `feat: build spanish responsive frontend`.

### Task 7: Documentation, usability/evidence materials and end-to-end verification

**Files:**
- Create/update: `docs/architecture/*`, `docs/usability/*`, `docs/evidence/*`, `README.md`, `.env.example`
- Create: smoke/e2e scripts and representative test fixtures (not fabricated evidence)
- Create ignored: `BITACORA_PRIVADA.md`

- [ ] Write verification checklist and docs tests/linters where practical.
- [ ] Run backend tests, frontend tests/build, API smoke tests against local app, and external SRI endpoint checks.
- [ ] Fix any failures with regression tests first.
- [ ] Commit `docs: complete architecture usability and verification guide`.

### Task 8: Final review, commit/push status and handoff

- [ ] Inspect complete diff, secrets, ignored files, and commit history.
- [ ] Perform a self-review against the spec and review focus.
- [ ] Run final verification commands from a clean working state.
- [ ] Push the current branch to its configured GitHub remote if authentication and branch policy permit.
- [ ] Report actual test results, limitations, commits and manual evidence steps in Spanish.

