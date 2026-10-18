package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Country(
    val code: String,
    val nameFr: String,
    val nameAr: String,
    val flag: String,
    val currencyCode: String,
    val currencySymbol: String,
    val rateToTndMultiplier: Double // for base conversion if needed
) {
    TUNISIA("TN", "Tunisie", "تونس", "🇹🇳", "TND", "د.ت", 1.0),
    FRANCE("FR", "France / Europe", "فرنسا / أوروبا", "🇫🇷", "EUR", "€", 0.30),
    ALGERIA("DZ", "Algérie", "الجزائر", "🇩🇿", "DZD", "د.ج", 43.5),
    MOROCCO("MA", "Maroc", "المغرب", "🇲🇦", "MAD", "د.م", 3.25),
    CANADA("CA", "Canada", "كندا", "🇨🇦", "CAD", "$", 0.44),
    SAUDI("SA", "Arabie Saoudite", "السعودية", "🇸🇦", "SAR", "ر.س", 1.20),
    UAE("AE", "Émirats Arabes Unis", "الإمارات", "🇦🇪", "AED", "د.إ", 1.18),
    EGYPT("EG", "Égypte", "مصر", "🇪🇬", "EGP", "ج.م", 15.6),
    LIBYA("LY", "Libye", "ليبيا", "🇱🇾", "LYD", "د.ل", 1.55);

    companion object {
        fun fromCode(code: String): Country = entries.firstOrNull { it.code == code } ?: TUNISIA
    }
}

enum class AppLanguage(val code: String, val displayName: String, val subTitle: String) {
    FRENCH("fr", "Français", "Terminologie professionnelle BTP"),
    ARABIC("ar", "العربية الفصحى", "المصطلحات الهندسية المعيارية"),
    ENGLISH("en", "English (Global)", "International Construction Terminology"),
    DERJA("derja", "الدارجة التونسية (Derja)", "مصطلحات الشانتي التونسي");

    companion object {
        fun fromCode(code: String): AppLanguage = entries.firstOrNull { it.code == code } ?: FRENCH
    }
}

data class MetierTrade(
    val id: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val nameEn: String,
    val iconEmoji: String,
    val isAvailable: Boolean,
    val tradeCategory: TradeCategory?,
    val badgeLabelFr: String,
    val badgeLabelAr: String,
    val descriptionFr: String,
    val descriptionAr: String
)

