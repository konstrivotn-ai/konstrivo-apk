package com.example.domain.material

import com.example.data.model.TradeCategory
import com.example.domain.model.Material
import com.example.domain.model.MaterialCategory
import com.example.domain.model.StandardUnit

/**
 * Centralized Domain Catalog of all materials in KONSTRIVO PRO.
 * Enforces immutable IDs, standardized physical units, categories, and compatible trades.
 */
object MaterialCatalog {

    val ALL_MATERIALS: List<Material> = listOf(
        // ==========================================
        // 1. PLACO & DRYWALL PANELS
        // ==========================================
        Material(
            id = "plaque_ba13_standard",
            category = MaterialCategory.PLACO,
            nameFr = "Plaque BA13 Standard (1.2x2.5m / 3m²)",
            nameAr = "لوح جبس قياسي BA13 (3 م²)",
            nameDerja = "لوح بلاكو عادي BA13 بيضاء",
            nameEn = "Standard BA13 Drywall Board (1.2x2.5m / 3m²)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf / Siniat / Fassa",
            technicalSpec = "Épaisseur 12.5mm • Largeur 1.2m • Longueur 2.5m • Poids ~8.5 kg/m²",
            stockLengthM = 2.5,
            stockWidthM = 1.2
        ),
        Material(
            id = "plaque_ba13_hydrofuge",
            category = MaterialCategory.PLACO,
            nameFr = "Plaque BA13 Hydrofuge Vert (Anti-humidité)",
            nameAr = "لوح جبس مقاوم للرطوبة BA13 أخضر",
            nameDerja = "لوح بلاكو خضراء ضد الرطوبة والماء",
            nameEn = "Moisture-Resistant BA13 Green Board",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf Hydro / Siniat",
            technicalSpec = "Absorption d'eau < 5% • Locaux humides EB+ privatif (SDB / Cuisine)"
        ),
        Material(
            id = "plaque_ba13_coupe_feu",
            category = MaterialCategory.PLACO,
            nameFr = "Plaque BA13 Coupe-Feu Rose (Ignifuge)",
            nameAr = "لوح جبس مقاوم للحريق BA13 وردي",
            nameDerja = "لوح بلاكو غوز ضد النار والحرارة",
            nameEn = "Fire-Resistant BA13 Pink Board",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf Flam / Siniat",
            technicalSpec = "Résistance au feu EI30 / EI60 • Cœur enrichi en fibres de verre"
        ),
        Material(
            id = "plaque_ba13_phonique",
            category = MaterialCategory.PLACO,
            nameFr = "Plaque BA13 Phonique Bleu (Isolation acoustique)",
            nameAr = "لوح جبس عازل للصوت BA13 أزرق",
            nameDerja = "لوح بلاكو زرقاء عازلة للصوت",
            nameEn = "Acoustic BA13 Blue Board",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf Diamant / Acoustique",
            technicalSpec = "Gain acoustique +3 à +5 dB • Haute densité cristalline"
        ),
        Material(
            id = "plaque_aquapanel_ciment",
            category = MaterialCategory.PLACO,
            nameFr = "Plaque Ciment Aquapanel Outdoor (Extérieur)",
            nameAr = "لوح إسمنت بورد أكوابانيل خارجي",
            nameDerja = "لوح سيمانت بورد أكوابانيل للواجهات",
            nameEn = "Aquapanel Outdoor Cement Board",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf Aquapanel / Externa",
            technicalSpec = "100% inorganique • Imputrescible • Résiste aux intempéries extrêmes"
        ),
        Material(
            id = "plaque_glassroc_exterieur",
            category = MaterialCategory.PLACO,
            nameFr = "Plaque Glassroc X / Outwear Glass (Extérieur)",
            nameAr = "لوح جلاس روك مقاوم للطقس والمطر",
            nameDerja = "لوح غلاس روك فيبر خارجي",
            nameEn = "Glassroc X Exterior Board",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Saint-Gobain / Aygips",
            technicalSpec = "Armature voile de verre • Résistance UV et pluie directe"
        ),

        // ==========================================
        // 2. METAL PROFILES & FRAMING (OSSATURE)
        // ==========================================
        Material(
            id = "rail_48",
            category = MaterialCategory.OSSATURE,
            nameFr = "Rail R48 (Profilé horizontal 3m)",
            nameAr = "سكة أفقية ريل Rail 48 (3 متر)",
            nameDerja = "ريل 48 للسطح والأرضية (3م)",
            nameEn = "R48 Floor/Ceiling Track Profile (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Perfilkit / Knauf",
            technicalSpec = "Acier galvanisé Z140 • Épaisseur nominale 0.6mm • Longueur 3000mm",
            stockLengthM = 3.0
        ),
        Material(
            id = "montant_48",
            category = MaterialCategory.OSSATURE,
            nameFr = "Montant M48 (Profilé vertical 3m)",
            nameAr = "قائم عمودي مونتون Montant 48 (3 متر)",
            nameDerja = "مونتون 48 عمودي للجدران (3م)",
            nameEn = "M48 Vertical Stud Profile (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Perfilkit / Knauf",
            technicalSpec = "Acier galvanisé Z140 • Entraxe 40cm / 60cm • Hauteur standard 3m",
            stockLengthM = 3.0
        ),
        Material(
            id = "rail_70",
            category = MaterialCategory.OSSATURE,
            nameFr = "Rail R70 (Profilé horizontal renforcé 3m)",
            nameAr = "سكة ريل 70 مقواة (3 متر)",
            nameDerja = "ريل 70 عريض للجدران العالية (3م)",
            nameEn = "R70 Heavy-Duty Track (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Perfilkit / Knauf",
            technicalSpec = "Pour cloisons grandes hauteurs (> 3.20m)"
        ),
        Material(
            id = "montant_70",
            category = MaterialCategory.OSSATURE,
            nameFr = "Montant M70 (Profilé vertical renforcé 3m)",
            nameAr = "قائم مونتون 70 مقوى (3 متر)",
            nameDerja = "مونتون 70 عريض للجدران السميكة (3م)",
            nameEn = "M70 Heavy-Duty Stud (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Perfilkit / Knauf",
            technicalSpec = "Inertie renforcée pour isolation acoustique et thermique accrue"
        ),
        Material(
            id = "fourrure",
            category = MaterialCategory.OSSATURE,
            nameFr = "Fourrure F47 (Profilé plafond 3m)",
            nameAr = "قضيب فورور F47 للأسقف المعلقة (3 متر)",
            nameDerja = "فورور F47 فوبلافون (3م)",
            nameEn = "F47 Ceiling Furring Channel (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Perfilkit / Knauf",
            technicalSpec = "Profilé porteur plafond suspendu • Entraxe standard 50cm",
            stockLengthM = 3.0
        ),
        Material(
            id = "corniere_angle",
            category = MaterialCategory.OSSATURE,
            nameFr = "Cornière d'Angle / Cornière L (3m)",
            nameAr = "زاوية محيطية كورنيار L (3 متر)",
            nameDerja = "كورنيار L دايرة بالبيوت (3م)",
            nameEn = "L-Shaped Perimeter Angle Profile (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Favori / Akifix",
            technicalSpec = "Profilé périphérique pour guidage mural et d'angle"
        ),
        Material(
            id = "omega_profil",
            category = MaterialCategory.OSSATURE,
            nameFr = "Profilé Oméga / Flex (3m)",
            nameAr = "بروفيل أوميغا فليكس للديكورات (3 متر)",
            nameDerja = "أوميغا ديكور وفليكس للدوران (3م)",
            nameEn = "Omega / Flexible Profile (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix",
            technicalSpec = "Pour gorges lumineuses, courbes et retombées décoratives"
        ),

        // ==========================================
        // 3. DEMOUNTABLE CEILING SYSTEM (DALLES 60x60)
        // ==========================================
        Material(
            id = "dalle_vinyl_60x60",
            category = MaterialCategory.DALLES,
            nameFr = "Dalle Plâtre Vinyle Lavable 60x60 cm",
            nameAr = "بلاطة جبس فينيل قابلة للغسل 60×60 سم",
            nameDerja = "دالة 60×60 فينيل بيضاء للمحلات والمكاتب",
            nameEn = "60x60 Washable Vinyl Ceiling Tile",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Favori / Knauf Karolam",
            technicalSpec = "Face avant film PVC lavable • Face arrière film aluminium pare-vapeur"
        ),
        Material(
            id = "dalle_laine_roche_60x60",
            category = MaterialCategory.DALLES,
            nameFr = "Dalle Acoustique Laine de Roche 60x60 cm",
            nameAr = "بلاطة صوف صخري صوتية 60×60 سم",
            nameDerja = "دالة روكفون صوتية وديكورية 60×60",
            nameEn = "60x60 Mineral Wool Acoustic Tile",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Rockfon Artic / AMF",
            technicalSpec = "Absorption acoustique αw = 0.85 • Résistance humidité 100% RH"
        ),
        Material(
            id = "porteur_3600",
            category = MaterialCategory.DALLES,
            nameFr = "Porteur T24 Principal 3.60m",
            nameAr = "حامل رئيسي طويل بورتور T24 (3.60 متر)",
            nameDerja = "بورتور 3.60م سكة رئيسية",
            nameEn = "T24 Main Runner 3.60m",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Favori / Rockfon / Perfilkit",
            technicalSpec = "Profilé porteur principal système T24 suspendu tous les 1.20m"
        ),
        Material(
            id = "entretoise_1200",
            category = MaterialCategory.DALLES,
            nameFr = "Entretoise T24 Secondaire 1.20m",
            nameAr = "عارضة عرضية أنتروتواز T24 (1.20 متر)",
            nameDerja = "أنتروتواز 1.20م بين البورتورات",
            nameEn = "T24 Cross Tee 1.20m",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Favori / Rockfon",
            technicalSpec = "Entretoise secondaire T24 à emboîtement rapide"
        ),
        Material(
            id = "entretoise_600",
            category = MaterialCategory.DALLES,
            nameFr = "Entretoise T24 Petite 0.60m",
            nameAr = "عارضة صغيرة أنتروتواز T24 (0.60 متر)",
            nameDerja = "أنتروتواز صغيرة 60سم للمربعات",
            nameEn = "T24 Cross Tee 0.60m",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Favori / Rockfon",
            technicalSpec = "Forme les cases carrées 600x600mm pour les dalles"
        ),
        Material(
            id = "corniere_rive_L",
            category = MaterialCategory.DALLES,
            nameFr = "Cornière de Rive Laquée Blanc (3m)",
            nameAr = "زاوية حافة بيضاء مدهونة للأسقف التفكيكية (3م)",
            nameDerja = "كورنيار ريف بيضاء دايرة بالحائط (3م)",
            nameEn = "White Wall Edge Angle Trim (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Profilex / Favori",
            technicalSpec = "Finition périmétrique laquée blanche 24x24mm"
        ),

        // ==========================================
        // 4. FASTENERS, SCREWS & ACCESSORIES (VISSERIE)
        // ==========================================
        Material(
            id = "suspente",
            category = MaterialCategory.VISSERIE,
            nameFr = "Suspente Pivot / Articulée F47",
            nameAr = "علاقة سوسبانت مع مشبك للفورور",
            nameDerja = "سوسبانت F47 تعليق في السقف",
            nameEn = "F47 Ceiling Hanger Clip",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix / Sbfix",
            technicalSpec = "Fixation rapide pour fourrures de faux plafond"
        ),
        Material(
            id = "tige_filetee_1m",
            category = MaterialCategory.VISSERIE,
            nameFr = "Tige Filetée M6 (1 mètre)",
            nameAr = "قضيب سلك مسنن تيج فيلتي M6 (1 متر)",
            nameDerja = "تيج فيلتي M6 للتعليق (1م)",
            nameEn = "M6 Threaded Rod (1m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix / Perfilkit",
            technicalSpec = "Suspension robuste avec écrous M6 et chevilles laiton"
        ),
        Material(
            id = "cavalier_pivot",
            category = MaterialCategory.VISSERIE,
            nameFr = "Cavalier Pivot F47",
            nameAr = "مشترك كافالييه F47",
            nameDerja = "كافاليي كليبس للفورور",
            nameEn = "F47 Pivot Clip Connector",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix",
            technicalSpec = "Raccord pivot pour tige filetée et profilé F47"
        ),
        Material(
            id = "vis_placo_25",
            category = MaterialCategory.VISSERIE,
            nameFr = "Vis Placo TTPC 3.5x25 mm (Boîte 1000 pcs)",
            nameAr = "براغي بلاكو سوداء 25 ملم (علبة 1000 برغي)",
            nameDerja = "علبة فيس بلاكو كحلة 25 مم (1000 فيس)",
            nameEn = "Drywall Screws 3.5x25mm (Box of 1000)",
            standardUnit = StandardUnit.BOX_1000,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix / Knauf",
            technicalSpec = "Tête trompette phosphatée noire auto-perceuse pour plaque simple"
        ),
        Material(
            id = "vis_placo_35",
            category = MaterialCategory.VISSERIE,
            nameFr = "Vis Placo TTPC 3.5x35 mm (Boîte 1000 pcs)",
            nameAr = "براغي بلاكو 35 ملم للطبقة المزدوجة (1000 برغي)",
            nameDerja = "علبة فيس بلاكو 35 مم للدوبلاج (1000)",
            nameEn = "Drywall Screws 3.5x35mm (Box of 1000)",
            standardUnit = StandardUnit.BOX_1000,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix",
            technicalSpec = "Pour double peau (2ème plaque de plâtre)"
        ),
        Material(
            id = "vis_trpf",
            category = MaterialCategory.VISSERIE,
            nameFr = "Vis TRPF Auto-perceuse Métal (Boîte 1000 pcs)",
            nameAr = "براغي حديد TRPF رأس عريض البقرة (1000 برغي)",
            nameDerja = "فيس تي إر بي إف البقرة للحديد (1000 فيس)",
            nameEn = "Framing Wafer Head Screws TRPF (Box of 1000)",
            standardUnit = StandardUnit.BOX_1000,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix",
            technicalSpec = "Fixation métal sur métal (rail sur montant / cornière)"
        ),
        Material(
            id = "vis_aquapanel",
            category = MaterialCategory.VISSERIE,
            nameFr = "Vis Aquapanel Inox Extérieur (Boîte 500 pcs)",
            nameAr = "براغي أكوابانيل مقاومة للصدأ (500 برغي)",
            nameDerja = "فيس أكوابانيل إينوكس للواجهات (500)",
            nameEn = "Aquapanel Stainless Screws (Box of 500)",
            standardUnit = StandardUnit.BOX_500,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf / Akifix",
            technicalSpec = "Traitement anti-corrosion spécial ciment et milieu extérieur"
        ),
        Material(
            id = "cheville_a_frapper",
            category = MaterialCategory.VISSERIE,
            nameFr = "Cheville à Frapper 6x40 mm (Boîte 200 pcs)",
            nameAr = "أوتاد ضرب شوفي 6×40 ملم لتثبيت السكك (200 قطعة)",
            nameDerja = "شوفي ضرب 6×40 للتثبيت في الإسمنت (200 كعبة)",
            nameEn = "Hammer Drive Anchors 6x40mm (Box of 200)",
            standardUnit = StandardUnit.BOX_200,
            compatibleTrades = listOf(TradeCategory.PLACO, TradeCategory.MACONNERIE),
            brandInfo = "Akifix",
            technicalSpec = "Ancrage direct dans béton plein, maçonnerie ou chape"
        ),

        // ==========================================
        // 5. JOINT COMPOUNDS, TAPES & ADHESIVES (ENDUITS)
        // ==========================================
        Material(
            id = "bande_a_joint_90m",
            category = MaterialCategory.ENDUITS,
            nameFr = "Bande à Joint Papier Micro-perforé (90m)",
            nameAr = "شريط فواصل ورقي مثقب (لفة 90 متر)",
            nameDerja = "رول بوند اجوان ورقة للفواصل (90م)",
            nameEn = "Paper Joint Tape (90m Roll)",
            standardUnit = StandardUnit.ROLL_90M,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf / Siniat / Akifix",
            technicalSpec = "Papier kraft blanc micro-perforé avec rainure axiale de pliage"
        ),
        Material(
            id = "bande_armee_30m",
            category = MaterialCategory.ENDUITS,
            nameFr = "Bande Armée Métallique d'Angle (30m)",
            nameAr = "شريط زوايا مسلح بالمعدن ضد الصدمات (30م)",
            nameDerja = "بوند أرمي حديد للزوايا البارزة (30م)",
            nameEn = "Metal Reinforced Corner Tape (30m)",
            standardUnit = StandardUnit.ROLL_30M,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix",
            technicalSpec = "Protection et renfort des angles sortants à 90°"
        ),
        Material(
            id = "trame_fibre_exterieur",
            category = MaterialCategory.ENDUITS,
            nameFr = "Trame Fibre de Verre Extérieure 160g (50m)",
            nameAr = "شبكة فيبر خارجية 160غ للأكوابانيل والواجهات (50م)",
            nameDerja = "ترام فيبر 160غ للواجهات الخارجية (50م)",
            nameEn = "Exterior Fiberglass Mesh 160g (50m)",
            standardUnit = StandardUnit.ROLL_50M,
            compatibleTrades = listOf(TradeCategory.PLACO, TradeCategory.CREPISSAGE),
            brandInfo = "Fassa Bortolo / Knauf",
            technicalSpec = "Résistante aux alcalis • Renfort obligatoire pour ciment et façades"
        ),
        Material(
            id = "enduit_joint_25kg",
            category = MaterialCategory.ENDUITS,
            nameFr = "Enduit à Joint Prise Lente 8H / 24H (Sac 25kg)",
            nameAr = "معجون فواصل أندوي سريع/بطيء (كيس 25 كغ)",
            nameDerja = "شكارة أندوي معجون بلاكو لافارج أو كوفيبلاست (25كغ)",
            nameEn = "Joint Filler Compound (25kg Bag)",
            standardUnit = StandardUnit.BAG_25KG,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf Jointfiller / Fassa / Afrochim",
            technicalSpec = "Enduit en poudre à base de plâtre et adjuvants pour collage des bandes"
        ),
        Material(
            id = "mortier_colle_map_25kg",
            category = MaterialCategory.ENDUITS,
            nameFr = "Mortier Adhésif MAP / Gypsomaf (Sac 25kg)",
            nameAr = "غراء تلصيق مباشر ماب MAP للألواح (كيس 25 كغ)",
            nameDerja = "غراء كول ماب MAP لتلصيق البلاكو عالحائط (25كغ)",
            nameEn = "Adhesive Mortar MAP (25kg Bag)",
            standardUnit = StandardUnit.BAG_25KG,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Knauf HP2 / Fassa Gypsomaf",
            technicalSpec = "Collage direct de plaques de plâtre sur murs bruts maçonnés"
        ),
        Material(
            id = "colle_ciment_exterieur_25kg",
            category = MaterialCategory.ENDUITS,
            nameFr = "Enduit & Colle Ciment Extérieur SM700 / A96 (25kg)",
            nameAr = "غراء ومعجون إسمنتي خارجي للواجهات (25 كغ)",
            nameDerja = "كول سيمو خارجي SM700 للأكوابانيل (25كغ)",
            nameEn = "Exterior Basecoat & Adhesive SM700 (25kg Bag)",
            standardUnit = StandardUnit.BAG_25KG,
            compatibleTrades = listOf(TradeCategory.PLACO, TradeCategory.CREPISSAGE),
            brandInfo = "Knauf SM700 / Fassa A96",
            technicalSpec = "Mortier d'armature minéral hydrofugé pour sous-enduit de façade"
        ),

        // ==========================================
        // 6. INSULATION (ISOLATION)
        // ==========================================
        Material(
            id = "laine_de_verre_50mm",
            category = MaterialCategory.ISOLATION,
            nameFr = "Laine de Verre Acoustique 50mm (Rouleau 15m²)",
            nameAr = "صوف زجاجي عازل 50 ملم (لفة 15 م²)",
            nameDerja = "لين دو فار عازل للصوت والسخانة (رول 15م²)",
            nameEn = "Acoustic Glass Wool 50mm (15m² Roll)",
            standardUnit = StandardUnit.ROLL_15M2,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Isover / Knauf Insulation",
            technicalSpec = "Conductivité thermique λ = 0.040 W/m.K • Absorption phonique"
        ),
        Material(
            id = "laine_de_roche_50mm",
            category = MaterialCategory.ISOLATION,
            nameFr = "Laine de Roche Haute Densité 50mm (Paquet 7.2m²)",
            nameAr = "صوف صخري كثافة عالية 50 ملم (باكي 7.2 م²)",
            nameDerja = "لين دو روش ضد الصوت والحرارة والنار (باكي 7.2م²)",
            nameEn = "High Density Rockwool 50mm (7.2m² Pack)",
            standardUnit = StandardUnit.BOX_7_2M2,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Rockwool / Knauf",
            technicalSpec = "Densité ~40-50 kg/m³ • Incombustible Euroclasse A1"
        ),
        Material(
            id = "bande_resiliente_48mm",
            category = MaterialCategory.ISOLATION,
            nameFr = "Bande Résiliente Acoustique 50mm (Rouleau 30m)",
            nameAr = "شريط عازل للصوت أسفل السكك (30 متر)",
            nameDerja = "بوند ريزيليانت كوتشوك تحت الريل (30م)",
            nameEn = "Acoustic Foam Gasket 50mm (30m Roll)",
            standardUnit = StandardUnit.ROLL_30M,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Akifix",
            technicalSpec = "Désolidarisation acoustique sous rails et cornières périphériques"
        ),
        Material(
            id = "silicone_coupe_feu",
            category = MaterialCategory.ISOLATION,
            nameFr = "Cartouche Silicone Coupe-Feu / Mastic",
            nameAr = "خرطوشة سيليكون مقاوم للحريق والإغلاق",
            nameDerja = "كارتوش سيليكون أو ماستيك معجون",
            nameEn = "Firestop Intumescent Sealant Cartridge",
            standardUnit = StandardUnit.CARTRIDGE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Soudal / Akifix",
            technicalSpec = "Calfeutrement coupe-feu des traversées et joints de dilatation"
        ),

        // ==========================================
        // 7. PVC PLAFONDS
        // ==========================================
        Material(
            id = "lambris_pvc_blanc",
            category = MaterialCategory.PLACO,
            nameFr = "Lambris PVC Blanc Brillant (Lame 6m)",
            nameAr = "شرائح بلاستيك PVC أبيض لامع للأسقف (6 متر)",
            nameDerja = "شرائح PVC فوبلافون للادوش والكوجينة (6م)",
            nameEn = "PVC Ceiling Panel Glossy White (6m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Standard PVC",
            technicalSpec = "Étanche 100% aux projections d'eau pour pièces d'eau"
        ),
        Material(
            id = "profile_u_pvc",
            category = MaterialCategory.PLACO,
            nameFr = "Profilé de Rive en U PVC (3m)",
            nameAr = "بروفيل حافة U بلاستيك محيطي (3 متر)",
            nameDerja = "بروفيل U داير بالحائط للـ PVC (3م)",
            nameEn = "PVC U-Channel Edge Profile (3m)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Standard PVC",
            technicalSpec = "Profilé de départ et de finition périphérique pour lambris PVC"
        ),

        // ==========================================
        // 8. PAINTING & FINISHES (PEINTURE)
        // ==========================================
        Material(
            id = "enduit_lissage_peinture_25kg",
            category = MaterialCategory.PEINTURE,
            nameFr = "Enduit de Lissage Blanc (Sac 25kg)",
            nameAr = "معجون تسوية وتمليس ناعم للدهان (25 كغ)",
            nameDerja = "شكارة أندوي ليسّاج بيضاء للدهينة (25كغ)",
            nameEn = "Skim Coat Finishing Plaster (25kg Bag)",
            standardUnit = StandardUnit.BAG_25KG,
            compatibleTrades = listOf(TradeCategory.PEINTURE),
            brandInfo = "Astral / Sovap / Fassa",
            technicalSpec = "Rendement moyen ~1.2 kg/m² pour 2 passes de ratissage"
        ),
        Material(
            id = "fixateur_primaire_20l",
            category = MaterialCategory.PEINTURE,
            nameFr = "Fixateur / Primaire d'Accrochage (Bidon 20L)",
            nameAr = "سائل أساس فيكساتور لربط الغبار (20 لتر)",
            nameDerja = "سطال فيكساتور لربط الغبرة والتأسيس (20 لتر)",
            nameEn = "Binding Primer / Fixative (20L Drum)",
            standardUnit = StandardUnit.BUCKET_20L,
            compatibleTrades = listOf(TradeCategory.PEINTURE),
            brandInfo = "Astral / Sovap / Corona",
            technicalSpec = "Bloque les fonds poreux et neutralise l'absorption du plâtre"
        ),
        Material(
            id = "peinture_finition_satinee_15l",
            category = MaterialCategory.PEINTURE,
            nameFr = "Peinture Finition Satinée / Mate (Bidon 15L)",
            nameAr = "دهان تشطيب ساتيني ناعم قابل للغسل (15 لتر)",
            nameDerja = "سطال دهينة سيتان ممتاز قابل للغسيل (15 لتر)",
            nameEn = "Washable Satin Acrylic Finish (15L Bucket)",
            standardUnit = StandardUnit.BUCKET_15L,
            compatibleTrades = listOf(TradeCategory.PEINTURE),
            brandInfo = "Astral Pantex / Sovap",
            technicalSpec = "Rendement ~10-12 m²/L par couche • 2 couches recommandées"
        ),
        Material(
            id = "peinture_sable_decoratif_5kg",
            category = MaterialCategory.PEINTURE,
            nameFr = "Peinture Décorative Sablé (Bidon 5kg)",
            nameAr = "دهان سابلي ديكوري رملي فاخر (5 كغ)",
            nameDerja = "سطال سابلي ديكور جاهز رملي (5 كغ)",
            nameEn = "Textured Sand-Effect Decorative Paint (5kg Bucket)",
            standardUnit = StandardUnit.BUCKET_5KG,
            compatibleTrades = listOf(TradeCategory.PEINTURE),
            brandInfo = "Astral / Sovap Décor",
            technicalSpec = "Aspect nacré et sablé • Consommation ~0.20-0.25 kg/m²"
        ),

        // ==========================================
        // 9. MASONRY (MACONNERIE)
        // ==========================================
        Material(
            id = "brique_12_trous",
            category = MaterialCategory.MACONNERIE,
            nameFr = "Brique Rouge 12 Trous (Mur Extérieur)",
            nameAr = "آجر أحمر 12 ثقب للجدران الخارجية",
            nameDerja = "ياجور 12 عين للحوائط والعرصات",
            nameEn = "12-Hole Red Clay Brick (Exterior)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.MACONNERIE),
            brandInfo = "Tuilerie Tunisienne",
            technicalSpec = "Dimensions 30x20x15 cm • Consommation ~18-20 briques/m²"
        ),
        Material(
            id = "brique_8_trous",
            category = MaterialCategory.MACONNERIE,
            nameFr = "Brique Rouge 8 Trous (Cloison Intérieure)",
            nameAr = "آجر أحمر 8 ثقوب للقواطع الداخلية",
            nameDerja = "ياجور 8 أعين للفواصل الداخلية",
            nameEn = "8-Hole Red Clay Brick (Partition)",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.MACONNERIE),
            brandInfo = "Tuilerie Tunisienne",
            technicalSpec = "Dimensions 30x10x15 cm • Pour cloisons traditionnelles"
        ),
        Material(
            id = "ciment_portland_50kg",
            category = MaterialCategory.MACONNERIE,
            nameFr = "Ciment Gris Portland CEM II (Sac 50kg)",
            nameAr = "إسمنت رمادي بورتلاند للبناء والملاط (50 كغ)",
            nameDerja = "شكارة سيمون رمادي للبنيان والترطيبة (50كغ)",
            nameEn = "Portland Cement CEM II 32.5/42.5 (50kg Bag)",
            standardUnit = StandardUnit.BAG_50KG,
            compatibleTrades = listOf(TradeCategory.MACONNERIE, TradeCategory.CREPISSAGE),
            brandInfo = "Ciments de Bizerte / Gabès / Enfidha",
            technicalSpec = "Norme NT 47.01 • Sac kraft 50kg standard Tunisie"
        ),
        Material(
            id = "sable_lave_m3",
            category = MaterialCategory.MACONNERIE,
            nameFr = "Sable Lavé de Carrière (Mètre Cube m³)",
            nameAr = "رمل مغسول للبناء والملاط (متر مكعب)",
            nameDerja = "متر مكعب رملة مغسولة للبنيان (m³)",
            nameEn = "Washed Construction Sand (m³)",
            standardUnit = StandardUnit.CUBIC_METER,
            compatibleTrades = listOf(TradeCategory.MACONNERIE),
            brandInfo = "Carrières Oued",
            technicalSpec = "Granulométrie 0/4mm propre et sans argile"
        ),

