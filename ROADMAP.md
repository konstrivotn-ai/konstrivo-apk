# ROADMAP.md — Konstrivo Pro Development Roadmap

**Last updated**: 2026-09-09
**Current commit**: `5771e1a` (feat(ui): polish micro UX and accessibility)
**Branch**: `main`

---

## Overview

Konstrivo Pro follows a staged development approach. The Android calculation engine and
local persistence are complete. The Web dashboard has a functional homepage and API client
but lacks business pages. The backend API server is entirely unimplemented.

The roadmap below is organized by **Phase** (logical grouping) with ** Milestones** (deliverable
targets) and **Key Results** (measurable outcomes).

---

## Phase 1: Android Calculation Engine ✅ Complete

**Status**: ✅ Done (commits 1–17)

### Milestone 1.1: Core Calculator
- ✅ `ConstructionCalculator.kt` — 764 lines, supports 6 trades × 17 calculation types
- ✅ `PaintCalculator.kt` — Paint-specific coverage/coat calculations
- ✅ `PaintCalculationConstants.kt` — Coverage rates, default waste margins
- ✅ Calculation types: Placo (9), Peinture (2), Maçonnerie (1), Crépissage (1), Carrelage (1), Menuiserie (2)

### Milestone 1.2: Domain Models & Catalogs
- ✅ `Models.kt` — Country (9), AppLanguage (4), TradeCategory (6), CalculationType (17), CalculatedItem, CalculationSummary
- ✅ `MarketContext.kt` — 9-country labor rates, exchange rates, waste margins
- ✅ `FiscalCatalog.kt` — TVA profiles per country (13–20%), TaxMath utility
- ✅ `CurrencyConverter.kt` — DefaultExchangeRateProvider with 9 fixed rates vs TND
- ✅ `PriceModels.kt` — PriceSource (7 sources), MaterialPrice, PriceHistoryRecord
- ✅ `MaterialCatalog`, `TradeCatalog`, `PaintProductCatalog`, `PaintPackageCatalog`
- ✅ `PriceSelection.kt` — Package-aware price map selection

### Milestone 1.3: Security & Auth
- ✅ `AdminAuth.kt` — PBKDF2-HMAC-SHA256, fail-closed, 150,000 iterations
- ✅ Test vector: password `KstrvFixVec-2026-R001`, email `owner@example.com`

---

## Phase 2: Android Data & Presentation ✅ Complete

**Status**: ✅ Done (commits 3–22)

### Milestone 2.1: Room Persistence
- ✅ `AppDatabase.kt` — Version 2, exportSchema=false
- ✅ 3 entities: `MaterialPriceEntity`, `ProjectEstimateEntity`, `UserSettingsEntity`
- ✅ 3 DAOs: MaterialPriceDao (8 queries), ProjectEstimateDao (5 queries), UserSettingsDao (3 queries)
- ✅ `Migration1To2.kt` — Adds `packageId` column to `material_prices` table
- ✅ `InitialData.kt` — Generates default prices for 9 countries with 36+ material entries

### Milestone 2.2: Repository & Use Cases
- ✅ `ConstructionRepository.kt` — Implements 4 clean architecture interfaces
- ✅ `RepositoryInterfaces.kt` — MaterialRepository, PriceRepository, EstimateRepository, UserSettingsRepository
- ✅ 10 use cases: CalculateConstruction, SaveQuote, DeleteQuote, GetMarketPrices, ResetPrices,
  FormatWhatsAppQuote, GetSavedQuotes, GetUserSettings, UpdateMaterialPrice, UpdateUserSettings

