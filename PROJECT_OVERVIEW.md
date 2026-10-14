# PROJECT_OVERVIEW.md — Konstrivo Pro

**Audience:** This file is written for a completely new AI assistant (or developer) that has
never seen Konstrivo Pro before. Every statement below was verified against the actual source
code, existing documentation, tests, and Git history on 2026-09-09 (branch `main`,
HEAD `5771e1a` — "feat(ui): polish micro UX and accessibility"). Nothing is invented; where a
number comes from documentation rather than code, this is stated.

---

## 1. Project Name

**KONSTRIVO PRO** (OpenAPI title: *"KONSTRIVO Platform API"*). The Android application ID is
`com.aistudio.placobtp.vrtz` (`app/build.gradle.kts`); the source namespace is `com.example`.

## 2. What the Project Is

A professional **construction estimating and quoting suite** for plaquisterie / BTP trades,
delivered as **two client applications with no live backend**:

- **Android app** (`app/`) — the primary product: an offline-first on-site calculator that
  converts dimensions into material quantities, costs, and shareable quotes (devis).
- **Web companion** (`web/`) — a Next.js **static-export** dashboard containing a calculation
  verification toolkit, a complete (currently dormant) API client, and a local admin gate.
- **Backend** (`docs/`) — **not implemented**. It exists only as a contract-first OpenAPI 3.1.0
  specification and an API contract document.

The whole system is designed **offline-first**: every feature works without a network
connection, and no backend is required to run anything today.

## 3. The Problem It Solves

Plaquistes (drywall tradespeople), artisans, and hardware stores in North African, Middle
Eastern, and European markets need to produce fast, correct material quantity estimates and
price quotes on site, in their own language and currency, without reliable internet access.

Konstrivo Pro solves this by:

- Computing material quantities (boards, rails, montants, insulation, joints, paint liters,
  bricks, tiles, doors/alu) from simple site measurements (area, perimeter, height).
- Pricing them against an **editable, per-market material price catalog** stored locally.
- Applying country-specific labor rates, waste margins, and TVA/VAT fiscal profiles.
- Producing saved quotes (devis) and WhatsApp-formatted quote text for clients.
- Working 100% offline (Android) with data persisted locally in Room.

## 4. Target Users

Per `README.md`: **plaquistes, artisans, and hardware stores** ("Marché 2026" positioning).
Per `AGENTS.md`: markets in **North Africa, the Middle East, and Europe**. The default UI
language is Derja/Tunisian Arabic and the default market is Tunisia (TND, base currency for
exchange rates), with 9 selectable countries.

## 5. Main Features

Verified from `README.md`, the Android domain layer, and the web homepage:

- **Calculator** — plaquisterie/gyproc and paint estimation: BA13 boards, rails, montants,
  double skin (configurable sides/layers per side), insulation, joints & corners, finish
  quality, trappes de visite (access panels), demountable tiles, brick selector.
- **Market Prices** — local material price catalog per market, editable by the user,
  package-aware price selection (`PriceSelection.kt`).
- **Devis / Reports** — quotes and reports generated from calculations; WhatsApp quote
  formatting (`FormatWhatsAppQuoteUseCase`).
- **Chantier Guide** — practical on-site guide.
- **Trades** — trade catalog (placo, peinture, maçonnerie, carrelage, menuiserie, crépissage).
- **Settings** — language, country, currency, fiscal parameters.
- **Admin** — local owner gate ("R-001"): PBKDF2 credential verification, fail-closed, on both
  Android and Web.
- **Web verification toolkit** (`web/src/app/page.tsx`) — checks a gyproc quote's materials /
  labor / misc costs, VAT, and stamp duty and flags inconsistencies.

## 6. Android Application (`app/`)

- **Stack**: Kotlin, Jetpack Compose (Material 3), Room. Test: JUnit 4 + Robolectric +
  Roborazzi. No DI framework (manual instantiation). Navigation is **state-based**
  (`AppTab` + bottom bar) — intentionally no Navigation-Compose dependency.
- **Build config** (verified in `app/build.gradle.kts`): minSdk 24, target/compileSdk 36,
  versionCode 1, versionName "1.0". Retrofit/OkHttp/Moshi dependencies are **commented out**
  (verified zero usages). Firebase BOM is present but non-functional: no
  `google-services.json` exists and the google-services plugin uses a `WARN` strategy.
