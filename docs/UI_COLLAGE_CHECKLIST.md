# KONSTRIVO PRO — Collage implementation checklist (image.jpg)

This checklist is derived from `image.jpg` and the generated crops under `docs/ref_*.png`.

## Top row
1. ref_top_01 — Splash
   - Blueprint background lines
   - Centered brand mark + "KONSTRIVO PRO" + subtitle
2. ref_top_02 — Login
   - Brand mark + title
   - Email + password fields
   - Primary "Log in" button
   - Links: forgot password (if supported) + create account
3. ref_top_03 — Create Account
   - Fields: Nom, Prénom, Entreprise, Téléphone, Email, Mot de passe
   - Primary "Sign up" button
   - Terms acceptance line
   - Link back to sign in
4. ref_top_04 — Home Dashboard
   - Header with title + tab icon
   - KPI cards row
   - Notifications card
   - Calculators & tools grid
5. ref_top_05 — Construction Calculator (selection)
   - Selection grid of calculators (cards)
   - Bottom nav + FAB
6. ref_top_06 — Material & Price Calculator
   - Calculator result list with categories chips
7. ref_top_07 — Market Prices
   - Header w/ tabs and chips
   - Material list with prices
8. ref_top_08 — Quote / Devis
   - Project selector + items table
   - Summary section
9. ref_top_09 — Project Details
   - Header image card
   - Timeline list
   - Uploaded files list

## Bottom row
1. ref_bot_01 — Services
2. ref_bot_02 — Construction tools
3. ref_bot_03 — Technical documents
4. ref_bot_04 — Notifications
5. ref_bot_05 — Profile / Account
6. ref_bot_06 — Subscription
7. ref_bot_07 — Settings
8. ref_bot_08 — Materials / Catalog

## Verification notes
- The GitHub Actions workflow runs `:app:testDebugUnitTest` and `:app:assembleDebug`, then uploads `app-debug.apk` as a short-lived workflow artifact.
- A green CI build confirms compilation and unit tests only; it does not prove pixel-level visual matching.
- Visual verification still requires installing the APK on an Android device/emulator and comparing screenshots screen-by-screen against the `ref_top_*.png` and `ref_bot_*.png` crops.
- Keep this checklist as the acceptance list; do not mark visual items complete based on compilation alone.