val GLOBAL_METIERS_LIST = listOf(
    MetierTrade(
        id = "placo",
        nameFr = "Placo & Faux Plafonds",
        nameAr = "البلاكو والأسقف المعلقة",
        nameDerja = "البلاكو بلاتر والفوبلافون",
        nameEn = "Drywall & False Ceilings",
        iconEmoji = "🏗️",
        isAvailable = true,
        tradeCategory = TradeCategory.PLACO,
        badgeLabelFr = "Complet & Actif",
        badgeLabelAr = "مكتمل وجاهز للعمل",
        descriptionFr = "BA13, cloisons, 60x60, caissons, aquapanel",
        descriptionAr = "ألواح BA13، قواطع، أسقف 60×60، كرتوش ليد، أكوابانيل"
    ),
    MetierTrade(
        id = "peinture",
        nameFr = "Peinture & Finition",
        nameAr = "الدهان والديكور",
        nameDerja = "الدهينة والديكورات",
        nameEn = "Painting & Finishing",
        iconEmoji = "🎨",
        isAvailable = true,
        tradeCategory = TradeCategory.PEINTURE,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Enduit, fixateur, peinture satinée, sablé, stucco",
        descriptionAr = "معجون، فيكساتور، دهان زيتي ومائي، سابلي وستيكو"
    ),
    MetierTrade(
        id = "maconnerie",
        nameFr = "Maçonnerie & Briques",
        nameAr = "بناء الجدران والآجر",
        nameDerja = "بنيان الآجور والعرصة",
        nameEn = "Masonry & Brickwork",
        iconEmoji = "🧱",
        isAvailable = true,
        tradeCategory = TradeCategory.MACONNERIE,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Murs en briques 8/12 trous, mortier ciment",
        descriptionAr = "جدران الآجر التونسي، ملاط الإسمنت والعرص"
    ),
    MetierTrade(
        id = "carrelage",
        nameFr = "Revêtement & Carrelage",
        nameAr = "السيراميك والأرضيات",
        nameDerja = "الفايونس والقاعة غرس",
        nameEn = "Tiling & Flooring",
        iconEmoji = "📐",
        isAvailable = true,
        tradeCategory = TradeCategory.CARRELAGE,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Carrelage sol, faïence murale, ciment colle, joints",
        descriptionAr = "سيراميك الأرضيات، فايونس الجدران، غراء وملاط الترويب"
    ),
    MetierTrade(
        id = "menuiserie",
        nameFr = "Menuiserie Bois & Alu",
        nameAr = "النجارة والألمنيوم",
        nameDerja = "النجارة والبيبان والشبابك",
        nameEn = "Carpentry & Aluminum",
        iconEmoji = "🪟",
        isAvailable = true,
        tradeCategory = TradeCategory.MENUISERIE,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Portes intérieures, fenêtres alu, baies vitrées",
        descriptionAr = "أبواب الخشب، نوافذ وواجهات الألمنيوم"
    ),
    MetierTrade(
        id = "crepissage",
        nameFr = "Enduit Ciment & Crépissage",
        nameAr = "اللياسة والتلبيس الإسمنتي",
        nameDerja = "الترطيبة واللياسة",
        nameEn = "Plastering & Rendering",
        iconEmoji = "🖌️",
        isAvailable = true,
        tradeCategory = TradeCategory.CREPISSAGE,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Crépissage 1.5cm, sable de rivière, ciment",
        descriptionAr = "لياسة 1.5 سم، رمل ورمل حمصي وإسمنت"
    ),
    MetierTrade(
        id = "electricite",
        nameFr = "Électricité & Câblage",
        nameAr = "الكهرباء والتمديدات",
        nameDerja = "الضو والتريسينتي",
        nameEn = "Electrical & Wiring",
        iconEmoji = "⚡",
        isAvailable = true,
        tradeCategory = TradeCategory.ELECTRICITE,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Tableaux électriques, gaines, câbles, spots LED",
        descriptionAr = "لوحات التوزيع، القنوات الكهربائية، الإضاءة والأسلاك"
    ),
    MetierTrade(
        id = "plomberie",
        nameFr = "Plomberie & Sanitaire",
        nameAr = "السباكة والصحي",
        nameDerja = "البلومبي والصحي",
        nameEn = "Plumbing & Sanitary",
        iconEmoji = "🚿",
        isAvailable = true,
        tradeCategory = TradeCategory.PLOMBERIE,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Tuyauterie PER/Multicouche, évacuations PVC, sanitaires",
        descriptionAr = "تمديدات الأنابيب، شبكة الصرف، تجهيزات الحمام"
    ),
    MetierTrade(
        id = "isolation",
        nameFr = "Isolation Thermique & Phonique",
        nameAr = "العزل الحراري والصوتي",
        nameDerja = "العزل والحرارة والصوت",
        nameEn = "Thermal & Acoustic Insulation",
        iconEmoji = "🛡️",
        isAvailable = true,
        tradeCategory = TradeCategory.ISOLATION,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Laine minérale, polyuréthane, membranes acoustiques",
        descriptionAr = "صوف صخري وزجاجي، عوازل الرطوبة والحرارة"
    ),
    MetierTrade(
        id = "etancheite",
        nameFr = "Étanchéité & Toitures",
        nameAr = "العزل المائي والأسطح",
        nameDerja = "الكتامة والسطوح",
        nameEn = "Waterproofing & Roofing",
        iconEmoji = "☔",
        isAvailable = true,
        tradeCategory = TradeCategory.ETANCHEITE,
        badgeLabelFr = "Actif",
        badgeLabelAr = "متاح",
        descriptionFr = "Membranes bitumineuses, résines liquides, pare-vapeur",
        descriptionAr = "عوازل البيتومين، الراتنج المائي، حماية الأسطح"
    ),




    MetierTrade(
        id = "sols", nameFr = "Sols & Parquet", nameAr = "الأرضيات والباركيه",
        nameDerja = "الأرضيات والباركي", nameEn = "Flooring & Parquet", iconEmoji = "🪵",
        isAvailable = true, tradeCategory = TradeCategory.SOLS,
        badgeLabelFr = "Actif", badgeLabelAr = "متاح",
        descriptionFr = "Parquet stratifié et béton ciré",
        descriptionAr = "باركيه وخرسانة ديكورية"
    ),
    MetierTrade(
        id = "facade", nameFr = "Façade", nameAr = "الواجهات",
        nameDerja = "الواجهة", nameEn = "Facade", iconEmoji = "🏢",
        isAvailable = true, tradeCategory = TradeCategory.FACADE,
        badgeLabelFr = "Actif", badgeLabelAr = "متاح",
        descriptionFr = "Enduit monocouche et peinture extérieure",
        descriptionAr = "تلبيس ودهان خارجي"
    ),
    MetierTrade(
        id = "demolition", nameFr = "Démolition & Gravats", nameAr = "الهدم والأنقاض",
        nameDerja = "الهدم والفضلات", nameEn = "Demolition & Debris", iconEmoji = "🧱",
        isAvailable = true, tradeCategory = TradeCategory.DEMOLITION,
        badgeLabelFr = "Actif", badgeLabelAr = "متاح",
        descriptionFr = "Dépose, démolition et évacuation",
        descriptionAr = "تفكيك وهدم وإزالة الأنقاض"
    )
,
    MetierTrade(
        id = "sols", nameFr = "Sols & Parquet", nameAr = "الأرضيات والباركيه",
        nameDerja = "الأرضيات والباركي", nameEn = "Flooring & Parquet", iconEmoji = "🪵",
        isAvailable = true, tradeCategory = TradeCategory.SOLS,
        badgeLabelFr = "Actif", badgeLabelAr = "متاح",
        descriptionFr = "Parquet stratifié et béton ciré",
        descriptionAr = "باركيه وخرسانة ديكورية"
    ),
    MetierTrade(
        id = "facade", nameFr = "Façade", nameAr = "الواجهات",
        nameDerja = "الواجهة", nameEn = "Facade", iconEmoji = "🏢",
        isAvailable = true, tradeCategory = TradeCategory.FACADE,
        badgeLabelFr = "Actif", badgeLabelAr = "متاح",
        descriptionFr = "Enduit monocouche et peinture extérieure",
        descriptionAr = "تلبيس ودهان خارجي"
    ),
    MetierTrade(
        id = "demolition", nameFr = "Démolition & Gravats", nameAr = "الهدم والأنقاض",
        nameDerja = "الهدم والفضلات", nameEn = "Demolition & Debris", iconEmoji = "🧱",
        isAvailable = true, tradeCategory = TradeCategory.DEMOLITION,
        badgeLabelFr = "Actif", badgeLabelAr = "متاح",
        descriptionFr = "Dépose, démolition et évacuation",
        descriptionAr = "تفكيك وهدم وإزالة الأنقاض"
    )
)

