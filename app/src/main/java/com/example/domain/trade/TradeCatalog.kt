package com.example.domain.trade

import com.example.data.model.CalculationType
import com.example.data.model.TradeCategory
import com.example.domain.model.Trade
import com.example.domain.model.TradeStatus
import com.example.domain.model.WorkType
import com.example.domain.model.WorkTypeStatus

/**
 * Central repository catalog for all Construction Trades (Métiers BTP)
 * supported or planned in KONSTRIVO PRO.
 */
object TradeCatalog {

    val PLACO_TRADE = Trade(
        id = "placo",
        category = TradeCategory.PLACO,
        nameFr = "Placo & Faux Plafonds",
        nameAr = "البلاكو والأسقف المعلقة",
        nameDerja = "البلاكو بلاتر والفوبلافون",
        nameEn = "Drywall & False Ceilings",
        iconEmoji = "🏗️",
        descriptionFr = "Plafonds fixes BA13, cloisons de séparation, dalles 60x60, caissons LED, Aquapanel et PVC",
        descriptionAr = "أسقف جبسية مستعارة، جدران عازلة، بلاطات 60×60، إنارة مخفية كرتوش، وألواح إسمنتية",
        descriptionDerja = "فوبلافون مستوي، كلووزون بالسكك، بلاك 60×60، كرتوش ليد، أكوابانيل وسقف بي في سي",
        descriptionEn = "BA13 false ceilings, partition walls, 60x60 grid tiles, light caissons, Aquapanel & PVC",
        status = TradeStatus.ACTIVE,
        workTypes = listOf(
            WorkType(
                id = "placo_plafond_fixe",
                nameFr = "Faux Plafond Simple BA13",
                nameAr = "سقف معلق مستوي بسيط BA13",
                nameDerja = "فوبلافون مستوي بالـ Fourrure",
                nameEn = "Standard BA13 Suspended Ceiling",
                descriptionFr = "Ossature F47 suspendue, suspentes, cavaliers, cornières L et plaques de plâtre 3m²",
                descriptionAr = "هيكل معدني F47، علاقات سوسبانت، زوايا محيطية وألواح جبسية 1.2×2.5م",
                descriptionDerja = "هيكل حديد F47، سوسبانت، كافاليي وكورنيار دايرة مع بلاك 3م²",
                descriptionEn = "F47 metal framing, hangers, pivot brackets, L angles and 3m² plasterboards",
                iconEmoji = "📐",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.PLACO_PLAFOND_FIXE
            ),
            WorkType(
                id = "placo_cloison",
                nameFr = "Cloison de Séparation (Rails & Montants)",
                nameAr = "جدار فاصل كلووزون مزدوج الهيكل",
                nameDerja = "فاصل كلووزون بالسكك والـ Montants",
                nameEn = "Drywall Partition Wall",
                descriptionFr = "Rails 48, montants 48 espacés 40/60cm, simple ou double peau avec isolation",
                descriptionAr = "سكك 48 وقوائم 48 بتباعد 40/60 سم، وجه مفرد أو مزدوج مع صوف صخري",
                descriptionDerja = "سكك 48 ومونطون 48، وجهين مع الصوف الصخري وشريط العزل",
                descriptionEn = "48mm tracks & studs at 40/60cm spacing, single/double skin with insulation",
                iconEmoji = "🧱",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.PLACO_CLOISON
            ),
            WorkType(
                id = "placo_demontable",
                nameFr = "Plafond Démontable 60x60 cm",
                nameAr = "سقف تفكيكي شبكي 60×60 سم",
                nameDerja = "فوبلافون ديكور 60×60 سم",
                nameEn = "Demountable Grid Ceiling 60x60",
                descriptionFr = "Grille apparente T24 (Porteurs 3.6m, entretoises 1.2m/0.6m) et dalles vinyle ou laine minérale",
                descriptionAr = "شبكة T24 ظاهرة (حوامل 3.6م وعوارض 1.2م/0.6م) وبلاطات فينيل أو صوف معدني",
                descriptionDerja = "شبكة T24 بورتور 3.6م وأونتروتواز مع بلاك فينيل أو صوف صخري",
                descriptionEn = "T24 exposed grid (3.6m mains, 1.2m/0.6m cross tees) and 60x60 acoustic tiles",
                iconEmoji = "🔲",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.PLACO_DEMONTABLE
            ),
            WorkType(
                id = "placo_cache_lumiere",
                nameFr = "Décors & Cache-Lumière (Caisson / الكرتوش)",
                nameAr = "إنارة مخفية وديكور كرتوش (Caisson)",
                nameDerja = "الكرتوش والميزان (شريط الليد)",
                nameEn = "Light Pockets & Decorative Caissons",
                descriptionFr = "Métré linéaire (ml), retombée verticale, cornières d'angle et bande armée",
                descriptionAr = "حساب بالمتر الخطي، هبوط الميزان، زوايا تثبيت وشريط زوايا مسلح",
                descriptionDerja = "حساب بالمتر طولي (ml)، هبوط الميزان، كورنيار وبوند أرمي للزوايا",
                descriptionEn = "Linear meter takeoff (lm), vertical drop, corner beads and reinforced tape",
                iconEmoji = "💡",
                unit = "ml",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.PLACO_CACHE_LUMIERE
            ),
            WorkType(
                id = "placo_aquapanel",
                nameFr = "Habillage Extérieur Aquapanel / Ciment",
                nameAr = "تغليف خارجي بألواح إسمنتية أكوابانيل",
                nameDerja = "أكوابانيل للواجهات الخارجية ومقاومة الماء",
                nameEn = "Aquapanel Exterior Cement Board",
                descriptionFr = "Plaques de ciment extérieur, trame de renfort 160g, colle ciment SM700 et vis traitées",
                descriptionAr = "ألواح إسمنتية خارجية، شبكة تسليح فيبر، غراء إسمنتي وبراغي مقاومة للصدأ",
                descriptionDerja = "بلاك سيما أكوابانيل، شبكة فيبر كول سيما SM700 وفيس مقاوم للصدأ",
                descriptionEn = "Exterior cement boards, 160g mesh, SM700 cement mortar and anti-rust screws",
                iconEmoji = "🛡️",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.PLACO_AQUAPANEL
            ),
            WorkType(
                id = "placo_pvc",
                nameFr = "Plafond PVC Anti-Humidité",
                nameAr = "سقف بلاستيك PVC مقاوم للرطوبة",
                nameDerja = "فوبلافون PVC ضد الماء للادوش",
                nameEn = "Moisture-Resistant PVC Ceiling",
                descriptionFr = "Lames PVC 6m, profilés de rive U, fourrures F47 et vis TRPF",
                descriptionAr = "شرائح بلاستيكية 6 أمتار، زوايا U، قطاعات معدنية وبراغي TRPF",
                descriptionDerja = "لام بي في سي 6م، بروفيل U، فورور F47 وفيس تي إر بي إف",
                descriptionEn = "6m PVC panels, perimeter U profiles, F47 metal furring and TRPF screws",
                iconEmoji = "💧",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.PLACO_PVC
            )
        )
    )

