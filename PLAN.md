# PLAN.md — Actionable Next Steps

**Last updated**: 2026-09-09
**Current state**: Phase 2 complete · Phase 3 (backend) pending

---

## Completed (Audit & Documentation)

- [x] Comprehensive codebase audit (Android + Web + docs)
- [x] Created `AGENTS.md` — Coding agent guide with architecture, entry points, known issues
- [x] Created `PROJECT_STATE.md` — Detailed current state with discrepancy analysis
- [x] Created `ROADMAP.md` — 8-phase development roadmap
- [x] Created `PHASES.md` — Phase-by-phase breakdown with deliverables
- [x] Created `PLAN.md` — This file

---

## Phase A: Documentation Corrections (High Priority)

### A.1: Resolve Labor Rate Discrepancies
**Files affected**: `app/src/main/java/com/example/domain/model/MarketContext.kt`, `web/src/config/markets.ts`

| Country | Android | Web | Action |
|---------|---------|-----|--------|
| SA | 25.0 SAR | 45.0 SAR | ❓ Investigate which is correct |
| AE | 25.0 AED | 50.0 AED | ❓ Investigate which is correct |
| EG | 180.0 EGP | 150.0 EGP | ❓ Investigate which is correct |
| LY | 20.0 LYD | 25.0 LYD | ❓ Investigate which is correct |

**Decision needed**: Confirm source of truth (field data? official BTP tariff?) before aligning.

### A.2: OpenAPI Version Alignment — ✅ Resolved (non-issue)
- `docs/openapi.yaml` declares `openapi: 3.1.0`
- Verified 2026-09-09: no repo file references "OpenAPI 3.0"; the discrepancy existed only in the prior audit summary. Documentation updated to state the verified fact.

### A.3: `.env.example` Templates — ✅ Done
- Root `.env.example` already existed with correct `ADMIN_EMAIL` / `ADMIN_PASSWORD_CREDENTIAL` placeholders (no change needed)
- `web/.env.example` existed but defined `EMAIL` / `PASSWORD` — keys matched nothing in code; renamed to `NEXT_PUBLIC_ADMIN_EMAIL` / `NEXT_PUBLIC_ADMIN_CREDENTIAL` (API/locale vars kept)

### A.4: README.md — ✅ Done (created externally; reviewed)
- Covers structure, offline-first architecture, features, run instructions, credential generation
- Two factual fixes applied: language list (`Derja (derja)`, not "Algérien (dz)") and `AppLanguage.entries` (was `AppLanguage.tr`)
- Added a note that the web gate uses `NEXT_PUBLIC_*` keys in `web/.env`

### A.5: Web Admin Gate Env Keys — ✅ Fixed
- `web/.env` keys renamed to `NEXT_PUBLIC_ADMIN_EMAIL` / `NEXT_PUBLIC_ADMIN_CREDENTIAL` (values unchanged)
- Bug: unprefixed keys are never inlined by Next.js static export → gate was permanently fail-closed

---

## Phase B: Backend API Implementation (Next Priority)

> The Web API client (`endpoints.ts`) is already fully implemented against the OpenAPI contract. Implementing the server will immediately unlock Web-backend connectivity.

### B.1: Choose Tech Stack
- **Recommended**: Node.js + Express (TypeScript) or Go
- **Database**: PostgreSQL (matches OpenAPI schema design)
- Rationale: OpenAPI uses JSON/REST, TypeScript types already generated, easy to match types

### B.2: Implement Authentication Endpoints (4 operations)
- `POST /auth/login` — Password (PBKDF2) + Google OAuth2
- `POST /auth/refresh` — Token refresh
- `POST /auth/logout` — Session termination
- `GET /auth/me` — User claims
- `GET /users/me` — User profile
- `POST /companies` — Company registration
- `GET/PATCH /companies/{id}` — Company management

### B.3: Implement Catalog Endpoints (read-only)
- `GET /trades`, `GET /trades/{id}`, `GET /trades/{id}/work-types`
- `GET /materials`, `GET /materials/{id}`, `GET /materials/{id}/prices`
- `GET /prices`, `GET /markets`, `GET /countries`, `GET /currencies`

### B.4: Implement Business Entity Endpoints (CRUD)
- `GET/POST /projects`, `GET/PATCH/DELETE /projects/{id}`
- `GET/POST /estimates`, `GET/PATCH/DELETE /estimates/{id}`
- `POST /calculations` — Server-side calculation (mirror Android engine)
- `POST /sync/delta` — Offline-first sync protocol

### B.5: Database Schema
- 14 core tables matching OpenAPI schemas
- Seed data aligned with Android `InitialData.kt` (9 countries, 36+ materials)
- Price indexes on (countryCode, materialId, packageId, effectiveFrom)

---

## Phase C: Android Integration (Backend Connectivity)

### C.1: Retrofit + OkHttp
- Uncomment dependencies in `build.gradle.kts` (lines 124, 121, 122)
- Create API service interface matching `endpoints.ts`
- Create remote data source implementations of `PriceRepository`, `EstimateRepository`, `UserSettingsRepository`

### C.2: Sync Strategy
- Implement `POST /sync/delta` with conflict resolution
- Offline-first: Room is source of truth, sync reconciles changes
- Token refresh: handle 401 with automatic token refresh

---

## Phase D: Web Dashboard Business Pages

### D.1: Auth Flows
- Login page with email/password + Google OAuth2
- JWT session management (HttpOnly cookies)
- Route protection middleware

### D.2: Business Pages
- Quotes list + detail + create/edit
- Project management (CRUD)
- Material catalog browser
- Price management by market
- Market context dashboard
- Calculation form with live API computation

---

## Phase E: Testing & CI/CD

### E.1: Testing
- Backend API integration tests (supertest or similar)
- Web component tests (React Testing Library or Playwright)
- Cross-platform test vector alignment (prices, calculations, auth tokens)

### E.2: CI/CD
- GitHub Actions: Android (`./gradlew test`)
- GitHub Actions: Web (`npm run type-check && npm test`)
- GitHub Actions: Backend (build, test, deploy)
- Deployment: Web to Vercel, Backend to container service

---

## Quick Start (After Audit)

1. **Resolve labor-rate discrepancies** → Phase A.1 (requires business decision on correct values)
2. ~~Create env templates~~ ✅ Done (root + web; web keys corrected)
3. **Implement backend** → Phase B (unlocks Web API client connectivity)
4. **Wire Android to backend** → Phase C (uncomment Retrofit deps)
5. **Build Web business pages** → Phase D
6. **Add CI/CD** → Phase E

## Priority Order

| Priority | Task | Rationale |
|----------|------|-----------|
| 🔴 P0 | Fix labor rate discrepancies | Data integrity issue — calculations differ between platforms |
| 🟠 P1 | Implement backend API | Unlocks 30 existing Web API client operations |
| ✅ Done | Create `.env.example` files | Existed; web admin keys corrected |
| ✅ Done | Fix OpenAPI version docs | Verified non-issue — no repo file says 3.0 |
| ✅ Done | Fix web admin gate env keys | `NEXT_PUBLIC_*` names now match `page.tsx` |
| 🟢 P3 | Android → Backend retrofit | Enables cloud sync and cross-device consistency |
| 🟢 P3 | Web business pages | Completes admin dashboard functionality |