enum class TradeCategory(
    val id: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val iconName: String
) {
    PLACO("placo", "Placo & Faux Plafonds", "البلاكو والأسقف المعلقة", "البلاكو بلاتر والفوبلافون", "Layers"),
    PEINTURE("peinture", "Peinture & Finition", "الدهان والديكور", "الدهينة والديكورات", "FormatPaint"),
    MACONNERIE("maconnerie", "Maçonnerie & Briques", "بناء الجدران والآجر", "بنيان الآجور والعرصة", "Foundation"),
    CREPISSAGE("crepissage", "Enduit Ciment & Crépissage", "اللياسة والتلبيس الإسمنتي", "الترطيبة واللياسة", "Brush"),
    CARRELAGE("carrelage", "Revêtement & Carrelage", "السيراميك والأرضيات", "الفايونس والقاعة", "GridOn"),
    MENUISERIE("menuiserie", "Menuiserie Bois & Alu", "النجارة والألمنيوم", "النجارة والبيبان والشبابك", "DoorSliding"),
    PLOMBERIE("plomberie", "Plomberie & Sanitaire", "السباكة والصحي", "البلومبي والصحي", "Plumbing"),
    ELECTRICITE("electricite", "Électricité & Câblage", "الكهرباء والتمديدات", "الضو والتريسينتي", "Bolt"),
    ETANCHEITE("etancheite", "Étanchéité & Toitures", "العزل المائي والأسطح", "الكتامة والسطوح", "Umbrella"),
    ISOLATION("isolation", "Isolation Thermique & Phonique", "العزل الحراري والصوتي", "العزل والحرارة والصوت", "Shield"),
    SOLS("sols", "Sols & Parquet", "الأرضيات والباركيه", "الأرضيات والباركي", "Layers"),
    FACADE("facade", "Façade", "الواجهات", "الواجهة", "House"),
    DEMOLITION("demolition", "Démolition & Gravats", "الهدم والأنقاض", "الهدم والفضلات", "Hammer")
}

