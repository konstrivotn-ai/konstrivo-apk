package com.example.domain.model

import com.example.data.model.AppLanguage
import com.example.data.model.TradeCategory

/**
 * Standardized Material Categories in KONSTRIVO PRO.
 * Groups construction materials logically across all trades.
 */
enum class MaterialCategory(
    val id: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val nameEn: String,
    val iconEmoji: String
) {
    PLACO("placo", "Plaques de Plâtre & Parements", "ألواح الجبس والتبطين", "ألواح البلاكو والواجهات", "Plasterboards & Panels", "🧱"),
    OSSATURE("ossature", "Ossatures & Profilés Métalliques", "الهياكل والقطاعات المعدنية", "الريل والمونتون والحديد", "Metal Profiles & Framing", "📐"),
    VISSERIE("visserie", "Visserie, Chevilles & Fixations", "البراغي والأوتاد والتثبيت", "الفيس والشوفي والكافالييه", "Fasteners & Screws", "🔩"),
    ENDUITS("enduits", "Enduits, Bandes & Colles", "المعجون وأشرطة الفواصل والغراء", "الأندوي ورول الجوان والكول", "Joint Compounds, Tapes & Adhesives", "🪣"),
    DALLES("dalles", "Dalles & Grilles pour Plafonds", "بلاطات وشبكات الأسقف التفكيكية", "دالات وأسقف 60×60", "Ceiling Tiles & Grid Systems", "🔲"),
    ISOLATION("isolation", "Isolation Thermique & Acoustique", "العزل الحراري والصوتي", "الصوف الزجاجي والصخري وعوازل الصوت", "Thermal & Acoustic Insulation", "🛡️"),
    PEINTURE("peinture", "Peintures, Primaires & Décors", "الدهانات والمواد الأساسية والديكورية", "الدهينة والفيكساتور والسابلي", "Paints, Primers & Decorative Finishes", "🎨"),
    MACONNERIE("maconnerie", "Maçonnerie, Briques & Ciments", "البناء والآجر والإسمنت", "البنيان والياجور والسيمون", "Masonry, Bricks & Cement", "🧱"),
    CREPISSAGE("crepissage", "Crépissage, Mortiers & Sables", "اللياسة والملاط والرمال", "الترطيبة والرملة الحمصية", "Rendering, Mortars & Sand", "🖌️"),
    CARRELAGE("carrelage", "Carrelage, Faïence & Colles", "السيراميك والبلاط وغراء البلاط", "الفايونس والقاعة وكول البلاط", "Tiling, Ceramic & Grout", "🟦"),
    BOIS_ALU("bois_alu", "Menuiserie Bois & Aluminium", "النجارة الخشبية والألمنيوم", "البيبان والشبابك والألمنيوم", "Wood & Aluminum Joinery", "🪟"),
    PLOMBERIE("plomberie", "Plomberie & Tuyauterie", "السباكة والتمديدات الصحية", "البلومبي والأنابيب", "Plumbing & Piping", "🚿"),
    ELECTRICITE("electricite", "Électricité & Câblage", "الكهرباء والتمديدات", "التريسينتي والخيوط", "Electrical & Wiring", "⚡"),
    ETANCHEITE("etancheite", "Étanchéité & Membranes", "العزل المائي والأغشية", "الكتامة والزفت", "Waterproofing & Membranes", "☔"),
    OUTILLAGE("outillage", "Outillage & Équipements", "المعدات وأدوات العمل", "العدة والماكينات", "Tools & Equipment", "🛠️"),
    OTHER("autre", "Autres Matériaux", "مواد أخرى متنوعة", "سلعة أخرى", "Other Materials", "📦");

    fun localizedName(language: AppLanguage): String = when (language) {
        AppLanguage.FRENCH -> nameFr
        AppLanguage.ARABIC -> nameAr
        AppLanguage.DERJA -> nameDerja
        AppLanguage.ENGLISH -> nameEn
    }

    companion object {
        fun fromId(id: String): MaterialCategory = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: OTHER
    }
}

/**
 * Standard immutable Domain Model representing a Construction Material.
 * Completely separates material identity and physical attributes from market pricing and regions.
 */
data class Material(
    val id: String, // Stable identifier e.g. "plaque_ba13_standard"
    val category: MaterialCategory,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val nameEn: String,
    val standardUnit: StandardUnit,
    val compatibleTrades: List<TradeCategory>,
    val brandInfo: String = "",
    val technicalSpec: String = "",
    val stockLengthM: Double? = null,
    val stockWidthM: Double? = null,
    val isActive: Boolean = true
) {
    fun localizedName(language: AppLanguage): String = when (language) {
        AppLanguage.FRENCH -> nameFr
        AppLanguage.ARABIC -> nameAr
        AppLanguage.DERJA -> nameDerja
        AppLanguage.ENGLISH -> nameEn
    }

    fun localizedUnit(language: AppLanguage): String = standardUnit.localizedName(language)
}
