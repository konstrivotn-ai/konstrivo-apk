# AI Change Log

## 2026-10-13 — Shared website authentication (PR #2)
- Added Android internet permission.
- Added `SiteAuthApi` for the website's `/api/v1/auth/login` and `/api/v1/auth/register` routes.
- Routed successful regular-user login back to the app home instead of the admin dashboard; preserved the local admin credential path.
- Wired account registration form to the website API and surfaced API errors.
- Updated registration password validation to match the website API minimum of 8 characters.
- Validation pending: GitHub Actions Android build/unit-test workflow was running when recorded; no APK/device test has been claimed.
