# AGENTS.md — Konstrivo Pro Coding Agent Guide

## 1. Project Purpose

**Konstrivo Pro** is a dual-platform (Android + Web) construction estimation and quoting
application targeting North African, Middle Eastern, and European markets. It provides:

- **Android app**: On-site construction material quantity calculations (paint, gyproc/placo,
  masonry, tiling, joinery), local price management via Room database, saved quote/devis
  management, and client-side PBKDF2 admin authentication.
- **Web dashboard**: Next.js static-export admin portal with full API client implementation,
  local admin auth gate, and a calculation verification toolkit.
- **Backend API**: Not yet implemented — fully described by OpenAPI contract in `docs/`.

## 2. Tech Stack

### Android (Kotlin / Jetpack Compose)
- **Language**: Kotlin 2.1.10
- **Build**: Gradle (AGP 8.9.2)
- **Persistence**: Room (version 2, migration 1→2 implemented)
- **UI**: Jetpack Compose (Material 3)
- **Test**: JUnit 4 + Robolectric + Roborazzi (screenshot testing)
- **DI**: None (manual instantiation)
- **Firebase**: BOM imported, google-services plugin applied (WARN strategy for missing config)

### Web (Next.js / TypeScript / Tailwind)
- **Framework**: Next.js 15.1.7 (App Router, static export `output: 'export'`)
- **React**: 19.0.0
- **UI**: Tailwind CSS 3.4.17
- **TypeScript**: 5.7.3
- **Test**: Node.js native `node:test` runner via `tsx`
- **API Client**: Full TypeScript client (`ApiClient` + `KonstrivoApi`) against OpenAPI contract

### Backend / API
- **No server code exists.** API is contract-only (`docs/openapi.yaml`, `docs/API_CONTRACT.md`).
- OpenAPI Specification version: **3.1.0** — verified: no file in the repo references "OpenAPI 3.0" (that claim originated in a prior audit summary and was corrected).

## 3. Source of Truth

| Layer | Location |
|-------|----------|
| Android source code | `app/src/main/java/com/example/` |
| Android tests | `app/src/test/java/com/example/` and `app/src/androidTest/` |
| Web source code | `web/src/` |
| Web tests | `web/tests/` |

## 4. Key Entry Points

### Android
- `app/src/main/java/com/example/calculator/ConstructionCalculator.kt` — Core calculation engine (764 lines)
- `app/src/main/java/com/example/calculator/PaintCalculator.kt` — Paint-specific calculation logic
- `app/src/main/java/com/example/data/local/InitialData.kt` — Initial price data generation for 9 countries
- `app/src/main/java/com/example/data/model/Models.kt` — Country, AppLanguage, TradeCategory, CalculationType enums, CalculatedItem, CalculationSummary
- `app/src/main/java/com/example/data/local/Entities.kt` — Room entities (MaterialPriceEntity, ProjectEstimateEntity, UserSettingsEntity)
- `app/src/main/java/com/example/data/local/AppDatabase.kt` — Room database (version 2, migration 1→2)
- `app/src/main/java/com/example/data/repository/ConstructionRepository.kt` — Repository (implements MaterialRepository, PriceRepository, EstimateRepository, UserSettingsRepository)
- `app/src/main/java/com/example/ui/viewmodel/CalculatorViewModel.kt` — Calculator UI state
- `app/src/main/java/com/example/ui/viewmodel/DevisViewModel.kt` — Saved quotes management
- `app/src/main/java/com/example/ui/viewmodel/SettingsViewModel.kt` — User settings
- `app/src/main/java/com/example/domain/security/AdminAuth.kt` — PBKDF2 admin credential verification
- `app/src/main/java/com/example/domain/fiscal/FiscalCatalog.kt` — VAT/TVA profiles and tax math
- `app/src/main/java/com/example/domain/currency/CurrencyConverter.kt` — Exchange rate provider
- `app/src/main/java/com/example/ui/screens/` — 10 screen composables

### Web
- `web/src/lib/api/client.ts` — Generic HTTP API client (fetch wrapper with error handling)
- `web/src/lib/api/endpoints.ts` — Typed API endpoint methods (KonstrivoApi class)
- `web/src/lib/adminAuth.ts` — PBKDF2 admin credential verification (mirrors Android AdminAuth)
- `web/src/app/page.tsx` — Homepage with calculation verification toolkit
- `web/src/config/markets.ts` — 9 market contexts (labor rates, currencies)
- `web/src/config/trades.ts` — 6 trades with work type counts
- `web/src/config/languages.ts` — 4 languages (fr, ar, tn, en) with RTL support
- `web/src/lib/money.ts` — Money value object utilities
- `web/src/lib/errors.ts` — KonstrivoApiError class
- `web/src/types/generated/api-types.ts` — Generated TypeScript types from OpenAPI

