# PHASES.md — Development Phase Breakdown

**Last updated**: 2026-09-09 | **Branch**: `main` · **HEAD**: `5771e1a`

---

## Phase 1: Domain & Calculation Engine

**Status**: ✅ Complete

The core business logic for construction material estimation is fully implemented and tested.

### Key Files
- `ConstructionCalculator.kt` (764 lines) — Main calculation engine
- `PaintCalculator.kt`, `PaintCalculationConstants.kt` — Paint calculations
- `Models.kt` (~430 lines) — Country (9), AppLanguage (4), TradeCategory (6), CalculationType (17)
- `MarketContext.kt` — Labor rates per M² for 9 countries + exchange rates
- `FiscalCatalog.kt` — TVA profiles (13–20% per country) + TaxMath
- `CurrencyConverter.kt` — ExchangeRateProvider + DefaultExchangeRateProvider

### Calculation Types (17 total)
- **Placo** (9): PLAFOND_FIXE, CLOISON, DEMONTABLE, AQUAPANEL, CACHE_LUMIERE, PVC, DOUBLAGE_COLLE, DOUBLAGE_OSSATURE, PLAFOND_AUTOPORTANT, JOINTS_FINITION
- **Peinture** (2): INTERIEURE, SABLE
- **Maçonnerie** (1): BRIQUE | **Crépissage** (1): CIMENT
- **Carrelage** (1): SOL | **Menuiserie** (2): PORTES, ALU

### Calculation Policies
- `docs/PEINTURE_CALCULATION_POLICY.md` — Paint quantity rules
- `docs/PLACO_MONTANT_POLICY.md` — Gyproc/mounting rules
- `docs/PAINT_PACKAGE_PRICE_MAPPING.md` — Package → price mapping design

---

## Phase 2: Data & Presentation (Android + Web Foundation)

**Status**: ✅ Complete

### Android Data Layer (Room)
- `AppDatabase.kt` — Room v2, 3 entities, migration 1→2, idempotent price seeding on every open
- `Entities.kt` — MaterialPriceEntity, ProjectEstimateEntity, UserSettingsEntity
- `Daos.kt` — 3 DAOs, 16 total queries (Flow + suspend variants)
- `InitialData.kt` — Price generation for 9 countries (36+ material rows)
- `Migration1To2.kt` — Adds `packageId` column to `material_prices`
- `ConstructionRepository.kt` — Implements 4 repository interfaces via Room DAOs

### Android Use Cases (10)
CalculateConstruction, SaveQuote, DeleteQuote, GetMarketPrices, ResetPrices,
FormatWhatsAppQuote, GetSavedQuotes, GetUserSettings, UpdateMaterialPrice, UpdateUserSettings

### Android Domain (Interfaces & Security)
- `RepositoryInterfaces.kt` — MaterialRepository, PriceRepository, EstimateRepository, UserSettingsRepository
- `AdminAuth.kt` — PBKDF2-HMAC-SHA256, fail-closed, 150K iterations