enum class CalculationType(
    val id: String,
    val trade: TradeCategory,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val defaultWasteMargin: Double
) {
    // Placo types
    PLACO_PLAFOND_FIXE(
        "placo_plafond_fixe",
        TradeCategory.PLACO,
        "Faux Plafond Simple BA13",
        "سقف مستعار معلق بسيط BA13",
        "فوبلافون مستوي بالـ Fourrure",
        1.10
    ),
    PLACO_CLOISON(
        "placo_cloison",
        TradeCategory.PLACO,
        "Cloison de Séparation (Rails & Montants)",
        "جدار فاصل مزدوج الهيكل",
        "فاصل كلووزون (Double Face)",
        1.10
    ),
    PLACO_DEMONTABLE(
        "placo_demontable",
        TradeCategory.PLACO,
        "Plafond Démontable 60x60",
        "سقف تفكيكي شبكي 60×60 سم",
        "فوبلافون ديكور 60×60 سم",
        1.10
    ),
    PLACO_AQUAPANEL(
        "placo_aquapanel",
        TradeCategory.PLACO,
        "Habillage Extérieur (Aquapanel / Ciment)",
        "تغليف خارجي ألواح إسمنتية",
        "أكوابانيل للواجهات والرطوبة",
        1.10
    ),
    PLACO_CACHE_LUMIERE(
        "placo_cache_lumiere",
        TradeCategory.PLACO,
        "Décors & Cache-Lumière (Caisson)",
        "إنارة مخفية وديكور كرتوش (Caisson)",
        "الكرتوش والميزان (شريط الليد)",
        1.10
    ),
    PLACO_PVC(
        "placo_pvc",
        TradeCategory.PLACO,
        "Plafond PVC (Anti-Humidité)",
        "سقف بلاستيك PVC للحمامات",
        "فوبلافون PVC ضد الماء للادوش",
        1.10
    ),
    PLACO_DOUBLAGE_COLLE(
        "placo_doublage_colle",
        TradeCategory.PLACO,
        "Doublage Collé (Plaques collées au mur)",
        "تغليف الحوائط باللصق المباشر",
        "تغليف بإللصاق على الحيط",
        1.10
    ),
    PLACO_DOUBLAGE_OSSATURE(
        "placo_doublage_ossature",
        TradeCategory.PLACO,
        "Doublage sur Ossature Métallique",
        "تغليف الحوائط بهيكل معدني",
        "تغليف بالريل والمونتون",
        1.10
    ),
    PLACO_PLAFOND_AUTOPORTANT(
        "placo_plafond_autoportant",
        TradeCategory.PLACO,
        "Plafond Autoportant (Rails & Montants)",
        "سقف ذاتي الارتكاز بالريل والمونتون",
        "فوبلافون أوتوپورتان (Rails & Montants)",
        1.10
    ),
    PLACO_JOINTS_FINITION(
        "placo_joints_finition",
        TradeCategory.PLACO,
        "Joints & Finition (Enduit / Bande / Ponçage)",
        "مرحلة الفواصل والمعالجة والتشطيب",
        "الجوان والفينيسيو (Enduit / Bande)",
        1.10
    ),

    // Peinture types
    PEINTURE_INTERIEURE(
        "peinture_interieure",
        TradeCategory.PEINTURE,
        "Peinture Intérieure Complète (Enduit + Fixateur + Finition)",
        "دهان داخلي كامل (معجون + أساس + تشطيب)",
        "دهينة كاملة (أندوي + فيكساتور + سيتان)",
        1.10
    ),
    PEINTURE_SABLE(
        "peinture_sable",
        TradeCategory.PEINTURE,
        "Décor Sablé / Stucco",
        "دهان ديكوري سابلي أو ستوكو",
        "ديكور سابلي أو ستيكو",
        1.10
    ),

    // Maçonnerie
    MACONNERIE_BRIQUE(
        "maconnerie_brique",
        TradeCategory.MACONNERIE,
        "Mur en Briques (8 ou 12 trous)",
        "بناء جدار بالآجر (8 أو 12 ثقب)",
        "بنيان حائط بالآجور التونسي",
        1.10
    ),

    // Crépissage
    CREPISSAGE_CIMENT(
        "crepissage_ciment",
        TradeCategory.CREPISSAGE,
        "Enduit Ciment / Crépissage (1.5 cm)",
        "لياسة وتلبيس إسمنتي (1.5 سم)",
        "ترطيبة ولياسة بالحمصي والرمل",
        1.10
    ),

    // Carrelage
    CARRELAGE_SOL(
        "carrelage_sol",
        TradeCategory.CARRELAGE,
        "Pose Carrelage / Grès / Faïence",
        "تركيب سيراميك أرضيات أو جدران",
        "تركيب فايونس أو قاعة غرس",
        1.10
    ),

    // Menuiserie
    MENUISERIE_PORTES(
        "menuiserie_portes",
        TradeCategory.MENUISERIE,
        "Portes Bois & Blocs-Portes",
        "أبواب خشبية وتركيب الكوادر",
        "بيبان لوح وكوادر وفوم",
        1.0
    ),
    MENUISERIE_ALU(
        "menuiserie_alu",
        TradeCategory.MENUISERIE,
        "Fenêtres Aluminium & Baies Vitrées",
        "نوافذ وأبواب ألمنيوم",
        "شبابك ألمنيوم وسيليكون وجلد",
        1.0
    ),
    PLOMBERIE_INSTALLATION("plomberie_installation", TradeCategory.PLOMBERIE, "Installation plomberie sanitaire", "تركيب السباكة والصحي", "تركيب البلومبي والصحي", 1.10),
    ELECTRICITE_INSTALLATION("electricite_installation", TradeCategory.ELECTRICITE, "Installation électrique", "تركيب الكهرباء", "تركيب الضو والتريسينتي", 1.10),
    ETANCHEITE_TOITURE("etancheite_toiture", TradeCategory.ETANCHEITE, "Étanchéité toiture", "عزل السطح مائيًا", "كتامة السطح", 1.10),
    ISOLATION_THERMIQUE("isolation_thermique", TradeCategory.ISOLATION, "Isolation thermique et phonique", "عزل حراري وصوتي", "عزل الحرارة والصوت", 1.10),
    SOLS_PARQUET("sols_parquet", TradeCategory.SOLS, "Pose de parquet stratifié", "تركيب باركيه", "تركيب باركي", 1.10),
    FACADE_FINITION("facade_finition", TradeCategory.FACADE, "Finition de façade", "تشطيب الواجهة", "فينيسيو الواجهة", 1.10),
    DEMOLITION_DEPOSE("demolition_depose", TradeCategory.DEMOLITION, "Démolition et dépose", "الهدم والإزالة", "الهدم والقلع", 1.10)
}

