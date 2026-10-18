package com.example.ui.screens

import android.widget.Toast
import android.content.Intent
import android.net.Uri
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
    val openingArea by viewModel.openingArea.collectAsState()
    val wasteMargin by viewModel.wasteMargin.collectAsState()
    val boardType by viewModel.selectedBoardType.collectAsState()
    val boardDimensions by viewModel.boardDimensions.collectAsState()
    val selectedMontantType by viewModel.selectedMontantType.collectAsState()
    val selectedRailType by viewModel.selectedRailType.collectAsState()
    val isDoubleSkin by viewModel.isDoubleSkin.collectAsState()
    val includeInsulation by viewModel.includeInsulation.collectAsState()
    val insulationType by viewModel.insulationType.collectAsState()
    val customLabor by viewModel.customLaborRate.collectAsState()
    val tvaRateOverride by viewModel.tvaRateOverride.collectAsState()
    val fiscalStampEnabled by viewModel.fiscalStampEnabled.collectAsState()
    val retentionRate by viewModel.retentionRate.collectAsState()
    val inputJointsMeters by viewModel.inputJointsMeters.collectAsState()
    val inputCornersCount by viewModel.inputCornersCount.collectAsState()
    val finishQuality by viewModel.finishQuality.collectAsState()
    val trappeLines by viewModel.trappeLines.collectAsState()

    val summary by viewModel.calculationSummary.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var showInvoicePreview by remember { mutableStateOf(false) }
    var useDimensionsMode by remember { mutableStateOf(true) }

    val entraxeMontants by viewModel.entraxeMontants.collectAsState()
    val entraxeSpacingCm = kotlin.math.round(entraxeMontants * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // رأس الحاسبة الاحترافي
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

        // شريط تقدم المراحل (Step Indicator)
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
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // شبكة اختصارات الحاسبة المرجعية للأقسام (ياجور، سقف، دهان، ترطيبة...)
        item {
            ReferenceCalculatorGrid(
                language = language,
                onSelect = { trade, type -> viewModel.navigateToCalculator(trade, type) },
            )
        }

        // Catalogue live partagé avec le site : nouveaux métiers, prestations et matériaux.
        item {
            SiteDynamicCalculatorCard(language = language, countryCode = country.code, currencyCode = country.currencyCode)
        }

        // شريط اختيار المهن والأنشطة الرئيسية
        item {
            TradeSelectorBar(
                selectedTrade = selectedTrade,
                onTradeSelected = { viewModel.setTrade(it) },
                language = language
            )
        }

        // أنواع الأنشطة الفرعية (Chips)
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

        // إعدادات الغرف السريعة (Room Presets)
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

        // مخطط الشانطي الهيكلي المرئي (Blueprint Visualizer)
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

        // بطاقة المعاملات والمقاسات الرئيسية
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

                    // المساحة والمحيط التقديرية (للقراءة فقط)
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
                        OutlinedTextField(
                            value = unitsCount,
                            onValueChange = { viewModel.setInputUnitsCount(it) },
                            label = { Text(if (selectedType == CalculationType.MENUISERIE_PORTES) "Nombre de portes (عدد الأبواب)" else "Nombre de fenêtres (عدد النوافذ)") },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_units_count")
                        )
                    } else if (selectedType == CalculationType.PLOMBERIE_INSTALLATION) {
                        // Plumbing is measured by network length and water points below, not by floor area.
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

                        if (selectedType == CalculationType.PLACO_CLOISON || selectedType == CalculationType.MACONNERIE_BRIQUE || selectedType == CalculationType.DEMOLITION_DEPOSE) {
                            OutlinedTextField(
                                value = height,
                                onValueChange = { viewModel.setInputHeight(it) },
                                label = { Text(if (selectedType == CalculationType.DEMOLITION_DEPOSE) "Épaisseur à démolir (cm)" else language.tr("Hauteur sous plafond (m)","Hauteur sous plafond (m)","ارتفاع السقف (متر)","ارتفاع السقف (متر)")) },
                                shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth().testTag("input_height")
                            )
                        }
                    }

                    if (selectedType == CalculationType.PLACO_CLOISON || selectedType == CalculationType.MACONNERIE_BRIQUE || selectedType == CalculationType.FACADE_FINITION) {
                        OutlinedTextField(
                            value = openingArea,
                            onValueChange = { viewModel.setOpeningArea(it) },
                            label = { Text(language.tr("Surface des ouvertures à déduire (m²)", "Opening area to deduct (m²)", "مساحة البيبان والشبابك للطرح (م²)", "مساحة البيبان والشبابك للطرح (م²)")) },
                            supportingText = { Text(language.tr("Déduction appliquée à partir de 1,00 m² au total.", "Deduction applies when total opening area is at least 1.00 m².", "الطرح يتطبق كي المساحة الجملية للفتحات توصل 1 م².", "الطرح يتطبق كي المساحة الجملية للفتحات توصل 1 م².")) },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().testTag("input_opening_area")
                        )
                    }

                    if (selectedType == CalculationType.PLOMBERIE_INSTALLATION || selectedType == CalculationType.ELECTRICITE_INSTALLATION) {
                        OutlinedTextField(
                            value = unitsCount,
                            onValueChange = { viewModel.setInputUnitsCount(it) },
                            label = { Text(if (selectedType == CalculationType.PLOMBERIE_INSTALLATION) "Nombre de points d'eau" else "Nombre de points électriques") },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_units_count")
                        )
                    }
                    if (selectedType == CalculationType.PLOMBERIE_INSTALLATION || selectedType == CalculationType.ETANCHEITE_TOITURE) {
                        OutlinedTextField(
                            value = linearMeters,
                            onValueChange = { viewModel.setInputLinearMeters(it) },
                            label = { Text(if (selectedType == CalculationType.PLOMBERIE_INSTALLATION) "Longueur du réseau (m)" else "Périmètre des acrotères (m)") },
                            shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().testTag("input_linear_meters")
                        )
                    }

                    // Select a real catalog board for calculations that consume boardType.
                    if (selectedTrade == TradeCategory.PLACO &&
                        (selectedType == CalculationType.PLACO_PLAFOND_FIXE || selectedType == CalculationType.PLACO_CLOISON)
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        KonstrivoSectionLabel(
                            text = language.tr("Type de plaque", "Board type", "نوع اللوح", "نوع اللوح")
                        )
                        val boardOptions = MaterialCatalog.ALL_MATERIALS.filter {
                            it.category == MaterialCategory.PLACO && it.id.startsWith("plaque_")
                        }
                        val boardItems = boardOptions.map { mat ->
                            val label = when (language) {
                                AppLanguage.DERJA -> mat.nameDerja
                                AppLanguage.ARABIC -> mat.nameAr
                                AppLanguage.FRENCH -> mat.nameFr
                                AppLanguage.ENGLISH -> mat.nameEn
                            }
                            mat.id to label
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("placo_board_type_selector")
                        ) {
                            items(boardItems) { (key, label) ->
                                FilterChip(selected = boardType == key, onClick = { viewModel.setSelectedBoardType(key) }, label = { Text(label, style = MaterialTheme.typography.labelSmall) })
                            }
                        }
                        Text(language.tr("Dimensions de la plaque", "Board dimensions", "أبعاد اللوح", "قياس اللوح"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        val dimensionOptions = listOf(
                            "1.2x2.5" to "1,20 × 2,50 m · 3,00 m²",
                            "1.2x2.8" to "1,20 × 2,80 m · 3,36 m²",
                            "1.2x3.0" to "1,20 × 3,00 m · 3,60 m²",
                            "0.9x2.0" to "0,90 × 2,00 m · 1,80 m²"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            dimensionOptions.forEach { (key, label) ->
                                Row(modifier = Modifier.fillMaxWidth().clickable { viewModel.setBoardDimensions(key) }.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = boardDimensions == key, onClick = { viewModel.setBoardDimensions(key) })
                                    Text(label, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }

                    // اختيار البلاط للأسقف القابلة للفك (Dalles)
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

                    // مدخلات الفواصل والتشطيب (Joints & Finition)
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
                    }
                }
            }
        }

        // Website parity: fiscal settings, configurable labor and waste margin.
        item {
            KonstrivoCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = language.tr("Fiscalité & paramètres du devis", "Tax & estimate settings", "الضرائب وإعدادات عرض السعر", "الضرائب وإعدادات عرض السعر"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(language.tr("Taux TVA", "VAT rate", "نسبة الأداء على القيمة المضافة", "نسبة الأداء على القيمة المضافة"), fontWeight = FontWeight.SemiBold)
                    if (country == Country.TUNISIA) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("19", "13", "7", "0").forEach { rate ->
                                FilterChip(
                                    selected = (tvaRateOverride.toDoubleOrNull()?.toInt()?.toString() ?: summary?.tvaRate?.toInt()?.toString()) == rate,
                                    onClick = { viewModel.setTvaRateOverride(rate) },
                                    label = { Text("$rate%") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = language.tr("Le taux du pays sélectionné est appliqué.", "The selected country's tax profile is applied.", "يتم تطبيق النظام الجبائي للبلد المختار.", "يتم تطبيق النظام الجبائي للبلد المختار."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { viewModel.setTvaRateOverride("") }) {
                        Text(language.tr("Taux du pays (par défaut)", "Use country default", "استعمال نسبة البلد الأصلية", "استعمال نسبة البلد الأصلية"))
                    }
                    if (country == Country.TUNISIA) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(language.tr("Timbre fiscal", "Fiscal stamp", "الطابع الجبائي", "الطابع الجبائي"), fontWeight = FontWeight.SemiBold)
                                Text("1.000 DT", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = fiscalStampEnabled, onCheckedChange = viewModel::setFiscalStampEnabled)
                        }
                    }
                    Text(language.tr("Retenue de garantie", "Guarantee retention", "خصم الضمان", "خصم الضمان"), fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("0", "5", "10").forEach { rate ->
                            FilterChip(
                                selected = retentionRate == rate,
                                onClick = { viewModel.setRetentionRate(rate) },
                                label = { Text("$rate%") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    OutlinedTextField(
                        value = customLabor,
                        onValueChange = viewModel::setCustomLaborRate,
                        label = { Text(language.tr("Main d'œuvre (DT/m², ml ou unité selon métier)", "Labor rate (DT/m², linear m or unit)", "سعر اليد العاملة (حسب وحدة الخدمة)", "سوم اليد العاملة (حسب وحدة الخدمة)")) },
                        supportingText = { Text(language.tr("Laisser vide pour le tarif estimé du métier.", "Leave blank to use the trade estimate.", "خلّيه فارغ لاستعمال السعر التقديري للصنعة.", "خلّيه فارغ لاستعمال سوم الصنعة التقديري.")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                        modifier = Modifier.fillMaxWidth().testTag("input_custom_labor_rate")
                    )
                    OutlinedTextField(
                        value = wasteMargin,
                        onValueChange = viewModel::setWasteMargin,
                        label = { Text(language.tr("Marge de chute (%)", "Waste margin (%)", "نسبة الضياع (%)", "نسبة الضياع (%)")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                        modifier = Modifier.fillMaxWidth().testTag("input_waste_margin")
                    )
                    Text(
                        text = language.tr("Les réglages fiscaux sont intégrés au même résultat de calcul.", "Tax settings are applied to the same calculation result.", "الإعدادات الجبائية داخلة في نفس نتيجة الحساب.", "الإعدادات الجبائية داخلة في نفس نتيجة الحساب."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Visible estimate results: material details and HT/TVA/TTC totals.
        item {
            val result = summary
            if (result != null) {
                val reportText = buildString {
                    appendLine("KONSTRIVO PRO — ${result.calculationType.nameFr}")
                    appendLine("Surface: ${String.format(java.util.Locale.US, "%.2f", result.inputArea)} m²")
                    appendLine("Matériaux HT: ${String.format(java.util.Locale.US, "%.2f", result.totalMaterialCost)} ${result.currency}")
                    appendLine("Main d'œuvre HT: ${String.format(java.util.Locale.US, "%.2f", result.estimatedLaborCost)} ${result.currency}")
                    appendLine("Total HT: ${String.format(java.util.Locale.US, "%.2f", result.grandTotalHT)} ${result.currency}")
                    appendLine("TVA (${result.tvaRate}%): ${String.format(java.util.Locale.US, "%.2f", result.totalTva)} ${result.currency}")
                    appendLine("Timbre fiscal: ${String.format(java.util.Locale.US, "%.2f", result.fiscalStampAmount)} ${result.currency}")
                    appendLine("Total TTC: ${String.format(java.util.Locale.US, "%.2f", result.grandTotalTTC)} ${result.currency}")
                    appendLine("Retenue de garantie (${result.retentionRate}%): -${String.format(java.util.Locale.US, "%.2f", result.retentionAmount)} ${result.currency}")
                    appendLine("Net à payer: ${String.format(java.util.Locale.US, "%.2f", result.netPayableAfterRetention)} ${result.currency}")
                    appendLine()
                    result.items.forEach { line ->
                        appendLine("${line.nameFr}: ${String.format(java.util.Locale.US, "%.2f", line.quantity)} ${line.unit} × ${String.format(java.util.Locale.US, "%.2f", line.unitPrice)} = ${String.format(java.util.Locale.US, "%.2f", line.totalPrice)} ${result.currency}")
                    }
                }
                KonstrivoCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = language.tr("Résultat du métré", "Estimate result", "نتيجة حساب الكميات", "نتيجة حساب المترية"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f", result.inputArea)} m²",
                            style = MaterialTheme.typography.titleMedium,
                            color = KonstrivoGold,
                            fontWeight = FontWeight.Bold
                        )
                        result.items.forEach { line ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(line.nameFr, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%.2f", line.quantity)} ${line.unit} × ${String.format(java.util.Locale.US, "%.2f", line.unitPrice)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.2f", line.totalPrice)} ${result.currency}",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        }
                        ResultTotalRow("Matériaux HT", result.totalMaterialCost, result.currency)
                        ResultTotalRow("Main d'œuvre HT", result.estimatedLaborCost, result.currency)
                        ResultTotalRow("Total HT", result.grandTotalHT, result.currency, bold = true)
                        ResultTotalRow("TVA (${result.tvaRate}%)", result.totalTva, result.currency)
                        if (result.fiscalStampAmount > 0.0) ResultTotalRow("Timbre fiscal", result.fiscalStampAmount, result.currency)
                        ResultTotalRow("Total TTC", result.grandTotalTTC, result.currency, bold = true)
                        if (result.retentionRate > 0.0) ResultTotalRow("Retenue de garantie (${result.retentionRate}%)", -result.retentionAmount, result.currency)
                        ResultTotalRow("Net à payer", result.netPayableAfterRetention, result.currency, bold = true)
                        HorizontalDivider()
                        Text(language.tr("Méthodologie d’exécution", "Work methodology", "منهجية التنفيذ", "طريقة الخدمة"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        val methodology = when (selectedType) {
                            CalculationType.PLACO_PLAFOND_FIXE -> listOf("Tracer le niveau périphérique et contrôler les diagonales.", "Fixer les cornières de rive et poser les suspentes selon le support.", "Installer les fourrures au pas prévu par le système.", "Poser les plaques avec joints décalés et vis adaptées.", "Traiter les joints en plusieurs passes, laisser sécher puis poncer.")
                            CalculationType.PLACO_CLOISON -> listOf("Tracer l’axe de la cloison et repérer les ouvertures.", "Fixer les rails au sol et au plafond avec bandes adaptées.", "Installer les montants à l’entraxe choisi et renforcer les baies.", "Poser isolant et plaques selon le nombre de faces configuré.", "Réaliser bandes, enduits et contrôle final de planéité.")
                            CalculationType.PLACO_DEMONTABLE -> listOf("Déterminer le niveau fini et le calepinage des dalles.", "Fixer les cornières périphériques au niveau.", "Suspendre et mettre à niveau l’ossature T24.", "Installer les dalles 60 × 60 et contrôler les coupes.")
                            CalculationType.PLOMBERIE_INSTALLATION -> listOf("Repérer les points d’eau et le cheminement des réseaux.", "Dimensionner alimentation et évacuation selon le chantier.", "Fixer les tubes, réaliser les raccords et prévoir l’accès maintenance.", "Effectuer un essai d’étanchéité avant fermeture.")
                            CalculationType.ELECTRICITE_INSTALLATION -> listOf("Tracer les circuits et séparer éclairage et prises.", "Poser gaines et câbles selon les sections validées par l’électricien.", "Installer protections et tableau selon le schéma du chantier.", "Tester continuité, terre et protections avant mise en service.")
                            CalculationType.ETANCHEITE_TOITURE -> listOf("Préparer le support, réparer fissures et vérifier les pentes.", "Appliquer le primaire compatible avec le système choisi.", "Poser la membrane avec recouvrements et relevés traités.", "Contrôler évacuations et réaliser un essai d’étanchéité.")
                            CalculationType.ISOLATION_THERMIQUE -> listOf("Vérifier la continuité de l’enveloppe et l’absence d’humidité.", "Découper l’isolant sans le comprimer et limiter les ponts thermiques.", "Traiter joints, raccords et points singuliers.", "Respecter les prescriptions du fabricant pour le pare-vapeur.")
                            CalculationType.SOLS_PARQUET -> listOf("Contrôler planéité, humidité et acclimatation du parquet.", "Poser la sous-couche compatible et prévoir le jeu périphérique.", "Assembler les lames en décalant les joints.", "Poser les plinthes sans bloquer la dilatation.")
                            CalculationType.FACADE_FINITION -> listOf("Contrôler la stabilité du support et traiter les fissures.", "Appliquer le primaire et le système de façade compatibles.", "Respecter épaisseurs, renforts et temps de séchage fabricant.", "Vérifier les points singuliers et la protection contre l’eau.")
                            CalculationType.DEMOLITION_DEPOSE -> listOf("Vérifier les réseaux cachés et sécuriser la zone.", "Protéger les accès et définir l’ordre de dépose.", "Démonter progressivement et trier les gravats.", "Évacuer les déchets selon les règles locales.")
                            else -> listOf("Vérifier les mesures et l’état du support avant intervention.", "Préparer les matériaux et respecter les instructions du fabricant.", "Contrôler dimensions, fixations et finitions avant réception.")
                        }
                        methodology.forEach { step ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                                Text("•", color = KonstrivoGold, fontWeight = FontWeight.Bold)
                                Text(step, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text(language.tr("Référence indicative : confirmez les normes locales et les instructions du fabricant selon le système installé.", "Guidance only: verify local standards and manufacturer instructions for the installed system.", "معلومات إرشادية: تثبّت من المعايير المحلية وتعليمات المصنّع حسب النظام المركّب.", "معلومات إرشادية: تثبّت من المعايير المحلية وتعليمات المصنّع حسب النظام المركّب."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(reportText))
                                    Toast.makeText(context, "Résultat copié", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(language.tr("Copier", "Copy", "نسخ", "نسخ"))
                            }
                            Button(
                                onClick = {
                                    val url = "https://wa.me/?text=" + Uri.encode(reportText)
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "WhatsApp indisponible", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("WhatsApp")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultTotalRow(label: String, amount: Double, currency: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(
            text = "${String.format(java.util.Locale.US, "%.2f", amount)} ${currency}",
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (bold) KonstrivoGold else MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Compact progress indicator for the calculator's four user-facing stages.
 * Presentation only; it does not alter calculation inputs or formulas.
 */
@Composable
private fun CalculatorStepIndicator(
    currentStep: Int,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        language.tr("Métier", "Trade", "الصنعة", "الصنعة"),
        language.tr("Mesures", "Measurements", "القياسات", "القياسات"),
        language.tr("Calcul", "Calculate", "الحساب", "الحساب"),
        language.tr("Résultat", "Result", "النتيجة", "النتيجة")
    )
    val activeStep = currentStep.coerceIn(1, steps.size)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            val stepNumber = index + 1
            val active = stepNumber <= activeStep
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            color = if (active) KonstrivoGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(50)
                        )
                ) {}
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (stepNumber == activeStep) FontWeight.Bold else FontWeight.Normal,
                    color = if (stepNumber <= activeStep) KonstrivoGold else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
