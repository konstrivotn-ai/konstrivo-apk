package com.example.calculator

import com.example.data.local.MaterialPriceEntity
import com.example.data.model.*
import com.example.domain.fiscal.FiscalCatalog
import com.example.domain.material.MaterialCatalog
import com.example.domain.fiscal.TaxMath
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

object ConstructionCalculator {

    /** Optional access-panel line chosen by the user (STEP 9C2). */
    data class TrappeLine(
        val dimensionKey: String, // e.g. "40x40" -> material id "trappe_40x40"
        val quantity: Int
    )

    data class CalculationParams(
        val type: CalculationType,
        val area: Double,
        val perimeter: Double,
        val height: Double = 2.8,
        val linearMeters: Double = 0.0,
        val unitCount: Double = 1.0,
        val wasteMarginPercent: Double = 10.0,
        val boardType: String = "plaque_ba13_standard", // standard, hydrofuge, phonique, coupe_feu, aquapanel
        val isDoubleSkin: Boolean = false,
        val includeInsulation: Boolean = true,
        val insulationType: String = "laine_de_verre_50mm", // laine_de_verre, laine_de_roche
        val montantType: String? = null,
        // Selected horizontal track (rail) profile id. null keeps the legacy
        // behaviour (rail_48 for framing systems, rail_70 for Aquapanel).
        val railType: String? = null,
        val customLaborRatePerM2: Double? = null,
        // ==========================================
        // Drywall dimension parameters (Placo / BA13)
        // ==========================================
        val entraxeMontants: Double = 0.40, // maillage des montants en mètres (default 40 cm)
        val boardWidthM: Double = 1.2, // largeur de plaque BA13
        val boardLengthM: Double = 2.5, // longueur de plaque BA13
        val wallLengthMeters: Double = 0.0, // longueur du mur / de la cloison (0 => dérivée de area/height)
        // Openings area to subtract from masonry surfaces (portes / fenêtres)
        val netOpeningsArea: Double = 0.0,
        val jointsMeters: Double = 0.0, // longueur totale des joints (JOINTS_FINITION)
        val cornersCount: Double = 0.0, // nombre de mètres linéaires d'arêtes (bande armée)
        val finishQuality: Int = 1, // 1 = standard, 2 = renforcé (joints & finition)

        // STEP 9C2 — Optional access panels ("Trappe de visite"). Empty by default
        // => zero impact on legacy calculations. Prices are resolved exclusively
        // from the price map; initial seed value is 0.0 and stays editable in Tarifs.
        val trappes: List<TrappeLine> = emptyList(),
        // Cloison semantics: allow explicit configuration of sides and layers per side.
        // Backward compatibility: if these are left as 0, `isDoubleSkin` is used to infer layers.
        val numberOfSides: Int = 0,
        val layersPerSide: Int = 0,
        // ==========================================
        // Expense breakdown (HT) + fiscal overrides
        // ==========================================
        val transportCostHT: Double = 0.0,
        val equipmentCostHT: Double = 0.0,
        val otherCostHT: Double = 0.0,
        val tvaRate: Double? = null,
        val fiscalProfileId: String = ""
    )