    val PEINTURE_TRADE = Trade(
        id = "peinture",
        category = TradeCategory.PEINTURE,
        nameFr = "Peinture & Finition",
        nameAr = "الدهان والديكور",
        nameDerja = "الدهينة والديكورات",
        nameEn = "Painting & Finishing",
        iconEmoji = "🎨",
        descriptionFr = "Enduit de lissage, primaire fixateur, peintures satinées, sablé décoratif et finitions murales",
        descriptionAr = "معجون التسوية، سائل الأساس الفيكساتور، الدهان الساتيني، والدهان الرملي الديكوري",
        descriptionDerja = "أندوي ليسّاج، فيكساتور، دهينة سيتان قابلة للغسيل وديكورات سابلي وستيكو",
        descriptionEn = "Smoothing skim coats, primers, satin washables, sand-effect textures and wall finishes",
        status = TradeStatus.ACTIVE,
        workTypes = listOf(
            WorkType(
                id = "peinture_interieure",
                nameFr = "Peinture Intérieure Complète (Enduit + Fixateur + Finition)",
                nameAr = "دهان داخلي كامل (معجون + أساس + تشطيب ساتيني)",
                nameDerja = "دهينة كاملة (أندوي + فيكساتور + سيتان)",
                nameEn = "Complete Interior Painting (Skim + Primer + Finish)",
                descriptionFr = "2 couches d'enduit de lissage blanc, 1 couche primaire fixateur, 2 couches de finition satinée",
                descriptionAr = "طبقتان معجون تسوية، طبقة أساس فيكساتور، وطبقتان دهان ساتيني قابل للغسيل",
                descriptionDerja = "طبقتين أندوي، كوش فيكساتور لربط الغبرة، وزوز كوشات دهينة سيتان ممتازة",
                descriptionEn = "2 coats smoothing skim, 1 coat bonding primer, 2 coats washable satin finish",
                iconEmoji = "🖌️",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.PEINTURE_INTERIEURE
            ),
            WorkType(
                id = "peinture_sable",
                nameFr = "Décor Sablé / Stucco",
                nameAr = "دهان ديكوري رملي (سابلي) أو ستوكو",
                nameDerja = "ديكور سابلي رملي أو ستيكو",
                nameEn = "Decorative Sand Texture / Stucco",
                descriptionFr = "Sous-couche spécifique et finition à effet sablé perlé ou marbré",
                descriptionAr = "طبقة أساس مخصصة ومعجون ديكوري بتأثير رملي أو رخامي",
                descriptionDerja = "فيكساتور مخصص وسطال سابلي ديكور رملي أو ستيكو رخامي",
                descriptionEn = "Dedicated undercoat and metallic or pearlescent sand-textured finish",
                iconEmoji = "✨",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.PEINTURE_SABLE
            ),
            WorkType(
                id = "peinture_facade_exterieure",
                nameFr = "Peinture Façade Extérieure (Siloxane / RPE)",
                nameAr = "دهان واجهات خارجية (سيلوكسان / RPE)",
                nameDerja = "دهينة واجهات خارجية ضد الشمس والمطر",
                nameEn = "Exterior Façade Paint (Siloxane / RPE)",
                descriptionFr = "Revêtement plastique épais résistant aux UV, intempéries et fissures",
                descriptionAr = "طبقات عازلة مقاومة للأشعة فوق البنفسجية والأمطار والتشققات الخارجية",
                descriptionDerja = "دهان مخصص للواجهات الخارجية مقاوم للشمس والندوة والتشققات",
                descriptionEn = "UV-resistant and waterproof exterior coatings for building facades",
                iconEmoji = "🏛️",
                unit = "m²",
                status = WorkTypeStatus.IN_DEVELOPMENT,
                calculationType = null
            ),
            WorkType(
                id = "peinture_sol_epoxy",
                nameFr = "Résine & Peinture Sol Époxy",
                nameAr = "دهان أرضيات إيبوكسي ومواقف",
                nameDerja = "دهينة قاعة ريزين إيبوكسي للكراجات",
                nameEn = "Epoxy Floor Coating & Resin",
                descriptionFr = "Revêtement de sol haute résistance pour garages, entrepôts et locaux techniques",
                descriptionAr = "طلاء أرضيات عالي المقاومة للمرائب والمستودعات والمنشآت الصناعية",
                descriptionDerja = "ريزين إيبوكسي مقاوم للزيوت والأوزان الثقيلة في الديبوات والڨاراجات",
                descriptionEn = "Heavy-duty epoxy resin system for garages, warehouses and technical rooms",
                iconEmoji = "🚗",
                unit = "m²",
                status = WorkTypeStatus.COMING_SOON,
                calculationType = null
            )
        )
    )

