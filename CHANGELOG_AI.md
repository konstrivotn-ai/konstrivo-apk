# KONSTRIVO Android — CHANGELOG_AI

> Append-only AI change log for the Android application. Newest first.

- **2026-10-19 — Live website catalog in Android calculator (in progress):**
  - Added a read-only Android API client for the same website `/api/v1/trades`, `/trades/:id/services`, `/trades/:id/metre-elements`, `/materials`, and `/prices` endpoints.
  - Added a live catalog panel to the calculator: refreshable trade list, website-configured services and suggested rates, dynamic metric element/dimension inputs, and the trade's material list with published prices when available.
  - Newly registered active trade codes are fetched as data rather than mapped to a Kotlin enum, so the app can discover new site trades without an APK release. CSV/import-created trades still need their service and metric definitions configured on the website before those advanced calculator options can be used.
  - Basic whitelisted metric operations (`single`, `product`, `perimeter_h`) are evaluated locally with declared layers/waste only. Elements containing openings, deductions or yield/coverage rules intentionally do not display a computed quantity yet, to avoid a misleading result.
  - Catalog and service data are fetched on demand; no production records are written. Offline cache, full advanced-rule parity, quote integration, and live end-to-end production verification remain outstanding.
  - Latest source must pass GitHub Actions before it can be treated as a verified APK.

- **2026-10-19 — Board dimension selector and trade methodology (in progress):**
  - Added selectable board dimensions (1.20×2.50, 1.20×2.80, 1.20×3.00 and 0.90×2.00 m) and wired selected dimensions into existing board-area calculations.
  - Added trade-aware methodology guidance for ceilings, partitions, plumbing, electrical, waterproofing, insulation, parquet, facade finishing and demolition.
  - Methodology is advisory and does not claim automatic DTU certification.
  - Requires CI validation; complete website parity, PDF audit export and shared-site catalog synchronization remain outstanding.

- **2026-10-18 — Interactive fiscal and estimate settings (in progress):**
  - Added selectable VAT overrides (19%, 13%, 7%, 0%) with a reset to the selected country's fiscal profile.
  - Added Tunisia fiscal-stamp toggle (1.000 DT), guarantee-retention choices (0%, 5%, 10%), custom labor-rate input, and waste-margin input.
  - Wired tax rate, stamp, and retention into the calculator's shared summary flow; result and share text show stamp, withholding and net payable separately.
  - Existing calculator defaults remain backward-compatible when the new parameters are not supplied.
  - Still requires CI/build validation and further per-trade option/material parity before this can be considered complete.

- **2026-10-18 — Visible calculation results and share actions (in progress):**
  - Added a result panel to the calculator UI with each calculated material line and totals for materials, labor, HT, TVA and TTC.
  - Added copy-to-clipboard and WhatsApp share actions for the generated estimate text.
  - This does not replace the requested PDF technical audit report, fiscal timbre/retention controls, or complete website parity.

- **2026-10-18 — Opening deduction parity (in progress):**
  - Added an explicit opening-area input for placo partitions, masonry walls and facade finishing.
  - Matched the website rule: openings are deducted only when the combined area is at least 1.00 m²; placo partition board quantity is calculated from net wall area before waste.
  - Added a regression test for 0.99 m² (no deduction) and 2.00 m² (deduction).
  - This is one parity increment, not full website parity; other trade-specific controls, fiscal options, result details, PDF/quote/WhatsApp and shared sync remain to be completed.

- **2026-10-18 — Android calculator parity expansion (in progress):**
  - Set the default waste margin to 10% across all native calculation types and the calculator parameter fallback.
  - Added native calculator categories and calculation branches for plumbing, electrical installation, waterproofing, insulation, parquet flooring, facade finishing, and demolition.
  - Activated corresponding trade/work-type entries in the domain trade catalog; added point-count and linear-meter fields for plumbing/electrical and roof waterproofing.
  - Material costs for these new branches are resolved from the existing app price catalog; no test products or production records were inserted.
  - Added unit tests for the 10% defaults and expected website trade coverage.
  - **Not yet full parity:** per-trade subtypes/options, full tax/timbre/guarantee-retention controls, detailed methodology/standards and website result/PDF/quote/WhatsApp parity still require implementation and verification.
  - GitHub Actions build and unit tests are the validation source; check run status before considering this slice verified.