        // ==========================================
        // 10. RENDERING & CRÉPISSAGE
        // ==========================================
        Material(
            id = "sable_fin_crepissage_m3",
            category = MaterialCategory.CREPISSAGE,
            nameFr = "Sable Fin Tamisé pour Crépissage (m³)",
            nameAr = "رمل ناعم مصفى للياسة والترطيبة (متر مكعب)",
            nameDerja = "متر مكعب رملة جويدة مصفية للترطيبة (m³)",
            nameEn = "Fine Screened Plastering Sand (m³)",
            standardUnit = StandardUnit.CUBIC_METER,
            compatibleTrades = listOf(TradeCategory.CREPISSAGE),
            brandInfo = "Carrières",
            technicalSpec = "Granulométrie fine 0/2mm pour enduit de finition"
        ),

        // ==========================================
        // 11. TILING (CARRELAGE)
        // ==========================================
        Material(
            id = "ciment_colle_carrelage_25kg",
            category = MaterialCategory.CARRELAGE,
            nameFr = "Ciment Colle Carrelage C1/C2 (Sac 25kg)",
            nameAr = "إسمنت لاصق للسيراميك والبلاط (25 كغ)",
            nameDerja = "شكارة كول فايونس وسيراميك (25كغ)",
            nameEn = "Tile Adhesive Ciment-Colle (25kg Bag)",
            standardUnit = StandardUnit.BAG_25KG,
            compatibleTrades = listOf(TradeCategory.CARRELAGE),
            brandInfo = "Keraflex / Ciment Colle",
            technicalSpec = "Classe C1/C2E pour grès cérame et faïence murale"
        ),
        Material(
            id = "joint_carrelage_5kg",
            category = MaterialCategory.CARRELAGE,
            nameFr = "Mortier Joint Carrelage Fin (Sac 5kg)",
            nameAr = "معجون ترويب وفواصل السيراميك (5 كغ)",
            nameDerja = "شكارة جوان فايونس ملون (5كغ)",
            nameEn = "Tile Grout Powder (5kg Bag)",
            standardUnit = StandardUnit.BAG_5KG,
            compatibleTrades = listOf(TradeCategory.CARRELAGE),
            brandInfo = "Fassa Bortolo / Mapei",
            technicalSpec = "Joint fin 1-6mm hydrofugé anti-moisissure"
        ),