    val MACONNERIE_TRADE = Trade(
        id = "maconnerie",
        category = TradeCategory.MACONNERIE,
        nameFr = "Maçonnerie & Briques",
        nameAr = "بناء الجدران والآجر",
        nameDerja = "بنيان الآجور والعرصة",
        nameEn = "Masonry & Brickwork",
        iconEmoji = "🧱",
        descriptionFr = "Murs en briques 8 et 12 trous, mortier de ciment, sable lavé et chaînages",
        descriptionAr = "جدران الآجر 8 و12 ثقب، ملاط الإسمنت والرمل المغسول والأعمدة",
        descriptionDerja = "بنيان حيوط بالياجور 8 و12 عين، سيمون ورملة مغسولة وسبايك حديد",
        descriptionEn = "8 & 12-hole hollow brick walls, cement mortar, washed sand and reinforcement",
        status = TradeStatus.ACTIVE,
        workTypes = listOf(
            WorkType(
                id = "maconnerie_brique",
                nameFr = "Mur en Briques (8 ou 12 trous)",
                nameAr = "بناء جدار بالآجر (8 أو 12 ثقب)",
                nameDerja = "بنيان حائط بالآجور التونسي",
                nameEn = "Hollow Brick Wall (8 or 12 holes)",
                descriptionFr = "22 briques/m², ciment Portland 10kg/m², sable lavé 0.04m³/m²",
                descriptionAr = "22 آجرة/م²، إسمنت 10 كغ/م²، ورمل 0.04 م³/م²",
                descriptionDerja = "22 ياجورة للمتر، شكاير سيمون ورملة مغسولة للبنيان",
                descriptionEn = "22 bricks/m², 10kg Portland cement/m², 0.04m³ washed sand/m²",
                iconEmoji = "🧱",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.MACONNERIE_BRIQUE
            ),
            WorkType(
                id = "maconnerie_agglo_beton",
                nameFr = "Murs en Blocs Béton / Agglos",
                nameAr = "بناء جدران بالبلوك الخرساني (البلك)",
                nameDerja = "بنيان بالبلوك والسيمون المصبوب",
                nameEn = "Concrete Hollow Blockwork",
                descriptionFr = "Maçonnerie lourde en blocs de béton 15/20 cm et mortier renforcé",
                descriptionAr = "بناء بلوك إسمنتي مفرغ ومصمت مع ملاط إسمنتي قوي",
                descriptionDerja = "بنيان ثقيل بالبلوك للمستودعات والأسوار الخارجية",
                descriptionEn = "Heavy-duty 15/20cm concrete block masonry and joint mortar",
                iconEmoji = "🏗️",
                unit = "m²",
                status = WorkTypeStatus.IN_DEVELOPMENT,
                calculationType = null
            )
        )
    )