- **Architecture** (clean layers, `com.example.*`):
  - `data/local` — Room DB v2 (`AppDatabase.kt`) with 3 entities (`MaterialPriceEntity`,
    `ProjectEstimateEntity`, `UserSettingsEntity`), 3 DAOs, `Migration1To2` (adds
    `packageId` column to `material_prices`), and `InitialData.kt` which idempotently seeds
    default prices for 9 countries on every open.
  - `data/repository` — `ConstructionRepository.kt` implements 4 domain interfaces
    (`MaterialRepository`, `PriceRepository`, `EstimateRepository`, `UserSettingsRepository`)
    and delegates to Room DAOs.
  - `domain` — `ConstructionCalculator.kt` (~764 lines) + `PaintCalculator.kt` +
    `PaintCalculationConstants.kt` (the core engine); catalogs (`MaterialCatalog`,
    `TradeCatalog`, `PaintProductCatalog`, `PaintPackageCatalog`); `MarketContext.kt`
    (per-country labor rates); `FiscalCatalog.kt` + `TaxMath` (TVA profiles ~13–20%);
    `CurrencyConverter.kt` (fixed rates vs TND); `AdminAuth.kt` (PBKDF2-HMAC-SHA256,
    fail-closed); 10 use cases (CalculateConstruction, SaveQuote, DeleteQuote, GetMarketPrices,
    ResetPrices, FormatWhatsAppQuote, GetSavedQuotes, GetUserSettings, UpdateMaterialPrice,
    UpdateUserSettings).
  - `ui` — 5 ViewModels (Calculator, Devis, Main, Prices, Settings) and **10 screens**
    (verified by name): `HomeScreen`, `CalculatorScreen`, `TradeDetailsScreen`,
    `MarketPricesScreen`, `ReportsDevisScreen`, `ChantierGuideScreen`, `SettingsScreen`,
    `MoreScreen`, `LoginScreen`, `AdminDashboardScreen`. Shared design system under
    `ui/components/designsystem` (KonstrivoComponents, KonstrivoSystemComponents,
    KonstrivoLocalization); theme: gold `#F59E0B` on navy `#0F172A`, light & dark.
- **Tests** (8 files): `ConstructionCalculatorTest`, `AdminAuthTest` (Robolectric, shares exact
  PBKDF2 test vectors with the web tests), `PriceSelectionUnitTest`,
  `PricePackageSelectionSpecTest`, `PriceDomainTest`, `ExampleUnitTest`,
  `ExampleRobolectricTest`, plus instrumented `MigrationIntegrationTest` (Room v1→v2) and
  `ExampleInstrumentedTest`.
- **Build-time secrets**: the Secrets Gradle Plugin reads a root `.env`
  (`ADMIN_EMAIL`, `ADMIN_PASSWORD_CREDENTIAL`) into `BuildConfig` for the admin gate.

## 7. Website (`web/`)

- **Stack** (verified in `web/package.json`): Next.js 15.1.7 (App Router), React 19.0.0,
  TypeScript 5.7.3, Tailwind CSS 3.4.17, `clsx`/`lucide-react`/`tailwind-merge`.
- **Static export**: `web/next.config.mjs` sets `output: 'export'` — **no server, no
  serverless functions, no server-side auth**. It is a client-only bundle.
- **Pages**: homepage (`page.tsx`) — a gyproc calculation **verification toolkit** that audits
  quantities/costs, computes VAT and stamp duty, and shows pass/warn badges — plus `404` and
  `layout`. There are **no business pages yet** (no quotes list, no price management).
- **API client (ready but dormant)**: `web/src/lib/api/client.ts` (generic fetch wrapper;
  default base URL `https://api.konstrivo.tn/api/v1`, overridable via `NEXT_PUBLIC_API_URL`)
  and `web/src/lib/api/endpoints.ts` (`KonstrivoApi` class implementing the full OpenAPI
  contract). `web/src/types/generated/api-types.ts` holds generated types. Nothing can connect
  to it because no server exists.