## 5. Known Discrepancies (Important)

### Labor Rate Mismatches (Android vs. Web)
The `defaultLaborRatePerM2` values differ between platforms for 4 countries:

| Country | Android (`MarketContext.kt`) | Web (`markets.ts`) | Status |
|---------|-----------------------------|-------------------|--------|
| SA (Saudi Arabia) | 25.0 SAR | 45.0 SAR | ❌ Mismatch |
| AE (UAE) | 25.0 AED | 50.0 AED | ❌ Mismatch |
| EG (Egypt) | 180.0 EGP | 150.0 EGP | ❌ Mismatch |
| LY (Libya) | 20.0 LYD | 25.0 LYD | ❌ Mismatch |
| TN, FR, DZ, MA, CA | ✅ Match | ✅ Match | ✅ |

### OpenAPI Version
- `docs/openapi.yaml` declares `openapi: 3.1.0` ✅
- Verified 2026-09-09: no file in the repo (docs, code, or specs) references "OpenAPI 3.0". The earlier discrepancy report was an artifact of the prior audit summary, not of repo content.

### Web `package.json` Test Script
- Test command: `tsx --test tests/**/*.test.ts`
- This uses the `tsx` CLI which is listed in `devDependencies` — tests will fail if `tsx` is not installed via `npm install`.
- The `web/.env` file contains real admin credentials (PBKDF2 hash + email).

## 6. Android Architecture Details

### Package Structure
```
com.example
├── calculator/           # ConstructionCalculator, PaintCalculator, PaintCalculationConstants
├── data/
│   ├── local/            # Room: AppDatabase, Entities, Daos, InitialData, Migration1To2
│   ├── model/            # Models.kt (enums + data classes), Country, AppLanguage
│   └── repository/       # ConstructionRepository (Room DAO-backed)
├── domain/
│   ├── currency/         # CurrencyConverter / ExchangeRateProvider
│   ├── fiscal/           # FiscalCatalog, FiscalProfile, TaxMath
│   ├── material/         # MaterialCatalog
│   ├── model/            # PriceModels, UnitModels, TradeModels, MaterialModels, MarketContext
│   ├── paint/            # PaintPackageCatalog, PaintProductCatalog, PaintPackage, PaintProduct
│   ├── repository/       # RepositoryInterfaces (clean architecture interfaces)
│   ├── security/         # AdminAuth (PBKDF2)
│   ├── trade/            # TradeCatalog
│   └── usecase/          # 10 use cases
│   └── PriceSelection.kt  # selectPriceMap, package-aware price selection
├── ui/
│   ├── components/
│   │   ├── CommonComponents.kt
│   │   └── designsystem/   # KonstrivoComponents, KonstrivoSystemComponents, KonstrivoLocalization
│   ├── screens/            # 10 screen composables
│   ├── theme/              # Color, Theme, Type
│   └── viewmodel/         # CalculatorViewModel, DevisViewModel, MainViewModel, PricesViewModel, SettingsViewModel
└── MainActivity.kt
```

### Use Cases (10)
1. `CalculateConstructionUseCase` — Delegates to ConstructionCalculator
2. `SaveQuoteUseCase` — Saves ProjectEstimateEntity to Room
3. `DeleteQuoteUseCase` — Deletes estimate by ID
4. `GetMarketPricesUseCase` — Returns Flow<List<MaterialPriceEntity>> by country
5. `ResetPricesUseCase` — Resets country prices to defaults
6. `FormatWhatsAppQuoteUseCase` — Formats quote for WhatsApp sharing
7. `GetSavedQuotesUseCase` — Returns Flow<List<ProjectEstimateEntity>>
8. `GetUserSettingsUseCase` — Returns Flow<UserSettingsEntity?>
9. `UpdateMaterialPriceUseCase` — Updates a single price
10. `UpdateUserSettingsUseCase` — Updates user settings

### Tests (8 test files)
- `app/src/test/java/com/example/calculator/ConstructionCalculatorTest.kt` — Calculation logic
- `app/src/test/java/com/example/domain/security/AdminAuthTest.kt` — PBKDF2 auth (Robolectric)
- `app/src/test/java/com/example/ExampleUnitTest.kt` — Basic unit test
- `app/src/test/java/com/example/ExampleRobolectricTest.kt` — Robolectric test
- `app/src/test/java/com/example/domain/PriceSelectionUnitTest.kt` — Price selection logic
- `app/src/test/java/com/example/domain/PricePackageSelectionSpecTest.kt` — Package-aware selection
- `app/src/androidTest/java/com/example/migration/MigrationIntegrationTest.kt` — Room migration v1→v2
- `app/src/androidTest/java/com/example/ExampleInstrumentedTest.kt` — Instrumentation test