    val CREPISSAGE_TRADE = Trade(
        id = "crepissage",
        category = TradeCategory.CREPISSAGE,
        nameFr = "Enduit Ciment & Crépissage",
        nameAr = "اللياسة والتلبيس الإسمنتي",
        nameDerja = "الترطيبة واللياسة",
        nameEn = "Plastering & Cement Rendering",
        iconEmoji = "🖌️",
        descriptionFr = "Crépissage 1.5cm d'épaisseur, gobetis d'accrochage, sable fin tamisé et ciment",
        descriptionAr = "لياسة إسمنتية 1.5 سم، طرطشة ابتدائية، رمل ناعم مصفى وإسمنت بورتلاند",
        descriptionDerja = "ترطيبة 1.5 سم، رملة جويدة مصفية، شكاير سيمون وطرطشة",
        descriptionEn = "1.5cm cement render, key coat, sieved fine sand and Portland cement",
        status = TradeStatus.ACTIVE,
        workTypes = listOf(
            WorkType(
                id = "crepissage_ciment",
                nameFr = "Enduit Ciment / Crépissage (1.5 cm)",
                nameAr = "لياسة وتلبيس إسمنتي (1.5 سم)",
                nameDerja = "ترطيبة ولياسة بالحمصي والرمل",
                nameEn = "1.5cm Cement Plastering & Rendering",
                descriptionFr = "15 kg de ciment/m², sable fin 0.05m³/m² pour 1.5 cm d'épaisseur",
                descriptionAr = "15 كغ إسمنت/م²، ورمل ناعم 0.05 م³/م² للطبقة المعيارية",
                descriptionDerja = "15 كغ سيمون للمتر، رملة جويدة مصفية للترطيبة المستوية",
                descriptionEn = "15kg cement/m², 0.05m³ fine sand/m² for standard 1.5cm thickness",
                iconEmoji = "🖌️",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.CREPISSAGE_CIMENT
            )
        )
    )

