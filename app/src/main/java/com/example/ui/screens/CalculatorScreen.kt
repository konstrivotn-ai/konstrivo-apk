package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.components.designsystem.KonstrivoCard
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSectionLabel
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.*
import com.example.domain.material.MaterialCatalog
import com.example.domain.model.MaterialCategory
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val language by viewModel.selectedLanguage.collectAsState()
    val country by viewModel.selectedCountry.collectAsState()
    val selectedTrade by viewModel.selectedTrade.collectAsState()
    val selectedType by viewModel.selectedCalculationType.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    val area by viewModel.inputArea.collectAsState()
    val length by viewModel.inputLength.collectAsState()
    val width by viewModel.inputWidth.collectAsState()
    val height by viewModel.inputHeight.collectAsState()
    val linearMeters by viewModel.inputLinearMeters.collectAsState()
    val unitsCount by viewModel.inputUnitsCount.collectAsState()
    val wasteMargin by viewModel.wasteMargin.collectAsState()
    val boardType by viewModel.selectedBoardType.collectAsState()
    val selectedMontantType by viewModel.selectedMontantType.collectAsState()
    val selectedRailType by viewModel.selectedRailType.collectAsState()
    val isDoubleSkin by viewModel.isDoubleSkin.collectAsState()
    val includeInsulation by viewModel.includeInsulation.collectAsState()
    val insulationType by viewModel.insulationType.collectAsState()
    val customLabor by viewModel.customLaborRate.collectAsState()
    val inputJointsMeters by viewModel.inputJointsMeters.collectAsState()
    val inputCornersCount by viewModel.inputCornersCount.collectAsState()
    val finishQuality by viewModel.finishQuality.collectAsState()
    val trappeLines by viewModel.trappeLines.collectAsState()

    val summary by viewModel.calculationSummary.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var showInvoicePreview by remember { mutableStateOf(false) }
    var useDimensionsMode by remember { mutableStateOf(true) }

    // Entraxe des montants is owned by CalculatorViewModel (source of truth).
    // Convert meters -> cm for display only: 0.40 -> 40 cm, 0.60 -> 60 cm.
    val entraxeMontants by viewModel.entraxeMontants.collectAsState()
    val entraxeSpacingCm = kotlin.math.round(entraxeMontants * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
        // Professional calculator heading: establishes hierarchy before the workflow controls.
        // Presentation-only; calculation state and formulas remain owned by the existing ViewModel.
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .testTag("calculator_header"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = KonstrivoNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, KonstrivoGold.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = KonstrivoGold.copy(alpha = 0.16f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = KonstrivoGold,
                                modifier = Modifier.size(29.dp)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = language.tr(
                                "Calculateur de chantier",
                                "Construction calculator",
                                "حاسبة الأشغال",
                                "حاسبة الشانطي"
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = language.tr(
                                "Choisissez le métier, saisissez les mesures et vérifiez le métré.",
                                "Choose a trade, enter measurements, then review your estimate.",
                                "اختار الخدمة، دخّل المقاسات وبعد راجع حساب الكميات.",
                                "اختار الصنعة، دخّل القياسات وبعد راجع المترية."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.82f)
                        )
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = KonstrivoGold.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = selectedTrade.name.replace('_', ' '),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = KonstrivoGold
                            )
                        }
                    }
                }
            }
        }

        // === SPEC #17 — Visual step progress bar (engineering estimation pipeline) ===
        // Purely presentational; derives the active step from live state only. Never mutates
        // calculation logic, callbacks, or existing testTags.
        item {
            val inputReady = (length.toDoubleOrNull() ?: 0.0) > 0 ||
                (width.toDoubleOrNull() ?: 0.0) > 0 ||
                (area.toDoubleOrNull() ?: 0.0) > 0 ||
                (linearMeters.toDoubleOrNull() ?: 0.0) > 0 ||
                (unitsCount.toDoubleOrNull() ?: 0.0) > 0
            CalculatorStepIndicator(
                currentStep = if (summary != null) 4 else if (inputReady) 3 else 2,
                language = language,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        // Reference "Selection calculator" grid — shortcuts into the SAME engine.
        // No new calculation path: each tile selects existing trade/type.
        item {
            ReferenceCalculatorGrid(
                language = language,
                onSelect = { trade, type -> viewModel.navigateToCalculator(trade, type) },
            )
        }
        // Trade Category Selector
        item {
            TradeSelectorBar(
                selectedTrade = selectedTrade,
                onTradeSelected = { viewModel.setTrade(it) },
                language = language
            )
        }

        // Sub-Type Chips for the selected trade
        item {
            val availableTypes = CalculationType.entries.filter { it.trade == selectedTrade }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(availableTypes) { type ->
                    val isSelected = type == selectedType
                    val title = when (language) {
                        AppLanguage.DERJA -> type.nameDerja
                        AppLanguage.ARABIC -> type.nameAr
                        AppLanguage.FRENCH -> type.nameFr
                        AppLanguage.ENGLISH -> type.nameFr
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCalculationType(type) },
                        label = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KonstrivoGold,
                            selectedLabelColor = KonstrivoNavy
                        ),
                        modifier = Modifier.testTag("calc_type_${type.id}")
                    )
                }
            }
        }

        // Room Presets Row (Fast 1-tap dimension loader)
        if (selectedType != CalculationType.MENUISERIE_PORTES && selectedType != CalculationType.MENUISERIE_ALU && selectedType != CalculationType.PLACO_CACHE_LUMIERE) {
            item {
                RoomPresetsRow(
                    onPresetSelected = { l, w ->
                        viewModel.setInputLength(l.toString())
                        viewModel.setInputWidth(w.toString())
                        useDimensionsMode = true
                    },
                    language = language
                )
            }
        }

        // Architectural Blueprint Visualizer Schematic
        val areaDouble = area.toDoubleOrNull() ?: 0.0
        val lengthDouble = length.toDoubleOrNull() ?: 0.0
        val widthDouble = width.toDoubleOrNull() ?: 0.0

        if (areaDouble > 0 && selectedType != CalculationType.MENUISERIE_PORTES && selectedType != CalculationType.MENUISERIE_ALU) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ArchitecturalSchematicVisualizer(
                        length = lengthDouble,
                        width = widthDouble,
                        area = areaDouble,
                        type = selectedType,
                        language = language,
                        spacingCm = entraxeSpacingCm
                    )
                }
            }
        }

        // Main Input Parameters Card
        item {
            KonstrivoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.SquareFoot,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = language.tr("Dimensions & Mesures","Dimensions & Mesures","المقاسات والأبعاد بالشانتي","المقاسات والأبعاد بالشانتي"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (selectedType != CalculationType.MENUISERIE_PORTES && selectedType != CalculationType.MENUISERIE_ALU && selectedType != CalculationType.PLACO_CACHE_LUMIERE) {
                            // Toggle between Length x Width or Direct Area
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = language.tr("L × l (m)","L × l (m)","طول×عرض","طول×عرض"),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (useDimensionsMode) FontWeight.Bold else FontWeight.Normal
                                )
                                Switch(
                                    checked = useDimensionsMode,
                                    onCheckedChange = { useDimensionsMode = it },
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                                        }

                    // === SPEC #4 — Computed Surface & Périmètre (read-only metrics) ===
                    // Presentation-only: mirrors CalculatorViewModel's engine formula exactly so the
                    // displayed values always match the engine (ViewModel/CalculationEngine untouched):
                    //   length>0 & width>0 -> surface = l*w , périmètre = 2(l+w)
                    //   area>0            -> surface = area, périmètre = 4*sqrt(area)
                    val ld = length.toDoubleOrNull() ?: 0.0
                    val wd = width.toDoubleOrNull() ?: 0.0
                    val ad = area.toDoubleOrNull() ?: .0
                    val computedSurface = if (ld > 0 && wd > 0) ld * wd else if (ad > 0) ad else 0.0
                    val computedPerimeter = when {
                        ld > 0 && wd > 0 -> 2 * (ld + wd)
                        ad > 0 -> 4 * kotlin.math.sqrt(ad)
                        else -> 0.0
                    }
                    if (computedSurface > 0) {
                        val areaUnit = language.tr(fr = "m²", en = "m²", ar = "م²", dz = "م²")
                        val lengthUnit = language.tr(fr = "m", en = "m", ar = "م", dz = "م")
                        Card(
                            shape = RoundedCornerShape(KonstrivoRadius.Card),
                            colors = CardDefaults.cardColors(containerColor = KonstrivoGold.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KonstrivoGold.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = language.tr("Surface & Périmètre","Surface & Périmètre","المساحة والمحيط","المساحة والمحيط"),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = KonstrivoGold
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.testTag("surface_value")
                                    ) {
                                        Text(
                                            text = String.format(java.util.Locale.US, "%.2f", computedSurface),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = " $areaUnit",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.testTag("perimeter_value")
                                    ) {
                                        Text(
                                            text = String.format(java.util.Locale.US, "%.2f", computedPerimeter),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = " $lengthUnit",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (selectedType == CalculationType.MENUISERIE_PORTES || selectedType == CalculationType.MENUISERIE_ALU) {
                        // Count inputs
                        OutlinedTextField(
                            value = unitsCount,
                            onValueChange = { viewModel.setInputUnitsCount(it) },
                            label = { Text(if (selectedType == CalculationType.MENUISERIE_PORTES) "Nombre de portes (عدد الأبواب)" else "Nombre de fenêtres (عدد النوافذ)") },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_units_count")
                        )
                    } else if (selectedType == CalculationType.PLACO_CACHE_LUMIERE) {
                        OutlinedTextField(
                            value = linearMeters,
                            onValueChange = { viewModel.setInputLinearMeters(it) },
                            label = { Text(language.tr("Mètres Linéaires (ml de caisson)","Mètres Linéaires (ml de caisson)","الأمتار الخطية (متر كرتوش)","الأمتار الخطية (متر كرتوش)")) },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().testTag("input_linear_meters")
                        )
                    } else {
                        if (useDimensionsMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = length,
                                    onValueChange = { viewModel.setInputLength(it) },
                                    label = { Text(language.tr("Longueur (m)","Longueur (m)","الطول (م)","الطول (م)")) },
                                    shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f).testTag("input_length")
                                )
                                OutlinedTextField(
                                    value = width,
                                    onValueChange = { viewModel.setInputWidth(it) },
                                    label = { Text(language.tr("Largeur (m)","Largeur (m)","العرض (م)","العرض (م)")) },
                                    shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f).testTag("input_width")
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = area,
                                onValueChange = { viewModel.setInputArea(it) },
                                label = { Text(language.tr("Surface Totale (m²)","Surface Totale (m²)","المساحة الإجمالية (م²)","المساحة الإجمالية (م²)")) },
                                shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth().testTag("input_area")
                            )
                        }

                        if (selectedType == CalculationType.PLACO_CLOISON || selectedType == CalculationType.MACONNERIE_BRIQUE) {
                            OutlinedTextField(
                                value = height,
                                onValueChange = { viewModel.setInputHeight(it) },
                                label = { Text(language.tr("Hauteur sous plafond (m)","Hauteur sous plafond (m)","ارتفاع السقف (متر)","ارتفاع السقف (متر)")) },
                                shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth().testTag("input_height")
                            )
                        }
                    }

                    // STEP 9B — Démontable Tile Selector (source of truth: MaterialCatalog).
                    // Shown only for PLACO_DEMONTABLE. Reuses the existing selectedBoardType
                    // state end-to-end; the engine keeps its own fallback mapping, so the
                    // default calculated behaviour (Vinyl) stays untouched.
                    if (selectedTrade == TradeCategory.PLACO && selectedType == CalculationType.PLACO_DEMONTABLE) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            KonstrivoSectionLabel(
                                text = language.tr("MATÉRIAUX & SYSTÈME","MATÉRIAUX & SYSTÈME","المواد والنظام","المواد والنظام")
                            )
                        }

                        Text(
                            text = language.tr("Type de Dalle","Type de Dalle","نوع البلاطة","نوع البلاطة"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        val dalleOptions = MaterialCatalog.ALL_MATERIALS.filter {
                            it.category == MaterialCategory.DALLES && it.id.startsWith("dalle_")
                        }
                        val dalleItems = dalleOptions.map { mat ->
                            val matName = when (language) {
                                AppLanguage.DERJA -> mat.nameDerja
                                AppLanguage.ARABIC -> mat.nameAr
                                AppLanguage.FRENCH -> mat.nameFr
                                AppLanguage.ENGLISH -> mat.nameEn
                            }
                            mat.id to matName
                        }
                        // Highlight mirror: keep the user's choice when it is a catalog
                        // tile; otherwise highlight the first catalog tile, mirroring the
                        // engine's calculated default (Vinyl) without hardcoding any id.
                        val selectedDalleKey = dalleItems.firstOrNull { it.first == boardType }?.first
                            ?: dalleItems.firstOrNull()?.first.orEmpty()

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth().testTag("dalle_type_selector")
                        ) {
                            items(dalleItems) { (key, label) ->
                                FilterChip(
                                    selected = selectedDalleKey == key,
                                    onClick = { viewModel.setSelectedBoardType(key) },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }

                    // STEP 9C1 — Joints & Finition inputs (PLACO_JOINTS_FINITION only).
                    // Feeds the existing CalculationParams.jointsMeters / cornersCount /
                    // finishQuality consumed by the existing minimal engine handler.
                    if (selectedTrade == TradeCategory.PLACO && selectedType == CalculationType.PLACO_JOINTS_FINITION) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            KonstrivoSectionLabel(
                                text = language.tr("JOINTS & FINITION","JOINTS & FINITION","الفواصل والتشطيب","الفواصل والتشطيب")
                            )
                        }

                        OutlinedTextField(
                            value = inputJointsMeters,
                            onValueChange = { viewModel.setInputJointsMeters(it) },
                            label = { Text(language.tr("Joints (mètres linéaires)","Joints (mètres linéaires)","أمتار الفواصل (متر خطي)","أمتار الفواصل (متر خطي)")) },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().testTag("input_joints_meters")
                        )

                        OutlinedTextField(
                            value = inputCornersCount,
                            onValueChange = { viewModel.setInputCornersCount(it) },
                            label = { Text(language.tr("Coins / Angles (nombre)","Coins / Angles (nombre)","عدد الزوايا","عدد الزوايا")) },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_corners_count")
                        )

                        Text(
                            text = language.tr("Qualité de finition","Qualité de finition","جودة التشطيب","جودة التشطيب"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = finishQuality == 1,
                                onClick = { viewModel.setFinishQuality(1) },
                                label = { Text(language.tr("Standard","Standard","عادية","عادية")) }
                            )
                            FilterChip(
                                selected = finishQuality == 2,
                                onClick = { viewModel.setFinishQuality(2) },
                                label = { Text(language.tr("Renforcée","Renforcée","معززة","معززة")) }
                            )
                        }

                        Text(
                            text = language.tr(
                                fr = "Entrez le métrage total des joints pour calculer les enduits et bandes.",
                                en = "Enter the total joint length to compute compounds and tapes.",
                                ar = "أدخل الطول الإجمالي للفواصل لحساب المعجون وأشرطة اللصق.",
                                dz = "أدخل الطول الإجمالي للفواصل لحساب المعجون وأشرطة اللصق."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Placo Options
                    if (selectedTrade == TradeCategory.PLACO && selectedType != CalculationType.PLACO_PVC && selectedType != CalculationType.PLACO_DEMONTABLE && selectedType != CalculationType.PLACO_JOINTS_FINITION) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Section group: materials & construction system
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            KonstrivoSectionLabel(
                                text = language.tr("MATÉRIAUX & SYSTÈME","MATÉRIAUX & SYSTÈME","المواد والنظام","المواد والنظام")
                            )
                        }
                        
                        // Board Type Selector
                        Text(
                            text = language.tr("Type de Plaque BA13","Type de Plaque BA13","نوع لوح البلاكو المستخدم","نوع لوح البلاكو المستخدم"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        val boardMaterials = MaterialCatalog.ALL_MATERIALS.filter { it.category == MaterialCategory.PLACO }

                        val boardOptions = boardMaterials.map { mat ->
                            val matName = when (language) {
                                AppLanguage.DERJA -> mat.nameDerja
                                AppLanguage.ARABIC -> mat.nameAr
                                AppLanguage.FRENCH -> mat.nameFr
                                AppLanguage.ENGLISH -> mat.nameEn
                            }

                            val dims = if (mat.stockWidthM != null && mat.stockLengthM != null) {
                                "${(mat.stockWidthM * 100).toInt()} × ${(mat.stockLengthM * 100).toInt()} cm"
                            } else {
                                mat.technicalSpec?.takeIf { it.isNotBlank() } ?: "Dimensions unavailable"
                            }

                            mat.id to "$matName • $dims"
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(boardOptions) { (key, label) ->
                                val isSel = boardType == key
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setSelectedBoardType(key) },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Text(
                            text = language.tr("Type de Montant","Type de Montant","نوع المونتون","نوع المونتون"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        val montantOptions = MaterialCatalog.ALL_MATERIALS.filter {
                            it.category == MaterialCategory.OSSATURE && it.id.startsWith("montant_")
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(montantOptions) { mat ->
                                val label = when (language) {
                                    AppLanguage.DERJA -> mat.nameDerja
                                    AppLanguage.ARABIC -> mat.nameAr
                                    AppLanguage.FRENCH -> mat.nameFr
                                    AppLanguage.ENGLISH -> mat.nameEn
                                }
                                val shortCode = mat.id.removePrefix("montant_").uppercase()
                                val isSel = selectedMontantType == mat.id
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setSelectedMontantType(mat.id) },
                                    label = {
                                        Text(
                                            text = "[$shortCode] ${label}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }

                        Text(
                            text = language.tr("Type de Rail","Type de Rail","نوع الريل","نوع الريل"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        val railMaterials = MaterialCatalog.ALL_MATERIALS.filter {
                            it.category == MaterialCategory.OSSATURE && it.id.startsWith("rail_")
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(railMaterials) { mat ->
                                val label = when (language) {
                                    AppLanguage.DERJA -> mat.nameDerja
                                    AppLanguage.ARABIC -> mat.nameAr
                                    AppLanguage.FRENCH -> mat.nameFr
                                    AppLanguage.ENGLISH -> mat.nameEn
                                }
                                val shortCode = mat.id.removePrefix("rail_").uppercase()
                                val isSel = selectedRailType == mat.id
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setSelectedRailType(mat.id) },
                                    label = {
                                        Text(
                                            text = "[$shortCode] ${label}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }

                        // Stud Spacing / Entraxe des montants (Phase C wiring: ViewModel = source of truth)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = language.tr("Entraxe des montants","Entraxe des montants","تباعد القوائم والسكك","تباعد القوائم والسكك"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = entraxeSpacingCm == 40,
                                    onClick = { viewModel.setEntraxeMontantsMeters(0.40) },
                                    label = { Text("40 cm") }
                                )
                                FilterChip(
                                    selected = entraxeSpacingCm == 60,
                                    onClick = { viewModel.setEntraxeMontantsMeters(0.60) },
                                    label = { Text("60 cm") }
                                )
                            }
                        }

                        // STEP 9C2 — Trappes de visite (optional add-on, PLAFOND_FIXE).
                        // Options sourced dynamically from MaterialCatalog (trappe_*).
                        if (selectedType == CalculationType.PLACO_PLAFOND_FIXE) {
                            val trappeOptions = MaterialCatalog.ALL_MATERIALS.filter { it.id.startsWith("trappe_") }
                            var selectedTrappeId by remember(trappeOptions) {
                                mutableStateOf(trappeOptions.firstOrNull()?.id.orEmpty())
                            }
                            var trappeQtyText by remember { mutableStateOf("") }

                            Text(
                                text = language.tr("Trappes de visite (optionnel)","Trappes de visite (optionnel)","أبواب التفتيش (اختياري)","أبواب التفتيش (اختياري)"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().testTag("trappe_dimension_selector")
                            ) {
                                items(trappeOptions) { mat ->
                                    val dimLabel = mat.id.removePrefix("trappe_")
                                    FilterChip(
                                        selected = selectedTrappeId == mat.id,
                                        onClick = { selectedTrappeId = mat.id },
                                        label = { Text(dimLabel, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = trappeQtyText,
                                    onValueChange = { trappeQtyText = it.filter { ch -> ch.isDigit() } },
                                    label = { Text(language.tr("Quantité","Quantité","الكمية","الكمية")) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(140.dp).testTag("input_trappe_quantity")
                                )
                                Button(
                                    modifier = Modifier.testTag("add_trappe_button").heightIn(min = 48.dp),
                                    onClick = {
                                        val qty = trappeQtyText.toIntOrNull() ?: 0
                                        if (selectedTrappeId.isNotEmpty() && qty > 0) {
                                            viewModel.setTrappeQuantity(selectedTrappeId.removePrefix("trappe_"), qty)
                                            trappeQtyText = ""
                                        }
                                    },
                                    enabled = selectedTrappeId.isNotEmpty() && (trappeQtyText.toIntOrNull() ?: 0) > 0,
                                    shape = RoundedCornerShape(KonstrivoRadius.Button)
                                ) {
                                    Text(language.tr("Ajouter","Ajouter","إضافة","إضافة"))
                                }
                            }
                            if (trappeLines.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    trappeLines.forEach { line ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("trappe_line_${line.dimensionKey}")
                                        ) {
                                            Text(
                                                text = "• ${line.dimensionKey} cm × ${line.quantity}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(onClick = { viewModel.setTrappeQuantity(line.dimensionKey, 0) }) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = language.tr(
                                                        "Retirer la trappe",
                                                        "Remove trapdoor",
                                                        "إزالة فتحة التفتيش",
                                                        "أخرج باب التفتيش"
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // STEP 9D — Brick type selector (MACONNERIE_BRIQUE only).
                        // Options sourced dynamically from MaterialCatalog (brique_*).
                        // Writes through the existing selectedBoardType state consumed
                        // by the engine's brick branch (boardType != brique_8 => 12).
                        if (selectedTrade == TradeCategory.MACONNERIE && selectedType == CalculationType.MACONNERIE_BRIQUE) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                KonstrivoSectionLabel(
                                    text = language.tr("MATÉRIAUX","MATÉRIAUX","المواد","المواد")
                                )
                            }

                            Text(
                                text = language.tr("Type de Brique","Type de Brique","نوع الآجر","نوع الآجر"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            val briqueOptions = MaterialCatalog.ALL_MATERIALS.filter {
                                it.category == MaterialCategory.MACONNERIE && it.id.startsWith("brique_")
                            }
                            val briqueItems = briqueOptions.map { mat ->
                                val count = mat.id.removePrefix("brique_").removeSuffix("_trous")
                                mat.id to language.tr("$count trous","$count trous","$count ثقب","$count ثقب")
                            }
                            // Highlight mirror of the engine rule: keep the user's choice
                            // when it is a catalog brick; otherwise highlight the first
                            // catalog brick (= engine's calculated default) w/o hardcoding.
                            val selectedBriqueKey = briqueItems.firstOrNull { it.first == boardType }?.first
                                ?: briqueItems.firstOrNull()?.first.orEmpty()

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().testTag("brique_type_selector")
                            ) {
                                items(briqueItems) { (key, label) ->
                                    FilterChip(
                                        selected = selectedBriqueKey == key,
                                        onClick = { viewModel.setSelectedBoardType(key) },
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = "0.0",
                                onValueChange = { newVal ->
                                    // Note: ensure CalculatorViewModel exposes a setter `setNetOpeningsArea`
                                    // to persist this value into CalculationParams. Placeholder here.
                                    try {
                                        viewModel::class.java.getMethod("setNetOpeningsArea", String::class.java)
                                            .invoke(viewModel, newVal)
                                    } catch (e: Exception) {
                                        // best-effort: ignore if setter not present yet
                                    }
                                },
                                label = { Text(language.tr("Surface Ouvertures (m²)", "Openings Area (m²)", "مساحة الفتحات (م²)", "مساحة الفتحات (م²)")) },
                                shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth().testTag("input_net_openings_area")
                            )
                        }

                        if (selectedType == CalculationType.PLACO_CLOISON) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = language.tr("Double Peau (4 Plaques)","Double Peau (4 Plaques)","تغليف دوبل (4 صفائح للصلابة)","تغليف دوبل (4 صفائح للصلابة)"),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Switch(
                                    checked = isDoubleSkin,
                                    onCheckedChange = { viewModel.setIsDoubleSkin(it) }
                                )
                            }
                        }

                        // Insulation option
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = language.tr("Isolation Thermique / Phonique","Isolation Thermique / Phonique","إضافة الصوف العازل (Laine)","إضافة الصوف العازل (Laine)"),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Switch(
                                checked = includeInsulation,
                                onCheckedChange = { viewModel.setIncludeInsulation(it) }
                            )
                        }

                        if (includeInsulation) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilterChip(
                                    selected = insulationType == "laine_de_verre_50mm",
                                    onClick = { viewModel.setInsulationType("laine_de_verre_50mm") },
                                    label = { Text(language.tr("Laine de Verre 50mm","Laine de Verre 50mm","صوف زجاجي (Laine verre)","صوف زجاجي (Laine verre)")) }
                                )
                                FilterChip(
                                    selected = insulationType == "laine_de_roche_50mm",
                                    onClick = { viewModel.setInsulationType("laine_de_roche_50mm") },
                                    label = { Text(language.tr("Laine de Roche 50mm","Laine de Roche 50mm","صوف صخري (Laine roche)","صوف صخري (Laine roche)")) }
                                )
                            }
                        }
                    }

                    // Peinture Options & Layer Spec Info
                    if (selectedTrade == TradeCategory.PEINTURE) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PeintureTradeColor.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PeintureTradeColor.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatPaint,
                                        contentDescription = null,
                                        tint = PeintureTradeColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (selectedType == CalculationType.PEINTURE_INTERIEURE) {
                                            language.tr("Spécification Technique: 3 Étapes (2 Enduit + 1 Fixateur + 2 Finition)","Spécification Technique: 3 Étapes (2 Enduit + 1 Fixateur + 2 Finition)","المواصفات الفنية: 3 مراحل (2 أندوي + 1 فيكساتور + 2 دهان ساتيني)","المواصفات الفنية: 3 مراحل (2 أندوي + 1 فيكساتور + 2 دهان ساتيني)")
                                        } else {
                                            language.tr("Spécification Technique: Décor Sablé (1 Fixateur + Finition Sablé)","Spécification Technique: Décor Sablé (1 Fixateur + Finition Sablé)","المواصفات الفنية: ديكور سابلي (1 فيكساتور + معجون سابلي رملي)","المواصفات الفنية: ديكور سابلي (1 فيكساتور + معجون سابلي رملي)")
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (selectedType == CalculationType.PEINTURE_INTERIEURE) {
                                        language.tr("Rendements BTP: Enduit ~1.2 kg/m² (Sacs 25kg) • Fixateur ~0.15 L/m² (Bidon 20L) • Peinture ~0.25 L/m² (Bidon 15L)","Rendements BTP: Enduit ~1.2 kg/m² (Sacs 25kg) • Fixateur ~0.15 L/m² (Bidon 20L) • Peinture ~0.25 L/m² (Bidon 15L)","المعدلات القياسية: أندوي 1.2 كغ/م² (شكاير 25كغ) • فيكساتور 0.15 لتر/م² (بيدون 20ل) • دهان 0.25 لتر/م² (سطال 15ل)","المعدلات القياسية: أندوي 1.2 كغ/م² (شكاير 25كغ) • فيكساتور 0.15 لتر/م² (بيدون 20ل) • دهان 0.25 لتر/م² (سطال 15ل)")
                                    } else {
                                        language.tr("Rendements BTP: Fixateur primaire ~0.15 L/m² • Peinture décorative sablée ~0.25 kg/m² (Seaux 5kg)","Rendements BTP: Fixateur primaire ~0.15 L/m² • Peinture décorative sablée ~0.25 kg/m² (Seaux 5kg)","المعدلات القياسية: فيكساتور أساسي 0.15 لتر/م² • معجون سابلي ديكوري 0.25 كغ/م² (سطال 5كغ)","المعدلات القياسية: فيكساتور أساسي 0.15 لتر/م² • معجون سابلي ديكوري 0.25 كغ/م² (سطال 5كغ)")
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Margins and Labor
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Section group: margins & labor rate
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        KonstrivoSectionLabel(
                            text = language.tr("MARGES & MAIN-D'ŒUVRE","MARGES & MAIN-D'ŒUVRE","الهدر واليد العاملة","الهدر واليد العاملة")
                        )
                    }

                    // Waste Margin Quick Chips
                    Column {
                        Text(
                            text = language.tr("Marge de chute / Casse","Marge de chute / Casse","نسبة هدر وقص السلعة (Marge de chute)","نسبة هدر وقص السلعة (Marge de chute)"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("0", "5", "10", "15").forEach { marginVal ->
                                val isSel = wasteMargin == marginVal
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setWasteMargin(marginVal) },
                                    label = { Text("+$marginVal%") }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = wasteMargin,
                            onValueChange = { viewModel.setWasteMargin(it) },
                            label = { Text(language.tr("Marge Chutes (%)","Marge Chutes (%)","نسبة الهدر (%)","نسبة الهدر (%)")) },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customLabor,
                            onValueChange = { viewModel.setCustomLaborRate(it) },
                            label = { Text(language.tr("Main d'œuvre/m²","Main d'œuvre/m²","اليد العاملة/م²","اليد العاملة/م²")) },
                            placeholder = { Text("${country.currencySymbol}") },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Result Block — Input parameters card, hero, materials list, fiscal breakdown.
        if (summary != null) {
            val sum = summary!!
            // 1) Input parameters actually used by the calculation (Phase B/C values in sync).
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CalculationParamsCard(
                        boardLabel = if (selectedTrade == TradeCategory.PLACO) {
                            placoBoardLabel(boardType, language)
                        } else {
                            null
                        },
                        montantLabel = if (selectedTrade == TradeCategory.PLACO) {
                            montantDisplayLabel(selectedMontantType, language)
                        } else {
                            null
                        },
                        entraxeCm = if (selectedTrade == TradeCategory.PLACO) {
                            entraxeSpacingCm
                        } else {
                            null
                        },
                        area = sum.inputArea,
                        height = sum.inputHeight,
                        language = language
                    )
                }
            }

            // 2) Summary Hero Card
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SummaryCostHeroCard(
                        summary = sum,
                        language = language,
                        onSaveClick = { showSaveDialog = true },
                        onShareClick = {
                            val text = viewModel.generateWhatsAppText(sum)
                            viewModel.shareWhatsApp(context, text)
                        },
                        onPreviewInvoiceClick = { showInvoicePreview = true }
                    )
                }
            }

            // List Title with Quick Copy Text button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = language.tr("Matériaux Requis (${sum.items.size})","Matériaux Requis (${sum.items.size})","قائمة السلع والقطع بالتفصيل (${sum.items.size})","قائمة السلع والقطع بالتفصيل (${sum.items.size})"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                    TextButton(
                        modifier = Modifier.heightIn(min = 48.dp),
                        onClick = {
                            val text = viewModel.generateWhatsAppText(sum)
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, language.tr("Devis copié dans le presse-papier!","Devis copié dans le presse-papier!","تم نسخ التقرير بنجاح!","تم نسخ التقرير بنجاح!"), Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(language.tr("Copier texte","Copier texte","نسخ النص","نسخ النص"))
                    }
                }
            }

            // List of calculated items
            items(sum.items) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CalculatedItemCard(
                        item = item,
                        language = language,
                        currency = sum.currency
                    )
                }
            }

            // Fiscal breakdown — HT / TVA / TTC récapitulatif (Phase D)
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CalculationBreakdownCard(
                        summary = sum,
                        language = language
                    )
                }
            }
        } else {
            // PHASE 6 — Clear preparation state instead of a silent blank area.
            // Purely presentational: calculation logic and triggers are untouched.
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Card(
                        shape = RoundedCornerShape(KonstrivoRadius.Card),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth().testTag("calc_summary_loading")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = when (language) {
                                        AppLanguage.DERJA -> "جاري تجهيز التقدير..."
                                        AppLanguage.ARABIC -> "جارٍ إعداد التقدير..."
                                        AppLanguage.FRENCH -> "Préparation de l'estimation..."
                                        AppLanguage.ENGLISH -> "Preparing your estimate..."
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (language) {
                                        AppLanguage.DERJA -> "أدخل المقاسات باش تظهر قائمة السلع والأسعار فيسع"
                                        AppLanguage.ARABIC -> "أدخل المقاسات لعرض قائمة المواد والأسعار فورًا"
                                        AppLanguage.FRENCH -> "Saisissez vos dimensions pour afficher instantanément le métré et les prix."
                                        AppLanguage.ENGLISH -> "Enter your dimensions to instantly see quantities & prices."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSaveDialog && summary != null) {
        SaveEstimateDialog(
            language = language,
            onDismiss = { showSaveDialog = false },
            onSave = { client, phone, loc, notes ->
                viewModel.saveEstimate(client, phone, loc, notes) {
                    showSaveDialog = false
                    Toast.makeText(context, language.tr("Devis enregistré avec succès!","Devis enregistré avec succès!","تم حفظ عرض السعر بنجاح!","تم حفظ عرض السعر بنجاح!"), Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (showInvoicePreview && summary != null) {
        DevisInvoicePreviewDialog(
            summary = summary!!,
            companyHeader = userSettings.companyHeader,
            contractorName = userSettings.contractorName,
            contractorPhone = userSettings.contractorPhone,
            language = language,
            onDismiss = { showInvoicePreview = false },
            onShare = {
                val text = viewModel.generateWhatsAppText(summary!!)
                viewModel.shareWhatsApp(context, text)
            }
        )
    }
}
/**
 * Phase D — Resolves the displayed board label for the Input Parameters card.
 * Uses the exact `boardType` value that the calculation consumes.
 */
private fun placoBoardLabel(boardId: String, language: AppLanguage): String {
    val mat = MaterialCatalog.ALL_MATERIALS.firstOrNull { it.id == boardId }
    if (mat == null) return boardId
    return when (language) {
        AppLanguage.DERJA -> mat.nameDerja
        AppLanguage.ARABIC -> mat.nameAr
        AppLanguage.FRENCH -> mat.nameFr
        AppLanguage.ENGLISH -> mat.nameEn
    }
}

/**
 * Resolves the displayed Montant label (e.g. "[M48] Montant M48...") for the
 * Input Parameters card. `null` montantType corresponds to the default montant_48.
 */
private fun montantDisplayLabel(montantType: String?, language: AppLanguage): String {
    val type = montantType ?: "montant_48"
    val short = type.removePrefix("montant_").uppercase()
    val mat = MaterialCatalog.ALL_MATERIALS.firstOrNull { it.id == type }
    if (mat == null) return short
    val name = when (language) {
        AppLanguage.DERJA -> mat.nameDerja
        AppLanguage.ARABIC -> mat.nameAr
        AppLanguage.FRENCH -> mat.nameFr
                AppLanguage.ENGLISH -> mat.nameEn
    }
    return "[$short] $name"
}

/**
 * SPEC #17 — Visual step progress bar for the BTP estimation pipeline:
 *   01 Travaux -> 02 Dimensions -> 03 Calcul -> 04 Résultat.
 * Presentational only.
 */
@Composable
private fun CalculatorStepIndicator(
    currentStep: Int,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        StepInfo(language.tr("Travail","Travail","المعامل","المعامل"), 1),
        StepInfo(language.tr("Dimensions","Dimensions","الأبعاد","الأبعاد"), 2),
        StepInfo(language.tr("Calcul","Calcul","الحساب","الحساب"), 3),
        StepInfo(language.tr("Résultat","Résultat","النتيجة","النتيجة"), 4)
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val active = step.number <= currentStep
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = if (active) KonstrivoGold else KonstrivoSlate.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(50.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.number.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (active) KonstrivoNavy else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = step.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (active) KonstrivoGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(KonstrivoSlate.copy(alpha = 0.25f))
                )
            }
        }
    }
}

private data class StepInfo(val label: String, val number: Int)