        // ==========================================
        // 12. JOINERY (BOIS & ALU)
        // ==========================================
        Material(
            id = "vis_bois_agglo_500",
            category = MaterialCategory.BOIS_ALU,
            nameFr = "Vis Bois Agglo 40mm (Boîte 500 pcs)",
            nameAr = "براغي خشب 40 ملم لتثبيت المفصلات (500 برغي)",
            nameDerja = "علبة فيس لوح 40 مم للكوادر والبيبان (500)",
            nameEn = "Wood Screws 40mm (Box of 500)",
            standardUnit = StandardUnit.BOX_500,
            compatibleTrades = listOf(TradeCategory.MENUISERIE),
            brandInfo = "Akifix",
            technicalSpec = "Tête fraisée zinguée pour bois massif et panneaux MDF"
        ),
        Material(
            id = "paumelle_porte_bois",
            category = MaterialCategory.BOIS_ALU,
            nameFr = "Paumelle / Charnière de Porte Standard",
            nameAr = "مفصلة باب خشبية قياسية",
            nameDerja = "بومال مفصلة للباب اللوح",
            nameEn = "Standard Door Hinge",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.MENUISERIE),
            brandInfo = "Quincaillerie",
            technicalSpec = "Acier bichromaté 140mm gauche/droite"
        ),
        Material(
            id = "mousse_polyurethane_750ml",
            category = MaterialCategory.BOIS_ALU,
            nameFr = "Mousse Polyuréthane Expansive (Aérosol 750ml)",
            nameAr = "فوم عازل بولي يوريثان لتثبيت الإطارات (750 مل)",
            nameDerja = "فوم رغوة تثبيت وعزل للكوادر والشبابك (750مل)",
            nameEn = "Expanding PU Foam (750ml Aerosol)",
            standardUnit = StandardUnit.AEROSOL_750ML,
            compatibleTrades = listOf(TradeCategory.MENUISERIE, TradeCategory.PLACO),
            brandInfo = "Soudal / Akifix",
            technicalSpec = "Expansion contrôlée pour calage thermique et phonique"
        ),
        Material(
            id = "joint_etancheite_fenetre_10m",
            category = MaterialCategory.BOIS_ALU,
            nameFr = "Joint d'Étanchéité Caoutchouc Noir (10m)",
            nameAr = "جلد عازل للمياه والرياح للألمنيوم (10 متر)",
            nameDerja = "جلد كوتشوك كحل عازل لشبابك الألمنيوم (10م)",
            nameEn = "EPDM Window Weatherstrip Gasket (10m)",
            standardUnit = StandardUnit.ROLL_10M,
            compatibleTrades = listOf(TradeCategory.MENUISERIE),
            brandInfo = "TPR / Akifix",
            technicalSpec = "Caoutchouc EPDM résistant aux UV et infiltrations d'air/eau"
        ),
        Material(
            id = "silicone_transparent_cartouche",
            category = MaterialCategory.BOIS_ALU,
            nameFr = "Cartouche Silicone Neutre / Transparent",
            nameAr = "خرطوشة سيليكون عازل شفاف",
            nameDerja = "كارتوش سيليكون شفاف لغلق الشبابك",
            nameEn = "Clear Neutral Silicone Sealant Cartridge",
            standardUnit = StandardUnit.CARTRIDGE,
            compatibleTrades = listOf(TradeCategory.MENUISERIE, TradeCategory.PLACO),
            brandInfo = "Soudal / Akifix",
            technicalSpec = "Mastic d'étanchéité transparent pour vitrage et menuiserie"
        ),

        // ==========================================
        // 13. ACCESS PANELS (TRAPPES DE VISITE)
        // ==========================================
        Material(
            id = "trappe_20x20",
            category = MaterialCategory.VISSERIE,
            nameFr = "Trappe de visite 20x20 cm",
            nameAr = "باب تفتيش 20×20 سم",
            nameDerja = "باب دكسيتي 20 في 20",
            nameEn = "Access Panel 20x20 cm",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Quincaillerie",
            technicalSpec = "Dimensions 20x20 cm"
        ),
        Material(
            id = "trappe_30x30",
            category = MaterialCategory.VISSERIE,
            nameFr = "Trappe de visite 30x30 cm",
            nameAr = "باب تفتيش 30×30 سم",
            nameDerja = "باب دكسيتي 30 في 30",
            nameEn = "Access Panel 30x30 cm",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Quincaillerie",
            technicalSpec = "Dimensions 30x30 cm"
        ),
        Material(
            id = "trappe_40x40",
            category = MaterialCategory.VISSERIE,
            nameFr = "Trappe de visite 40x40 cm",
            nameAr = "باب تفتيش 40×40 سم",
            nameDerja = "باب دكسيتي 40 في 40",
            nameEn = "Access Panel 40x40 cm",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Quincaillerie",
            technicalSpec = "Dimensions 40x40 cm"
        ),
        Material(
            id = "trappe_50x50",
            category = MaterialCategory.VISSERIE,
            nameFr = "Trappe de visite 50x50 cm",
            nameAr = "باب تفتيش 50×50 سم",
            nameDerja = "باب دكسيتي 50 في 50",
            nameEn = "Access Panel 50x50 cm",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Quincaillerie",
            technicalSpec = "Dimensions 50x50 cm"
        ),
        Material(
            id = "trappe_60x60",
            category = MaterialCategory.VISSERIE,
            nameFr = "Trappe de visite 60x60 cm",
            nameAr = "باب تفتيش 60×60 سم",
            nameDerja = "باب دكسيتي 60 في 60",
            nameEn = "Access Panel 60x60 cm",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Quincaillerie",
            technicalSpec = "Dimensions 60x60 cm"
        ),
        Material(
            id = "trappe_40x60",
            category = MaterialCategory.VISSERIE,
            nameFr = "Trappe de visite 40x60 cm",
            nameAr = "باب تفتيش 40×60 سم",
            nameDerja = "باب دكسيتي 40 في 60",
            nameEn = "Access Panel 40x60 cm",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Quincaillerie",
            technicalSpec = "Dimensions 40x60 cm"
        ),
        Material(
            id = "trappe_60x120",
            category = MaterialCategory.VISSERIE,
            nameFr = "Trappe de visite 60x120 cm",
            nameAr = "باب تفتيش 60×120 سم",
            nameDerja = "باب دكسيتي 60 في 120",
            nameEn = "Access Panel 60x120 cm",
            standardUnit = StandardUnit.PIECE,
            compatibleTrades = listOf(TradeCategory.PLACO),
            brandInfo = "Quincaillerie",
            technicalSpec = "Dimensions 60x120 cm"
        )
    )

    private val materialMap: Map<String, Material> by lazy {
        ALL_MATERIALS.associateBy { it.id }
    }

    fun getById(id: String): Material? = materialMap[id]

    fun getByCategory(category: MaterialCategory): List<Material> =
        ALL_MATERIALS.filter { it.category == category }

    fun getByTrade(trade: TradeCategory): List<Material> =
        ALL_MATERIALS.filter { it.compatibleTrades.contains(trade) }

    val allIds: Set<String> by lazy { materialMap.keys }
}
