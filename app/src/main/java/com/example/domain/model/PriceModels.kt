package com.example.domain.model

import com.example.data.model.AppLanguage

/**
 * Origin and provenance of a material or labor price.
 * Ensures complete transparency without misrepresenting sources.
 */
enum class PriceSource(
    val id: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val nameEn: String,
    val isOfficial: Boolean
) {
    OFFICIAL_BTP_MERCATO(
        "official_btp",
        "Barème BTP Officiel",
        "التسعيرة الرسمية للبناء والأشغال العامة",
        "تسعيرة رسمية مقننة",
        "Official BTP Tariff",
        true
    ),
    MARKET_BENCHMARK_2026(
        "market_2026",
        "Moyenne Marché 2026",
        "معدل السوق الميداني 2026",
        "سوم السوق والكنكايرية 2026",
        "Market Benchmark 2026",
        false
    ),
    SUPPLIER_QUOTE(
        "supplier",
        "Devis Quincaillerie / Dépôt",
        "تسعيرة المورد أو مستودع مواد البناء",
        "سوم المستودع والكنكايري",
        "Supplier / Store Quote",
        false
    ),
    CONTRACTOR_CUSTOM(
        "contractor_custom",
        "Tarif Spécial Artisan / Chantier",
        "سعر الحرفي المخصص للمشروع",
        "سوم المعلم الخاص",
        "Contractor Custom Rate",
        false
    ),
    IMPORTED_FILE(
        "imported",
        "Catalogue Importé (CSV / Excel)",
        "كتالوج مستورد من ملف",
        "مستورد من ملف",
        "Imported Catalog",
        false
    ),
    API_SYNC(
        "api_sync",
        "Synchronisation API Cloud",
        "مزامنة سحابية من الخادم",
        "مزامنة عبر السيرفر",
        "Cloud API Sync",
        false
    ),
    USER_OVERRIDE(
        "user_override",
        "Modifié Manuellement",
        "معدل يدويًا من قبل المستخدم",
        "مبدل يدوياً",
        "User Modified",
        false
    );

    fun localizedName(language: AppLanguage): String = when (language) {
        AppLanguage.FRENCH -> nameFr
        AppLanguage.ARABIC -> nameAr
        AppLanguage.DERJA -> nameDerja
        AppLanguage.ENGLISH -> nameEn
    }

    companion object {
        fun fromId(id: String): PriceSource = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MARKET_BENCHMARK_2026
    }
}

/**
 * Domain model representing a verified material price point in a specific market.
 * Decoupled from hardcoded currencies and calculations.
 */
data class MaterialPrice(
    val id: String, // Unique composite id e.g. "TN_plaque_ba13_standard"
    val materialId: String, // References Material.id
    val countryCode: String, // e.g. "TN", "FR", "DZ", "MA"
    val region: String = "", // e.g. "Grand Tunis", "Sousse", "Sfax", "Paris"
    val currency: String, // Currency code e.g. "TND", "EUR", "MAD"
    val defaultPrice: Double, // Baseline reference price
    val currentPrice: Double, // Active current price used in calculations
    val unit: StandardUnit, // Standardized unit
    val source: PriceSource = PriceSource.MARKET_BENCHMARK_2026,
    val supplierName: String = "",
    val packageId: String? = null,
    val effectiveFrom: Long = 0L,
    val effectiveTo: Long? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isCustom: Boolean = false
)

/**
 * Domain model representing a historical price record for trend analysis and audit trails.
 */
data class PriceHistoryRecord(
    val id: Long = 0L,
    val materialId: String,
    val countryCode: String,
    val region: String = "",
    val recordedPrice: Double,
    val currency: String,
    val source: PriceSource,
    val recordedTimestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
