package com.example.domain.fiscal

import com.example.data.model.Country

/**
 * KONSTRIVO Fiscal Layer (centralized).
 *
 * Rules enforced by the project:
 *  - Never hardcode a TVA rate inside a calculation strategy.
 *  - TVA is computed in this central layer only.
 *  - The existing `material_prices.currentPrice` remains the authoritative HT price source.
 *  - A [FiscalProfile] is resolved from the operating Country (or an explicit profileId /
 *    tvaRate override provided by the caller). No new database table is introduced.
 */
data class FiscalProfile(
    val id: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val tvaRate: Double
)

object FiscalCatalog {

    /** Default national VAT profiles (2026 baselines, configurable later without schema changes). */
    private val COUNTRY_PROFILES: Map<Country, FiscalProfile> = mapOf(
        Country.TUNISIA to FiscalProfile("TN_STD", "TVA Tunisie 19%", "أداء القيمة المضافة تونس 19%", "أداء تونس 19%", 19.0),
        Country.FRANCE to FiscalProfile("FR_STD", "TVA France 20%", "ضريبة القيمة المضافة فرنسا 20%", "TVA فرانسا 20%", 20.0),
        Country.ALGERIA to FiscalProfile("DZ_STD", "TVA Algérie 19%", "الرسم على القيمة المضافة الجزائر 19%", "TVA دزاير 19%", 19.0),
        Country.MOROCCO to FiscalProfile("MA_STD", "TVA Maroc 20%", "الرسم على القيمة المضافة المغرب 20%", "TVA مغريب 20%", 20.0),
        Country.CANADA to FiscalProfile("CA_STD", "TVQ/TPS Canada 13%", "ضريبة كندا الموحدة 13%", "TVA كندا 13%", 13.0),
        Country.SAUDI to FiscalProfile("SA_STD", "VAT Arabie Saoudite 15%", "ضريبة القيمة المضافة السعودية 15%", "TVA سعودية 15%", 15.0),
        Country.UAE to FiscalProfile("AE_STD", "VAT Émirats Arabes Unis 5%", "ضريبة القيمة المضافة الإمارات 5%", "TVA إمارات 5%", 5.0),
        Country.EGYPT to FiscalProfile("EG_STD", "VAT Égypte 14%", "ضريبة القيمة المضافة مصر 14%", "TVA مصر 14%", 14.0),
        Country.LIBYA to FiscalProfile("LY_STD", "TVA Libye 17%", "ضريبة القيمة المضافة ليبيا 17%", "TVA ليبيا 17%", 17.0)
    )

    /**
     * Resolves the effective fiscal profile.
     * Precedence: explicit [tvaRateOverride] > explicit [profileId] (supports future DB-backed
     * fiscal_profiles without behavior change) > national default of [country].
     */
    fun resolve(
        country: Country,
        profileId: String = "",
        tvaRateOverride: Double? = null
    ): FiscalProfile {
        if (tvaRateOverride != null && tvaRateOverride >= 0.0) {
            return FiscalProfile(
                id = profileId.ifEmpty { "CUSTOM_${tvaRateOverride.toInt()}" },
                nameFr = "TVA personnalisée ${formatRate(tvaRateOverride)}",
                nameAr = "ضريبة مخصصة ${formatRate(tvaRateOverride)}",
                nameDerja = "TVA مخصصة ${formatRate(tvaRateOverride)}",
                tvaRate = tvaRateOverride
            )
        }
        if (profileId.isNotEmpty()) {
            return COUNTRY_PROFILES.values.firstOrNull { it.id.equals(profileId, ignoreCase = true) }
                ?: profileForCountry(country)
        }
        return profileForCountry(country)
    }

    fun profileForCountry(country: Country): FiscalProfile =
        COUNTRY_PROFILES[country] ?: COUNTRY_PROFILES.getValue(Country.TUNISIA)

    private fun formatRate(rate: Double): String =
        (rate * 100).let { p -> if (p % 1.0 == 0.0) p.toInt().toString() else p.toString() } + " %"
}

/**
 * Central tax arithmetic. All VAT/rounding is performed here — strategies only produce quantities.
 */
object TaxMath {

    /** Round to 2 decimals (banker-free simple truncation consistent with the rest of the app). */
    fun round2(value: Double): Double = (value * 100.0).toLong() / 100.0

    /**
     * Computes TVA amount and TTC total from an HT amount and a TVA rate (percent).
     * Returns Pair(tvaAmount, totalTTC).
     */
    fun compute(amountHT: Double, tvaRate: Double): Pair<Double, Double> {
        val safeRate = if (tvaRate < 0.0) 0.0 else tvaRate
        val tva = round2(amountHT * safeRate / 100.0)
        return tva to round2(amountHT + tva)
    }
}