# KONSTRIVO PRO — Project Context

## Current Android authentication integration
- Android application remains Kotlin + Jetpack Compose and offline-first for calculator, prices, estimates, and local data.
- Shared website API auth is implemented in `app/src/main/java/com/example/data/remote/SiteAuthApi.kt` against the configured same-origin `/api/v1/auth` endpoint.
- Regular user sign-in calls the website login endpoint; the existing local admin credential gate remains as fallback for administrator access.
- Account creation submits `fullName`, `email`, `password`, `phone`, `companyName`, and the public `artisan` role to the website registration endpoint.
- `AndroidManifest.xml` declares `android.permission.INTERNET`.

## Safety and validation status
- Changes are isolated on branch `fix/shared-site-auth` and PR #2; do not merge until CI and real-device API checks pass.
- API availability at the configured production hostname is not yet independently confirmed.
- This connector cannot build/install the APK locally. Run `./gradlew :app:testDebugUnitTest :app:assembleDebug`, then test registration and login against the deployed website before merging.
- Existing calculator formulas, local data model, catalog IDs, and unrelated features were not intentionally changed.