- **Admin gate**: `web/src/lib/adminAuth.ts` verifies PBKDF2-HMAC-SHA256 credential strings
  (`pbkdf2-sha256$<iterations>$<Base64 salt>$<Base64 hash>`) via the Web Crypto API,
  **fail-closed** — any missing/malformed/unconfigured input verifies to `false`. It mirrors
  Android's `AdminAuth` exactly (same format, same test vectors). The config comes from
  `NEXT_PUBLIC_ADMIN_EMAIL` / `NEXT_PUBLIC_ADMIN_CREDENTIAL` in `web/.env`; because of static
  export these values are embedded in the client bundle and are **not secrets**.
- **Config files**: `web/src/config/markets.ts` (9 markets with `Money`-typed labor rates),
  `web/src/config/trades.ts` (6 trades), `web/src/config/languages.ts` (4 languages, one RTL).
  Also `web/src/lib/money.ts` (Money value object) and `web/src/lib/errors.ts`
  (`KonstrivoApiError`).
- **Tests**: `web/tests/admin-auth.test.ts` (6 cases) and `web/tests/api.test.ts` (9 cases) —
  15 total, run with `npm run test` = `tsx --test tests/**/*.test.ts` (Node native test runner;
  requires `npm install` first).

## 8. Backend

**There is no backend code.** Nothing runs, no server exists, no Dockerfile, no CI/CD.

What does exist is a complete **contract**:

- `docs/openapi.yaml` — OpenAPI **3.1.0**, version 1.0.0, for the "KONSTRIVO Platform API".
  Documented servers: `https://api.konstrivo.tn/api/v1` (production) and
  `http://localhost:8080/api/v1` (local). ~23 paths covering Authentication, Users, Companies,
  Trades, Materials, Prices, Markets/Countries/Currencies, Projects, Estimates, Calculations,
  and Sync — 30 operations and 17 schemas (counts per `AGENTS.md`/`PHASES.md`; path/operation
  structure verified by inspection). Many operations are annotated **"(Future Contract)"**.
- `docs/API_CONTRACT.md` — prose contract (Money object, ISO-8601 UTC timestamps, pagination
  envelope, RFC 7807-style error envelope, standard error codes).
- `docs/API_CONTRACT_REVIEW.md` and calculation policy docs (`PEINTURE_CALCULATION_POLICY.md`,
  `PLACO_MONTANT_POLICY.md`, `PAINT_PACKAGE_PRICE_MAPPING.md`).

The roadmap (`ROADMAP.md`, `PLAN.md`) designates backend implementation as the next priority.

## 9. Database

- **Implemented**: only the **Android local Room database** — version 2, three entities
  (`MaterialPriceEntity`, `ProjectEstimateEntity`, `UserSettingsEntity`), three DAOs, migration
  1→2, and idempotent default-price seeding per country. The web app has **no persistence**
  (static export). There is **no server-side database**.
- **Planned**: a PostgreSQL schema mirroring the 17 OpenAPI entities (`PHASES.md` Phase 4),
  designed to be forward-compatible with package-aware pricing.

## 10. API and How the Systems Communicate

**Today (verified):**

- **Android ↔ nothing.** It is fully offline; the calculator reads prices/settings/estimates
  from Room. Retrofit/OkHttp dependencies are commented out in `app/build.gradle.kts`.
- **Web ↔ nothing (yet).** The static-export homepage computes locally. `KonstrivoApi` is
  fully implemented against the contract but has no server to talk to.
- **Android ↔ Web (indirect alignment, not communication)**: both platforms independently
  implement the same domain concepts (markets, trades, languages, PBKDF2 credential format)
  and share identical PBKDF2 test vectors between `AdminAuthTest.kt` and
  `admin-auth.test.ts`.

**Planned (from `docs/API_CONTRACT.md` / `openapi.yaml`):** REST/JSON over HTTPS with Bearer
JWT auth; strict `Money` objects (`{ amount, currency }`, never localized strings); ISO 8601
UTC `Z` timestamps; a standard pagination envelope; RFC 7807-style error envelope with codes
(`UNAUTHORIZED`, `FORBIDDEN`, `NOT_FOUND`, `CONFLICT`, `VALIDATION_FAILED`, `RATE_LIMITED`,
`INTERNAL_SERVER_ERROR`); server-side calculation as an optional "cloud mirror"; and
offline-first sync from the Android client via `POST /api/v1/sync/delta`. The Material and
MaterialPrice schemas intentionally separate physical material definition from market pricing.

## 11. Main Technologies