## 7. Web Architecture Details

### Key Behaviors
- **Static export**: `next.config.mjs` uses `output: 'export'` — no serverless functions
- **Client-side admin auth**: `adminAuth.ts` implements PBKDF2-HMAC-SHA256 via Web Crypto API
- **Fail-closed security**: Any malformed/missing/unconfigured input returns `false`
- **Local authority gate only**: Credential hash is embedded in the static bundle — NOT server-side auth
- **Shared test vectors**: Android `AdminAuthTest.kt` and Web `admin-auth.test.ts` use identical PBKDF2 vectors
- **Full API client**: `endpoints.ts` implements all 30 OpenAPI operations

### Environment Variables
| Variable | Location | Purpose |
|----------|----------|---------|
| `ADMIN_EMAIL` | root `.env` | Android `BuildConfig.ADMIN_EMAIL` (Secrets Gradle Plugin) |
| `ADMIN_PASSWORD_CREDENTIAL` | root `.env` | Android `BuildConfig.ADMIN_PASSWORD_CREDENTIAL` (PBKDF2 string) |
| `NEXT_PUBLIC_ADMIN_EMAIL` | `web/.env` | Admin email for web local gate |
| `NEXT_PUBLIC_ADMIN_CREDENTIAL` | `web/.env` | PBKDF2 credential string for web gate |
| `NEXT_PUBLIC_API_URL` | (optional) | Override API base URL |

Templates: root `.env.example` (Android keys) and `web/.env.example` (NEXT_PUBLIC_ keys).
Generate credentials with `node generate-admin.js YOUR_STRONG_PASSWORD` (project root).

### Web Tests
- **Test runner**: Node.js native `node:test` runner via `tsx`
- **`admin-auth.test.ts`**: 6 test cases — credential parsing, fail-closed, verify logic
- **`api.test.ts`**: 9 test cases — Money model, error parsing, markets (9), trades (6), languages (RTL), API client init

## 8. Constraints

- Shell command execution is **not functional** in this environment — git commands, npm, gradle,
  and Next.js builds cannot be executed. All verification must be done via file inspection.
- `web/.env` contains real admin credentials and must never be committed. It is properly
  listed in `web/.gitignore` as `.env*`. Its keys were corrected (2026-09-09) to
  `NEXT_PUBLIC_ADMIN_EMAIL` / `NEXT_PUBLIC_ADMIN_CREDENTIAL` — the previous unprefixed names
  are never read by `page.tsx`, so the web gate could never have succeeded.
- Android admin credentials are injected at build time via the Secrets Gradle Plugin from a
  root `.env` (BuildConfig fields `ADMIN_EMAIL` / `ADMIN_PASSWORD_CREDENTIAL`). A tracked,
  intentionally-invalid root `.env.example` exists as the template; credentials are generated
  with `node generate-admin.js`.
- No `google-services.json` exists — Firebase features are non-functional (google-services
  plugin set to WARN strategy).
- Room persistence IS implemented on Android (3 entities, DAOs, migration 1→2) — not in-memory only.
- The backend API server is NOT implemented — all 30 OpenAPI operations are marked "(Future Contract)".

## 9. Recommended Next Steps

### Immediate (Documentation)
1. ~~Create `AGENTS.md`, `PROJECT_STATE.md`, `ROADMAP.md`, `PHASES.md`, `PLAN.md`~~ ✅ Done
2. Resolve the 4 labor rate discrepancies between Android `MarketContext.kt` and Web `markets.ts`
3. ~~Fix OpenAPI version references~~ ✅ Verified non-issue — no repo file says "OpenAPI 3.0"
4. ~~Create `.env.example` templates~~ ✅ Done — root + web templates exist; web admin keys corrected

### Medium-term (Backend)
5. Implement the backend API server per `docs/openapi.yaml` (30 operations, 17 schemas)
6. Design PostgreSQL schema matching the OpenAPI data models

### Long-term (Integration)
7. Android → Backend sync via `POST /api/v1/sync/delta`
8. Web dashboard pages: quotes list, material management, pricing management
9. End-to-end testing across all platforms
10. CI/CD pipeline setup