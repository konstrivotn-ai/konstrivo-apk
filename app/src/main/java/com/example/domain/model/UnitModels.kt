package com.example.domain.model

import com.example.data.model.AppLanguage

/**
 * Standard standardized Unit of Measurement across the KONSTRIVO platform.
 * Unifies all calculation units into stable immutable identifiers.
 */
enum class StandardUnit(
    val id: String,
    val standardSymbol: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val nameEn: String
) {
    PIECE("piece", "pièce", "Pièce", "قطعة", "كعبة / قطعة", "Piece"),
    SQUARE_METER("m2", "m²", "Mètre carré (m²)", "متر مربع (م²)", "ميترو كاري (م²)", "Square Meter (m²)"),
    LINEAR_METER("ml", "ml", "Mètre linéaire (ml)", "متر طولي (م)", "ميترو طولي (م)", "Linear Meter (lm)"),
    CUBIC_METER("m3", "m³", "Mètre cube (m³)", "متر مكعب (م³)", "ميترو مكعب (م³)", "Cubic Meter (m³)"),
    KILOGRAM("kg", "kg", "Kilogramme (kg)", "كيلوغرام (كغ)", "كيلو (كغ)", "Kilogram (kg)"),
    GRAM("g", "g", "Gramme (g)", "غرام (غ)", "غرام (غ)", "Gram (g)"),
    LITER("liter", "L", "Litre (L)", "لتر (ل)", "لتر (ل)", "Liter (L)"),
    BAG_25KG("bag_25kg", "sac 25kg", "Sac 25 kg", "كيس 25 كغ", "شكارة 25 كغ", "Bag 25 kg"),
    BAG_50KG("bag_50kg", "sac 50kg", "Sac 50 kg", "كيس 50 كغ", "شكارة 50 كغ", "Bag 50 kg"),
    BAG_5KG("bag_5kg", "sac 5kg", "Sac 5 kg", "كيس 5 كغ", "شكارة 5 كغ", "Bag 5 kg"),
    BOX_1000("box_1000", "boîte 1000", "Boîte de 1000", "علبة 1000 قطعة", "باكي 1000 كعبة", "Box of 1000"),
    BOX_500("box_500", "boîte 500", "Boîte de 500", "علبة 500 قطعة", "باكي 500 كعبة", "Box of 500"),
    BOX_200("box_200", "boîte 200", "Boîte de 200", "علبة 200 قطعة", "باكي 200 كعبة", "Box of 200"),
    BOX_7_2M2("box_7_2m2", "paquet 7.2m²", "Paquet 7.2 m²", "علبة 7.2 م²", "باكي 7.2 م²", "Box 7.2 m²"),
    BUCKET_15L("bucket_15l", "seau 15L", "Seau / Bidon 15L", "سطل 15 لتر", "سطال 15 لتر", "Bucket 15L"),
    BUCKET_20L("bucket_20l", "seau 20L", "Seau / Bidon 20L", "سطل 20 لتر", "سطال 20 لتر", "Bucket 20L"),
    BUCKET_5KG("bucket_5kg", "seau 5kg", "Seau 5 kg", "سطل 5 كغ", "سطال 5 كغ", "Bucket 5 kg"),
    ROLL_90M("roll_90m", "rouleau 90m", "Rouleau 90 m", "لفة 90 متر", "رول 90 م", "Roll 90m"),
    ROLL_50M("roll_50m", "rouleau 50m", "Rouleau 50 m", "لفة 50 متر", "رول 50 م", "Roll 50m"),
    ROLL_30M("roll_30m", "rouleau 30m", "Rouleau 30 m", "لفة 30 متر", "رول 30 م", "Roll 30m"),
    ROLL_15M2("roll_15m2", "rouleau 15m²", "Rouleau 15 m²", "لفة 15 م²", "رول 15 م²", "Roll 15 m²"),
    ROLL_10M("roll_10m", "rouleau 10m", "Rouleau 10 m", "لفة 10 متر", "رول 10 م", "Roll 10m"),
    CARTRIDGE("cartridge", "cartouche", "Cartouche (300ml)", "خرطوشة", "كارتوش", "Cartridge"),
    AEROSOL_750ML("aerosol_750ml", "aérosol 750ml", "Aérosol 750 ml", "قارورة بخاخ 750 مل", "دبوزة فوم 750 مل", "Aerosol 750ml"),
    CUSTOM("custom", "unité", "Unité", "وحدة", "كعبة", "Unit");

    fun localizedName(language: AppLanguage): String = when (language) {
        AppLanguage.FRENCH -> nameFr
        AppLanguage.ARABIC -> nameAr
        AppLanguage.DERJA -> nameDerja
        AppLanguage.ENGLISH -> nameEn
    }

    companion object {
        fun fromId(id: String): StandardUnit = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CUSTOM

        /**
         * Parses arbitrary legacy string representations from DB or calculators into normalized StandardUnit.
         */
        fun fromRaw(raw: String): StandardUnit {
            val clean = raw.lowercase().trim()
            return when {
                clean.contains("1000") -> BOX_1000
                clean.contains("500") -> BOX_500
                clean.contains("200") -> BOX_200
                clean.contains("7.2") -> BOX_7_2M2
                clean.contains("aérosol") || clean.contains("aerosol") || clean.contains("750") || clean.contains("قارورة") || clean.contains("دبوزة") -> AEROSOL_750ML
                clean.contains("cartouche") || clean.contains("خرطوشة") || clean.contains("كارتوش") -> CARTRIDGE
                clean.contains("90m") || clean.contains("90 m") -> ROLL_90M
                clean.contains("50m²") || clean.contains("50 m²") || clean.contains("50m") || clean.contains("50 m") -> ROLL_50M
                clean.contains("30m") || clean.contains("30 m") -> ROLL_30M
                clean.contains("15m²") || clean.contains("15 m²") -> ROLL_15M2
                clean.contains("10m") || clean.contains("10 m") -> ROLL_10M
                clean.contains("20l") || clean.contains("20 l") -> BUCKET_20L
                clean.contains("15l") || clean.contains("15 l") -> BUCKET_15L
                clean.contains("50kg") || clean.contains("50 kg") -> BAG_50KG
                clean.contains("25kg") || clean.contains("25 kg") -> BAG_25KG
                (clean.contains("5kg") || clean.contains("5 kg")) && (clean.contains("seau") || clean.contains("bidon") || clean.contains("سطل") || clean.contains("سطال")) -> BUCKET_5KG
                clean.contains("5kg") || clean.contains("5 kg") -> BAG_5KG
                clean.contains("m³") || clean.contains("متر مكعب") || clean.contains("m3") -> CUBIC_METER
                clean.contains("m²") || clean.contains("متر مربع") || clean.contains("m2") -> SQUARE_METER
                clean.contains("ml") || clean.contains("متر طولي") || clean.contains("mètre linéaire") -> LINEAR_METER
                clean.contains("kg") || clean.contains("كغ") || clean.contains("كيلو") -> KILOGRAM
                clean.contains("sac") || clean.contains("شكارة") || clean.contains("كيس") -> BAG_25KG
                clean.contains("rouleau") || clean.contains("رول") || clean.contains("لفة") -> ROLL_90M
                clean.contains("boîte") || clean.contains("علبة") || clean.contains("باكي") -> BOX_1000
                clean.contains("bidon") || clean.contains("seau") || clean.contains("سطل") || clean.contains("سطال") -> BUCKET_15L
                clean.contains("plaque") || clean.contains("لوح") || clean.contains("pièce") || clean.contains("قطعة") || clean.contains("dalle") || clean.contains("lame") -> PIECE
                else -> CUSTOM
            }
        }
    }
}
