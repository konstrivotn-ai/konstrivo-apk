package com.example.ui.components.designsystem

import com.example.data.model.AppLanguage
import com.example.data.model.Country

/**
 * KONSTRIVO PRO — PHASE 3 Localization Foundation.
 *
 * Single source of truth for resolving user-visible strings across the four
 * supported languages (French / English / Arabic / Derja).
 *
 * RULES (spec #17):
 *  - Never write `if (language == FRENCH) x else y` in screens anymore.
 *    Use [AppLanguage.tr] so every call-site MUST provide all four variants;
 *    the compiler fails the build when one is forgotten.
 *  - English never silently falls back: [tr] requires an explicit `en` value.
 *  - French and English stay LTR; Arabic and Derja render RTL (handled once
 *    at the app root in MainActivity).
 */
fun AppLanguage.tr(fr: String, en: String, ar: String, dz: String): String = when (this) {
    AppLanguage.FRENCH -> fr
    AppLanguage.ENGLISH -> en
    AppLanguage.ARABIC -> ar
    AppLanguage.DERJA -> dz
}

/**
 * Localized display name for a market/country.
 *
 * The protected domain model only ships FR/AR names; this UI-layer resolver
 * adds the English identity without touching `data/model/Models.kt`.
 * Derja reuses the Arabic script form (country names are script-stable).
 */
fun Country.localizedName(language: AppLanguage): String = when (language) {
    AppLanguage.FRENCH -> nameFr
    AppLanguage.ENGLISH -> englishName
    AppLanguage.ARABIC, AppLanguage.DERJA -> nameAr
}

/** English display names, keyed by the protected [Country] enum. */
private val Country.englishName: String
    get() = when (this) {
        Country.TUNISIA -> "Tunisia"
        Country.FRANCE -> "France / Europe"
        Country.ALGERIA -> "Algeria"
        Country.MOROCCO -> "Morocco"
        Country.CANADA -> "Canada"
        Country.SAUDI -> "Saudi Arabia"
        Country.UAE -> "United Arab Emirates"
        Country.EGYPT -> "Egypt"
        Country.LIBYA -> "Libya"
    }