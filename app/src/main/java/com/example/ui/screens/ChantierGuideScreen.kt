package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy

import com.example.ui.theme.ConstructionAmberSecondary
import com.example.ui.theme.ConstructionBlue
import com.example.ui.theme.ConstructionNavyPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.components.designsystem.tr

// PHASE 4.7 — UI-local display data (NOT domain models).
// English fields added to fix the confirmed localization defect: English
// previously fell back silently to French for section titles, step titles and
// step descriptions. `proTipsEn` / `toolsNeededEn` complete English coverage;
// the French / Arabic / Derja lists keep their original content unchanged.
data class GuideSection(
    val id: String,
    val titleFr: String,
    val titleEn: String,
    val titleAr: String,
    val titleDerja: String,
    val category: String,
    val steps: List<GuideStep>,
    val proTips: List<String>,
    val proTipsEn: List<String>,
    val toolsNeeded: List<String>,
    val toolsNeededEn: List<String>
)

data class GuideStep(
    val stepNumber: Int,
    val titleFr: String,
    val titleEn: String,
    val titleAr: String,
    val titleDerja: String,
    val descriptionFr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val descriptionDerja: String
)

@Composable
fun ChantierGuideScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.selectedLanguage.collectAsState()
    var selectedCategory by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }
    var expandedSectionId by remember { mutableStateOf<String?>("cloison") }

    val guideSections = remember { getGuideSections() }

    val categories = listOf(
        "all" to language.tr("Tout le Guide","Tout le Guide","كامل الدليل","كامل الدليل"),
        "cloison" to language.tr("Cloisons & Doublage","Cloisons & Doublage","الجدران والفاصل","الجدران والفاصل"),
        "plafond" to language.tr("Plafonds Fixes","Plafonds Fixes","الأسقف الثابتة","الأسقف الثابتة"),
        "demontable" to language.tr("Plafonds 60x60","Plafonds 60x60","الأسقف 60×60","الأسقف 60×60"),
        "decor" to language.tr("Décors & Cache-Lumière","Décors & Cache-Lumière","الديكور والإنارة","الديكور والإنارة"),
        "exterieur" to language.tr("PVC & Aquapanel","PVC & Aquapanel","الأشغال الخارجية","الأشغال الخارجية"),
        "finition" to language.tr("Joints & Finition","Joints & Finition","الفواصل والفينيسيون","الفواصل والفينيسيون"),
        "autres_metiers" to language.tr("Autres Métiers BTP","Autres Métiers BTP","بقية مهن البناء","بقية مهن البناء")
    )

    val filteredSections = remember(selectedCategory, searchQuery, language) {
        guideSections.filter { section ->
            val matchCategory = selectedCategory == "all" || section.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    section.titleFr.contains(searchQuery, ignoreCase = true) ||
                    section.titleEn.contains(searchQuery, ignoreCase = true) ||
                    section.titleAr.contains(searchQuery, ignoreCase = true) ||
                    section.titleDerja.contains(searchQuery, ignoreCase = true) ||
                    section.steps.any { step ->
                        step.descriptionDerja.contains(searchQuery, ignoreCase = true) ||
                        step.descriptionFr.contains(searchQuery, ignoreCase = true) ||
                        step.descriptionEn.contains(searchQuery, ignoreCase = true) ||
                        step.descriptionAr.contains(searchQuery, ignoreCase = true)
                    }
            matchCategory && matchQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chantier_guide_screen")
    ) {
        // Banner
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                        .background(ConstructionAmberSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color.White)
                }
                Column {
                    Text(
                        text = language.tr(
                            "Guide des travaux",
                            "Site Works Guide",
                            "دليل الأشغال والتقنيات (من A إلى Z)",
                            "دليل أشغال الشانتي (من A إلى Z)"
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = language.tr(
                            "Guide pratique du chantier : méthodes professionnelles étape par étape.",
                            "Practical site handbook: professional methods, step by step.",
                            "دليل عملي للموقع: منهجيات عمل احترافية خطوة بخطوة.",
                            "دليل عملي للشانتي: طرق العمل خطوة بخطوة."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
            placeholder = { Text(language.tr("Rechercher une étape, outil, technique...","Rechercher une étape, outil, technique...","ابحث في الدليل (مثل: ليزر، كرتوش، رولونج، ماب، جراف...)","ابحث في الدليل (مثل: ليزر، كرتوش، رولونج، ماب، جراف...)")) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("input_guide_search")
        )

        // Categories Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(categories) { (catKey, catTitle) ->
                val isSelected = selectedCategory == catKey
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = catKey },
                    label = { Text(catTitle, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KonstrivoGold,
                        selectedLabelColor = KonstrivoNavy
                    ),
                    // PHASE 4.7 — practical touch target (>= 48dp) + useful tag
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("guide_category_$catKey")
                )
            }
        }

        // Guide List
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // PHASE 4.7 — localized empty state (search / category without results)
            if (filteredSections.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = KonstrivoSpacing.Xxl)
                            .testTag("guide_empty_state"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = language.tr("Aucun résultat","No results found","لا توجد نتائج","ما فماش نتائج"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = language.tr(
                                "Essayez un autre mot-clé ou une autre catégorie.",
                                "Try another keyword or category.",
                                "جرّب كلمة مفتاحية أو قسمًا آخر.",
                                "جرب كلمة أخرى ولا قسم آخر."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            items(filteredSections, key = { it.id }) { section ->
                val isExpanded = expandedSectionId == section.id
                val sectionTitle = when (language) {
                    AppLanguage.DERJA -> section.titleDerja
                    AppLanguage.ARABIC -> section.titleAr
                    AppLanguage.FRENCH -> section.titleFr
                    AppLanguage.ENGLISH -> section.titleEn
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedSectionId = if (isExpanded) null else section.id }
                        .testTag("guide_section_${section.id}"),
                    shape = RoundedCornerShape(KonstrivoRadius.Card),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isExpanded) ConstructionAmberSecondary else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Construction,
                                        contentDescription = null,
                                        tint = if (isExpanded) Color.White else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = sectionTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${section.steps.size} ${language.tr("étapes de réalisation","étapes de réalisation","مراحل تطبيقية في الشانتي","مراحل تطبيقية في الشانتي")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Expanded Steps & Tips
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // Tools needed badge row (PHASE 4.7: localized EN list + vector icon)
                                if (section.toolsNeeded.isNotEmpty()) {
                                    val sectionTools = when (language) {
                                        AppLanguage.ENGLISH -> section.toolsNeededEn
                                        else -> section.toolsNeeded
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.Build,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = language.tr("Outillage Requis:","Required Tools:","العتاد والأدوات اللازمة:","العتاد والأدوات اللازمة:"),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            items(sectionTools) { tool ->
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.surfaceVariant
                                                ) {
                                                    Text(
                                                        text = tool,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                }

                                // Steps List
                                section.steps.forEach { step ->
                                    val stepTitle = when (language) {
                                        AppLanguage.DERJA -> step.titleDerja
                                        AppLanguage.ARABIC -> step.titleAr
                                        AppLanguage.FRENCH -> step.titleFr
                                        AppLanguage.ENGLISH -> step.titleEn
                                    }
                                    val stepDesc = when (language) {
                                        AppLanguage.DERJA -> step.descriptionDerja
                                        AppLanguage.ARABIC -> step.descriptionAr
                                        AppLanguage.FRENCH -> step.descriptionFr
                                        AppLanguage.ENGLISH -> step.descriptionEn
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(KonstrivoGold),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = step.stepNumber.toString().padStart(2, '0'),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = KonstrivoNavy
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = stepTitle,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = stepDesc,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Pro Tips Banner (PHASE 4.7: localized EN list, vector icon only)
                                if (section.proTips.isNotEmpty()) {
                                    val sectionTips = when (language) {
                                        AppLanguage.ENGLISH -> section.proTipsEn
                                        else -> section.proTips
                                    }
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ConstructionAmberSecondary, modifier = Modifier.size(18.dp))
                                                Text(
                                                    text = language.tr("Conseils du Pro & Astuces de Chantier","Pro Tips & Site Tricks","نصائح الصحاحبية والخبرة الميدانية","نصائح الصحاحبية والخبرة الميدانية"),
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                            sectionTips.forEach { tip ->
                                                Text(
                                                    text = "• $tip",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getGuideSections(): List<GuideSection> {
    return listOf(
        GuideSection(
            id = "cloison",
            category = "cloison",
            titleFr = "1. Cloisons Séparatives & Doublage (الجدران والفاصل)",
            titleEn = "1. Partition Walls & Drywall Lining (Cloison & Doublage)",
            titleAr = "1. الجدران الفاصلة والتغليف (Cloison & Doublage)",
            titleDerja = "1. حائط فاصل كلووزون وتغليف الحيوط (Cloison)",
            toolsNeeded = listOf("ميزان ليزر Laser", "مقص حديد Cisaille", "شنيور Perforateur", "كالتوز Pince à sertir", "مفك براغي Visseuse"),
            toolsNeededEn = listOf("Laser level", "Tin snips (Cisaille)", "Hammer drill (Perforateur)", "Crimping pliers (Pince à sertir)", "Screw gun (Visseuse)"),
            proTips = listOf(
                "في الارتفاعات الأكثر من 3 أمتار، قم بعمل تمليح/تمديد (Rallonge) بتداخل Montant داخل الآخر بمسافة 30-40 سم وتثبيتهما بـ 4 براغي TRPF.",
                "لا تجعل فواصل الألواح للجهة الأولى تلتقي مع فواصل الجهة الثانية أبداً (طريقة Décalage) لضمان متانة الجدار.",
                "اترك مسافة 1 سم بين رأس الـ Montant والسقف لتفادي ضغط المبنى والتشققات."
            ),
            proTipsEn = listOf(
                "For heights above 3 m, extend the Montant by splicing one inside the other with a 30–40 cm overlap and fixing them with 4 TRPF screws (rallonge).",
                "Never let the board joints of the first side line up with the joints of the second side (décalage) — this is what guarantees the strength of the wall.",
                "Leave a 1 cm gap between the top of the Montant and the ceiling to absorb building movement and prevent cracking."
            ),
            steps = listOf(
                GuideStep(
                    1,
                    "Le Traçage (الترسيم والتخطيط)",
                    "Layout & Marking (Traçage)",
                    "تخطيط وترسيم الخطوط بالليزر",
                    "الترسيم بالليزر وخيط الطبشور",
                    "Allumer le niveau laser pour tracer l'alignement au sol, puis reporter la ligne au plafond et sur les murs avec le cordeau traceur.",
                    "Switch on the laser level to mark the alignment line on the floor, then transfer the line to the ceiling and walls using the chalk line.",
                    "استخدم ميزان الليزر لتحديد مكان الجدار بدقة على الأرض، ثم انقل الخط للسقف والحوائط بالخيط المائي والطبشور.",
                    "يشعل الحرفي الليزر على القاعة، ثم ينقل الخط للسقف والحيوط بالخيط الأزرق (Bleu/Traceur)."
                ),
                GuideStep(
                    2,
                    "Fixation des Rails (تثبيت السكك الأفقية)",
                    "Fixing the Rails",
                    "تثبيت سكك الـ Rails في الأرض والسقف",
                    "تثبيت الـ Rails بالشوفي ضرب 6x40",
                    "Couper les Rails 48/70 à la cisaille, percer au perforateur tous les 50 cm et fixer avec des chevilles à frapper 6x40.",
                    "Cut the 48/70 Rails with the tin snips, drill with the hammer drill every 50 cm and fix with 6x40 hammer-set anchors (chevilles à frapper).",
                    "قص مقاطع الـ Rails وثبتها في الأرض والسقف كل 50 إلى 60 سم باستخدام أوتاد الضرب Cheville à frapper.",
                    "يقص الـ Rail بمقص الحديد ويثقب في القاعة والسقف كل 50 سم ويثبت بالشوفي ضرب."
                ),
                GuideStep(
                    3,
                    "Montage des Montants (توقيف القوائم العمودية)",
                    "Installing the Studs (Montants)",
                    "تركيب وتوزيع القوائم العمودية Montants",
                    "توقيف الـ Montants كل 40 أو 60 سم",
                    "Insérer les Montants verticalement dans les rails avec un espacement de 40 ou 60 cm. Sertir avec la pince ou fixer avec vis TRPF.",
                    "Insert the Montants (studs) vertically into the rails at 40 cm or 60 cm spacing. Crimp with the crimping pliers or fix with TRPF screws.",
                    "إدخال الـ Montants عمودياً كل 40 سم أو 60 سم، وقصها أقل من ارتفاع السقف بـ 1 سم لتفادي هبوط المبنى.",
                    "تدخل الـ Montants عمودياً وتثبت مع الـ Rail من الأسفل والأعلى بالكالتوز أو فيس TRPF."
                ),
                GuideStep(
                    4,
                    "Pose de la 1ère Face & Isolation (التغليف الأول والعزل)",
                    "First Side & Insulation",
                    "تثبيت ألواح الوجه الأول وتمرير العزل والأنابيب",
                    "تغليف الشيرة الأولى وتمرير الصوف وخراطيم الكهرباء",
                    "Visser les plaques BA13 tous les 25cm (Vis 25mm), passer les gaines électriques et poser la laine de verre/roche.",
                    "Screw the BA13 boards every 25 cm (25 mm screws), run the electrical conduits through the pre-cut openings and install the glass wool / rock wool insulation.",
                    "تثبيت ألواح الجبس على وجه واحد، تمرير خراطيم الكهرباء عبر الفتحات الجاهزة، ووضع ألواح الصوف الزجاجي.",
                    "يرفع لوح BA13 ويتبرغ بفيس 25مم كل 25سم، يمرر الكهربائي الجينات ويحط الصحبي اللين دو فار."
                ),
                GuideStep(
                    5,
                    "Fermeture 2ème Face (إغلاق الجدار والتغليف الثاني)",
                    "Closing the Second Side",
                    "تثبيت ألواح الجبس على الجهة الثانية",
                    "تسكير الحائط من الشيرة الثانية مع الديكالاج",
                    "Fermer la cloison avec la 2ème couche de BA13 en décalant les joints par rapport à la 1ère face.",
                    "Close the partition with the second layer of BA13, offsetting the joints (décalage) relative to the first side.",
                    "تثبيت ألواح الجبس من الجهة الثانية مع مراعاة البدء بنصف لوح إذا بدأت بلوح كامل في الجهة الأولى لزيادة الصلابة.",
                    "تثبيت الألواح من الجهة الثانية بعكس الأولى لكي لا تلتقي الفواصل وتضمن صلابة الجدار."
                )
            )
        ),
        GuideSection(
            id = "plafond_fixe",
            category = "plafond",
            titleFr = "2. Faux Plafond Fixe BA13 (الأسقف المستعارة الثابتة)",
            titleEn = "2. Fixed BA13 Suspended Ceiling (Faux Plafond Fixe)",
            titleAr = "2. الأسقف المستعارة الثابتة (Faux Plafond Fixe)",
            titleDerja = "2. فوبلافون مستوي بالـ Fourrures والسوسبانت",
            toolsNeeded = listOf("رافعة ألواح Lève-plaque", "ميزان ليزر 360°", "مفك براغي خاص بالبلاكو", "مشابك Cavaliers", "شنيور ثقب"),
            toolsNeededEn = listOf("Board lifter (Lève-plaque)", "360° laser level", "Drywall screw gun", "Cavalier clips (Cavaliers)", "Hammer drill"),
            proTips = listOf(
                "وزع خطوط الـ Fourrures بمسافة 50 أو 60 سم كحد أقصى لمنع هبوط أو تقوس الألواح.",
                "اترك فراغاً بسيطاً (1 إلى 2 ملم) بين كل لوح والآخر للسماح للمعجون بالتغلغل.",
                "استعمل رافعة الألواح (Lève-plaque) لتوفير الجهد وضمان دقة التثبيت."
            ),
            proTipsEn = listOf(
                "Space the furring channel (Fourrures) lines at 50 or 60 cm maximum to prevent the boards from sagging or bowing.",
                "Leave a small gap (1 to 2 mm) between each board so the jointing compound can penetrate.",
                "Use a board lifter (Lève-plaque) to save effort and ensure accurate fixing."
            ),
            steps = listOf(
                GuideStep(
                    1,
                    "Niveau & Cornières (تحديد المستوى وتثبيت الزوايا)",
                    "Level & Wall Angles (Cornières)",
                    "ضبط مستوى السقف وتثبيت زوايا الكورنيار المحيطية",
                    "ضبط النيفو بالليزر وتثبيت الكورنيار L دايرة بالبيت",
                    "Projeter le trait laser au niveau désiré et fixer les Cornières L sur le pourtour avec des chevilles à frapper.",
                    "Project the laser line at the desired height and fix the L-shaped wall angles (Cornières) around the perimeter with hammer-set anchors.",
                    "تحديد الارتفاع المطلوب بالليزر وتثبيت زوايا الـ Cornière L على كامل محيط الجدران.",
                    "يوضع الليزر على العلو المطلوب ويثبت الكورنيار L على كامل الدار بالشوفي ضرب."
                ),
                GuideStep(
                    2,
                    "Traçage & Suspentes (علاقات السقف والسوسبانت)",
                    "Grid Lines & Hangers (Suspentes)",
                    "تحديد خطوط السقف وتثبيت علاقات السوسبانت",
                    "تثبيت السوسبانت كل 60 سم في الدالة",
                    "Tracer les lignes d'ossature tous les 50-60 cm et fixer les suspentes au plafond béton.",
                    "Mark the framing lines every 50–60 cm and anchor the suspentes (hangers) into the concrete ceiling.",
                    "رسم خطوط متوازية في السقف كل 60 سم وثقب الخرسانة لتثبيت علاقات الـ Suspentes.",
                    "يرسم خطوط متوازية كل 60 سم ويثقب في الدالة لتعليق السوسبانتات."
                ),
                GuideStep(
                    3,
                    "Pose des Fourrures (تركيب الهيكل الحامل)",
                    "Installing the Furring Channels (Fourrures)",
                    "تعليق قضبان الـ Fourrures وضبط استوائها",
                    "تعليق الفورورات في السوسبانتات بالمشابك الكافاليي",
                    "Enclipser les fourrures F47 dans les cavaliers des suspentes et ajuster l'horizontalité parfaite.",
                    "Snap the F47 furring channels into the cavalier clips of the suspentes and adjust them to a perfectly level plane.",
                    "تعليق قضبان الـ Fourrures في مشابك الـ Cavaliers وضبط استوائها مع مستوى الكورنيار الجانبية.",
                    "تعلق قضبان الفورور F47 في السوسبانت عبر الكافاليي وتتوزن بالميليمتر مع الكورنيار."
                ),
                GuideStep(
                    4,
                    "Vissage des Plaques (تثبيت وبرغنة الألواح)",
                    "Screwing the Boards",
                    "رفع ألواح البلاكو وتثبيتها بالبراغي",
                    "رفع البلاكو وتبرغينها بالفيس 25 مم",
                    "Lever les plaques BA13 à l'aide du lève-plaque et visser sous les fourrures avec vis 25mm tous les 25cm.",
                    "Lift the BA13 boards with the board lifter (lève-plaque) and screw them under the furring channels with 25 mm screws every 25 cm.",
                    "رفع ألواح الجبس وتثبيتها أسفل الـ Fourrures ببراغي Vis Placo 25mm كل 25 سم.",
                    "تترفع ألواح البلاكو وتتبرغ تحت الفورورات بفيس بلاكو 25مم كل 25 سم."
                )
            )
        ),
        GuideSection(
            id = "plafond_demontable",
            category = "demontable",
            titleFr = "3. Plafond Démontable 60x60 (الأسقف التفكيكية)",
            titleEn = "3. Demountable 60x60 Ceiling Grid (Faux Plafond Démontable)",
            titleAr = "3. الأسقف التفكيكية المعلقة 60×60 سم",
            titleDerja = "3. فوبلافون ديكور ودالات 60×60 للمحلات والمكاتب",
            toolsNeeded = listOf("ميزان ليزر", "مقص صاج", "تيج فيلتي M6", "دالات 60x60 فينيل أو روكفون"),
            toolsNeededEn = listOf("Laser level", "Tin snips", "M6 threaded rod (Tige filetée)", "60x60 vinyl or Rockfon tiles"),
            proTips = listOf(
                "الميزة الكبرى لهذا السقف أنه لا يحتاج براغي أو معجون، ومثالي للمحلات لتسهيل الصيانة.",
                "ثبت السلك المسنن (Tige filetée) كل 1.20م على طول خطوط الحامل الرئيسي Porteur 3.60m.",
                "تأكد من تعامد المربعات 60×60 سم لتنزيل الصفائح بسلاسة تامة."
            ),
            proTipsEn = listOf(
                "The big advantage of this ceiling: no screws and no compound — ideal for shops because maintenance is easy.",
                "Fix the threaded rod (Tige filetée) every 1.20 m along the 3.60 m main carrier (Porteur) lines.",
                "Make sure the 60x60 squares are perfectly square so the tiles drop in smoothly."
            ),
            steps = listOf(
                GuideStep(
                    1,
                    "Cornière de Rive & Tiges (الزوايا والأسلاك المسننة)",
                    "Edge Angle & Threaded Rods",
                    "تثبيت الزاوية البيضاء وتنزيل الأسلاك المسننة",
                    "تثبيت كورنيار ريف بيضاء وتنزيل التيج فيلتي",
                    "Fixer la cornière de rive laquée blanc et ancrer les tiges filetées M6 espacées de 1.20m.",
                    "Fix the white lacquered edge angle around the perimeter and anchor the M6 threaded rods every 1.20 m into the concrete ceiling.",
                    "تثبيت زاوية الحافة البيضاء على المحيط وتثبيت أسلاك التيج فيلتي M6 في السقف الخرساني.",
                    "تتثبت الكورنيار ريف البيضاء في الحيوط وتتعلق التيجات في السقف."
                ),
                GuideStep(
                    2,
                    "Porteurs Principaux (السكك الرئيسية 3.60م)",
                    "Main Carriers (T24, 3.60 m)",
                    "تعليق السكك الرئيسية Porteur 3.60m",
                    "تعليق البورتور 3.60م في التيجات",
                    "Suspendre les porteurs T24 de 3.60m parallèlement aux murs principaux.",
                    "Hang the 3.60 m T24 main carriers on the threaded rods, parallel to the main walls, and level them.",
                    "تعليق قضبان البورتور الطويلة (3.60 متر) في الأسلاك المسننة وضبط استوائها.",
                    "يتعلق البورتور 3.60م في الأسلاك ويكون موازي للحيط."
                ),
                GuideStep(
                    3,
                    "Entretoises & Maillage (تشبيك المربعات 60x60)",
                    "Cross Tees & 60x60 Grid",
                    "تركيب عوارض Entretoises 1.20m و 0.60m",
                    "تشبيك الأنتروتوازات لتكوين كاريات 60×60",
                    "Encliqueter les entretoises de 1.20m puis celles de 0.60m pour former le quadrillage 60x60.",
                    "Snap in the 1.20 m cross tees, then the 0.60 m ones, to form the 60x60 cm grid.",
                    "إدخال العوارض العرضية (1.20م) ثم العوارض القصيرة (0.60م) لتشكيل شبكة مربعات 60×60 سم.",
                    "تركب الأنتروتوازات الطويلة ثم القصيرة ليتشكل هيكل شبكي كاريات 60×60 سم."
                ),
                GuideStep(
                    4,
                    "Pose des Dalles (تنزيل الصفائح)",
                    "Laying the Tiles",
                    "تنزيل صفائح الفينيل أو الصوف الصخري",
                    "تنزيل الدالات باليد وسط المربعات",
                    "Déposer délicatement les dalles vinyle ou Rockfon dans les alvéoles, sans colle ni vis.",
                    "Gently lay the vinyl or Rockfon tiles into the grid cells — no glue, no screws.",
                    "رفع صفائح الجبس المغلفة بالفينيل أو الصوف الصخري باليد وتنزيلها داخل المربعات المعدنية.",
                    "تترفع الدالات باليد وتتحط فوق المربعات المعدنية بدون أي براغي أو كول."
                )
            )
        ),
        GuideSection(
            id = "cache_lumiere",
            category = "decor",
            titleFr = "4. Décors & Cache-Lumière (الإنارة المخفية والكرتوش)",
            titleEn = "4. Décor & LED Light Coves (Cache-Lumière)",
            titleAr = "4. الديكور والإنارة المخفية والكرتوش (Cache-Lumière)",
            titleDerja = "4. كرتوش وهبوط وميزان الليد وفتحات الـ Spots",
            toolsNeeded = listOf("ريشة قص دوائر Scie cloche", "منشار بلاكو Scie guichet", "فرجار رسم", "شريط مسلح Bande armée"),
            toolsNeededEn = listOf("Hole saw (Scie cloche)", "Keyhole saw (Scie guichet)", "Marking compass", "Reinforced tape (Bande armée)"),
            proTips = listOf(
                "احرص على ترك شفة بارزة (Lèvre) بطول 5 إلى 7 سم في الكرتوش لوضع شريط الليد (Flexible Led) بزاوية مخفية.",
                "استعمل الشريط المسلح (Bande armée) في كافة الزوايا البارزة لحمايتها من الكسر.",
                "لا تقص فتحات الـ Spots إلا بعد مراجعة المخطط الكهربائي وقطر المصباح بالمليمتر."
            ),
            proTipsEn = listOf(
                "Leave a protruding lip (Lèvre) of 5 to 7 cm on the cartouche so the flexible LED strip sits at a hidden angle.",
                "Use reinforced tape (Bande armée) on every external corner to protect it from cracking.",
                "Only cut the spot openings after checking the electrical plan and the fixture diameter to the millimetre."
            ),
            steps = listOf(
                GuideStep(
                    1,
                    "Plafond de Base (تركيب السقف الأساسي)",
                    "Base Ceiling",
                    "تثبيت سقف مستوٍ عادي كقاعدة للديكور",
                    "تركيب فوبلافون عادي كقاعدة أساسية",
                    "Installer d'abord un faux plafond standard plan pour servir de base support.",
                    "First install a standard flat false ceiling to act as the supporting base.",
                    "تركيب سقف مستعار مستوٍ عادي كأرضية وقاعدة لتثبيت الديكورات والهبوط.",
                    "يتم أولاً تركيب سقف مستوٍ عادي كقاعدة للديكور."
                ),
                GuideStep(
                    2,
                    "Structure de Retombée (هيكل الهبوط والكرتوش)",
                    "Dropped Framing (Retombée)",
                    "بناء هيكل الهبوط بالـ Rails والـ Montants",
                    "بناء هيكل الهبوط بالحديد والمسافة المطلوبة",
                    "Construire l'ossature descendante (10 à 15 cm) avec des rails et profilés pliés.",
                    "Build the dropped framing (10 to 15 cm) with rails and bent profiles along the marked lines.",
                    "تثبيت هيكل حديدي على الخطوط المرسومة ليهبط السقف بالمسافة المطلوبة (10 إلى 15 سم).",
                    "تثبيت حديد على الخطوط المرسومة ليهبط السقف بالمسافة المطلوبة (10 سم أو 15 سم)."
                ),
                GuideStep(
                    3,
                    "Gorge & Lèvre LED (تشكيل شفة ومجرى شريط الليد)",
                    "LED Cove & Lip (Lèvre)",
                    "تغليف الجوانب وترك الشفة الموجهة للأعلى",
                    "تغليف الكرتوش وعمل الرف لشريط الليد",
                    "Habiller les côtés et créer une gorge intérieure ouverte pour accueillir le ruban LED.",
                    "Clad the sides and create an open inner cove (gorge) to receive the LED strip.",
                    "تغليف جوانب الهيكل الهابط بالبلاكو وترك بروز خارجي أو داخلي مفتوح لوضع شريط الإضاءة.",
                    "تغليف جوانب الهيكل وترك شفة مفتوحة ليوضع بداخلها شريط الليد المخفي."
                ),
                GuideStep(
                    4,
                    "Découpe des Spots (قص فتحات مصابيح الليد)",
                    "Cutting the Spot Openings",
                    "عمل ثقوب الـ Spots بريشة قص الدوائر",
                    "قص فتحات السبوتات بالـ Scie cloche",
                    "Percer les trous des spots avec une scie cloche adaptée selon le diamètre de l'appareil.",
                    "Drill the spotlight holes with a hole saw (scie cloche) matching the fixture diameter.",
                    "باستعمال الشنيور وريشة قص الدوائر (Scie cloche)، يتم عمل ثقوب مصابيح الليد بدقة.",
                    "قص فتحات السبوتات بالشنيور وريشة الكلوش حسب مقاس السبوت."
                )
            )
        ),
        GuideSection(
            id = "finition",
            category = "finition",
            titleFr = "5. Jointoyage & Finition (معالجة الفواصل والحك)",
            titleEn = "5. Jointing & Finishing (Jointoyage & Finition)",
            titleAr = "5. مرحلة الفواصل والفينيسيون الأخيرة (Jointoyage & Finition)",
            titleDerja = "5. البوراج، تلصيق الشريط، التثنية، والحك بماكينة الجراف",
            toolsNeeded = listOf("مشحاف Spatule 10-15cm", "سكين معجون Couteau 20-30cm", "ماكينة حك دائرية Girafe", "ورق صنفرة Papier verre 120/150"),
            toolsNeededEn = listOf("Spatula 10–15 cm", "Joint knife 20–30 cm", "Giraffe sander", "Sandpaper 120/150"),
            proTips = listOf(
                "أهم خطوة هي الضغط على الشريط الورقي بمشحاف عريض لإخراج فقاعات الهواء والمعجون الزائد تماماً.",
                "لا تقم بالصنفرة والحك إلا بعد جفاف المعجون تماماً (24 ساعة) واستعمل كشاف ضوء جانبي لكشف أي نتوء.",
                "ضع طبقة فيكساتور (Fixateur) بعد الحك مباشرة لربط الغبار قبل تطبيق الدهان النهائي."
            ),
            proTipsEn = listOf(
                "The key step: press the paper tape firmly with a wide spatula to expel all air bubbles and excess compound.",
                "Only sand once the compound is fully dry (24 hours) and use a side light to reveal any bump.",
                "Apply a fixer (Fixateur) coat right after sanding to bind the dust before the final paint."
            ),
            steps = listOf(
                GuideStep(
                    1,
                    "Le Bourrage (الملء الأول للفراغات)",
                    "First Fill (Bourrage)",
                    "ملء الفواصل ورؤوس البراغي بالمعجون",
                    "البوراج وملء الفواصل ورؤوس الفيسات بالأندوي",
                    "Garnir généreusement les creux entre plaques et les têtes de vis avec l'enduit à joint.",
                    "Generously fill the joints between boards and the screw heads with jointing compound using a 10 cm knife.",
                    "ملء الفراغات بين الألواح وتغطية رؤوس البراغي بالكامل باستعمال مشحاف صغير (10 سم).",
                    "يخلط معجون الفواصل بالماء ويملأ الفراغ بين الألواح ورؤوس البراغي بالمشحاف."
                ),
                GuideStep(
                    2,
                    "Collage de la Bande (تلصيق الشريط الورقي)",
                    "Taping the Paper Tape",
                    "وضع شريط الفواصل وضغطه بالسكين",
                    "تلصيق البوند اجوان وإخراج الهواء",
                    "Appliquer la bande à joint papier sur l'enduit frais et serrer avec le couteau pour chasser l'air.",
                    "Embed the paper joint tape into the fresh compound and press it firmly with a wide knife to expel the air.",
                    "وضع شريط الفواصل الورقي فوق المعجون الرطب وتمرير مشحاف عريض بضغط متوازن لإخراج الهواء.",
                    "يوضع شريط الفواصل الورقي فوق المعجون الرطب ويمرر مشحاف عريض لإخراج الهواء والمعجون الزائد."
                ),
                GuideStep(
                    3,
                    "Chargement (الطبقة الثانية والتثنية)",
                    "Second Coat (Chargement)",
                    "تطبيق الطبقة الثانية العريضة للتمويه",
                    "التثنية والطبقة الثانية لإخفاء حواف الشريط",
                    "Après séchage, appliquer une seconde couche large pour noyer complètement la bande.",
                    "Once dry, apply a wide second coat to bury the tape completely.",
                    "بعد جفاف الطبقة الأولى، وضع طبقة ثانية عريضة من المعجون فوق الشريط للتمويه ودمجه مع اللوح.",
                    "بعد الجفاف توضع طبقة ثانية عريضة من المعجون فوق الشريط لإخفائه تماماً."
                ),
                GuideStep(
                    4,
                    "Ponçage & Dépoussiérage (الحك والفينيسيون)",
                    "Sanding & Dusting",
                    "الصنفرة والحك بماكينة الجراف أو ورق الزجاج",
                    "الحك بماكينة الجراف أو ورق 150 حتى يصبح كالحرير",
                    "Poncer au papier verre 120-150 ou à la ponceuse girafe pour obtenir une surface ultra-lisse prête à peindre.",
                    "Sand with 120–150 grit paper or a giraffe sander until the surface is ultra-smooth and ready to paint.",
                    "استعمال ورق الصنفرة (120/150) أو ماكينة الحك الدائرية (Giraffe) حتى يصبح السطح أملس كالحرير وجاهزاً للدهان.",
                    "يحك السطح بورق الصنفرة أو ماكينة الجراف حتى يختفي أي أثر للمعجون ويصبح ناعم وجاهز للدهينة."
                )
            )
        ),
        GuideSection(
            id = "autres_metiers",
            category = "autres_metiers",
            titleFr = "6. Guide des Autres Métiers BTP (الدهان، البناء، السيراميك، النجارة)",
            titleEn = "6. Other BTP Trades (Painting, Masonry, Tiling)",
            titleAr = "6. دليل المهن الكبرى (الدهان، النجارة، السيراميك، والبناء)",
            titleDerja = "6. سير الأشغال لبقية المهن (الدهينة، بنيان الآجور، الفايونس، والشبابك)",
            toolsNeeded = listOf("رولو دهان Rouleau", "قيد ألمنيوم Règle", "ميزان ماء Niveau à bulle", "ماكينة قص سيراميك Coupe-carreaux"),
            toolsNeededEn = listOf("Paint roller (Rouleau)", "Aluminium straight edge (Règle)", "Spirit level (Niveau à bulle)", "Tile cutter (Coupe-carreaux)"),
            proTips = listOf(
                "في السيراميك، أضف دائماً 10% هامش أمان لحساب القص والزوايا (Chutes).",
                "في بناء الجدران، يحتاج المتر المربع دائماً 22 آجرّة تونسية (Brique 8 أو 12).",
                "في الدهان، يستهلك المتر المربع طبقتين حوالي 1.2 كغ من معجون الأندوي و 0.25 لتر من الدهان النهائي."
            ),
            proTipsEn = listOf(
                "For tiling, always add a 10% safety margin for cuts and corners (chutes).",
                "For wall building, one square metre always needs 22 Tunisian bricks (Brique 8 or 12).",
                "For painting, one square metre consumes about 1.2 kg of skim-coat filler and 0.25 L of finish paint over two coats."
            ),
            steps = listOf(
                GuideStep(
                    1,
                    "Peinture (الدهان والديكور)",
                    "Painting (Peinture)",
                    "خطوات الدهان: حك، فيكساتور، أندوي، ثم دهان نهائي",
                    "سير أشغال الدهينة: تنظيف، فيكساتور، 2 كوش أندوي، وسيتان",
                    "Grattage, application du fixateur primaire, 2 couches d'enduit de lissage, ponçage fin, puis 2 couches de peinture satinée ou sablé.",
                    "Scrape off loose material, apply the primary fixer/primer, 2 coats of skim-coat filler, fine sanding, then 2 coats of satin or sandy-texture paint.",
                    "تحضير الحائط، دهن طبقة الفيكساتور، تطبيق طبقتين من معجون التسوية، الصنفرة، ثم طبقتين من الدهان النهائي.",
                    "حك الجدران، ضرب الفيكساتور لربط الغبار، عمل 2 كوش أندوي، حك ناعم، ثم طبقتين دهينة سيتان أو سابلي."
                ),
                GuideStep(
                    2,
                    "Maçonnerie (بناء الآجر)",
                    "Brickwork (Maçonnerie)",
                    "تخطيط الغرف وبناء الآجر بالملاط الإسمنتي",
                    "تخطيط وبنيان الآجور 12 أو 8 عين بالملاط والميزان",
                    "Implantation, montage des briques au mortier de ciment et sable lavé en vérifiant l'aplomb au fil à plomb.",
                    "Set out the room, lay the bricks with cement + washed sand mortar, checking plumb with a plumb line.",
                    "تخطيط قياسات الغرف، بناء الآجر باستعمال خلطة الإسمنت والرمل، مع مراعاة شاقولية الحائط بميزان الخيط.",
                    "تخطيط القياسات، بنيان الياجور بخلطة السيمون والرملة، مع ضبط استواء الحائط بميزان الخيط."
                ),
                GuideStep(
                    3,
                    "Revêtement & Carrelage (السيراميك والأرضيات)",
                    "Screed & Tiling (Carrelage)",
                    "صب الشاب ولصق السيراميك بالغراء والميزان",
                    "صب الشاب ولصق الفايونس بالكول والميزان وملء الجوان",
                    "Coulage de la chape de nivellement, encollage des carreaux avec ciment colle, puis jointoiement soigné.",
                    "Pour the levelling screed (chape), fix the tiles with tile adhesive (ciment colle), then grout the joints carefully.",
                    "صب طبقة التسوية (Chape)، لصق السيراميك بالإسمنت اللاصق مع ضبط الميزان، ثم ملء الفواصل بمعجون الجوان.",
                    "صب الشاب، تلصيق الفايونس بكول السيراميك والميزان، وتسكير الفواصل بمعجون الجوان الملون."
                )
            )
        )
    )
}
