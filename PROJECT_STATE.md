# PROJECT_STATE.md — Current Project State

**Date of audit**: 2026-09-09
**Git branch**: `main`
**Head commit**: `5771e1aea1c97da74a4f1b5cc7c51d0fc5aa9155` — "feat(ui): polish micro UX and accessibility"
**Audit mode**: Read-only file inspection (shell commands non-functional — no build/test execution)

---

## 1. Executive Summary

Konstrivo Pro is a **dual-platform construction estimation application** with a functional
Android client (Kotlin/Jetpack Compose + Room persistence) and a Next.js static-export web
dashboard with a full API client and local admin auth. The **backend API server is not
implemented** — it exists only as an OpenAPI 3.1 contract with 30 operations and 17 schemas.

### Platform Maturity Matrix

| Area | Android | Web | Backend | Database |
|------|---------|-----|---------|----------|
| Calculation engine | ✅ Complete (764-line ConstructionCalculator) | ✅ Verification toolkit (page.tsx) | ❌ Contract only | ❌ (Android uses Room locally) |
| UI / Screens | ✅ 10 Composable screens | ⚠️ Homepage + 404 only | N/A | N/A |
| Persistence | ✅ Room (3 entities, migration v1→v2) | ❌ None (static export) | ❌ No server | ❌ No server DB |
| API client | ❌ None | ✅ Full TypeScript client (30 ops) | ❌ Not built | ❌ Not built |
| Auth | ✅ PBKDF2 local gate (fail-closed) | ✅ PBKDF2 local gate (mirrors Android) | ❌ Contract only | ❌ Not built |
| Pricing data | ✅ Room-backed (9 countries, InitialData) | ✅ Static config (9 markets) | ❌ Contract only | ❌ Not built |
| Saved quotes | ✅ Room (ProjectEstimateEntity) | ❌ None | ❌ Contract only | ❌ Not built |
| Sync | ❌ Offline-only (no backend) | ❌ Cannot sync | ❌ Not built | ❌ Not built |
| Tests | ✅ 8 test files (unit + instrumented) | ✅ 2 test files (node:test) | N/A | N/A |

## 2. Android Application State

### Architecture
- **Pattern**: Clean layered architecture (data → domain → ui), no DI framework
- **Namespace**: `com.example` (source packages)
- **Application ID**: `com.aistudio.placobtp.vrtz` (build.gradle.kts)
- **Compile SDK**: 36 (with `minorApiLevel = 1` for release)
- **Minimum SDK**: 24
- **Target SDK**: 36

### Data Layer
- **Room Database**: `AppDatabase.kt` — version 2, 3 entities (MaterialPriceEntity, ProjectEstimateEntity, UserSettingsEntity)
- **DAOs**: `Daos.kt` — 3 DAOs with 16 total queries
- **Migration**: `Migration1To2.kt` — adds `packageId` column to `material_prices`
- **Repository**: `ConstructionRepository.kt` — implements 4 clean architecture interfaces, delegates to Room DAOs

### Domain Layer
- **Calculation engine**: `ConstructionCalculator.kt` (764 lines), `PaintCalculator.kt`, `PaintCalculationConstants.kt`
- **Catalogs**: MaterialCatalog, TradeCatalog, PaintProductCatalog, PaintPackageCatalog
- **Models**: PriceModels, UnitModels, TradeModels, MaterialModels, MarketContext, PriceSelection
- **Fiscal**: FiscalCatalog (TVA/VAT profiles), TaxMath
- **Currency**: CurrencyConverter / DefaultExchangeRateProvider (9 currency rates vs TND)
- **Security**: AdminAuth (PBKDF2-HMAC-SHA256, fail-closed)
- **Use cases (10)**: CalculateConstruction, SaveQuote, DeleteQuote, GetMarketPrices, ResetPrices, FormatWhatsAppQuote, GetSavedQuotes, GetUserSettings, UpdateMaterialPrice, UpdateUserSettings