| Layer | Technologies |
|---|---|
| Android | Kotlin, Jetpack Compose (Material 3), Room v2 (KSP), Coroutines/Flow, JUnit 4, Robolectric, Roborazzi, Firebase BOM (non-functional), Secrets Gradle Plugin |
| Web | Next.js 15.1.7 (App Router, static export), React 19, TypeScript 5.7.3, Tailwind CSS 3.4.17, Web Crypto API, Node `node:test` via `tsx` |
| API contract | OpenAPI 3.1.0 (`docs/openapi.yaml`), generated TS types |
| Tooling | Gradle (AGP 8.9.2, version catalog), npm; `node generate-admin.js` (root) for PBKDF2 credential generation |

## 12. Supported Languages / Markets (verified from source)

**Languages — 4** (Android `AppLanguage` in `Models.kt`; web `web/src/config/languages.ts`):

| Code (Android) | Code (Web) | Language | Direction |
|---|---|---|---|
| `fr` | `fr` | Français | LTR |
| `ar` | `ar` | العربية (Standard Arabic) | **RTL** |
| `derja` | `tn` | الدارجة التونسية (Tunisian Derja) | RTL (web) |
| `en` | `en` | English | LTR |

Note the code mismatch between platforms (`derja` vs `tn` for the same language). Arabic gets
full RTL layout applied at the app root on Android.

**Markets — 9 countries** (Android `Country` enum in `Models.kt`; same set in
`web/src/config/markets.ts`):

| Country | Currency | Android labor rate/m² (`MarketContext.kt`) | Web labor rate/m² (`markets.ts`) |
|---|---|---|---|
| Tunisia (TN) | TND | 12.0 | 12.0 ✅ |
| France (FR) | EUR | 28.0 | 28.0 ✅ |
| Algeria (DZ) | DZD | 500.0 | 500.0 ✅ |
| Morocco (MA) | MAD | 40.0 | 40.0 ✅ |
| Canada (CA) | CAD | 35.0 | 35.0 ✅ |
| Saudi Arabia (SA) | SAR | 25.0 | 45.0 ❌ |
| UAE (AE) | AED | 25.0 | 50.0 ❌ |
| Egypt (EG) | EGP | 180.0 | 150.0 ❌ |
| Libya (LY) | LYD | 20.0 | 25.0 ❌ |

The four ❌ rows are **known, unresolved discrepancies** (documented in `AGENTS.md`,
`PROJECT_STATE.md`, `PLAN.md`; no decision has been made about which side is correct — do not
"fix" one side unilaterally). Default waste margin is 5% on both platforms. All unit systems
are METRIC. Trade coverage: 6 trades — placo (7–9 work types depending on platform docs),
peinture (2), maçonnerie (1), crépissage (1), carrelage (1), menuiserie (1–2).

## 13. Current Project Status

- **Git**: branch `main`, HEAD `5771e1aea1c97da74a4f1b5cc7c51d0fc5aa9155`
  ("feat(ui): polish micro UX and accessibility"); ~22 commits documented in
  `PROJECT_STATE.md` §5 (history from "Initial backup of Kustrivo Pro" through calculator
  features — rails, trappes de visite, demountable tiles, joints, brick selector — to UI
  polish). *(Direct `git log` could not be run in this environment — shell is non-functional
  here — so history is taken from `PROJECT_STATE.md` and matches the workspace HEAD metadata.)*
- **Android**: ✅ complete (Phases 1–2 of the roadmap). Engine, Room persistence, UI, admin
  gate, and tests all in place.
- **Web**: ⚠️ minimal (~10% per `ROADMAP.md`) — homepage toolkit + full-but-dormant API client
  + local admin gate; no business pages.
- **Backend**: ❌ 0% — contract only. **Server database**: ❌ none. **CI/CD**: ❌ none (no
  `.github/` directory). **Firebase**: non-functional (no `google-services.json`).
- **Existing documentation**: `README.md`, `AGENTS.md`, `PROJECT_STATE.md`, `ROADMAP.md`,
  `PHASES.md`, `PLAN.md`, plus the `docs/` contract and policy files.
- **Next planned milestone** (`PLAN.md` Phase B): implement the backend API to match
  `docs/openapi.yaml`, which would immediately unlock the existing web client.

## 14. Important Constraints and Rules