- **2026-10-13 — Shared-account session restoration wired into the UI (fixes the
  "restore invoked but never applied" defect):**
  - **`MainActivity`:** the startup `CloudAuthClient.restoreSession()` result is now applied
    to application state through `MainViewModel.applyCloudSession(...)`. A successful refresh
    is validated against the backend (`GET /api/v1/users/me`) before the UI treats the user as
    signed in; the role used for admin routing always comes from the server response, never
    from SharedPreferences alone. Expired/revoked sessions (401/403) are cleared and the UI
    stays signed out. Transient network failures keep the saved session and enter the
    signed-in UI in "unverified" mode (offline-friendly, clearly labelled in the profile).
  - **`MainViewModel`:** new `cloudAuthState` (`isRestoring` / `session` / `isVerified`),
    `beginCloudSessionRestore`, `applyCloudSession`, `signOutCloudSession`. Cloud-granted
    admin is tracked separately (`adminFromCloudSession`) so signing out of the cloud account
    never clears a local PBKDF2 admin login, and vice versa.
  - **`CloudAuthClient`:** new `fetchMe(context)` — fetches the authoritative identity from
    `GET /api/v1/users/me` with the saved Bearer token, refreshing once on 401, clearing the
    session when the server rejects it, and persisting the server-confirmed name/email/role.
  - **Customer sign-in is now real:** `UserLoginScreen` (previously a stub that always showed
    "Online sign-in is not enabled yet") now performs `POST /api/v1/auth/login` through
    `CloudAuthClient` with loading state, server error surfacing, success routing via
    `onSignInSuccess(session)`, and a forgot-password dialog backed by the existing
    `POST /api/v1/auth/forgot` flow (generic response — account existence is not revealed).
    It is reachable from the profile screen ("Sign in") and from account creation.
  - **`LoginScreen` (admin gate):** `onCloudLoginSuccess` now receives the full server
    session (not just the role string) so the same central session state is updated.
  - **`ProfileScreen`:** new KONSTRIVO Account card showing the session state (restoring /
    signed out with Sign in + Create account actions / signed in with name, email, an
    "unverified local session" note when the server could not be reached, an admin dashboard
    button visible only when the role was validated by the backend, and Sign out).
  - **Navigation:** the top-bar account button now opens the profile/account hub instead of
    the settings tab; admin authentication stays a separate explicit action.
  - **Registration** continues to create a normal `artisan` user (server rejects any admin
    role attempt — verified in the website repository's auth tests).
  - No calculator formulas, waste rates, units, Room schema/migrations, or local pricing data
    were touched.



## 2026-10-19 — Devis PDF and live-service quote handoff
- Added a PDF exporter for saved estimates using Android PdfDocument, with client/project details, material lines, totals and notes.
- Added secure FileProvider sharing for generated PDF files from the app cache.
- Added a PDF action to each saved Devis card; the exported PDF can be shared through Android's share sheet.
- Added a handoff from the live website service catalog to the existing saved-Devis repository for TND service rates. The saved item records the service, unit, quantity, rate and total; it navigates to the Devis workspace after saving.
- Foreign-currency service quote creation is intentionally not enabled while the site API only exposes suggested service rates in TND; no guessed currency conversion is applied.
- Validation pending for the current branch head; successful earlier APK builds do not prove this latest PDF/Devis patch compiles.

- **2026-10-19 — Website-priced material to Devis handoff (in progress):**
  - Added a native Android action for a selected website catalog material with a published price in the active currency to be saved as a Devis line.
  - Quantity is entered explicitly by the user; the app calculates line total from the displayed site price and blocks adding missing-price or currency-mismatch items rather than inventing a price.
  - Material quote lines are stored in the existing estimate repository and exported by the current PDF flow.
  - The site examples for PEINTURE and PLOMBERIE are reference cases; automatic all-material quantity derivation still requires verified website formula-to-material mapping and must not be inferred from labels alone.
  - CI validation is required for this change before it is considered verified.