### Android UI Layer
- 5 ViewModels (Calculator, Devis, Main, Prices, Settings)
- 10 screen composables (AdminDashboard, Calculator, ChantierGuide, Home, Login, MarketPrices, More, ReportsDevis, Settings, TradeDetails)
- 4 design system files + 3 theme files (R1 design: gold #F59E0B on navy #0F172A)

### Web Foundation
- Full TypeScript API client: `client.ts` (ApiClient), `endpoints.ts` (KonstrivoApi, 30 operations)
- Admin auth: `adminAuth.ts` (PBKDF2, mirrors Android AdminAuth)
- Config: `markets.ts` (9), `trades.ts` (6), `languages.ts` (4 with RTL)
- Utilities: `money.ts`, `errors.ts`
- Pages: `page.tsx` (verification toolkit), `layout.tsx`, `404/page.tsx`
- Tests: 2 files (15 test cases) via `node:test` + `tsx`

### Tests (8 Android + 2 Web)
| Platform | File | Framework | Purpose |
|----------|------|----------|---------|
| Android | ConstructionCalculatorTest | JUnit 4 | Calculation formulas |
| Android | AdminAuthTest | JUnit 4 + Robolectric | PBKDF2 verification |
| Android | PriceSelectionUnitTest | JUnit 4 | Price selection |
| Android | PricePackageSelectionSpecTest | JUnit 4 | Package-aware selection |
| Android | ExampleUnitTest | JUnit 4 | Basic |
| Android | ExampleRobolectricTest | Robolectric | Basic |
| Android | MigrationIntegrationTest | AndroidX | Room v1→v2 migration |
| Android | ExampleInstrumentedTest | AndroidX | Instrumentation |
| Web | admin-auth.test.ts | node:test | 6 auth tests |
| Web | api.test.ts | node:test | 9 API/model tests |

---

## Phase 3: Backend API (Not Started)

**Status**: ⏳ Next priority (0% complete)

### Contract Coverage
- `docs/openapi.yaml` — 1041 lines, OpenAPI 3.1.0, 23 paths, **30 operations**, 17 schemas
- `docs/API_CONTRACT.md` — 752+ lines, 11 sections
- `docs/API_CONTRACT_REVIEW.md` — 81 lines, cross-platform alignment review

### Operation Breakdown
| Category | Operations | Domain Status |
|----------|-----------|---------------|
| Authentication | 4 | Future Contract |
| Companies | 3 | Future Contract |
| Trades & WorkTypes | 3 | Implemented in Android Domain |
| Materials & Prices | 3 | Implemented in Android Domain |
| Markets/Currencies | 3 | Implemented in Android Domain |
| Projects | 5 (CRUD) | Future Contract |
| Estimates | 5 (CRUD) | Future Contract |
| Calculations | 1 | Cloud Mirror (offline in Android) |
| Sync | 1 | Future Contract |

### Schema Entities (17)
Money, PaginationMeta, ErrorResponse, LoginRequest, AuthResponse, UserClaims, UserProfile, Company,
Trade, WorkType, Material, MaterialPrice, MarketContext, Project, EstimateItem, Estimate,
CalculationRequest, CalculationResponse, SyncDeltaRequest, SyncDeltaResponse

---

## Phase 4: Database Schema (Planned)

**Status**: ⏳ Planned

### Design Targets
- PostgreSQL schema matching 17 OpenAPI entities
- Room schema alignment (Android already has Room; backend will mirror)
- Index strategy for: price lookups (country + material + package + date)
- Seed data for 9 markets, 6 trades, 17 calculation types, 36+ materials

### Migration Considerations
- Android Room migration 1→2 already adds `packageId` column
- Backend schema should be forward-compatible with package-aware pricing

---

## Phase 5: Backend Integration (Planned)

### Android → Backend
- Uncomment Retrofit/OkHttp/Moshi in `build.gradle.kts` (currently commented out with zero usages verified)
- Implement remote data sources alongside Room (offline-first)
- Token management (access + refresh tokens)
- Sync via `POST /sync/delta` with conflict resolution

### Web → Backend
- Wire `KonstrivoApi` to live backend
- JWT auth provider with HttpOnly cookies
- Business pages: quotes list, project management, material catalog
- Calculation form with live API computation

---

## Phase 6: Testing & Deployment (Planned)

### Current State
- **Android**: 8 test files ✅ (unit, Robolectric, instrumented)
- **Web**: 2 test files ✅ (node:test, 15 cases)
- **Backend**: None ❌
- **CI/CD**: None ❌ (`.github/` directory does not exist)

### Required CI/CD
- GitHub Actions: Android build + test (`./gradlew test`)
- GitHub Actions: Web type-check + test (`npm run type-check && npm test`)
- Backend: build + test + deploy pipeline
- Static export deployment (Vercel or similar for Web)