    val CARRELAGE_TRADE = Trade(
        id = "carrelage",
        category = TradeCategory.CARRELAGE,
        nameFr = "Revêtement & Carrelage",
        nameAr = "السيراميك والأرضيات",
        nameDerja = "الفايونس والقاعة غرس",
        nameEn = "Tiling & Floor Covering",
        iconEmoji = "📐",
        descriptionFr = "Pose de carrelage sol, grès cérame, faïence murale, ciment colle et mortier joint",
        descriptionAr = "تركيب سيراميك وبورسلان الأرضيات، فايونس الجدران، إسمنت لاصق وترويبة الفواصل",
        descriptionDerja = "فايونس الجدران، سيراميك وقاعة غرس، شكاير كول وشكارة جوان ملونة",
        descriptionEn = "Floor & wall ceramic tiling, porcelain stoneware, tile adhesive and grout mortar",
        status = TradeStatus.ACTIVE,
        workTypes = listOf(
            WorkType(
                id = "carrelage_sol",
                nameFr = "Pose Carrelage / Grès / Faïence",
                nameAr = "تركيب سيراميك أرضيات أو جدران",
                nameDerja = "تركيب فايونس أو قاعة غرس",
                nameEn = "Ceramic & Porcelain Tile Installation",
                descriptionFr = "Métré avec 10% de casse, ciment colle 4kg/m², mortier joint 0.3kg/m²",
                descriptionAr = "حساب مع 10% هدر وقص، غراء 4 كغ/م²، وترويبة 0.3 كغ/م²",
                descriptionDerja = "حساب مع 10% قص وزوايا، شكاير كول 4كغ للمتر وشكارة جوان",
                descriptionEn = "Takeoff with 10% cutting waste, 4kg adhesive/m², 0.3kg grout/m²",
                iconEmoji = "📐",
                unit = "m²",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.CARRELAGE_SOL
            )
        )
    )

    val MENUISERIE_TRADE = Trade(
        id = "menuiserie",
        category = TradeCategory.MENUISERIE,
        nameFr = "Menuiserie Bois & Alu",
        nameAr = "النجارة والألمنيوم",
        nameDerja = "النجارة والبيبان والشبابك",
        nameEn = "Carpentry & Aluminum",
        iconEmoji = "🪟",
        descriptionFr = "Pose de blocs-portes en bois, fenêtres aluminium, joints d'étanchéité et mousses PU",
        descriptionAr = "تركيب الأبواب الخشبية، نوافذ الألمنيوم والزجاج المزدوج، موانع التسرب وفوم التثبيت",
        descriptionDerja = "تركيب بيبان لوح، شبابك ألمنيوم، جوان كوتشوك كحل وفوم تثبيت",
        descriptionEn = "Wood door sets, aluminum windows, weatherproofing seals and polyurethane foam",
        status = TradeStatus.ACTIVE,
        workTypes = listOf(
            WorkType(
                id = "menuiserie_portes",
                nameFr = "Pose Portes Intérieures Bois",
                nameAr = "تركيب أبواب خشب داخلية",
                nameDerja = "تركيب بيبان لوح للبيوت",
                nameEn = "Interior Wooden Door Installation",
                descriptionFr = "Blocs-portes, vis bois 40mm, paumelles et mousse polyuréthane de fixation",
                descriptionAr = "أبواب، براغي 40مم، مفصلات بومال وفوم بولي يوريثان للتثبيت",
                descriptionDerja = "باب لوح، فيس 40مم، بومال وفوم تثبيت الكادر",
                descriptionEn = "Door sets, 40mm wood screws, hinges and expanding PU foam",
                iconEmoji = "🚪",
                unit = "unités",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.MENUISERIE_PORTES
            ),
            WorkType(
                id = "menuiserie_alu",
                nameFr = "Pose Fenêtres & Baies Alu",
                nameAr = "تركيب نوافذ وواجهات ألمنيوم",
                nameDerja = "تركيب شبابك ألمنيوم وبالارات",
                nameEn = "Aluminum Windows & Sliding Doors",
                descriptionFr = "Fenêtres aluminium, joints d'étanchéité caoutchouc (10m/u) et silicone neutre",
                descriptionAr = "نوافذ ألمنيوم، شريط جلد عازل 10م لكل نافذة، وسيليكون شفاف",
                descriptionDerja = "شبابك ألمنيوم، جوان كحل 10م للشباك، وكارتوش سيليكون",
                descriptionEn = "Aluminum frames, 10m weatherstripping seal per unit and neutral silicone",
                iconEmoji = "🪟",
                unit = "unités",
                status = WorkTypeStatus.ACTIVE,
                calculationType = CalculationType.MENUISERIE_ALU
            )
        )
    )