    fun calculate(
        params: CalculationParams,
        priceMap: Map<String, MaterialPriceEntity>,
        country: Country
    ): CalculationSummary {
        val wasteFactor = 1.0 + (params.wasteMarginPercent / 100.0)
        val items = mutableListOf<CalculatedItem>()

        fun getPrice(baseKey: String): Double {
            return priceMap[baseKey]?.currentPrice ?: 0.0
        }

        fun getEntity(baseKey: String): MaterialPriceEntity? {
            return priceMap[baseKey]
        }

        val fiscalProfile = FiscalCatalog.resolve(
            country = country,
            profileId = params.fiscalProfileId,
            tvaRateOverride = params.tvaRate
        )
        val rate = fiscalProfile.tvaRate

        fun addItem(
            baseKey: String,
            qty: Double,
            defaultUnit: String,
            fallbackNameFr: String,
            fallbackNameAr: String,
            fallbackNameDerja: String,
            desc: String = ""
        ) {
            val entity = getEntity(baseKey)
            val unitPrice = entity?.currentPrice ?: 0.0
            val nameFr = entity?.nameFr ?: fallbackNameFr
            val nameAr = entity?.nameAr ?: fallbackNameAr
            val nameDerja = entity?.nameDerja ?: fallbackNameDerja
            val unit = entity?.unit ?: defaultUnit
            val total = (qty * unitPrice * 100).toLong() / 100.0

            val itemTva = TaxMath.round2(total * rate / 100.0)
            val itemTTC = TaxMath.round2(total + itemTva)

            items.add(
                CalculatedItem(
                    id = baseKey,
                    nameFr = nameFr,
                    nameAr = nameAr,
                    nameDerja = nameDerja,
                    quantity = qty,
                    unit = unit,
                    unitPrice = unitPrice,
                    totalPrice = total,
                    category = entity?.category ?: "general",
                    description = desc,
                    unitPriceHT = unitPrice,
                    tvaRate = rate,
                    tvaAmount = itemTva,
                    totalPriceTTC = itemTTC
                )
            )
        }

        // Helper to read material stock length (meters) from domain catalog when available.
        fun stockLengthFor(baseKey: String): Double? {
            return MaterialCatalog.ALL_MATERIALS.firstOrNull { it.id == baseKey }?.stockLengthM
        }

        val area = max(0.0, params.area)
        val perimeter = if (params.perimeter > 0) params.perimeter else if (area > 0) 4 * Math.sqrt(area) else 0.0
        var estimatedLaborRate = params.customLaborRatePerM2 ?: 12.0

        when (params.type) {
            CalculationType.PLACO_PLAFOND_FIXE -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (12.0 * country.rateToTndMultiplier)
                val boardKey = params.boardType.ifEmpty { "plaque_ba13_standard" }
                val numBoards = ceil((area * wasteFactor) / 3.0)
                addItem(
                    boardKey,
                    numBoards,
                    "لوح (Plaque)",
                    "Plaques de plâtre BA13 (1.2x2.5m = 3m²)",
                    "ألواح جبس قياسية BA13 (3 م²)",
                    "ألواح بلاكو بلاتر BA13",
                    "Calculé avec marge de chute ${(params.wasteMarginPercent).toInt()}%"
                )

                val fourrures = ceil((area * 2.0) / 3.0)
                addItem("fourrure", fourrures, "قطعة 3م (Pièce)", "Fourrures F47 (3m)", "قضبان فورور F47 (3 متر)", "قضبان فورور فوبلافون (3م)", "Espacées tous les 50-60 cm")

                val corniers = ceil(perimeter / 3.0)
                addItem("corniere_angle", corniers, "قطعة 3م (Pièce)", "Cornières d'Angle L (3m)", "زوايا محيطية كورنيار L (3 متر)", "كورنيار L دايرة بالبيوت (3م)", "Fixation au périmètre")

                val suspentes = ceil(area * 1.2)
                addItem("suspente", suspentes, "قطعة (Pièce)", "Suspentes Pivot F47", "علاقات سوسبانت مع مشابك", "سوسبانت تعليق في السقف", "1.2 suspente par m²")

                val cavaliers = ceil(area * 1.2)
                addItem("cavalier_pivot", cavaliers, "قطعة (Pièce)", "Cavaliers F47", "مشابك كافاليي", "كافاليي كليبس للفورور")

                val visBoxes = ceil((area * 15.0) / 1000.0)
                addItem("vis_placo_25", visBoxes, "علبة 1000 (Boîte)", "Vis Placo TTPC 25mm (Boîte 1000)", "براغي بلاكو 25 ملم (علبة 1000)", "فيس بلاكو 25مم كحلة (1000 فيس)", "15 vis / m² (${(area * 15).toInt()} vis au total)")

                val enduitBags = ceil((area * 0.35) / 25.0)
                addItem("enduit_joint_25kg", enduitBags, "كيس 25kg (Sac)", "Enduit à Joint 8H (Sac 25kg)", "معجون فواصل أندوي (كيس 25 كغ)", "أندوي معجون بلاكو لافارج (25كغ)", "0.35 kg/m²")

                val bandeRolls = ceil(area / 90.0)
                addItem("bande_a_joint_90m", bandeRolls, "لفة 90m (Rouleau)", "Bande à Joint Papier (90m)", "شريط فواصل ورقي (لفة 90 متر)", "رول بوند اجوان ورقة (90م)", "1 mètre linéaire par m²")

                if (params.includeInsulation) {
                    val insKey = params.insulationType
                    if (insKey == "laine_de_roche_50mm") {
                        val boxes = ceil((area * wasteFactor) / 7.2)
                        addItem("laine_de_roche_50mm", boxes, "باكي 7.2m² (Paquet)", "Laine de Roche 50mm (7.2m²)", "صوف صخري كثافة عالية 50 ملم", "لين دو روش ضد الصوت والحرارة")
                    } else {
                        val rolls = ceil((area * wasteFactor) / 15.0)
                        addItem("laine_de_verre_50mm", rolls, "لفة 15m² (Rouleau)", "Laine de Verre 50mm (15m²)", "صوف زجاجي عازل 50 ملم (لفة 15 م²)", "لين دو فار عازل للصوت")
                    }
                }
                // STEP 9C2 — Trappes de visite (optional user add-on lines).
                // Pricing comes exclusively from the price map (entity.currentPrice):
                // initial seed value is 0.0 and stays editable in Tarifs. Nothing
                // related to prices is hardcoded here.
                params.trappes.forEach { trappe ->
                    val qty = trappe.quantity.toDouble()
                    if (qty > 0.0) {
                        addItem(
                            "trappe_${trappe.dimensionKey}",
                            qty,
                            "قطعة (Pièce)",
                            "Trappe de visite ${trappe.dimensionKey} cm",
                            "باب تفتيش ${trappe.dimensionKey} سم",
                            "تراب ديزيت ${trappe.dimensionKey} سم",
                            "Option ajoutée par l'utilisateur"
                        )
                    }
                }
            }

            CalculationType.PLACO_CLOISON -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (16.0 * country.rateToTndMultiplier)
                val boardKey = params.boardType.ifEmpty { "plaque_ba13_standard" }
                val boardArea = max(0.5, params.boardWidthM * params.boardLengthM) // 1.2 x 2.5 = 3.0 m²

                // Support explicit configuration for number of sides and layers per side while
                // remaining backward compatible with `isDoubleSkin`.
                val effectiveNumberOfSides = if (params.numberOfSides > 0) params.numberOfSides.toDouble() else 2.0
                val effectiveLayersPerSide = if (params.layersPerSide > 0) params.layersPerSide.toDouble() else if (params.isDoubleSkin) 2.0 else 1.0
                val faces = effectiveNumberOfSides * effectiveLayersPerSide

                // Plaques calculées depuis les dimensions réelles du panneau, le nombre de faces + la marge de chute.
                val numBoards = ceil((area * faces * wasteFactor) / boardArea)
                addItem(
                    boardKey,
                    numBoards,
                    "لوح (Plaque)",
                    if (faces == 4.0) "Plaques BA13 Double Peau (4 faces)" else "Plaques BA13 Double Face (2 faces)",
                    if (faces == 4.0) "ألواح جبس BA13 تغليف مزدوج (4 جهات)" else "ألواح جبس BA13 تغليف وجهين",
                    if (faces == 4.0) "ألواح بلاكو تغليف دوبل من الشيرتين" else "ألواح بلاكو تغليف وجهين للفاصل",
                    "${faces} faces x ${area} m² + chute ${(params.wasteMarginPercent).toInt()}% / ${boardArea} m²"
                )

                // Longueur de cloison (m) — fournie ou dérivée de la surface / hauteur.
                val wallLength = if (params.wallLengthMeters > 0) params.wallLengthMeters
                else if (params.height > 0) area / params.height
                else perimeter / 2.0

                // Rails plancher + plafond : 2 fois la longueur du mur.
                val railKey = params.railType ?: "rail_48"
                val railStock = stockLengthFor(railKey) ?: params.boardLengthM
                val rails = ceil((wallLength * 2.0) / railStock)
                addItem(railKey, rails, "قطعة 3م (Pièce)", "Rails 48 (3m)", "سكك أفقية ريل Rail 48 (3 متر)", "ريل 48 للسطح والأرضية (3م)", "2 rails (sol + plafond) x ${wallLength}m")

                // Montants verticaux espacés selon l'entraxe (0.40m par défaut).
                val studs = floor(wallLength / params.entraxeMontants).toLong() + 1
                // Grandes hauteurs : chaque montant est composé de plusieurs longueurs + recouvrement (~10%).
                val tallCoupling = if (params.height > 3.0) 1.1 else 1.0
                val montantLinearMeters = studs * params.height * tallCoupling
                val montants = ceil(montantLinearMeters / params.boardLengthM)
                val montantKey = params.montantType ?: "montant_48"
                addItem(
                    montantKey,
                    montants,
                    "قطعة 3م (Pièce)",
                    "Montants 48 (3m)",
                    "قوائم عمودية مونتون Montant 48 (3م)",
                    "مونتون 48 عمودي للجدران (3م)",
                    "${studs} montants @${(params.entraxeMontants * 100).toInt()}cm, hauteur ${params.height}m" +
                        if (params.height > 3.0) " (rallonges + recouvrement 10%)" else ""
                )

                // Visserie placo : ~18 vis par plaque.
                val visBoxes = ceil((numBoards * 18.0) / 1000.0)
                addItem("vis_placo_25", visBoxes, "علبة 1000 (Boîte)", "Vis Placo TTPC 25mm (Boîte 1000)", "براغي بلاكو 25 ملم (علبة 1000)", "فيس بلاكو 25مم كحلة (1000 فيس)", "${(numBoards * 18).toLong()} vis (~18 / plaque)")

                // Vis TRPF : fixation rail/montant (4 par montant).
                val trpfBoxes = ceil((studs * 4.0) / 1000.0)
                addItem("vis_trpf", trpfBoxes, "علبة 1000 (Boîte)", "Vis TRPF Métal (Boîte 1000)", "براغي حديد TRPF البقرة (1000)", "فيس تي إر بي إف البقرة للحديد", "${studs * 4L} vis TRPF (4 / montant)")

                // Chevilles : fixation rails au gros œuvre (1 cheville / 50cm sur 2 rails).
                val chevilleAnchors = ceil(wallLength * 2.0 / 0.5)
                val chevillesBoxes = ceil(chevilleAnchors / 200.0)
                addItem("cheville_a_frapper", chevillesBoxes, "علبة 200 (Boîte)", "Chevilles à Frapper 6x40 (Boîte 200)", "أوتاد ضرب شوفي 6×40 ملم (200 قطعة)", "شوفي ضرب 6×40 للتثبيت في الإسمنت", "${chevilleAnchors.toLong()} chevilles / 2 rails")

                // Enduit & bande : calculés sur la surface totale à jointoyer (2 faces).
                val jointArea = area * faces
                val enduitBags = ceil((jointArea * 0.35) / 25.0)
                addItem("enduit_joint_25kg", enduitBags, "كيس 25kg (Sac)", "Enduit à Joint 8H (Sac 25kg)", "معجون فواصل أندوي (كيس 25 كغ)", "أندوي معجون بلاكو لافارج", "0.35 kg / m² jointoyé (${jointArea} m²)")

                val bandeRolls = ceil(jointArea / 90.0)
                addItem("bande_a_joint_90m", bandeRolls, "لفة 90m (Rouleau)", "Bande à Joint Papier (90m)", "شريط فواصل ورقي (لفة 90 متر)", "رول بوند اجوان ورقة للفواصل", "~1 ml de bande / m² ($jointArea ml)")

                val bandResilienteRolls = ceil((wallLength * 2.0) / 30.0)
                addItem("bande_resiliente_48mm", bandResilienteRolls, "لفة 30m (Rouleau)", "Bande Résiliente Acoustique (30m)", "شريط عازل للصوت أسفل السكك (30م)", "بوند ريزيليانت كوتشوك تحت الريل", "Sous les 2 rails (${wallLength * 2.0} ml)")

                if (params.includeInsulation) {
                    val insKey = params.insulationType
                    if (insKey == "laine_de_roche_50mm") {
                        val boxes = ceil((area * wasteFactor) / 7.2)
                        addItem("laine_de_roche_50mm", boxes, "باكي 7.2m² (Paquet)", "Laine de Roche 50mm (7.2m²)", "صوف صخري كثافة عالية 50 ملم", "لين دو روش عازل وسط الفاصل")
                    } else {
                        val rolls = ceil((area * wasteFactor) / 15.0)
                        addItem("laine_de_verre_50mm", rolls, "لفة 15m² (Rouleau)", "Laine de Verre 50mm (15m²)", "صوف زجاجي عازل 50 ملم", "لين دو فار وسط الهيكل")
                    }
                }
            }

