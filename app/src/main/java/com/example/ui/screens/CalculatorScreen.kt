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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // شبكة اختصارات الحاسبة المرجعية
        item {
            ReferenceCalculatorGrid(
                language = language,
                onSelect = { trade, type -> viewModel.navigateToCalculator(trade, type) },
            )
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
    }
}