### UI Layer
- **ViewModels (5)**: CalculatorViewModel, DevisViewModel, MainViewModel, PricesViewModel, SettingsViewModel
- **Screens (10)**: AdminDashboardScreen, CalculatorScreen, ChantierGuideScreen, HomeScreen, LoginScreen, MarketPricesScreen, MoreScreen, ReportsDevisScreen, SettingsScreen, TradeDetailsScreen
- **Design system**: KonstrivoComponents, KonstrivoSystemComponents, KonstrivoLocalization, CommonComponents
- **Theme**: R1 design system (primary gold #F59E0B), light/dark, 4 languages (FR/AR/Derja/EN)

### Tests (8 files)
- JUnit 4 (Robolectric): ConstructionCalculatorTest, AdminAuthTest, PriceSelectionUnitTest, PricePackageSelectionSpecTest, ExampleUnitTest, ExampleRobolectricTest
- AndroidX: MigrationIntegrationTest (Room v1→v2), ExampleInstrumentedTest

## 3. Web Application State

### Framework & Config
- **Framework**: Next.js 15.1.7 (App Router, `output: 'export'` static export)
- **React**: 19.0.0 | **TypeScript**: 5.7.3 (strict) | **Tailwind**: 3.4.17
- **Test runner**: Node.js `node:test` via `tsx` v4.23.12

### Implemented Modules
- **API Client** (`web/src/lib/api/client.ts`, `endpoints.ts`): Full `ApiClient` + `KonstrivoApi` with all 30 OpenAPI operations
- **Admin Auth** (`web/src/lib/adminAuth.ts`): PBKDF2-HMAC-SHA256, fail-closed, mirrors Android `AdminAuth.kt`
- **Config** (`web/src/config/`): `markets.ts` (9 markets), `trades.ts` (6 trades), `languages.ts` (4 languages)
- **Utilities**: `money.ts`, `errors.ts`

### Pages
- `page.tsx` — Complex homepage with verification & audit toolkit
- `layout.tsx` — Root layout with metadata
- `404/page.tsx` — 404 page

### Environment
- `web/.env` — admin gate config; keys corrected (2026-09-09) to `NEXT_PUBLIC_ADMIN_EMAIL` / `NEXT_PUBLIC_ADMIN_CREDENTIAL` (values unchanged). Previously used unprefixed names that `page.tsx` never reads, which kept the gate permanently fail-closed.
- `web/.env.example` — exists; admin keys renamed from `EMAIL`/`PASSWORD` to the correct `NEXT_PUBLIC_*` names
- `web/.env.local` — does NOT exist
- Root `.env.example` — exists; correct Android BuildConfig keys with intentionally-invalid placeholder

### Tests (2 files, 15 test cases)
- `admin-auth.test.ts` — 6 tests (parsing, fail-closed, verify)
- `api.test.ts` — 9 tests (Money, errors, markets, trades, languages, API client)

## 4. Backend State: NOT IMPLEMENTED

- No server code anywhere in the repo
- API contract: 30 operations across 23 path routes, 17 schemas in `docs/openapi.yaml`
- All operations marked "(Future Contract)"
- Web API client fully implemented against contract but has no live server
- No Dockerfile, no CI/CD, no deployment scripts

## 5. Git History (22 commits on `main`)

| # | Short Hash | Message |
|---|-----------|---------|
| 1 | ec336b3 | chore: initial KONSTRIVO PRO baseline |
| 2 | 2bdc43a | fix(security): R-001 PBKDF2 admin credentials (Android+Web) |
| 3 | 7034289 | feat(android-ui): add Konstrivo design system core |
| 4 | a36b00c | feat(android-ui): add professional login experience |
| 5 | bba0b89 | feat(android-ui): professional BTP dashboard home |
| 6 | 314b062 | feat(android-ui): modernize navigation icons, MORE hub grouping |
| 7 | 578eb630 | feat(android-ui): apply design system to calculator UX |
| 8 | 52ca010 | feat(android-ui): polish trade details UX |
| 9 | 3567a2c | feat(android-ui): polish estimates reports UX |
| 10 | ed44c53 | feat(calculator): add rail type selection |
| 11 | e3ccb4c | feat(db): idempotent price top-up seeding for upgraded installs |
| 12 | 191e1b0 | feat(calculator): add demountable tile selection |
| 13 | 01c157c | feat(calculator): wire joints and finishing inputs |
| 14 | ab72665 | feat(calculator): joints wiring, trappes de visite, price top-up |
| 15 | 661c4ad | test(calculator): cover trappe lifecycle |
| 16 | 4f5fead | feat(calculator): add brick type selector for masonry |
| 17 | 4087989 | fix(calculator): apply country labor multiplier to joints |
| 18 | 7e5f461 | Initial backup of Kustrivo Pro (remote push) |
| 19 | b173478 | Update environment example |
| 20 | e28eeb4 | chore(security): remove real credentials from env examples |
| 21 | 618aa44 | chore: update documentation and clean unused dependencies |
| 22 | 5771e1a | feat(ui): polish micro UX and accessibility **(HEAD)** |

## 6. Key Discrepancies Found During Audit

### 6.1 Labor Rate Mismatches (Android vs. Web)

`defaultLaborRatePerM2` values differ between `MarketContext.kt` and `markets.ts`:

| Country | Android | Web | Delta |
|---------|---------|-----|-------|
| SA | 25.0 SAR | 45.0 SAR | Web +80% |
| AE | 25.0 AED | 50.0 AED | Web 2× |
| EG | 180.0 EGP | 150.0 EGP | Web -16.7% |
| LY | 20.0 LYD | 25.0 LYD | Web +25% |
| TN, FR, DZ, MA, CA | ✅ Match | ✅ Match | — |

### 6.2 OpenAPI Version
- `openapi.yaml` line 1: `openapi: 3.1.0`
- ✅ Verified 2026-09-09: **no repo file references "OpenAPI 3.0"**. The claim (including in early drafts of this report) originated from the prior audit summary, not from repo content.

### 6.3 Previous Audit Corrections
The previous context summary (prior audit session) contained inaccuracies this audit corrects:

| Previous Claim | Actual State |
|---------------|-------------|
| "No Room/database" | Room exists: AppDatabase v2, 3 entities, DAOs, migration 1→2 |
| "ConstructionRepository uses in-memory only" | Repository delegates to Room DAOs |
| "3 use cases" | 10 use cases (added CalculateConstruction, SaveQuote, DeleteQuote, GetMarketPrices, UpdateMaterialPrice, UpdateUserSettings, ResetPrices) |
| "No UI screens found" | 10 screen composables exist |
| "Web app is minimal stub" | Full API client, admin auth, homepage, 3 config files |
| "Only 404 page, layout, error utilities on Web" | Homepage with audit toolkit, money.ts, adminAuth.ts |

### 6.4 Missing / Corrected Files
- ✅ `README.md` — now exists (created externally since first audit; two factual fixes applied: language list labels, `AppLanguage.entries` reference)
- ✅ Root `.env.example` — exists (valid BuildConfig-style keys, intentionally-invalid placeholder)
- ✅ `web/.env.example` — exists; admin keys corrected from `EMAIL`/`PASSWORD` to `NEXT_PUBLIC_ADMIN_EMAIL` / `NEXT_PUBLIC_ADMIN_CREDENTIAL`
- No `google-services.json` (Firebase non-functional, google-services set to WARN)
- No backend server code, Dockerfile, CI/CD configuration

### 6.5 Web Admin Gate — Env Key Mismatch (found & fixed)
`web/.env` shipped Android-style keys (`ADMIN_EMAIL`, `ADMIN_PASSWORD_CREDENTIAL`) while
`web/src/app/page.tsx` reads `process.env.NEXT_PUBLIC_ADMIN_EMAIL` / `NEXT_PUBLIC_ADMIN_CREDENTIAL`.
In a Next.js static export only `NEXT_PUBLIC_*` variables are inlined into the client bundle, so
both config constants resolved to `''` and **the web admin gate could never succeed**. Fixed by
renaming the keys in `web/.env` (values unchanged) and correcting `web/.env.example`; the example
also dropped the misleading `PASSWORD` key (plaintext passwords are explicitly forbidden by the
project's R-001 rules).

---

## 2026-10-10 — UI/Auth/Language follow-up (branch audit/ui-auth-language-2026-10-10)

### Confirmed source state
- Android remains **Offline-First** with Room-backed calculator/prices/quotes; no live backend connection is documented in the Android README/API contract.
- LoginScreen is the **local administrator gate** using `MainViewModel.loginAdmin` / PBKDF2 / BuildConfig and fails closed. It is not a customer registration/login flow.
- App language is stored through `SettingsViewModel.setLanguage`; Arabic and Derja use RTL at the app root, French and English use LTR.
- Repository design reference asset `image.jpg` contains a multi-screen dark-navy/gold target, including a separate Create Account design. The reference is not evidence that those flows already exist or are backed by a live service.

### Changes made on this branch
- `MainActivity.kt`: the app root now explicitly uses the existing dark Material 3 palette to match the repository's dark-navy/gold reference consistently, independent of the phone's system theme. Palette tokens and per-screen logic are unchanged.
- `app/src/main/res/values/strings.xml`: the user-visible app name now reads `KONSTRIVO PRO`, matching the design reference. The Android `applicationId` remains unchanged.
- `LoginScreen.kt`: pressing the password keyboard's Done action now invokes the existing submit handler; authentication logic and credential handling are unchanged.
- `SettingsScreen.kt`: the four language chips now render in two columns so their labels have usable width on narrow devices. Existing language selection, persistence, and RTL/LTR behavior are unchanged.

### Not changed / release gates
- No register, forgot-password, customer-account, remote-auth, API URL, database, application ID, calculation formula, price, or shared business-logic changes.
- The Create Account screen in the reference must not be presented as functional until a verified account API contract/base URL and Android-compatible session handling are confirmed. Website browser refresh-cookie behavior must not be assumed to work identically on Android.
- **Verification pending:** this connector session cannot run Gradle or emulator tests. Before merge, run `./gradlew.bat :app:testDebugUnitTest` and `./gradlew.bat :app:assembleDebug`, then manually check language selection and password keyboard submission in FR/EN/AR/Derja, including RTL layout. No APK build or test pass is claimed by this audit.


### 2026-10-10 — Home dashboard layout follow-up
- Compared the current Compose navigation shell with the checked-in multi-screen reference image.
- Updated `MainActivity.kt` so the global tab-title bar is not stacked above the Home screen's own `ProfessionalHeader`. Other tabs retain the global title/context bar and language/account actions.
- This is a targeted layout correction, not a claim of full reference parity. The reference also includes a separate Create Account/customer sign-in flow; the current app only has a local administrator gate, so that flow remains unimplemented until a real account/auth contract is available.
- Verification gate: Android CI must pass after this commit; visual verification on a device/emulator is still required. Do not treat build success as proof of pixel-level matching.