    // Roadmap Trades
    val ELECTRICITE_TRADE = Trade(
        id = "electricite",
        category = null,
        nameFr = "Électricité & Câblage",
        nameAr = "الكهرباء والتمديدات",
        nameDerja = "الضو والتريسينتي",
        nameEn = "Electrical & Wiring",
        iconEmoji = "⚡",
        descriptionFr = "Tableaux de répartition, disjoncteurs, gaines ICTA, câblage cuivre et appareillage",
        descriptionAr = "لوحات التوزيع، القواطع، القنوات العازلة، الأسلاك النحاسية ومفاتيح الإنارة",
        descriptionDerja = "طابلوات تريسينتي، ديجونكتارات، قنوات ICTA وسلوكة نحاس وبريزات",
        descriptionEn = "Distribution panels, breakers, conduits, copper wiring and switches/sockets",
        status = TradeStatus.COMING_SOON,
        workTypes = listOf(
            WorkType(
                id = "elec_cablage",
                nameFr = "Câblage & Gaines ICTA",
                nameAr = "تمديد الأسلاك والقنوات",
                nameDerja = "تمرير السلوكة والقنوات",
                nameEn = "Wiring & Conduits",
                descriptionFr = "Gaines annelées et conducteurs H07VU 1.5mm² / 2.5mm²",
                descriptionAr = "قنوات مرنة وأسلاك معزولة 1.5 و2.5 مم²",
                descriptionDerja = "قنوات فلكسيبل وسلوكة نحاس 1.5 و2.5 مم",
                descriptionEn = "Corrugated conduits and copper insulated conductors",
                iconEmoji = "🔌",
                unit = "ml",
                status = WorkTypeStatus.COMING_SOON
            ),
            WorkType(
                id = "elec_tableau",
                nameFr = "Tableau Électrique & Protection",
                nameAr = "لوحة القواطع والحماية",
                nameDerja = "طابلو الضو والديجونكتورات",
                nameEn = "Distribution Board & Protection",
                descriptionFr = "Coffret modulaire, interrupteurs différentiels 30mA et disjoncteurs divisionnaires",
                descriptionAr = "صندوق قواطع، فواصل تفاضلية 30mA وقواطع حماية",
                descriptionDerja = "كوفري طابلو، ديجونكتورات 30mA لحماية الدار",
                descriptionEn = "Modular enclosure, 30mA RCDs and circuit breakers",
                iconEmoji = "⚡",
                unit = "unités",
                status = WorkTypeStatus.COMING_SOON
            )
        )
    )

    val PLOMBERIE_TRADE = Trade(
        id = "plomberie",
        category = null,
        nameFr = "Plomberie & Sanitaire",
        nameAr = "السباكة والتغذية الصحية",
        nameDerja = "البلومبي والصحي",
        nameEn = "Plumbing & Sanitary",
        iconEmoji = "🚿",
        descriptionFr = "Réseau d'alimentation PER/Multicouche, évacuations PVC, collecteurs et appareils sanitaires",
        descriptionAr = "شبكات التغذية بالأنابيب المتعددة الطبقات، شبكة الصرف، الموزعات والأطقم الصحية",
        descriptionDerja = "جعب مالتي كوش للماء، قنوات بي في سي للوسخ، وريديرات ودوش",
        descriptionEn = "PEX/Multilayer supply piping, PVC drainage, manifolds and sanitary fixtures",
        status = TradeStatus.COMING_SOON,
        workTypes = listOf(
            WorkType(
                id = "plomb_per",
                nameFr = "Alimentation Multicouche / PER",
                nameAr = "شبكة التغذية بالأنابيب المعزولة",
                nameDerja = "جعب التغذية بالماء الصالح للشراب",
                nameEn = "Multilayer Water Supply System",
                descriptionFr = "Tubes multicouche gainés Ø16/20mm et raccords à sertir",
                descriptionAr = "أنابيب متعددة الطبقات معزولة ووصلات كبس",
                descriptionDerja = "جعب مالتي كوش ورواكورات كبس للماء البارد والسخون",
                descriptionEn = "Insulated multilayer pipes and press fittings",
                iconEmoji = "🚰",
                unit = "ml",
                status = WorkTypeStatus.COMING_SOON
            )
        )
    )

