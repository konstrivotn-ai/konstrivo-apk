# KONSTRIVO Android — CHANGELOG_AI

> Append-only AI change log for the Android application. Newest first.

- **2026-10-18 — Calculator default waste margin aligned with website:**
  - Set all native `CalculationType.defaultWasteMargin` values to `1.10` (10%), including interior painting.
  - Set the calculator ViewModel's initial and invalid-input fallback waste margin to `10.0` percent.
  - Added a unit test that guards the default margin across every native calculation type.
  - Only defaults changed; quantity formulas, units, material prices, and tax arithmetic were not changed.
  - Android Gradle tests/build have not been run in this environment.


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