data class CalculatedItem(
    val id: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val quantity: Double,
    val unit: String,
    val unitPrice: Double,
    val totalPrice: Double,
    val category: String,
    val description: String = "",
    // Fiscal fields (HT / TVA / TTC). Backward compatible: defaults keep old behavior.
    val unitPriceHT: Double = unitPrice,
    val tvaRate: Double = 0.0,
    val tvaAmount: Double = 0.0,
    val totalPriceTTC: Double = totalPrice
)

data class CalculationSummary(
    val calculationType: CalculationType,
    val inputArea: Double,
    val inputPerimeter: Double,
    val inputHeight: Double,
    val inputQuantity: Double,
    val wasteMarginPercent: Double,
    val items: List<CalculatedItem>,
    val totalMaterialCost: Double,
    val estimatedLaborCost: Double,
    val grandTotalCost: Double,
    val currency: String,
    val countryCode: String,
    val notes: String = "",
    // ==========================================
    // FISCAL BLOCK (HT / TVA / TTC)
    // ==========================================
    // Source of truth: FiscalCatalog profile resolved from Country or explicit parameters.
    val fiscalProfileId: String = "",
    val tvaRate: Double = 0.0,
    // Expenses breakdown (HT)
    val transportCostHT: Double = 0.0,
    val equipmentCostHT: Double = 0.0,
    val otherCostHT: Double = 0.0,
    // Tax per block
    val materialTva: Double = 0.0,
    val laborTva: Double = 0.0,
    val transportTva: Double = 0.0,
    val equipmentTva: Double = 0.0,
    val otherTva: Double = 0.0,
    // Total TTC per block
    val materialTotalTTC: Double = 0.0,
    val laborTotalTTC: Double = 0.0,
    val transportTotalTTC: Double = 0.0,
    val equipmentTotalTTC: Double = 0.0,
    val otherTotalTTC: Double = 0.0,
    // Grand totals
    val totalTva: Double = 0.0,
    val grandTotalHT: Double = 0.0,
    val grandTotalTTC: Double = 0.0
)