### Milestone 2.3: UI Layer
- ✅ 5 ViewModels: Calculator, Devis, Main, Prices, Settings
- ✅ 10 screens: AdminDashboard, Calculator, ChantierGuide, Home, Login, MarketPrices, More, ReportsDevis, Settings, TradeDetails
- ✅ Design system: KonstrivoComponents, KonstrivoSystemComponents, KonstrivoLocalization, CommonComponents
- ✅ Theme: R1 design (gold #F59E0B on navy #0F172A), light/dark, 4 languages

### Milestone 2.4: Tests
- ✅ 8 test files: 6 unit (JUnit 4 + Robolectric), 2 instrumented (AndroidX)
- ✅ ConstructionCalculatorTest — Placo quantity/cost formulas
- ✅ AdminAuthTest — PBKDF2 verification (shared test vector with Web)
- ✅ MigrationIntegrationTest — Room v1→v2 migration

### Milestone 2.5: Web Foundation
- ✅ Next.js 15.1.7 (static export) + Tailwind CSS 3.4.17
- ✅ Full TypeScript API client (`ApiClient` + `KonstrivoApi`, 30 operations)
- ✅ Web admin auth (`adminAuth.ts`) mirrors Android `AdminAuth.kt`
- ✅ 3 config files: `markets.ts` (9), `trades.ts` (6), `languages.ts` (4)
- ✅ `money.ts`, `errors.ts` utilities
- ✅ Homepage with verification & audit toolkit
- ✅ 2 test files (15 test cases) using Node.js `node:test`

---

## Phase 3: Backend API Server 🚧 Not Started

**Status**: ⏳ Next priority (0% complete)

The OpenAPI contract (`docs/openapi.yaml`, 30 operations, 17 schemas) is fully specified
but no server code exists. The Web client has a complete API client ready to use once the
server is implemented.

### Milestone 3.1: Authentication & Users
- [ ] POST `/auth/login` — Password + Google OAuth2
- [ ] POST `/auth/refresh` — Token refresh
- [ ] POST `/auth/logout` — Session termination
- [ ] GET `/auth/me` — User claims
- [ ] GET `/users/me` — User profile and defaults
- [ ] POST `/companies` — Register contractor company
- [ ] GET/PATCH `/companies/{companyId}` — Company profile management

### Milestone 3.2: Catalogs (Read-only)
- [ ] GET `/trades` — List all trades
- [ ] GET `/trades/{tradeId}` — Trade detail
- [ ] GET `/trades/{tradeId}/work-types` — Work types per trade
- [ ] GET `/materials` — Search/filter materials
- [ ] GET `/materials/{materialId}` — Material detail
- [ ] GET `/materials/{materialId}/prices` — Material price history
- [ ] GET `/prices` — Market prices by country/region
- [ ] GET `/markets` — Market contexts (9 countries)
- [ ] GET `/countries` — Country list
- [ ] GET `/currencies` — Supported currencies

### Milestone 3.3: Business Entities
- [ ] CRUD for `/projects` (GET list, POST, GET/PATCH/DELETE by ID)
- [ ] CRUD for `/estimates` (GET list, POST, GET/PATCH/DELETE by ID)
- [ ] POST `/calculations` — Server-side calculation (cloud mirror of Android engine)
- [ ] POST `/sync/delta` — Offline-first synchronization protocol

### Milestone 3.4: Infrastructure
- [ ] Choose runtime (Node.js/Express or Go recommended per API_CONTRACT_REVIEW.md)
- [ ] PostgreSQL schema design (17 entities from OpenAPI schemas)
- [ ] Connection pooling, environment configuration
- [ ] JWT secret management, token signing
- [ ] Dockerfile for containerized deployment

---

## Phase 4: Database Schema 🚧 Not Started

**Status**: ⏳ Planned

### Milestone 4.1: Schema Design
- [ ] Design 3+ core tables: `companies`, `users`, `trades`, `work_types`, `materials`,
  `material_prices`, `markets`, `countries`, `currencies`, `projects`, `estimates`,
  `estimate_items`, `price_histories`, `price_overrides`, `admin_credentials`
- [ ] Index strategy for price lookups (country + material + package + date)
- [ ] Foreign key constraints and referential integrity

### Milestone 4.2: Migrations & Seed Data
- [ ] Initial schema migration scripts
- [ ] Seed data for 9 markets, 6 trades, 17 calculation types, 36+ materials
- [ ] Price data for 9 countries (align with Android `InitialData.kt`)

### Milestone 4.3: Android Migration
- [ ] Migrate Android Room schema to align with backend (or vice versa)
- [ ] Implement Room → API sync strategy (offline-first with conflict resolution)

---

## Phase 5: Backend Integration 🚧 Not Started

**Status**: ⏳ Planned

### Milestone 5.1: Android → Backend
- [ ] Implement Retrofit API client in Android (currently commented out in build.gradle.kts)
- [ ] Implement remote data source for `PriceRepository`, `EstimateRepository`, `UserSettingsRepository`
- [ ] Offline-first sync via `POST /sync/delta` with conflict resolution
- [ ] Token management (access + refresh tokens)

### Milestone 5.2: Web → Backend
- [ ] Wire existing `KonstrivoApi` client to live backend (replace contract-only types)
- [ ] Implement auth provider with JWT token management
- [ ] Implement business pages (quotes list, material management, pricing)

### Milestone 5.3: Shared Authentication
- [ ] Align backend auth with existing PBKDF2 client-side auth
- [ ] Server-side password hash verification (upgrade path from client-side gate)
- [ ] Role-based access control (admin, contractor, viewer)

---

## Phase 6: Web Dashboard Business Pages 🚧 Minimal

**Status**: ⚠️ 10% complete (homepage only)

### Milestone 6.1: Auth Flows
- [ ] Login page with email/password + Google OAuth2
- [ ] Session management (JWT in HttpOnly cookies or secure storage)
- [ ] Auth middleware/protection for private routes

### Milestone 6.2: Business Pages
- [ ] Quotes/Devis list page (GET `/estimates`)
- [ ] Quote detail page (GET `/estimates/{id}`)
- [ ] Quote creation/edit (POST/PATCH `/estimates`)
- [ ] Project management pages (full CRUD)
- [ ] Material catalog browser (GET `/materials`)
- [ ] Price management by market (GET `/prices`)
- [ ] Market context dashboard (GET `/markets`)

### Milestone 6.3: Calculation Interface
- [ ] Web-based calculation form (mirror Android calculator)
- [ ] Live calculation via `POST /calculations`
- [ ] Results display with material breakdown and totals

---

## Phase 7: Testing & CI/CD 🚧 Not Started

**Status**: ⏳ Planned

### Milestone 7.1: Testing
- [ ] Android instrumentation tests for Room DAOs
- [ ] Backend API integration tests
- [ ] Web component tests (React Testing Library)
- [ ] Cross-platform test vector alignment (prices, calculations, auth)

### Milestone 7.2: CI/CD
- [ ] GitHub Actions workflow for Android (`./gradlew test`)
- [ ] GitHub Actions workflow for Web (`npm run type-check && npm test`)
- [ ] Backend build + test + deploy pipeline
- [ ] Automated deployment to production

---

## Phase 8: Polish & Release 🚧 Planned

**Status**: ⏳ Future

### Milestone 8.1: UX Refinements
- [ ] Fix 4 labor rate discrepancies (SA, AE, EG, LY) between Android and Web
- [ ] Full RTL support testing (Arabic, Derja)
- [ ] Accessibility audit (WCAG 2.1 AA)

### Milestone 8.2: Documentation
- [x] Create `README.md` (project overview, setup, build instructions)
- [x] Create `web/.env.example` (admin keys corrected to `NEXT_PUBLIC_*`)
- [x] Create root `.env.example` (Android BuildConfig keys; already existed)
- [ ] Generate API documentation from OpenAPI spec (Redoc/swagger-ui)
- [ ] Update `API_CONTRACT_REVIEW.md` with implementation status

### Milestone 8.3: App Store / Web Launch
- [ ] Android release build with proper signing
- [ ] Web deployment (Vercel or similar static hosting)
- [ ] Analytics integration