# KONSTRIVO Android — PROJECT_CONTEXT

> Living context document for the Android application (`konstrivo-apk`).
> Companion files: `CHANGELOG_AI.md` (change log), `PROJECT_STATE.md` /
> `PLAN.md` / `ROADMAP.md` (historical planning docs in the repository root).

## What this app is

KONSTRIVO PRO — an offline-first construction estimation app (Kotlin, Jetpack
Compose, Material 3, Room). Trade calculators (placo, peinture, maçonnerie,
carrelage, menuiserie…), local price management per country, saved quotes
(devis), and a local PBKDF2 owner/admin gate.

## Shared website account system (2026-10)

The app signs in against the **website's Express `/api/v1` account system**
(`https://konstrivo.konstrivo.blitz.cloud/api/v1/`) — the same accounts work on
the website and in the app.

- `data/remote/CloudAuthClient.kt` — HTTP client for
  `auth/login`, `auth/register`, `auth/refresh`, `auth/forgot`,
  `auth/logout` (local clear), and `users/me`.
  Tokens (access + refresh) are stored in SharedPreferences
  (`konstrivo_cloud_session`); the server also refreshes via
  `POST auth/refresh` with `{ refreshToken }`, returning
  `{ token, refreshToken }` (matches the server contract).
- **Session restoration (fixed 2026-10-13):** `MainActivity` restores the
  session at startup and *applies* it to `MainViewModel.cloudAuthState`.
  A successful refresh is validated with `GET /users/me`; only the
  server-confirmed role can grant admin routing. 401/403 clears the session;
  transient network errors keep the saved session ("unverified" mode, labelled
  in the profile screen).
- **Customer sign-in:** `UserLoginScreen` performs the real cloud login with
  loading/error states and a forgot-password dialog (generic response).
  `LoginScreen` remains the owner/admin gate (local PBKDF2 via
  `domain/security/AdminAuth.kt` + BuildConfig secrets) and also supports
  cloud admin sign-in.
- **Profile:** `ProfileScreen` shows the KONSTRIVO Account card (restore
  state / sign in / create account / sign out / backend-validated admin
  dashboard entry).
- Registration always creates a normal `artisan` user — the server rejects any
  admin role in public registration (verified in the website repository).

## Security rules honoured here

- No privileged credentials are embedded in the APK beyond the existing
  fail-closed PBKDF2 admin gate fed by the untracked root `.env`
  (Secrets Gradle Plugin → `BuildConfig.ADMIN_*`).
- Role/tier from SharedPreferences is never trusted for admin; the backend
  (`GET /api/v1/users/me`) is the authority.
- Google sign-in: server-side implementation lives in the website repository;
  the Android button is NOT displayed until an OAuth web client ID is
  configured (see `CHANGELOG_AI.md`).

## Build & test

- Local: `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug`
  (requires JDK 17+ and `ANDROID_HOME`; both available in this workspace).
- CI: `.github/workflows/android-build.yml` (unit tests + debug APK artifact
  `konstrivo-pro-debug-apk`) and `android-debug-apk.yml`
  (`KONSTRIVO-PRO-debug-APK`), both on pushes to `main`.
- Tests: JUnit4 + Robolectric + Roborazzi under `app/src/test`;
  Room migration tests under `app/src/androidTest`.

## Known constraints

- No `google-services.json` (Firebase plugin is WARN-only).
- The backend API server lives in the separate `konstrivo_web` repository;
  `docs/openapi.yaml` in this repo is a legacy contract snapshot.
- Unmerged remote branches `fix/shared-site-auth` and
  `integration/website-api-auth` contain earlier, superseded iterations of the
  auth client (`SiteAuthApi.kt`, first `CloudAuthClient` draft);
  `ui/reference-auth-language` contains a divergent UI pass based on an older
  main (its diff would REMOVE the current session-restore wiring) and must not
  be merged wholesale.