    val ISOLATION_TRADE = Trade(
        id = "isolation",
        category = null,
        nameFr = "Isolation Thermique & Phonique",
        nameAr = "العزل الحراري والصوتي",
        nameDerja = "العزل والحرارة والصوت",
        nameEn = "Thermal & Acoustic Insulation",
        iconEmoji = "🛡️",
        descriptionFr = "Laines minérales de roche et de verre, panneaux polyuréthane et membranes résilientes",
        descriptionAr = "ألواح الصوف الصخري والزجاجي، عوازل البولي يوريثان والأغشية الصوتية",
        descriptionDerja = "الصوف الصخري والزجاجي، عوازل السخانة والندوة وحس الجيران",
        descriptionEn = "Rockwool & glasswool rolls, PIR polyurethane boards and acoustic membranes",
        status = TradeStatus.COMING_SOON,
        workTypes = listOf(
            WorkType(
                id = "iso_thermique",
                nameFr = "Isolation des Combles & Murs",
                nameAr = "عزل الأسقف والجدران",
                nameDerja = "عزل السطوح والحيوط من السخانة",
                nameEn = "Roof & Wall Insulation",
                descriptionFr = "Rouleaux de laine minérale 100mm R=2.5 et pare-vapeur",
                descriptionAr = "لفائف صوف معدني 100 مم مع حاجز بخار",
                descriptionDerja = "رولوات صوف عازل مع بار فابور",
                descriptionEn = "100mm mineral wool rolls and vapor barrier",
                iconEmoji = "🌡️",
                unit = "m²",
                status = WorkTypeStatus.COMING_SOON
            )
        )
    )

    val ETANCHEITE_TRADE = Trade(
        id = "etancheite",
        category = null,
        nameFr = "Étanchéité & Toitures",
        nameAr = "العزل المائي والأسطح",
        nameDerja = "الكتامة والسطوح",
        nameEn = "Waterproofing & Roofing",
        iconEmoji = "☔",
        descriptionFr = "Membranes bitumineuses thermosoudables, résines polyuréthane et primaires d'imprégnation",
        descriptionAr = "أغشية البيتومين الحرارية، راتنجات العزل المائي وسوائل الأساس",
        descriptionDerja = "زفت كحل حراري للسطوح، ريزين ضد قطران المطر وبريمار",
        descriptionEn = "Torch-applied bituminous membranes, polyurethane liquid waterproofing and primers",
        status = TradeStatus.COMING_SOON,
        workTypes = listOf(
            WorkType(
                id = "etanch_toiture",
                nameFr = "Étanchéité Toiture-Terrasse",
                nameAr = "عزل أسطح المباني والمدرجات",
                nameDerja = "كتامة السطح من المطر",
                nameEn = "Flat Roof Waterproofing",
                descriptionFr = "Membrane bitumineuse 4mm sablée/autoprotégée et relevés d'étanchéité",
                descriptionAr = "أغشية بيتومين 4 مم محمية وزوايا عزل معيارية",
                descriptionDerja = "زفت 4 مم محمي للسطوح مع جوان داير",
                descriptionEn = "4mm mineral surfaced bituminous membrane and perimeter upstands",
                iconEmoji = "🏠",
                unit = "m²",
                status = WorkTypeStatus.COMING_SOON
            )
        )
    )

    private val ALL_TRADES: List<Trade> = listOf(
        PLACO_TRADE,
        PEINTURE_TRADE,
        MACONNERIE_TRADE,
        CREPISSAGE_TRADE,
        CARRELAGE_TRADE,
        MENUISERIE_TRADE,
        ELECTRICITE_TRADE,
        PLOMBERIE_TRADE,
        ISOLATION_TRADE,
        ETANCHEITE_TRADE
    )

    fun getAllTrades(): List<Trade> = ALL_TRADES

    fun getActiveTrades(): List<Trade> = ALL_TRADES.filter { it.status == TradeStatus.ACTIVE }

    fun getTradeById(id: String): Trade? = ALL_TRADES.firstOrNull { it.id == id }

    fun getTradeByCategory(category: TradeCategory): Trade? = ALL_TRADES.firstOrNull { it.category == category }
}