            CalculationType.PLACO_DEMONTABLE -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (10.0 * country.rateToTndMultiplier)
                val isRockfon = params.boardType == "dalle_laine_roche_60x60"
                val dalleKey = if (isRockfon) "dalle_laine_roche_60x60" else "dalle_vinyl_60x60"
                val dallesCount = ceil(area * 2.8 * wasteFactor)
                addItem(
                    dalleKey,
                    dallesCount,
                    "قطعة (Dalle)",
                    if (isRockfon) "Dalles Laine de Roche 60x60 cm" else "Dalles Vinyle Lavables 60x60 cm",
                    if (isRockfon) "بلاطات صوف صخري صوتية 60×60 سم" else "بلاطات جبس فينيل قابلة للغسل 60×60 سم",
                    if (isRockfon) "دالات روكفون صوتية 60×60" else "دالات 60×60 فينيل بيضاء",
                    "2.8 dalles / m²"
                )

                val porteurs = ceil(area * 0.23)
                addItem("porteur_3600", porteurs, "قطعة 3.6m (Pièce)", "Porteurs T24 (3.60m)", "حوامل رئيسية بورتور T24 (3.60 متر)", "بورتور 3.60م سكة رئيسية", "0.23 pièce / m²")

                val entretoises12 = ceil(area * 1.4)
                addItem("entretoise_1200", entretoises12, "قطعة 1.2m (Pièce)", "Entretoises T24 (1.20m)", "عوارض عرضية أنتروتواز T24 (1.20م)", "أنتروتواز 1.20م بين البورتورات", "1.4 pièce / m²")

                val entretoises06 = ceil(area * 1.4)
                addItem("entretoise_600", entretoises06, "قطعة 0.6m (Pièce)", "Entretoises T24 (0.60m)", "عوارض صغيرة أنتروتواز T24 (0.60م)", "أنتروتواز صغيرة 60سم للمربعات", "1.4 pièce / m²")

                val corniersRive = ceil(perimeter / 3.0)
                addItem("corniere_rive_L", corniersRive, "قطعة 3m (Pièce)", "Cornières de Rive Laquées (3m)", "زوايا حافة بيضاء محيطية (3م)", "كورنيار ريف بيضاء دايرة بالحائط (3م)", "Périmètre de la pièce")

                val tiges = ceil(area * 0.7)
                addItem("tige_filetee_1m", tiges, "قطعة 1م (Pièce)", "Tiges Filetées M6 (1m)", "قضبان سلك مسنن تيج فيلتي M6 (1م)", "تيج فيلتي M6 للتعليق في الدالة", "0.7 tige / m²")

