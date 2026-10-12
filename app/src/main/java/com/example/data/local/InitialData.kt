package com.example.data.local

import com.example.data.model.Country

object InitialData {

    data class RawPrice(
        val baseKey: String,
        val category: String,
        val nameFr: String,
        val nameAr: String,
        val nameDerja: String,
        val unit: String,
        val defaultPriceTnd: Double,
        val brandInfo: String = ""
    )

    val BASE_PRICES: List<RawPrice> = listOf(
        // Plasterboards (Plaques BA13 1.2m x 2.5m / 3.0m = 3m²)
        RawPrice(
            "plaque_ba13_standard",
            "placo",
            "Plaque BA13 Standard (1.2x2.5m / 3m²)",
            "لوح جبس قياسي BA13 (3 م²)",
            "لوح بلاكو عادي BA13 بيضاء",
            "لوح (Plaque)",
            30.0,
            "Knauf / Siniat / Fassa"
        ),
        RawPrice(
            "plaque_ba13_hydrofuge",
            "placo",
            "Plaque BA13 Hydrofuge Vert (Anti-humidité)",
            "لوح جبس مقاوم للرطوبة BA13 أخضر",
            "لوح بلاكو خضراء ضد الرطوبة والماء",
            "لوح (Plaque)",
            46.0,
            "Knauf Hydro / Siniat"
        ),
        RawPrice(
            "plaque_ba13_coupe_feu",
            "placo",
            "Plaque BA13 Coupe-Feu Rose (Ignifuge)",
            "لوح جبس مقاوم للحريق BA13 وردي",
            "لوح بلاكو غوز ضد النار والحرارة",
            "لوح (Plaque)",
            50.0,
            "Knauf Flam / Siniat"
        ),
        RawPrice(
            "plaque_ba13_phonique",
            "placo",
            "Plaque BA13 Phonique Bleu (Isolation acoustique)",
            "لوح جبس عازل للصوت BA13 أزرق",
            "لوح بلاكو زرقاء عازلة للصوت",
            "لوح (Plaque)",
            55.0,
            "Knauf Diamant / Acoustique"
        ),
        RawPrice(
            "plaque_aquapanel_ciment",
            "placo",
            "Plaque Ciment Aquapanel Outdoor (Extérieur)",
            "لوح إسمنت بورد أكوابانيل خارجي",
            "لوح سيمانت بورد أكوابانيل للواجهات",
            "لوح (Plaque)",
            85.0,
            "Knauf Aquapanel / Externa"
        ),
        RawPrice(
            "plaque_glassroc_exterieur",
            "placo",
            "Plaque Glassroc X / Outwear Glass (Extérieur)",
            "لوح جلاس روك مقاوم للطقس والمطر",
            "لوح غلاس روك فيبر خارجي",
            "لوح (Plaque)",
            75.0,
            "Saint-Gobain / Aygips"
        ),

        // Metal Framework (Ossature métallique 3m)
        RawPrice(
            "rail_48",
            "ossature",
            "Rail 48 (Profilé horizontal 3m)",
            "سكة أفقية ريل Rail 48 (3 متر)",
            "ريل 48 للسطح والأرضية (3م)",
            "قطعة 3م (Pièce)",
            7.5,
            "Perfilkit / Knauf"
        ),
        RawPrice(
            "montant_48",
            "ossature",
            "Montant 48 (Profilé vertical 3m)",
            "قائم عمودي مونتون Montant 48 (3 متر)",
            "مونتون 48 عمودي للجدران (3م)",
            "قطعة 3م (Pièce)",
            8.0,
            "Perfilkit / Knauf"
        ),
        RawPrice(
            "rail_70",
            "ossature",
            "Rail 70 (Profilé horizontal renforcé 3m)",
            "سكة ريل 70 مقواة (3 متر)",
            "ريل 70 عريض للجدران العالية (3م)",
            "قطعة 3م (Pièce)",
            9.2,
            "Perfilkit / Knauf"
        ),
        RawPrice(
            "montant_70",
            "ossature",
            "Montant 70 (Profilé vertical renforcé 3m)",
            "قائم مونتون 70 مقوى (3 متر)",
            "مونتون 70 عريض للجدران السميكة (3م)",
            "قطعة 3م (Pièce)",
            11.3,
            "Perfilkit / Knauf"
        ),
        RawPrice(
            "fourrure",
            "ossature",
            "Fourrure F47 (Profilé plafond 3m)",
            "قضيب فورور F47 للأسقف المعلقة (3 متر)",
            "فورور F47 فوبلافون (3م)",
            "قطعة 3م (Pièce)",
            7.0,
            "Perfilkit / Knauf"
        ),
        RawPrice(
            "corniere_angle",
            "ossature",
            "Cornière d'Angle / Cornière L (3m)",
            "زاوية محيطية كورنيار L (3 متر)",
            "كورنيار L دايرة بالبيوت (3م)",
            "قطعة 3م (Pièce)",
            6.5,
            "Favori / Akifix"
        ),
        RawPrice(
            "omega_profil",
            "ossature",
            "Profilé Oméga / Flex (3m)",
            "بروفيل أوميغا فليكس للديكورات (3 متر)",
            "أوميغا ديكور وفليكس للدوران (3م)",
            "قطعة 3م (Pièce)",
            8.5,
            "Akifix"
        ),

        // Demountable Grid & Tiles (Dalles 60x60)
        RawPrice(
            "dalle_vinyl_60x60",
            "dalles",
            "Dalle Plâtre Vinyle Lavable 60x60 cm",
            "بلاطة جبس فينيل قابلة للغسل 60×60 سم",
            "دالة 60×60 فينيل بيضاء للمحلات والمكاتب",
            "قطعة (Dalle)",
            5.5,
            "Favori / Knauf Karolam"
        ),
        RawPrice(
            "dalle_laine_roche_60x60",
            "dalles",
            "Dalle Acoustique Laine de Roche 60x60 cm",
            "بلاطة صوف صخري صوتية 60×60 سم",
            "دالة روكفون صوتية وديكورية 60×60",
            "قطعة (Dalle)",
            10.0,
            "Rockfon Artic / AMF"
        ),
        RawPrice(
            "porteur_3600",
            "dalles",
            "Porteur T24 Principal 3.60m",
            "حامل رئيسي طويل بورتور T24 (3.60 متر)",
            "بورتور 3.60م سكة رئيسية",
            "قطعة 3.6m (Pièce)",
            14.0,
            "Favori / Rockfon / Perfilkit"
        ),
        RawPrice(
            "entretoise_1200",
            "dalles",
            "Entretoise T24 Secondaire 1.20m",
            "عارضة عرضية أنتروتواز T24 (1.20 متر)",
            "أنتروتواز 1.20م بين البورتورات",
            "قطعة 1.2m (Pièce)",
            4.5,
            "Favori / Rockfon"
        ),
        RawPrice(
            "entretoise_600",
            "dalles",
            "Entretoise T24 Petite 0.60m",
            "عارضة صغيرة أنتروتواز T24 (0.60 متر)",
            "أنتروتواز صغيرة 60سم للمربعات",
            "قطعة 0.6m (Pièce)",
            2.5,
            "Favori / Rockfon"
        ),
        RawPrice(
            "corniere_rive_L",
            "dalles",
            "Cornière de Rive Laquée Blanc (3m)",
            "زاوية حافة بيضاء مدهونة للأسقف التفكيكية (3م)",
            "كورنيار ريف بيضاء دايرة بالحائط (3م)",
            "قطعة 3m (Pièce)",
            8.0,
            "Profilex / Favori"
        ),

        // Accessories, Visserie & Fasteners
        RawPrice(
            "suspente",
            "visserie",
            "Suspente Pivot / Articulée F47",
            "علاقة سوسبانت مع مشبك للفورور",
            "سوسبانت F47 تعليق في السقف",
            "قطعة (Pièce)",
            0.8,
            "Akifix / Sbfix"
        ),
        RawPrice(
            "tige_filetee_1m",
            "visserie",
            "Tige Filetée M6 (1 mètre)",
            "قضيب سلك مسنن تيج فيلتي M6 (1 متر)",
            "تيج فيلتي M6 للتعليق (1م)",
            "قطعة 1م (Pièce)",
            3.0,
            "Akifix / Perfilkit"
        ),
        RawPrice(
            "cavalier_pivot",
            "visserie",
            "Cavalier Pivot F47",
            "مشترك كافالييه F47",
            "كافاليي كليبس للفورور",
            "قطعة (Pièce)",
            0.6,
            "Akifix"
        ),
        RawPrice(
            "vis_placo_25",
            "visserie",
            "Vis Placo TTPC 3.5x25 mm (Boîte 1000 pcs)",
            "براغي بلاكو سوداء 25 ملم (علبة 1000 برغي)",
            "علبة فيس بلاكو كحلة 25 مم (1000 فيس)",
            "علبة 1000 (Boîte)",
            22.0,
            "Akifix / Knauf"
        ),
        RawPrice(
            "vis_placo_35",
            "visserie",
            "Vis Placo TTPC 3.5x35 mm (Boîte 1000 pcs)",
            "براغي بلاكو 35 ملم للطبقة المزدوجة (1000 برغي)",
            "علبة فيس بلاكو 35 مم للدوبلاج (1000)",
            "علبة 1000 (Boîte)",
            26.0,
            "Akifix"
        ),
        RawPrice(
            "vis_trpf",
            "visserie",
            "Vis TRPF Auto-perceuse Métal (Boîte 1000 pcs)",
            "براغي حديد TRPF رأس عريض البقرة (1000 برغي)",
            "فيس تي إر بي إف البقرة للحديد (1000 فيس)",
            "علبة 1000 (Boîte)",
            26.0,
            "Akifix"
        ),
        RawPrice(
            "vis_aquapanel",
            "visserie",
            "Vis Aquapanel Inox Extérieur (Boîte 500 pcs)",
            "براغي أكوابانيل مقاومة للصدأ (500 برغي)",
            "فيس أكوابانيل إينوكس للواجهات (500)",
            "علبة 500 (Boîte)",
            35.0,
            "Knauf / Akifix"
        ),
        RawPrice(
            "cheville_a_frapper",
            "visserie",
            "Cheville à Frapper 6x40 mm (Boîte 200 pcs)",
            "أوتاد ضرب شوفي 6×40 ملم لتثبيت السكك (200 قطعة)",
            "شوفي ضرب 6×40 للتثبيت في الإسمنت (200 كعبة)",
            "علبة 200 (Boîte)",
            19.5,
            "Akifix"
        ),

        // Enduits, Bandes & Colles
        RawPrice(
            "bande_a_joint_90m",
            "enduits",
            "Bande à Joint Papier Micro-perforé (90m)",
            "شريط فواصل ورقي مثقب (لفة 90 متر)",
            "رول بوند اجوان ورقة للفواصل (90م)",
            "لفة 90m (Rouleau)",
            18.0,
            "Knauf / Siniat / Akifix"
        ),
        RawPrice(
            "bande_armee_30m",
            "enduits",
            "Bande Armée Métallique d'Angle (30m)",
            "شريط زوايا مسلح بالمعدن ضد الصدمات (30م)",
            "بوند أرمي حديد للزوايا البارزة (30م)",
            "لفة 30m (Rouleau)",
            40.0,
            "Akifix"
        ),
        RawPrice(
            "trame_fibre_exterieur",
            "enduits",
            "Trame Fibre de Verre Extérieure 160g (50m)",
            "شبكة فيبر خارجية 160غ للأكوابانيل والواجهات (50م)",
            "ترام فيبر 160غ للواجهات الخارجية (50م)",
            "لفة 50m (Rouleau)",
            45.0,
            "Fassa Bortolo / Knauf"
        ),
        RawPrice(
            "enduit_joint_25kg",
            "enduits",
            "Enduit à Joint Prise Lente 8H / 24H (Sac 25kg)",
            "معجون فواصل أندوي سريع/بطيء (كيس 25 كغ)",
            "شكارة أندوي معجون بلاكو لافارج أو كوفيبلاست (25كغ)",
            "كيس 25kg (Sac)",
            42.0,
            "Knauf Jointfiller / Fassa / Afrochim"
        ),
        RawPrice(
            "mortier_colle_map_25kg",
            "enduits",
            "Mortier Adhésif MAP / Gypsomaf (Sac 25kg)",
            "غراء تلصيق مباشر ماب MAP للألواح (كيس 25 كغ)",
            "غراء كول ماب MAP لتلصيق البلاكو عالحائط (25كغ)",
            "كيس 25kg (Sac)",
            51.0,
            "Knauf HP2 / Fassa Gypsomaf"
        ),
        RawPrice(
            "colle_ciment_exterieur_25kg",
            "enduits",
            "Enduit & Colle Ciment Extérieur SM700 / A96 (25kg)",
            "غراء ومعجون إسمنتي خارجي للواجهات (25 كغ)",
            "كول سيمو خارجي SM700 للأكوابانيل (25كغ)",
            "كيس 25kg (Sac)",
            65.0,
            "Knauf SM700 / Fassa A96"
        ),

        // Isolation & Acoustique
        RawPrice(
            "laine_de_verre_50mm",
            "isolation",
            "Laine de Verre Acoustique 50mm (Rouleau 15m²)",
            "صوف زجاجي عازل 50 ملم (لفة 15 م²)",
            "لين دو فار عازل للصوت والسخانة (رول 15م²)",
            "لفة 15m² (Rouleau)",
            75.0,
            "Isover / Knauf Insulation"
        ),
        RawPrice(
            "laine_de_roche_50mm",
            "isolation",
            "Laine de Roche Haute Densité 50mm (Paquet 7.2m²)",
            "صوف صخري كثافة عالية 50 ملم (باكي 7.2 م²)",
            "لين دو روش ضد الصوت والحرارة والنار (باكي 7.2م²)",
            "باكي 7.2m² (Paquet)",
            90.0,
            "Rockwool / Knauf"
        ),
        RawPrice(
            "bande_resiliente_48mm",
            "isolation",
            "Bande Résiliente Acoustique 50mm (Rouleau 30m)",
            "شريط عازل للصوت أسفل السكك (30 متر)",
            "بوند ريزيليانت كوتشوك تحت الريل (30م)",
            "لفة 30m (Rouleau)",
            25.0,
            "Akifix"
        ),
        RawPrice(
            "silicone_coupe_feu",
            "isolation",
            "Cartouche Silicone Coupe-Feu / Mastic",
            "خرطوشة سيليكون مقاوم للحريق والإغلاق",
            "كارتوش سيليكون أو ماستيك معجون",
            "خرطوشة (Cartouche)",
            22.0,
            "Soudal / Akifix"
        ),

        // PVC & Décors
        RawPrice(
            "lambris_pvc_blanc",
            "placo",
            "Lambris PVC Blanc Brillant (Lame 6m)",
            "شرائح بلاستيك PVC أبيض لامع للأسقف (6 متر)",
            "شرائح PVC فوبلافون للادوش والكوجينة (6م)",
            "شريحة 6م (Lame)",
            16.0,
            "Standard PVC"
        ),
        RawPrice(
            "profile_u_pvc",
            "placo",
            "Profilé de Rive en U PVC (3m)",
            "بروفيل حافة U بلاستيك محيطي (3 متر)",
            "بروفيل U داير بالحائط للـ PVC (3م)",
            "قطعة 3م (Pièce)",
            7.5,
            "Standard PVC"
        ),

        // Peinture & Décoration
        RawPrice(
            "enduit_lissage_peinture_25kg",
            "peinture",
            "Enduit de Lissage Blanc (Sac 25kg)",
            "معجون تسوية وتمليس ناعم للدهان (25 كغ)",
            "شكارة أندوي ليسّاج بيضاء للدهينة (25كغ)",
            "كيس 25kg (Sac)",
            35.0,
            "Astral / Sovap / Fassa"
        ),
        RawPrice(
            "fixateur_primaire_20l",
            "peinture",
            "Fixateur / Primaire d'Accrochage (Bidon 20L)",
            "سائل أساس فيكساتور لربط الغبار (20 لتر)",
            "سطال فيكساتور لربط الغبرة والتأسيس (20 لتر)",
            "سطال 20L (Bidon)",
            68.0,
            "Astral / Sovap / Corona"
        ),
        RawPrice(
            "peinture_finition_satinee_15l",
            "peinture",
            "Peinture Finition Satinée / Mate (Bidon 15L)",
            "دهان تشطيب ساتيني ناعم قابل للغسل (15 لتر)",
            "سطال دهينة سيتان ممتاز قابل للغسيل (15 لتر)",
            "سطال 15L (Bidon)",
            145.0,
            "Astral Pantex / Sovap"
        ),
        RawPrice(
            "peinture_sable_decoratif_5kg",
            "peinture",
            "Peinture Décorative Sablé (Bidon 5kg)",
            "دهان سابلي ديكوري رملي فاخر (5 كغ)",
            "سطال سابلي ديكور جاهز رملي (5 كغ)",
            "سطال 5kg (Bidon)",
            55.0,
            "Astral / Sovap Décor"
        ),

        // Maçonnerie & Gros Œuvres
        RawPrice(
            "brique_12_trous",
            "maconnerie",
            "Brique Rouge 12 Trous (Mur Extérieur)",
            "آجر أحمر 12 ثقب للجدران الخارجية",
            "ياجور 12 عين للحوائط والعرصات",
            "قطعة (Pièce)",
            1.15,
            "Tuilerie Tunisienne"
        ),
        RawPrice(
            "brique_8_trous",
            "maconnerie",
            "Brique Rouge 8 Trous (Cloison Intérieure)",
            "آجر أحمر 8 ثقوب للقواطع الداخلية",
            "ياجور 8 أعين للفواصل الداخلية",
            "قطعة (Pièce)",
            0.85,
            "Tuilerie Tunisienne"
        ),
        RawPrice(
            "ciment_portland_50kg",
            "maconnerie",
            "Ciment Gris Portland CEM II (Sac 50kg)",
            "إسمنت رمادي بورتلاند للبناء والملاط (50 كغ)",
            "شكارة سيمون رمادي للبنيان والترطيبة (50كغ)",
            "كيس 50kg (Sac)",
            15.5,
            "Ciments de Bizerte / Gabès / Enfidha"
        ),
        RawPrice(
            "sable_lave_m3",
            "maconnerie",
            "Sable Lavé de Carrière (Mètre Cube m³)",
            "رمل مغسول للبناء والملاط (متر مكعب)",
            "متر مكعب رملة مغسولة للبنيان (m³)",
            "متر مكعب (m³)",
            45.0,
            "Carrières Oued"
        ),
        RawPrice(
            "sable_fin_crepissage_m3",
            "crepissage",
            "Sable Fin Tamisé pour Crépissage (m³)",
            "رمل ناعم مصفى للياسة والترطيبة (متر مكعب)",
            "متر مكعب رملة جويدة مصفية للترطيبة (m³)",
            "متر مكعب (m³)",
            50.0,
            "Carrières"
        ),

        // Carrelage & Revêtement Sol
        RawPrice(
            "ciment_colle_carrelage_25kg",
            "carrelage",
            "Ciment Colle Carrelage C1/C2 (Sac 25kg)",
            "إسمنت لاصق للسيراميك والبلاط (25 كغ)",
            "شكارة كول فايونس وسيراميك (25كغ)",
            "كيس 25kg (Sac)",
            16.5,
            "Keraflex / Ciment Colle"
        ),
        RawPrice(
            "joint_carrelage_5kg",
            "carrelage",
            "Mortier Joint Carrelage Fin (Sac 5kg)",
            "معجون ترويب وفواصل السيراميك (5 كغ)",
            "شكارة جوان فايونس ملون (5كغ)",
            "كيس 5kg (Sac)",
            12.0,
            "Fassa Bortolo / Mapei"
        ),

        // Menuiserie Bois & Aluminium
        RawPrice(
            "vis_bois_agglo_500",
            "bois_alu",
            "Vis Bois Agglo 40mm (Boîte 500 pcs)",
            "براغي خشب 40 ملم لتثبيت المفصلات (500 برغي)",
            "علبة فيس لوح 40 مم للكوادر والبيبان (500)",
            "علبة 500 (Boîte)",
            18.0,
            "Akifix"
        ),
        RawPrice(
            "paumelle_porte_bois",
            "bois_alu",
            "Paumelle / Charnière de Porte Standard",
            "مفصلة باب خشبية قياسية",
            "بومال مفصلة للباب اللوح",
            "قطعة (Pièce)",
            6.5,
            "Quincaillerie"
        ),
        RawPrice(
            "mousse_polyurethane_750ml",
            "bois_alu",
            "Mousse Polyuréthane Expansive (Aérosol 750ml)",
            "فوم عازل بولي يوريثان لتثبيت الإطارات (750 مل)",
            "فوم رغوة تثبيت وعزل للكوادر والشبابك (750مل)",
            "قارورة 750ml (Aérosol)",
            16.0,
            "Soudal / Akifix"
        ),
        RawPrice(
            "joint_etancheite_fenetre_10m",
            "bois_alu",
            "Joint d'Étanchéité Caoutchouc Noir (10m)",
            "جلد عازل للمياه والرياح للألمنيوم (10 متر)",
            "جلد كوتشوك كحل عازل لشبابك الألمنيوم (10م)",
            "لفة 10m (Rouleau)",
            15.0,
            "TPR / Akifix"
        ),
        RawPrice(
            "silicone_transparent_cartouche",
            "bois_alu",
            "Cartouche Silicone Neutre / Transparent",
            "خرطوشة سيليكون عازل شفاف",
            "كارتوش سيليكون شفاف لغلق الشبابك",
            "خرطوشة (Cartouche)",
            12.5,
            "Soudal / Akifix"
        ),

        // STEP 9C2 — Trappes de visite. Initial seed price = 0.0 (unpriced
        // placeholder, NOT an invented price). Real prices are set by the owner
        // from the Tarifs screen and stay fully editable there.
        RawPrice(
            "trappe_20x20",
            "visserie",
            "Trappe de visite 20x20 cm",
            "باب تفتيش 20×20 سم",
            "باب دكسيتي 20 في 20",
            "قطعة (Pièce)",
            0.0
        ),
        RawPrice(
            "trappe_30x30",
            "visserie",
            "Trappe de visite 30x30 cm",
            "باب تفتيش 30×30 سم",
            "باب دكسيتي 30 في 30",
            "قطعة (Pièce)",
            0.0
        ),
        RawPrice(
            "trappe_40x40",
            "visserie",
            "Trappe de visite 40x40 cm",
            "باب تفتيش 40×40 سم",
            "باب دكسيتي 40 في 40",
            "قطعة (Pièce)",
            0.0
        ),
        RawPrice(
            "trappe_50x50",
            "visserie",
            "Trappe de visite 50x50 cm",
            "باب تفتيش 50×50 سم",
            "باب دكسيتي 50 في 50",
            "قطعة (Pièce)",
            0.0
        ),
        RawPrice(
            "trappe_60x60",
            "visserie",
            "Trappe de visite 60x60 cm",
            "باب تفتيش 60×60 سم",
            "باب دكسيتي 60 في 60",
            "قطعة (Pièce)",
            0.0
        ),
        RawPrice(
            "trappe_40x60",
            "visserie",
            "Trappe de visite 40x60 cm",
            "باب تفتيش 40×60 سم",
            "باب دكسيتي 40 في 60",
            "قطعة (Pièce)",
            0.0
        ),
        RawPrice(
            "trappe_60x120",
            "visserie",
            "Trappe de visite 60x120 cm",
            "باب تفتيش 60×120 سم",
            "باب دكسيتي 60 في 120",
            "قطعة (Pièce)",
            0.0
        )
    )

    fun generatePricesForCountry(country: Country): List<MaterialPriceEntity> {
        val multiplier = country.rateToTndMultiplier
        return BASE_PRICES.map { raw ->
            val computedPrice = (raw.defaultPriceTnd * multiplier * 100).toLong() / 100.0
            MaterialPriceEntity(
                id = "${country.code}_${raw.baseKey}",
                baseKey = raw.baseKey,
                countryCode = country.code,
                category = raw.category,
                nameFr = raw.nameFr,
                nameAr = raw.nameAr,
                nameDerja = raw.nameDerja,
                unit = raw.unit,
                defaultPrice = computedPrice,
                currentPrice = computedPrice,
                currency = country.currencySymbol,
                brandInfo = raw.brandInfo
            )
        }
    }

    fun getAllInitialPrices(): List<MaterialPriceEntity> {
        val allList = mutableListOf<MaterialPriceEntity>()
        for (country in Country.entries) {
            allList.addAll(generatePricesForCountry(country))
        }
        return allList
    }
}