1. **No real passwords anywhere in the repo.** Admin login verifies a PBKDF2-HMAC-SHA256
   credential string, never a plaintext password ("R-001" rule). Credentials are generated
   locally with `node generate-admin.js YOUR_STRONG_PASSWORD` and stored **only** in untracked
   `.env` files (root `.env` for Android BuildConfig keys `ADMIN_EMAIL` /
   `ADMIN_PASSWORD_CREDENTIAL`; `web/.env` with `NEXT_PUBLIC_`-prefixed keys). `web/.env`
   contains real credential material and must never be committed.
2. **Fail-closed auth everywhere.** `.env.example` placeholders are intentionally invalid, so
   admin login fails closed until a real credential is configured. Missing/malformed input
   always verifies to `false` on both platforms.
3. **`NEXT_PUBLIC_` prefix is mandatory on the web** — Next.js static export only inlines
   prefixed variables into the client bundle (this exact bug was found and fixed on
   2026-09-09; see `PROJECT_STATE.md` §6.5).
4. **Offline-first is a design principle, not an accident.** Don't add network calls to
   Android features without an offline-capable strategy; the API contract explicitly allows
   optional cloud computation on top of local independence.
5. **The environment where this repo is audited has non-functional shell execution** —
   builds, tests, npm, and git cannot be run; all verification is by file inspection
   (documented in `AGENTS.md` §8 and `PROJECT_STATE.md`).
6. **Known data discrepancies must not be silently "fixed"** — the 4 labor-rate mismatches
   (SA/AE/EG/LY) need a source-of-truth decision first (`PLAN.md` A.1).
7. **Do not commit** anything without explicit instruction; `.env*` files are gitignored
   (`web/.gitignore` covers `.env*`).

## 15. What an AI Agent Should Understand Before Modifying the Project

1. **Read `AGENTS.md` first** — it is the canonical coding-agent guide (architecture, entry
   points, known issues). Then `PROJECT_STATE.md` for the authoritative current state.
2. **Two platforms implement the same domain twice, on purpose.** Android
   (`MarketContext.kt`, `Models.kt`, `AdminAuth.kt`) and Web (`markets.ts`, `languages.ts`,
   `adminAuth.ts`) must stay semantically aligned. If you change a market, trade, language,
   or the PBKDF2 credential format on one side, check the other side — and the shared test
   vectors in `AdminAuthTest.kt` / `admin-auth.test.ts` must keep passing unchanged.
3. **Business rules live in `docs/` policies and the calculator.** Paint quantities follow
   `docs/PEINTURE_CALCULATION_POLICY.md`, gyproc mounting rules follow
   `docs/PLACO_MONTANT_POLICY.md`, and the engine is `ConstructionCalculator.kt` (~764 lines)
   + `PaintCalculator.kt`. Changing pricing/quantity logic means reading these first and
   updating `ConstructionCalculatorTest` accordingly.
4. **Android changes must respect the layering**: UI (Compose + ViewModel) → use cases →
   domain interfaces → `ConstructionRepository` → Room DAOs. There is no DI; ViewModels and
   use cases are wired manually (see `MainActivity.kt` / `MainViewModel.kt`). Navigation is
   state-based — do not introduce Navigation-Compose casually.
5. **Room schema changes require a migration.** The DB is at version 2; follow the
   `Migration1To2.kt` pattern (and its instrumented test) for any v3 change. `InitialData.kt`
   seeding is idempotent and must remain so for upgraded installs.
6. **Web changes must survive static export.** No server code, no API routes, no
   server-side rendering dependencies; anything environment-driven must use
   `NEXT_PUBLIC_*` and should be treated as public, not secret.
7. **Backend work starts from the contract, not from scratch**: implement
   `docs/openapi.yaml` (30 operations, 17 schemas) honoring the `Money` object, pagination
   and error envelopes of `docs/API_CONTRACT.md`; the web `KonstrivoApi` client and generated
   types already match this contract, and Android's Retrofit wiring (commented out in
   `app/build.gradle.kts`) is the intended future integration point.
8. **Never break the security posture**: keep PBKDF2 verification fail-closed, keep
   credentials out of Git, and don't weaken the "no plaintext password" rule when adding
   backend auth later (`ROADMAP.md` Phase 5.3 describes the upgrade path).
9. **Verification in this environment is file-inspection only** — you cannot run Gradle, npm,
   tests, or git here, so state assumptions explicitly and rely on existing tests and docs.