                val chevilles = ceil(area * 0.7)
                addItem("suspente", chevilles, "قطعة (Pièce)", "Attaches & Suspentes T24", "مشابك وعلاقات شبكة T24", "كليبس وسوسبانت شبكة T24")
            }

            CalculationType.PLACO_AQUAPANEL -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (25.0 * country.rateToTndMultiplier)
                val numBoards = ceil((area * wasteFactor) / 3.0)
                addItem("plaque_aquapanel_ciment", numBoards, "لوح (Plaque)", "Plaques Aquapanel Outdoor (3m²)", "ألواح إسمنت بورد أكوابانيل للواجهات", "ألواح سيمانت بورد أكوابانيل خارجي", "Résistant aux intempéries et à l'eau")

                val trameRolls = ceil((area * 1.1) / 50.0)
                addItem("trame_fibre_exterieur", trameRolls, "لفة 50m (Rouleau)", "Trame Fibre de Verre 160g (50m)", "شبكة فيبر خارجية 160غ للواجهات (50م)", "ترام فيبر 160غ عريضة مضادة للتشقق", "1.1 m / m²")

                val colleBags = ceil((area * 3.5) / 25.0)
                addItem("colle_ciment_exterieur_25kg", colleBags, "كيس 25kg (Sac)", "Enduit & Colle Ciment SM700 (25kg)", "غراء ومعجون إسمنتي خارجي (25 كغ)", "كول سيمو خارجي SM700 للأكوابانيل", "3.5 kg / m²")

                val visBoxes = ceil((area * 20.0) / 500.0)
                addItem("vis_aquapanel", visBoxes, "علبة 500 (Boîte)", "Vis Aquapanel Inox (Boîte 500)", "براغي أكوابانيل مقاومة للصدأ (500)", "فيس أكوابانيل إينوكس للواجهات", "20 vis / m²")

                val railKey = params.railType ?: "rail_70"
                val rails70 = ceil((perimeter * 2.0) / 3.0)
                addItem(railKey, rails70, "قطعة 3م (Pièce)", "Rails 70 Renforcés (3m)", "سكك ريل 70 مقواة للواجهات (3م)", "ريل 70 عريض ومدعم للواجهة")

                val montants70 = ceil((area * 2.0) / 3.0)
                addItem("montant_70", montants70, "قطعة 3م (Pièce)", "Montants 70 Renforcés (3m)", "قوائم مونتون 70 مقواة (3م)", "مونتون 70 عريض مقاوم للرياح")
            }

            CalculationType.PLACO_CACHE_LUMIERE -> {
                val linearM = if (params.linearMeters > 0) params.linearMeters else perimeter
                estimatedLaborRate = params.customLaborRatePerM2 ?: (18.0 * country.rateToTndMultiplier)

                val numBoards = ceil((linearM * 0.45 * wasteFactor) / 3.0)
                addItem("plaque_ba13_standard", numBoards, "لوح (Plaque)", "Plaques BA13 pour Caisson", "ألواح جبس BA13 للكرتوش والشفة", "ألواح بلاكو للكرتوش وهبوط الميزان", "Découpe des bandes de retombée et gorge LED")

                val fourrures = ceil((linearM * 1.5) / 3.0)
                addItem("fourrure", fourrures, "قطعة 3م (Pièce)", "Fourrures F47 (3m)", "قضبان فورور F47 (3م)", "فورور F47 لتجميع الكرتوش")

                val corniers = ceil((linearM * 2.0) / 3.0)
                addItem("corniere_angle", corniers, "قطعة 3م (Pièce)", "Cornières L (3m)", "زوايا كورنيار L محيطية (3م)", "كورنيار L دايرة بالكرتوش")

                val bandeArmee = ceil(linearM / 30.0)
                addItem("bande_armee_30m", bandeArmee, "لفة 30m (Rouleau)", "Bande Armée Métallique (30m)", "شريط زوايا مسلح بالمعدن (30م)", "بوند أرمي حديد لحماية حواف الكرتوش", "Protection des arêtes vives")

                val visBoxes = ceil((linearM * 20.0) / 1000.0)
                addItem("vis_placo_25", visBoxes, "علبة 1000 (Boîte)", "Vis Placo 25mm (Boîte 1000)", "براغي بلاكو 25 ملم (1000)", "فيس بلاكو 25مم كحلة (1000 فيس)")

                val enduitBags = ceil((linearM * 0.25) / 25.0)
                addItem("enduit_joint_25kg", enduitBags, "كيس 25kg (Sac)", "Enduit à Joint (Sac 25kg)", "معجون فواصل أندوي (25 كغ)", "أندوي معجون للكرتوش والفينيسيون")
            }

            CalculationType.PLACO_PVC -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (11.0 * country.rateToTndMultiplier)
                val lameCount = ceil((area * wasteFactor) / 1.2) // Each 6m lame covers 1.2m²
                addItem("lambris_pvc_blanc", lameCount, "شريحة 6م (Lame)", "Lambris PVC Blanc 6m", "شرائح بلاستيك PVC أبيض لامع (6م)", "شرائح PVC فوبلافون للادوش والكوجينة (6م)", "1.2 m² par lame de 6m")

                val profilURive = ceil(perimeter / 3.0)
                addItem("profile_u_pvc", profilURive, "قطعة 3م (Pièce)", "Profilés U de Rive PVC (3m)", "بروفيل حافة U بلاستيك محيطي (3م)", "بروفيل U داير بالحائط للـ PVC")

                val fourrures = ceil((area * 2.0) / 3.0)
                addItem("fourrure", fourrures, "قطعة 3م (Pièce)", "Fourrures F47 Ossature (3m)", "قضبان فورور F47 للهيكل (3م)", "فورور F47 فوبلافون")

                val corniers = ceil(perimeter / 3.0)
                addItem("corniere_angle", corniers, "قطعة 3م (Pièce)", "Cornières L (3m)", "زوايا كورنيار L (3م)", "كورنيار L دايرة بالبيوت")

                val trpfBoxes = ceil((area * 15.0) / 1000.0)
                addItem("vis_trpf", trpfBoxes, "علبة 1000 (Boîte)", "Vis TRPF pour PVC (Boîte 1000)", "براغي تثبيت TRPF (1000)", "فيس تي إر بي إف البقرة لتثبيت الـ PVC")
            }

            // Doublage Collé : plaques BA13 collées directement sur le mur (sans ossature)
            CalculationType.PLACO_DOUBLAGE_COLLE -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (9.0 * country.rateToTndMultiplier)
                val boardArea = max(0.5, params.boardWidthM * params.boardLengthM)

                val numBoards = ceil((area * wasteFactor) / boardArea)
                addItem(
                    params.boardType.ifEmpty { "plaque_ba13_standard" },
                    numBoards, "لوح (Plaque)",
                    "Plaques BA13 Collées (1 face)",
                    "ألواح جبس BA13 ملصقة على الحائط",
                    "لوح بلاكو ملصق بالكول",
                    "1 face x $area m² + chute ${(params.wasteMarginPercent).toInt()}%"
                )

                val mapBags = ceil(area / 8.0)
                addItem("mortier_colle_map_25kg", mapBags, "كيس 25kg (Sac)", "Mortier Adhésif MAP / Gypsomaf (Sac 25kg)", "غراء تلصيق مباشر ماب MAP للألواح (كيس 25 كغ)", "كول ماب MAP لتلصيق البلاكو (25كغ)", "1 sac / 8 m²")

                val edgeAnchors = if (perimeter > 0) perimeter else 4 * Math.sqrt(area)
                val chevillesBoxes = ceil((edgeAnchors / 0.5) / 200.0)
                addItem("cheville_a_frapper", chevillesBoxes, "علبة 200 (Boîte)", "Chevilles à Frapper 6x40 (Boîte 200)", "أوتاد ضرب شوفي 6×40 ملم (200 قطعة)", "شوفي ضرب 6×40 لتثبيت الأطراف", "Renfort bordures / joints de plaques")

                val enduitBags = ceil((area * 0.35) / 25.0)
                addItem("enduit_joint_25kg", enduitBags, "كيس 25kg (Sac)", "Enduit à Joint 8H (Sac 25kg)", "معجون فواصل أندوي (كيس 25 كغ)", "أندوي معجون بلاكو لافارج", "0.35 kg / m²")

                val bandeRolls = ceil(area / 90.0)
                addItem("bande_a_joint_90m", bandeRolls, "لفة 90m (Rouleau)", "Bande à Joint Papier (90m)", "شريط فواصل ورقي (لفة 90 متر)", "رول بوند اجوان ورقة للفواصل", "~1 ml / m²")
            }

            // Doublage sur ossature : structure métallique devant le mur existant
            CalculationType.PLACO_DOUBLAGE_OSSATURE -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (13.0 * country.rateToTndMultiplier)
                val boardArea = max(0.5, params.boardWidthM * params.boardLengthM)
                val wallLength = if (params.wallLengthMeters > 0) params.wallLengthMeters
                else if (params.height > 0) area / params.height
                else perimeter / 2.0

                val numBoards = ceil((area * wasteFactor) / boardArea)
                addItem(
                    params.boardType.ifEmpty { "plaque_ba13_standard" },
                    numBoards, "لوح (Plaque)",
                    "Plaques BA13 sur Ossature (1 face)",
                    "ألواح جبس BA13 على هيكل معدني",
                    "لوح بلاكو على الريل والمونتون",
                    "1 face x $area m² + chute ${(params.wasteMarginPercent).toInt()}%"
                )

                val railKey = params.railType ?: "rail_48"
                val railStock = stockLengthFor(railKey) ?: params.boardLengthM
                val rails = ceil((wallLength * 2.0) / railStock)
                addItem(railKey, rails, "قطعة 3م (Pièce)", "Rails 48 (3m)", "سكك أفقية ريل Rail 48 (3 متر)", "ريل 48 للسطح والأرضية (3م)", "2 rails (sol + plafond) x ${wallLength}m")

                val studs = floor(wallLength / params.entraxeMontants).toLong() + 1
                val tallCoupling = if (params.height > 3.0) 1.1 else 1.0
                val montants = ceil(studs * params.height * tallCoupling / params.boardLengthM)
                val montantKey = params.montantType ?: "montant_48"
                addItem(montantKey, montants, "قطعة 3م (Pièce)", "Montants 48 (3m)", "قوائم عمودية مونتون Montant 48 (3م)", "مونتون 48 عمودي للجدران (3م)", "${studs} montants @${(params.entraxeMontants * 100).toInt()}cm")

                val visBoxes = ceil((numBoards * 18.0) / 1000.0)
                addItem("vis_placo_25", visBoxes, "علبة 1000 (Boîte)", "Vis Placo TTPC 25mm (Boîte 1000)", "براغي بلاكو 25 ملم (علبة 1000)", "فيس بلاكو 25مم كحلة (1000 فيس)", "${(numBoards * 18).toLong()} vis (~18 / plaque)")

                val trpfBoxes = ceil((studs * 4.0) / 1000.0)
                addItem("vis_trpf", trpfBoxes, "علبة 1000 (Boîte)", "Vis TRPF Métal (Boîte 1000)", "براغي حديد TRPF البقرة (1000)", "فيس تي إر بي إف البقرة للحديد", "${studs * 4L} vis TRPF (4 / montant)")

                val chevilleAnchors = ceil(wallLength * 2.0 / 0.5)
                val chevillesBoxes = ceil(chevilleAnchors / 200.0)
                addItem("cheville_a_frapper", chevillesBoxes, "علبة 200 (Boîte)", "Chevilles à Frapper 6x40 (Boîte 200)", "أوتاد ضرب شوفي 6×40 ملم (200 قطعة)", "شوفي ضرب 6×40 للتثبيت في الإسمنت", "${chevilleAnchors.toLong()} chevilles / 2 rails")

                if (params.includeInsulation) {
                    val insKey = params.insulationType
                    if (insKey == "laine_de_roche_50mm") {
                        val boxes = ceil((area * wasteFactor) / 7.2)
                        addItem("laine_de_roche_50mm", boxes, "باكي 7.2m² (Paquet)", "Laine de Roche 50mm (7.2m²)", "صوف صخري كثافة عالية 50 ملم", "لين دو روش عازل خلف التغليف")
                    } else {
                        val rolls = ceil((area * wasteFactor) / 15.0)
                        addItem("laine_de_verre_50mm", rolls, "لفة 15m² (Rouleau)", "Laine de Verre 50mm (15m²)", "صوف زجاجي عازل 50 ملم", "لين دو فار خلف التغليف")
                    }
                }

                val enduitBags = ceil((area * 0.35) / 25.0)
                addItem("enduit_joint_25kg", enduitBags, "كيس 25kg (Sac)", "Enduit à Joint 8H (Sac 25kg)", "معجون فواصل أندوي (كيس 25 كغ)", "أندوي معجون بلاكو لافارج", "0.35 kg / m²")

                val bandeRolls = ceil(area / 90.0)
                addItem("bande_a_joint_90m", bandeRolls, "لفة 90m (Rouleau)", "Bande à Joint Papier (90m)", "شريط فواصل ورقي (لفة 90 متر)", "رول بوند اجوان ورقة للفواصل", "~1 ml / m²")
            }

            // Plafond autoportant : repose sur les murs latéraux — ni suspentes, ni cavaliers
            CalculationType.PLACO_PLAFOND_AUTOPORTANT -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (12.0 * country.rateToTndMultiplier)
                val boardArea = max(0.5, params.boardWidthM * params.boardLengthM)
                val span = Math.sqrt(area) * 1.2 // portée principale approximée (L max)
                val perimeterCalc = if (perimeter > 0) perimeter else 2.0 * (span + area / span)

                val numBoards = ceil((area * wasteFactor) / boardArea)
                addItem(params.boardType.ifEmpty { "plaque_ba13_standard" }, numBoards, "لوح (Plaque)",
                    "Plaques BA13 Plafond Autoportant",
                    "ألواح جبس BA13 سقف ذاتي الارتكاز",
                    "لوح بلاكو للفوبلافون أوتوپورتان",
                    "Surface $area m² + chute ${(params.wasteMarginPercent).toInt()}%")

                // Solives (montants) porteurs perpendiculaires à la portée.
                // The cross dimension is the perpendicular width to the main span.
                // Use area/span which has units of meters (m² / m = m). The previous
                // speculative Math.sqrt(area * perimeter) had units m^{3/2} and is
                // dimensionally incorrect for a length — removing it.
                val cross = if (span > 0) area / span else 0.0
                val joists = floor(cross / params.entraxeMontants).toLong() + 1
                val tallCoupling = if (params.height > 3.0) 1.1 else 1.0
                val montantLinearMeters = joists * span * tallCoupling
                val montants = ceil(montantLinearMeters / params.boardLengthM)
                val montantKey = params.montantType ?: "montant_48"
                addItem(montantKey, montants, "قطعة 3م (Pièce)", "Montants 48 (3m)",
                    "قوائم عمودية مونتون Montant 48 (3م)",
                    "مونتون 48 سوليف حامل للفوبلافون",
                    "${joists} solives @${(params.entraxeMontants * 100).toInt()}cm sur portée ${span}m")

                val railKey = params.railType ?: "rail_48"
                val railStock = stockLengthFor(railKey) ?: params.boardLengthM
                val rails = ceil(perimeterCalc / railStock)
                addItem(railKey, rails, "قطعة 3م (Pièce)", "Rails 48 (3m)",
                    "سكك أفقية ريل Rail 48 (3 متر)",
                    "ريل 48 للسطح والأرضية (3م)",
                    "Répartition périmètre ${perimeterCalc}m")

                val visBoxes = ceil((numBoards * 18.0) / 1000.0)
                addItem("vis_placo_25", visBoxes, "علبة 1000 (Boîte)", "Vis Placo TTPC 25mm (Boîte 1000)",
                    "براغي بلاكو 25 ملم (علبة 1000)", "فيس بلاكو 25مم كحلة (1000 فيس)",
                    "${(numBoards * 18).toLong()} vis (~18 / plaque)")

                val trpfBoxes = ceil((joists * 4.0) / 1000.0)
                addItem("vis_trpf", trpfBoxes, "علبة 1000 (Boîte)", "Vis TRPF Métal (Boîte 1000)",
                    "براغي حديد TRPF البقرة (1000)", "فيس تي إر بي إف البقرة للحديد",
                    "${joists * 4L} vis TRPF (4 / solive)")

                val enduitBags = ceil((area * 0.35) / 25.0)
                addItem("enduit_joint_25kg", enduitBags, "كيس 25kg (Sac)", "Enduit à Joint 8H (Sac 25kg)",
                    "معجون فواصل أندوي (كيس 25 كغ)", "أندوي معجون بلاكو لافارج", "0.35 kg / m²")

                val bandeRolls = ceil(area / 90.0)
                addItem("bande_a_joint_90m", bandeRolls, "لفة 90m (Rouleau)", "Bande à Joint Papier (90m)",
                    "شريط فواصل ورقي (لفة 90 متر)", "رول بوند اجوان ورقة للفواصل", "~1 ml / m²")
            }

            CalculationType.PEINTURE_INTERIEURE -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (8.0 * country.rateToTndMultiplier)
                val enduitSacs = ceil((area * PaintCalculationConstants.ENDUIT_KG_PER_M2) / PaintCalculationConstants.ENDUIT_SACK_KG)
                addItem("enduit_lissage_peinture_25kg", enduitSacs, "كيس 25kg (Sac)", "Enduit de Lissage Blanc (2 Couches)", "معجون تسوية وتمليس أندوي (طبقتين)", "شكارة أندوي ليسّاج بيضاء للدهينة (25كغ)", "1.2 kg / m² (2 couches complètes)")

                val fixateurBidons = ceil((area * PaintCalculationConstants.PRIMER_L_PER_M2) / PaintCalculationConstants.PRIMER_CONTAINER_L)
                addItem("fixateur_primaire_20l", fixateurBidons, "سطال 20L (Bidon)", "Fixateur / Primaire d'Accrochage (20L)", "سائل أساس فيكساتور لربط الغبار (20 لتر)", "سطال فيكساتور لربط الغبرة والتأسيس (20L)", "0.15 L / m²")

                // Finish paint: preserve existing numeric behaviour (0.25 L/m² basis).
                // Use PaintCalculator as single-source-of-truth for the quantity calculation,
                // but keep legacy semantics so results do not change.
                val coverageM2PerLiter = 1.0 / PaintCalculationConstants.FINISH_L_PER_M2
                val finishLiters = PaintCalculator.calculatePaintLiters(
                    area = area,
                    openingsArea = 0.0,
                    additionalArea = 0.0,
                    coverageM2PerLiter = coverageM2PerLiter,
                    coats = 1, // preserve legacy total consumption
                    surfaceMultiplier = 1.0,
                    wastePercent = 0.0 // legacy did not apply waste here
                )
                val peintureBidons = ceil(finishLiters / PaintCalculationConstants.FINISH_CONTAINER_L)
                addItem("peinture_finition_satinee_15l", peintureBidons, "سطال 15L (Bidon)", "Peinture Finition Satinée (15L)", "دهان تشطيب ساتيني قابل للغسل (15 لتر)", "سطال دهينة سيتان ممتاز قابل للغسيل (15L)", "0.25 L / m² (2 couches de finition)")
            }

            CalculationType.PEINTURE_SABLE -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (14.0 * country.rateToTndMultiplier)
                val fixateurBidons = ceil((area * PaintCalculationConstants.PRIMER_L_PER_M2) / PaintCalculationConstants.PRIMER_CONTAINER_L)
                addItem("fixateur_primaire_20l", fixateurBidons, "سطال 20L (Bidon)", "Primaire Décoratif (20L)", "سائل أساس للدهان الديكوري (20 لتر)", "فيكساتور تحضيري للديكور (20L)")

                val sableBidons = ceil((area * PaintCalculationConstants.SABLE_KG_PER_M2) / PaintCalculationConstants.SABLE_CONTAINER_KG)
                addItem("peinture_sable_decoratif_5kg", sableBidons, "سطال 5kg (Bidon)", "Peinture Décorative Sablé (5kg)", "دهان سابلي رملي فاخر (5 كغ)", "سطال سابلي ديكور جاهز رملي (5 كغ)", "0.25 kg / m²")
            }

            CalculationType.MACONNERIE_BRIQUE -> {
                // Masonry (brique) enhanced calculation
                // Derive wall length if provided otherwise fallback to area/height or perimeter/2
                val wallLength = if (params.wallLengthMeters > 0.0) params.wallLengthMeters
                else if (params.height > 0.0 && area > 0.0) area / params.height
                else perimeter / 2.0

                val netArea = max(0.0, (wallLength * params.height) - params.netOpeningsArea)

                // Brick type mapping: default to 12-hole (15 bricks/m²) unless explicit 8-hole selected
                val bricksPerM2 = if (params.boardType == "brique_8_trous") 12.0 else 15.0
                val bricksKey = if (params.boardType.startsWith("brique_")) params.boardType else if (bricksPerM2 == 12.0) "brique_8_trous" else "brique_12_trous"

                if (netArea > 0.0) {
                    val bricksQty = ceil(netArea * bricksPerM2 * wasteFactor)
                    addItem(
                        bricksKey,
                        bricksQty,
                        "قطعة (Pièce)",
                        if (bricksPerM2 == 15.0) "Briques Rouges 12 Trous" else "Briques Rouges 8 Trous",
                        if (bricksPerM2 == 15.0) "آجر أحمر 12 ثقب للجدران" else "آجر أحمر 8 ثقوب للفواصل",
                        if (bricksPerM2 == 15.0) "ياجور 12 عين للحوائط والعرصات" else "ياجور 8 أعين للفواصل الداخلية",
                        "${bricksPerM2.toInt()} briques / m² + chute ${(params.wasteMarginPercent).toInt()}%"
                    )

                    // Cement: assume ~12 kg / m² for a 15 cm wall (adjust by waste margin)
                    val cementKg = netArea * 12.0 * (1.0 + (params.wasteMarginPercent / 100.0))
                    val cimentSacs = ceil(cementKg / 50.0)
                    addItem(
                        "ciment_portland_50kg",
                        cimentSacs,
                        "كيس 50kg (Sac)",
                        "Ciment Gris Portland (Sac 50kg)",
                        "إسمنت رمادي بورتلاند للبناء (50 كغ)",
                        "شكارة سيمون رمادي للبنيان والملاط (50كغ)",
                        "~${String.format(java.util.Locale.US, "%.1f", cementKg)} kg de ciment (≈ ${cimentSacs.toInt()} sacs)"
                    )

                    // Sand: assume 0.03 m³ / m² + 10% waste
                    val sandM3 = netArea * 0.03 * 1.10
                    val sandVolume = (sandM3 * 100).toLong() / 100.0
                    addItem(
                        "sable_lave_m3",
                        sandVolume,
                        "متر مكعب (m³)",
                        "Sable Lavé de Carrière (m³)",
                        "رمل مغسول للبناء والملاط (متر مكعب)",
                        "متر مكعب رملة مغسولة للبنيان (m³)",
                        "~${String.format(java.util.Locale.US, "%.3f", sandM3)} m³ (incl. 10% perte)"
                    )

                    // Labor: allow per-m² rate (customLaborRatePerM2) or by estimated days
                    var masonryLaborOverride: Double? = null
                    val perM2Rate = params.customLaborRatePerM2 ?: (15.0 * country.rateToTndMultiplier)
                    // If the caller provided a `dailyLaborRate` flag via customLaborRatePerM2 == null
                    // fallback to per-m2; callers that want day-based calculation can compute externally
                    // For backward compatibility: default to per-m² unless a daily rate override exists on params (not present by default)
                    masonryLaborOverride = netArea * perM2Rate

                    // Attach a descriptive item for labor will be used later when summing costs
                    // We push a synthetic item to the materials list labelled "main_oeuvre_maconnerie"
                    // Note: monetary calculation is applied later; here we include a marker with qty=netArea
                    addItem(
                        "main_oeuvre_maconnerie",
                        netArea,
                        "m²",
                        "Main d'Œuvre Maçonnerie",
                        "اليد العاملة (بنيان)",
                        "خدمة بنّاء",
                        "Estimation: ${String.format(java.util.Locale.US, "%.2f", netArea)} m²"
                    )

                    // Store override into estimatedLaborRate/laborMultiplier later by setting variables
                    // We'll use a special local marker by updating estimatedLaborRate and laborMultiplier
                    estimatedLaborRate = perM2Rate
                }
            }

            CalculationType.CREPISSAGE_CIMENT -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (12.0 * country.rateToTndMultiplier)
                val cimentSacs = ceil((area * 15.0) / 50.0)
                addItem("ciment_portland_50kg", cimentSacs, "كيس 50kg (Sac)", "Ciment pour Crépissage (Sac 50kg)", "إسمنت للياسة والترطيبة (50 كغ)", "شكارة سيمون رمادي للترطيبة (50كغ)", "15 kg de ciment / m² pour 1.5 cm d'épaisseur")

                val sableVolume = (area * 0.05 * 100).toLong() / 100.0
                addItem("sable_fin_crepissage_m3", sableVolume, "متر مكعب (m³)", "Sable Fin Tamisé (m³)", "رمل ناعم مصفى للياسة (متر مكعب)", "متر مكعب رملة جويدة مصفية للترطيبة (m³)", "0.05 m³ / m²")
            }

            CalculationType.PLACO_JOINTS_FINITION -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (12.0 * country.rateToTndMultiplier)
                // Minimal joints & finishing handler to satisfy exhaustive when.
                // If `jointsMeters` provided, add enduit and bande accordingly.
                if (params.jointsMeters > 0.0) {
                    val jointArea = params.jointsMeters
                    val enduitBags = ceil((jointArea * 0.35) / 25.0)
                    addItem("enduit_joint_25kg", enduitBags, "كيس 25kg (Sac)", "Enduit à Joint 8H (Sac 25kg)", "معجون فواصل أندوي (كيس 25 كغ)", "أندوي معجون بلاكو لافارج", "0.35 kg / m² jointoyé")

                    val bandeRolls = ceil(jointArea / 90.0)
                    addItem("bande_a_joint_90m", bandeRolls, "لفة 90m (Rouleau)", "Bande à Joint Papier (90m)", "شريط فواصل ورقي (لفة 90 متر)", "رول بوند اجوان ورقة للفواصل", "~1 ml / m²")
                }
            }

            CalculationType.CARRELAGE_SOL -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (18.0 * country.rateToTndMultiplier)
                val carrelageArea = (area * 1.10 * 100).toLong() / 100.0
                addItem("ciment_colle_carrelage_25kg", ceil((area * 4.0) / 25.0), "كيس 25kg (Sac)", "Ciment Colle Carrelage (Sac 25kg)", "إسمنت لاصق للسيراميك (25 كغ)", "شكارة كول فايونس وسيراميك (25كغ)", "4 kg de colle / m²")

                val jointsSacs = ceil((area * 0.30) / 5.0)
                addItem("joint_carrelage_5kg", jointsSacs, "كيس 5kg (Sac)", "Mortier Joint Carrelage (Sac 5kg)", "معجون فواصل وترويب السيراميك (5 كغ)", "شكارة جوان فايونس ملون (5كغ)", "0.3 kg / m²")
            }

            CalculationType.MENUISERIE_PORTES -> {
                val doors = max(1.0, params.unitCount)
                estimatedLaborRate = params.customLaborRatePerM2 ?: (45.0 * country.rateToTndMultiplier)
                val visBoxes = ceil((doors * 24.0) / 500.0)
                addItem("vis_bois_agglo_500", visBoxes, "علبة 500 (Boîte)", "Vis Bois Agglo 40mm (500 pcs)", "براغي خشب لتثبيت المفصلات (500)", "علبة فيس لوح 40 مم للكوادر والبيبان", "24 vis par bloc-porte")

                val paumelles = doors * 3.0
                addItem("paumelle_porte_bois", paumelles, "قطعة (Pièce)", "Paumelles / Charnières de Porte", "مفصلات أبواب قياسية", "بومال مفصلة للباب اللوح", "3 paumelles par porte")

                val mousse = doors * 1.0
                addItem("mousse_polyurethane_750ml", mousse, "قارورة 750ml (Aérosol)", "Mousse PU Fixation (750ml)", "فوم عازل بولي يوريثان (750 مل)", "فوم رغوة تثبيت وعزل للكوادر", "1 aérosol par bloc-porte")
            }

            CalculationType.MENUISERIE_ALU -> {
                val windows = max(1.0, params.unitCount)
                estimatedLaborRate = params.customLaborRatePerM2 ?: (40.0 * country.rateToTndMultiplier)
                val jointsMeters = windows * 10.0
                val jointRolls = ceil(jointsMeters / 10.0)
                addItem("joint_etancheite_fenetre_10m", jointRolls, "لفة 10m (Rouleau)", "Joint d'Étanchéité Caoutchouc (10m)", "جلد عازل للمياه والرياح (10م)", "جلد كوتشوك كحل عازل لشبابك الألمنيوم", "10 m par fenêtre standard 1.2x1.2m")

                val silicone = windows * 1.0
                addItem("silicone_transparent_cartouche", silicone, "خرطوشة (Cartouche)", "Cartouche Silicone Neutre", "خرطوشة سيليكون عازل شفاف", "كارتوش سيليكون شفاف لغلق الشبابك", "1 cartouche d'étanchéité par fenêtre")
            }

            // Website trade parity slice: material IDs resolve only through the shared price catalog.
            CalculationType.PLOMBERIE_INSTALLATION -> {
                val network = max(0.0, if (params.linearMeters > 0.0) params.linearMeters else params.perimeter)
                val points = max(1.0, params.unitCount)
                estimatedLaborRate = params.customLaborRatePerM2 ?: (65.0 * country.rateToTndMultiplier)
                addItem("ppr_tube_20_4m", max(1.0, ceil((network * 0.70 / 4.0) * wasteFactor)), "barres 4m", "Tube PPR 20mm (barre 4m)", "أنبوب PPR 20 ملم", "أنبوب PPR 20 ملم", "(" + network + "m × 0.7 × " + wasteFactor + ") / 4")
                addItem("ppr_tube_25_4m", max(1.0, ceil((network * 0.30 / 4.0) * wasteFactor)), "barres 4m", "Tube PPR 25mm (barre 4m)", "أنبوب PPR 25 ملم", "أنبوب PPR 25 ملم", "(" + network + "m × 0.3 × " + wasteFactor + ") / 4")
                addItem("pvc_tube_40_4m", max(1.0, ceil((network * 0.50 / 4.0) * wasteFactor)), "barres 4m", "Tube PVC évacuation 40mm (barre 4m)", "أنبوب PVC 40 ملم", "أنبوب PVC 40 ملم", "(" + network + "m × 0.5 × " + wasteFactor + ") / 4")
                addItem("pvc_tube_110_4m", max(1.0, ceil((network * 0.30 / 4.0) * wasteFactor)), "barres 4m", "Tube PVC évacuation 110mm (barre 4m)", "أنبوب PVC 110 ملم", "أنبوب PVC 110 ملم", "(" + network + "m × 0.3 × " + wasteFactor + ") / 4")
                addItem("raccord_ppr_coude_te", ceil(points * 4.0 + network * 1.2), "pièces", "Raccords PPR", "وصلات PPR", "وصلات PPR", "points × 4 + réseau × 1.2")
                addItem("colle_pvc_pot", 1.0, "boîte", "Colle PVC", "غراء PVC", "غراء PVC", "1 pot par installation")
            }
            CalculationType.ELECTRICITE_INSTALLATION -> {
                val points = max(1.0, params.unitCount)
                estimatedLaborRate = params.customLaborRatePerM2 ?: (25.0 * country.rateToTndMultiplier)
                addItem("cable_1_5mm_100m", max(1.0, ceil((points * 18.0 / 100.0) * wasteFactor)), "rouleaux 100m", "Câble cuivre 1.5 mm² (100m)", "سلك نحاسي 1.5 ملم²", "سلك نحاسي 1.5 ملم²", "(points × 18m) / 100 × waste")
                addItem("gaine_icta_20_50m", max(1.0, ceil((points * 12.0 / 50.0) * wasteFactor)), "couronnes 50m", "Gaine ICTA 20mm (50m)", "أنبوب حماية ICTA 20 ملم", "أنبوب حماية ICTA 20 ملم", "(points × 12m) / 50 × waste")
                addItem("spot_led_encastrable", points, "pièces", "Spots LED encastrables", "سبوت LED", "سبوت LED", "1 point d'éclairage par point déclaré")
                addItem("tableau_et_disjoncteurs", 1.0, "forfait", "Tableau électrique et disjoncteurs", "لوحة كهربائية وقواطع", "لوحة كهربائية وقواطع", "1 tableau; price from catalog only")
            }
            CalculationType.ETANCHEITE_TOITURE -> {
                val roofArea = max(0.0, area + max(0.0, params.linearMeters) * 0.20)
                estimatedLaborRate = params.customLaborRatePerM2 ?: (14.0 * country.rateToTndMultiplier)
                addItem("membrane_etancheite_4mm_10m2", max(1.0, ceil((roofArea * 1.15 * wasteFactor) / 10.0)), "rouleaux 10m²", "Membrane bitumineuse 4mm (10m²)", "غشاء عزل بيتوميني 4 ملم", "غشاء عزل بيتوميني 4 ملم", "(surface + acrotère × 0.20) × 1.15 × waste / 10")
                addItem("bitume_liquide_bidon_20l", max(1.0, ceil(area * 0.35 / 20.0)), "bidons 20L", "Bitume liquide (20L)", "بيتومين سائل 20 لتر", "بيتومين سائل 20 لتر", "0.35 L/m²; catalog price")
                addItem("solin_alu", max(1.0, ceil(max(0.0, params.linearMeters) / 3.0)), "pièces 3m", "Solin aluminium (3m)", "حافة ألمنيوم للعزل", "حافة ألمنيوم للعزل", "linear meters / 3")
            }
            CalculationType.ISOLATION_THERMIQUE -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (6.0 * country.rateToTndMultiplier)
                addItem("laine_de_verre_50mm", max(1.0, ceil(area * wasteFactor / 15.0)), "rouleaux", "Laine de verre 50mm", "صوف زجاجي 50 ملم", "صوف زجاجي 50 ملم", "area × waste / 15")
                addItem("bande_resiliente_48mm", max(1.0, ceil(perimeter / 30.0)), "rouleaux 30m", "Bande résiliente 48mm", "شريط عازل للصوت", "شريط عازل للصوت", "perimeter / 30")
            }
            CalculationType.SOLS_PARQUET -> {
                estimatedLaborRate = params.customLaborRatePerM2 ?: (12.0 * country.rateToTndMultiplier)
                val parquetArea = area * wasteFactor
                addItem("parquet_stratifie_8mm_m2", parquetArea, "m²", "Parquet stratifié 8mm", "باركيه 8 ملم", "باركيه 8 ملم", "area × waste")
                addItem("sous_couche_acoustique_sol_m2", parquetArea, "m²", "Sous-couche acoustique", "طبقة عازلة تحت الأرضية", "طبقة عازلة تحت الأرضية", "same net parquet area")
                addItem("plinthes_mdf_decor_2_4m", ceil(perimeter * 1.10 / 2.4), "pièces 2.4m", "Plinthes MDF (2.4m)", "حواشي MDF", "حواشي MDF", "perimeter × 1.10 / 2.4")
                addItem("barre_seuil_porte_alu", max(1.0, ceil(kotlin.math.sqrt(area) / 4.0)), "pièces", "Barre de seuil aluminium", "حاجز عتبة ألمنيوم", "حاجز عتبة ألمنيوم", "ceil(sqrt(area) / 4)")
            }
            CalculationType.FACADE_FINITION -> {
                val netArea = max(0.0, area - params.netOpeningsArea)
                estimatedLaborRate = params.customLaborRatePerM2 ?: (18.0 * country.rateToTndMultiplier)
                addItem("enduit_monocouche_facade_25kg", ceil(netArea * 1.15 / 1.5), "sacs 25kg", "Enduit monocouche façade (25kg)", "ملاط تلبيس الواجهة 25 كغ", "ملاط تلبيس الواجهة 25 كغ", "net area × 1.15 / 1.5")
                addItem("corniere_facade_entoilee_pvc", max(1.0, ceil(kotlin.math.sqrt(area) * 4.0 / 2.5)), "pièces 2.5m", "Cornière façade entoilée", "زاوية واجهة بشبكة", "زاوية واجهة بشبكة", "perimeter estimate / 2.5")
            }
            CalculationType.DEMOLITION_DEPOSE -> {
                val thicknessCm = max(1.0, params.height)
                val rawVolume = area * (thicknessCm / 100.0)
                val expandedVolume = rawVolume * 1.40
                estimatedLaborRate = params.customLaborRatePerM2 ?: (12.0 * country.rateToTndMultiplier)
                addItem("camion_evacuation_gravats_6m3", max(1.0, ceil(expandedVolume / 6.0)), "camions 6m³", "Camion évacuation gravats (6m³)", "شاحنة إزالة الأنقاض 6م³", "شاحنة إزالة الأنقاض 6م³", "area × thickness × 1.40 / 6")
                addItem("sacs_gravats_renforces_10pcs", max(1.0, ceil(expandedVolume * 2.0)), "lots de 10", "Sacs gravats renforcés (lot de 10)", "أكياس أنقاض مقواة", "أكياس أنقاض مقواة", "volume foisonné × 20 bags / 10")
            }
        }

        val totalMaterialCost = items.sumOf { it.totalPrice }
        val laborMultiplier = when {
            params.type == CalculationType.MENUISERIE_PORTES || params.type == CalculationType.MENUISERIE_ALU ->
                max(1.0, params.unitCount)
            params.type == CalculationType.PLACO_CACHE_LUMIERE && params.linearMeters > 0 ->
                params.linearMeters
            params.type == CalculationType.PLOMBERIE_INSTALLATION || params.type == CalculationType.ELECTRICITE_INSTALLATION ->
                max(1.0, params.unitCount)
            else -> max(1.0, area)
        }

        val materialCostHT = TaxMath.round2(totalMaterialCost)
        val (materialTva, materialTotalTTC) = TaxMath.compute(materialCostHT, rate)

        val laborCostHT = TaxMath.round2(laborMultiplier * estimatedLaborRate)
        val (laborTva, laborTotalTTC) = TaxMath.compute(laborCostHT, rate)

        val transportCostHT = TaxMath.round2(params.transportCostHT)
        val (transportTva, transportTotalTTC) = TaxMath.compute(transportCostHT, rate)

        val equipmentCostHT = TaxMath.round2(params.equipmentCostHT)
        val (equipmentTva, equipmentTotalTTC) = TaxMath.compute(equipmentCostHT, rate)

        val otherCostHT = TaxMath.round2(params.otherCostHT)
        val (otherTva, otherTotalTTC) = TaxMath.compute(otherCostHT, rate)

        val grandTotalHT = TaxMath.round2(materialCostHT + laborCostHT + transportCostHT + equipmentCostHT + otherCostHT)
        val totalTva = TaxMath.round2(materialTva + laborTva + transportTva + equipmentTva + otherTva)
        val grandTotalTTC = TaxMath.round2(materialTotalTTC + laborTotalTTC + transportTotalTTC + equipmentTotalTTC + otherTotalTTC)

        return CalculationSummary(
            calculationType = params.type,
            inputArea = area,
            inputPerimeter = perimeter,
            inputHeight = params.height,
            inputQuantity = params.unitCount,
            wasteMarginPercent = params.wasteMarginPercent,
            items = items,
            totalMaterialCost = materialCostHT,
            estimatedLaborCost = laborCostHT,
            grandTotalCost = grandTotalHT,
            currency = country.currencySymbol,
            countryCode = country.code,
            fiscalProfileId = fiscalProfile.id,
            tvaRate = rate,
            transportCostHT = transportCostHT,
            equipmentCostHT = equipmentCostHT,
            otherCostHT = otherCostHT,
            materialTva = materialTva,
            laborTva = laborTva,
            transportTva = transportTva,
            equipmentTva = equipmentTva,
            otherTva = otherTva,
            materialTotalTTC = materialTotalTTC,
            laborTotalTTC = laborTotalTTC,
            transportTotalTTC = transportTotalTTC,
            equipmentTotalTTC = equipmentTotalTTC,
            otherTotalTTC = otherTotalTTC,
            totalTva = totalTva,
            grandTotalHT = grandTotalHT,
            grandTotalTTC = grandTotalTTC
        )
    }
}
