# KONSTRIVO PRO

Professional construction estimating suite for plaquisterie / BTP trades — **Android app + Web companion**, built for plaquistes, artisans and hardware stores (Marché 2026).

## Project Structure

| Component | Path | Stack |
|---|---|---|
| Android app | `app/` | Kotlin + Jetpack Compose (Material 3) + Room |
| Web companion | `web/` | Next.js 15 + React 19 + Tailwind CSS |

## Android Architecture

Layered, **Offline-First**:

- `data/local` — Room database (v2): entities, DAOs, `Migration1To2`
- `domain` — calculation engine, strategies, use cases, catalogs (trades, materials, prices)
- `data/repository` — repositories implementing domain interfaces
- `ui` — Compose screens + ViewModels + shared design system (`ui/components/designsystem`)
- Navigation is state-based (`AppTab` + bottom bar) — intentionally no Navigation-Compose dependency

**Offline-First:** every feature works without a network connection. Prices, estimates and settings are stored locally in Room (with idempotent price seeding on first launch). No backend is required to run the app.

## Features

- **Calculator** — plaquisterie / paint estimation: boards, rails, montants, double skin, insulation, joints & corners, finish quality, trappes de visite, demountable tiles, brick selector
- **Market Prices** — local material price catalog per market, editable by the user
- **Devis / Reports** — quotes and reports generated from calculations
- **Chantier Guide** — practical on-site guide
- **Trades** — trade catalog (plaquisterie, peinture, …)
- **Settings** — language, country, currency, fiscal parameters
- **Admin** — local owner gate (R-001): PBKDF2 credential verification, fail-closed

## Languages & RTL

- Languages: **Français, English, العربية, Derja (derja)** — enforced at compile time (`AppLanguage.entries`)
- Full **RTL layout** for Arabic (applied at the app root)
- **Dark & Light** themes via Material 3

## Backend / API Status

> **Current integration status (verified from source):** Android login and account registration call the website Express API at `/api/v1/auth/login` and `/api/v1/auth/register`; access and refresh tokens are saved in Android SharedPreferences. The Android calculator, local material/price catalog, estimates, and settings still use Room/local repositories and are not yet a fully synchronized cross-device catalog. The API contract remains documented in `docs/API_CONTRACT.md` and `docs/openapi.yaml`.
>
> Password recovery is available through the website API endpoint `POST /api/v1/auth/forgot`; the Android login screen now exposes a localized “Forgot password?” flow. Google OAuth and profile/project photo uploads are not implemented by this Android client yet and require configured OAuth/Storage support rather than UI-only buttons.

## Run Android

**Prerequisites:** [Android Studio](https://developer.android.com/studio) (SDK 36), JDK 11+.

1. Open the project root in Android Studio and let Gradle sync
2. Run the `app` configuration on an emulator or a physical device

Or from the command line:

```powershell
.\gradlew.bat :app:assembleDebug       # build debug APK
.\gradlew.bat :app:testDebugUnitTest   # run unit tests
```

**Optional admin login:** copy `.env.example` to a local `.env` at the project root and set your own generated credential (see below). Without it, admin login **fails closed** — all other features work normally. The `.env` file is untracked and must never be committed.

## Run Web

```bash
cd web
npm install
npm run dev      # http://localhost:3000
npm run test     # unit tests (tsx --test)
```

## Admin Credential Generation (safe)

Admin login verifies a **PBKDF2-HMAC-SHA256 credential**, never a plain password. To generate your own credential locally:

```bash
node generate-admin.js YOUR_STRONG_PASSWORD
```

This prints a string of the form:

```
pbkdf2-sha256$150000$<Base64 salt>$<Base64 hash>
```

Then, in your **local untracked `.env`** (never in Git, never in `.env.example`):

```
ADMIN_EMAIL=you@example.com
ADMIN_PASSWORD_CREDENTIAL=<paste the generated value here>
```

> **Web companion:** the same two values go into `web/.env` as `NEXT_PUBLIC_ADMIN_EMAIL` and
> `NEXT_PUBLIC_ADMIN_CREDENTIAL` (see `web/.env.example`). The `NEXT_PUBLIC_` prefix is required —
> Next.js only inlines prefixed variables into the static-export client bundle.

Rules:

- **Never** store a real password in `.env`, `.env.example`, the repo, or the README — only the generated hash.
- The placeholder shipped in `.env.example` is intentionally **invalid**, so login fails closed until you configure your own credential.
- Regenerate any time by running the command again with a new password.

