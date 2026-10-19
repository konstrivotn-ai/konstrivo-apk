package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MaterialPriceEntity
import com.example.data.model.*
import com.example.domain.usecase.formatQuantity
import com.example.ui.components.designsystem.KonstrivoOutlinedButton
import com.example.ui.components.designsystem.KonstrivoPriceStyle
import com.example.ui.components.designsystem.KonstrivoPriceText
import com.example.ui.components.designsystem.KonstrivoPrimaryButton
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSectionLabel
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import com.example.ui.components.designsystem.tr

data class RoomPreset(
    val id: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val length: Double,
    val width: Double,
    val icon: String
)

val PRESET_ROOMS = listOf(
    RoomPreset("chambre", "Chambre (12 m²)", "غرفة نوم (12 م²)", "بيت نوم (12 م²)", 4.0, 3.0, "🛏️"),
    RoomPreset("salon", "Salon (27 m²)", "صالون كبير (27 م²)", "صالون واسع (27 م²)", 6.0, 4.5, "🛋️"),
    RoomPreset("couloir", "Couloir (7.2 m²)", "ممر / هول (7.2 م²)", "كولوار طويل (7.2 م²)", 6.0, 1.2, "🚪"),
    RoomPreset("sdb", "SDB / Cuisine (6 m²)", "حمام / مطبخ (6 م²)", "بيت راحة / كوجينة (6 م²)", 3.0, 2.0, "🚿"),
    RoomPreset("bureau", "Bureau / Local (40 m²)", "مكتب / محل (40 م²)", "بيرو / حانوت (40 م²)", 8.0, 5.0, "🏢"),
    RoomPreset("grand_espace", "Grand Chantier (96 m²)", "مساحة كبيرة (96 م²)", "شانتي كبير (96 م²)", 12.0, 8.0, "🏬")
)

@Composable
fun RoomPresetsRow(
    onPresetSelected: (length: Double, width: Double) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = when (language) {
                    AppLanguage.DERJA -> "⚡ مقاسات جاهزة وسريعة للبيوت"
                    AppLanguage.ARABIC -> "⚡ أبعاد ونماذج غرف جاهزة"
                    AppLanguage.FRENCH -> "⚡ Modèles de pièces préconfigurés"
                    AppLanguage.ENGLISH -> "⚡ Quick Room Presets"
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "1-Tap",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
        ) {
            items(PRESET_ROOMS) { preset ->
                val title = when (language) {
                    AppLanguage.DERJA -> "${preset.icon} ${preset.nameDerja}"
                    AppLanguage.ARABIC -> "${preset.icon} ${preset.nameAr}"
                    AppLanguage.FRENCH -> "${preset.icon} ${preset.nameFr}"
                    AppLanguage.ENGLISH -> "${preset.icon} ${preset.nameFr}"
                }
                Surface(
                    onClick = { onPresetSelected(preset.length, preset.width) },
                    shape = RoundedCornerShape(KonstrivoRadius.Button),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.testTag("preset_${preset.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${preset.length}×${preset.width}m)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArchitecturalSchematicVisualizer(
    length: Double,
    width: Double,
    area: Double,
    type: CalculationType,
    language: AppLanguage,
    spacingCm: Int = 60,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("architectural_schematic_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KonstrivoNavy),
        border = androidx.compose.foundation.BorderStroke(1.dp, KonstrivoGold.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(KonstrivoGold)
                    )
                    Text(
                        text = when (language) {
                            AppLanguage.DERJA -> "مخطط الشانتي الحي والهيكل (Blueprint)"
                            AppLanguage.ARABIC -> "المخطط التنفيذي وتوزيع الهيكل"
                            AppLanguage.FRENCH -> "Plan Schématique & Ossature Directe"
                            AppLanguage.ENGLISH -> "Live Blueprint & Framing Layout"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = KonstrivoGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KonstrivoGold.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (type == CalculationType.PLACO_DEMONTABLE) "Grille 60×60 cm" else "Espacement: $spacingCm cm",
                        style = MaterialTheme.typography.labelSmall,
                        color = KonstrivoGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Blueprint Drawing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070D1E))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    val w = size.width
                    val h = size.height

                    // Blueprint grid background
                    val gridSpacing = 20f
                    val gridPaintColor = Color(0xFF1E293B).copy(alpha = 0.5f)
                    var x = 0f
                    while (x <= w) {
                        drawLine(gridPaintColor, Offset(x, 0f), Offset(x, h), strokeWidth = 0.8f)
                        x += gridSpacing
                    }
                    var y = 0f
                    while (y <= h) {
                        drawLine(gridPaintColor, Offset(0f, y), Offset(w, y), strokeWidth = 0.8f)
                        y += gridSpacing
                    }

                    // Room Box Dimension Calculation
                    val margin = 24f
                    val roomW = w - (margin * 2)
                    val roomH = h - (margin * 2)

                    // Outer Wall Box (Double border)
                    drawRect(
                        color = Color(0xFF38BDF8),
                        topLeft = Offset(margin, margin),
                        size = Size(roomW, roomH),
                        style = Stroke(width = 2.5f)
                    )

                    // Studs / Fourrures / Grid lines inside
                    val isGridMode = type == CalculationType.PLACO_DEMONTABLE || type == CalculationType.CARRELAGE_SOL
                    val numVerticalStuds = if (isGridMode) 6 else if (spacingCm == 40) 8 else 5
                    val studStepX = roomW / (numVerticalStuds + 1)

                    for (i in 1..numVerticalStuds) {
                        val studX = margin + (i * studStepX)
                        drawLine(
                            color = if (type == CalculationType.PLACO_CLOISON) KonstrivoGold else Color(0xFF60A5FA).copy(alpha = 0.7f),
                            start = Offset(studX, margin),
                            end = Offset(studX, margin + roomH),
                            strokeWidth = 1.5f,
                            pathEffect = if (type == CalculationType.PLACO_CACHE_LUMIERE) PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f) else null
                        )
                    }

                    if (isGridMode) {
                        val numHoriz = 3
                        val stepY = roomH / (numHoriz + 1)
                        for (j in 1..numHoriz) {
                            val horizY = margin + (j * stepY)
                            drawLine(
                                color = Color(0xFF60A5FA).copy(alpha = 0.7f),
                                start = Offset(margin, horizY),
                                end = Offset(margin + roomW, horizY),
                                strokeWidth = 1.5f
                            )
                        }
                    }

                    // Dimension markers
                    // Top width dimension
                    drawLine(KonstrivoGold, Offset(margin, margin - 10f), Offset(margin + roomW, margin - 10f), strokeWidth = 1f)
                    drawLine(KonstrivoGold, Offset(margin, margin - 14f), Offset(margin, margin - 6f), strokeWidth = 1.2f)
                    drawLine(KonstrivoGold, Offset(margin + roomW, margin - 14f), Offset(margin + roomW, margin - 6f), strokeWidth = 1.2f)

                    // Left length dimension
                    drawLine(KonstrivoGold, Offset(margin - 10f, margin), Offset(margin - 10f, margin + roomH), strokeWidth = 1f)
                    drawLine(KonstrivoGold, Offset(margin - 14f, margin), Offset(margin - 6f, margin), strokeWidth = 1.2f)
                    drawLine(KonstrivoGold, Offset(margin - 14f, margin + roomH), Offset(margin - 6f, margin + roomH), strokeWidth = 1.2f)
                }

                // Schematics Info Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(KonstrivoNavy.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📐 $area m²",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = KonstrivoGold
                    )
                    Text(
                        text = "(${if (length > 0) length else "—"}m × ${if (width > 0) width else "—"}m)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun TradeSelectorBar(
    selectedTrade: TradeCategory,
    onTradeSelected: (TradeCategory) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(TradeCategory.entries) { trade ->
            val isSelected = trade == selectedTrade
            val (icon, color) = when (trade) {
                TradeCategory.PLACO -> Icons.Default.Layers to PlacoTradeColor
                TradeCategory.PEINTURE -> Icons.Default.FormatPaint to PeintureTradeColor
                TradeCategory.MACONNERIE -> Icons.Default.Foundation to MaconnerieTradeColor
                TradeCategory.CREPISSAGE -> Icons.Default.Brush to CrepissageTradeColor
                TradeCategory.CARRELAGE -> Icons.Default.GridOn to CarrelageTradeColor
                TradeCategory.MENUISERIE -> Icons.Default.DoorSliding to MenuiserieTradeColor
                TradeCategory.PLOMBERIE -> Icons.Default.WaterDrop to Color(0xFF0EA5E9)
                TradeCategory.ELECTRICITE -> Icons.Default.Bolt to Color(0xFFF59E0B)
                TradeCategory.ETANCHEITE -> Icons.Default.Umbrella to Color(0xFF2563EB)
                TradeCategory.ISOLATION -> Icons.Default.Shield to Color(0xFF10B981)
                TradeCategory.SOLS -> Icons.Default.Layers to Color(0xFF8B5E3C)
                TradeCategory.FACADE -> Icons.Default.Home to Color(0xFF64748B)
                TradeCategory.DEMOLITION -> Icons.Default.Build to Color(0xFFDC2626)
            }

            val title = when (language) {
                AppLanguage.DERJA -> trade.nameDerja
                AppLanguage.ARABIC -> trade.nameAr
                AppLanguage.FRENCH -> trade.nameFr
                AppLanguage.ENGLISH -> trade.nameFr
            }

            Surface(
                onClick = { onTradeSelected(trade) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shadowElevation = if (isSelected) 3.dp else 0.dp,
                modifier = Modifier
                    .testTag("trade_tab_${trade.id}")
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) Color.White else color,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCostHeroCard(
    summary: CalculationSummary,
    language: AppLanguage,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onPreviewInvoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("summary_hero_card"),
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KonstrivoSpacing.CardInner)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(KonstrivoGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = language.tr("Estimation Globale","Estimation Globale","المجموع التقديري للشانتي","المجموع التقديري للشانتي"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${summary.inputArea} m² • ${summary.items.size} ${language.tr("matériaux","matériaux","مواد","مواد")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                // Grand total highlight — Phase D: show the real TTC (VAT included).
                // `grandTotalCost` is the HT total; `grandTotalTTC` is the customer-facing total.
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KonstrivoGold
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        KonstrivoPriceText(
                            text = "${summary.grandTotalTTC} ${summary.currency}",
                            style = KonstrivoPriceStyle.Display,
                            color = Color.White
                        )
                        Text(
                            text = language.tr("Total TTC","Total TTC","المجموع (شامل الأداء)","المجموع (شامل الأداء)"),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Material vs Labor Ratio Bar
            val total = summary.grandTotalCost.coerceAtLeast(1.0)
            val matRatio = (summary.totalMaterialCost / total).toFloat().coerceIn(0.05f, 0.95f)
            val laborRatio = 1f - matRatio

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📦 ${(matRatio * 100).toInt()}% ${language.tr("Fournitures","Fournitures","سلعة","سلعة")}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "👷 ${(laborRatio * 100).toInt()}% ${language.tr("Main d'œuvre","Main d'œuvre","خدمة اليد","خدمة اليد")}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KonstrivoBlue
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(matRatio)
                            .fillMaxHeight()
                            .background(KonstrivoGold)
                    )
                    Box(
                        modifier = Modifier
                            .weight(laborRatio)
                            .fillMaxHeight()
                            .background(KonstrivoBlue)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Subtotals Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = language.tr("Total Matériaux (Fourniture)","Total Matériaux (Fourniture)","كلفة السلعة والمواد","كلفة السلعة والمواد"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    KonstrivoPriceText(
                        text = "${summary.totalMaterialCost} ${summary.currency}",
                        style = KonstrivoPriceStyle.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = language.tr("Main d'œuvre estimée","Main d'œuvre estimée","اليد العاملة (الخدمة)","اليد العاملة (الخدمة)"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    KonstrivoPriceText(
                        text = "${summary.estimatedLaborCost} ${summary.currency}",
                        style = KonstrivoPriceStyle.Medium,
                        color = KonstrivoBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PDF / Printable preview — Design System Outlined button
                KonstrivoOutlinedButton(
                    text = language.tr("Devis PDF","Devis PDF","معاينة Devis","معاينة Devis"),
                    onClick = onPreviewInvoiceClick,
                    modifier = Modifier.weight(1f).testTag("btn_preview_devis"),
                    icon = Icons.AutoMirrored.Filled.ReceiptLong
                )

                // Save — primary action of the estimation
                KonstrivoPrimaryButton(
                    text = language.tr("Enregistrer","Enregistrer","حفظ التقرير","حفظ التقرير"),
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1.2f).testTag("btn_save_devis"),
                    icon = Icons.Default.BookmarkBorder
                )

                // WhatsApp — brand-green share affordance, system-standardized shape/height
                Button(
                    onClick = onShareClick,
                    modifier = Modifier.weight(1.2f).heightIn(min = 48.dp).testTag("btn_share_whatsapp"),
                    shape = RoundedCornerShape(KonstrivoRadius.Badge),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WhatsApp",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CalculationParamsCard(
    boardLabel: String?,
    montantLabel: String?,
    entraxeCm: Int?,
    area: Double,
    height: Double,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calculation_params_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = language.tr("PARAMÈTRES DU CALCUL","PARAMÈTRES DU CALCUL","معطيات الحساب المستعملة","معطيات الحساب المستعملة"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            if (boardLabel != null) {
                CalcParamRow(
                    label = language.tr("Plaque","Plaque","اللوح","اللوح"),
                    value = boardLabel
                )
            }
            if (montantLabel != null) {
                CalcParamRow(
                    label = language.tr("Montant","Montant","المونتون","المونتون"),
                    value = montantLabel
                )
            }
            if (entraxeCm != null) {
                CalcParamRow(
                    label = language.tr("Entraxe","Entraxe","تباعد القوائم","تباعد القوائم"),
                    value = "$entraxeCm cm"
                )
            }
            CalcParamRow(
                label = language.tr("Surface","Surface","المساحة","المساحة"),
                value = "${friendlyNumber(area)} m²"
            )
            CalcParamRow(
                label = language.tr("Hauteur","Hauteur","الارتفاع","الارتفاع"),
                value = "${friendlyNumber(height)} m"
            )
        }
    }
}

@Composable
private fun CalcParamRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

private fun friendlyNumber(value: Double): String {
    val rounded = (value * 100.0).toLong() / 100.0
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
}

@Composable
fun CalculationBreakdownCard(
    summary: CalculationSummary,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val currency = summary.currency
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calculation_breakdown_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KonstrivoSpacing.CardInner),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KonstrivoSectionLabel(text = language.tr("RÉCAPITULATIF","RÉCAPITULATIF","ملخص الحساب النهائي","ملخص الحساب النهائي"))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            BreakdownSectionTitle(language.tr("MATÉRIAUX","MATÉRIAUX","المواد","المواد"))
            BreakdownRow(language.tr("Matériaux HT","Matériaux HT","المواد (HT)","المواد (HT)"), "${summary.totalMaterialCost} $currency")
            BreakdownRow(language.tr("TVA matériaux","TVA matériaux","أداء المواد","أداء المواد"), "${summary.materialTva} $currency")
            BreakdownRow(language.tr("Matériaux TTC","Matériaux TTC","المواد (TTC)","المواد (TTC)"), "${summary.materialTotalTTC} $currency", isSubTotal = true)

            BreakdownSectionTitle(language.tr("MAIN-D'ŒUVRE","MAIN-D'ŒUVRE","اليد العاملة","اليد العاملة"))
            BreakdownRow(language.tr("Main-d'œuvre HT","Main-d'œuvre HT","اليد العاملة (HT)","اليد العاملة (HT)"), "${summary.estimatedLaborCost} $currency")
            BreakdownRow(language.tr("TVA main-d'œuvre","TVA main-d'œuvre","أداء اليد العاملة","أداء اليد العاملة"), "${summary.laborTva} $currency")
            BreakdownRow(language.tr("Main-d'œuvre TTC","Main-d'œuvre TTC","اليد العاملة (TTC)","اليد العاملة (TTC)"), "${summary.laborTotalTTC} $currency", isSubTotal = true)

            // AUTRES expenses — shown only when they actually exist (values stay in the summary).
            val hasOtherCosts = summary.transportCostHT > 0.0 ||
                summary.equipmentCostHT > 0.0 ||
                summary.otherCostHT > 0.0
            if (hasOtherCosts) {
                BreakdownSectionTitle(language.tr("AUTRES (HT)","AUTRES (HT)","تكاليف أخرى (HT)","تكاليف أخرى (HT)"))
                if (summary.transportCostHT > 0.0) {
                    BreakdownRow(language.tr("Transport","Transport","النقل","النقل"), "${summary.transportCostHT} $currency")
                }
                if (summary.equipmentCostHT > 0.0) {
                    BreakdownRow(language.tr("Équipement","Équipement","المعدات","المعدات"), "${summary.equipmentCostHT} $currency")
                }
                if (summary.otherCostHT > 0.0) {
                    BreakdownRow(language.tr("Autres","Autres","أخرى","أخرى"), "${summary.otherCostHT} $currency")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

            BreakdownTotalRow(
                label = language.tr("TOTAL HT","TOTAL HT","المجموع (HT)","المجموع (HT)"),
                value = "${summary.grandTotalHT} $currency"
            )
            BreakdownTotalRow(
                label = language.tr("TVA (${friendlyNumber(summary.tvaRate)}%)","TVA (${friendlyNumber(summary.tvaRate)}%)","الأداء (${friendlyNumber(summary.tvaRate)}%)","الأداء (${friendlyNumber(summary.tvaRate)}%)"),
                value = "${summary.totalTva} $currency"
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            BreakdownTotalRow(
                label = language.tr("TOTAL TTC","TOTAL TTC","المجموع الشامل (TTC)","المجموع الشامل (TTC)"),
                value = "${summary.grandTotalTTC} $currency",
                accentValue = true
            )
        }
    }
}

@Composable
private fun BreakdownSectionTitle(text: String) {
    Spacer(modifier = Modifier.height(2.dp))
    KonstrivoSectionLabel(text = text)
}

@Composable
private fun BreakdownRow(label: String, value: String, isSubTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum"),
            fontWeight = if (isSubTotal) FontWeight.Bold else FontWeight.Medium,
            color = if (isSubTotal) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun BreakdownTotalRow(label: String, value: String, accentValue: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = (if (accentValue) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall)
                .copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.ExtraBold,
            color = if (accentValue) KonstrivoGold else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ItemPriceRow(label: String, value: String, bold: Boolean = false, accent: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum"),
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (accent) KonstrivoGold else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CalculatedItemCard(
    item: CalculatedItem,
    language: AppLanguage,
    currency: String,
    onQuantityChange: ((Double) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val title = when (language) {
        AppLanguage.DERJA -> item.nameDerja
        AppLanguage.ARABIC -> item.nameAr
        AppLanguage.FRENCH -> item.nameFr
        AppLanguage.ENGLISH -> item.nameFr
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calculated_item_${item.id}"),
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Item details
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${formatQuantity(item.quantity)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${item.quantity} ${item.unit} • ${item.unitPrice} $currency / u",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Total Price badge
                Surface(
                    shape = RoundedCornerShape(KonstrivoRadius.Badge),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    KonstrivoPriceText(
                        text = "${item.totalPrice} $currency",
                        style = KonstrivoPriceStyle.Small,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            if (item.tvaAmount > 0.0) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 52.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ItemPriceRow(
                        language.tr(
                            fr = "Prix unitaire HT",
                            en = "Unit price excl. VAT",
                            ar = "سعر الوحدة دون أداء",
                            dz = "سعر الوحدة بلا الأداء"
                        ),
                        "${item.unitPriceHT} $currency / u"
                    )
                    ItemPriceRow(
                        language.tr(
                            fr = "Total HT",
                            en = "Total excl. VAT",
                            ar = "المجموع دون أداء",
                            dz = "المجموع بلا الأداء"
                        ),
                        "${item.totalPrice} $currency",
                        bold = true
                    )
                    ItemPriceRow(
                        language.tr(
                            fr = "TVA",
                            en = "VAT",
                            ar = "أداء القيمة المضافة",
                            dz = "الأداء على القيمة"
                        ),
                        "${item.tvaAmount} $currency"
                    )
                    ItemPriceRow(
                        language.tr(
                            fr = "Total TTC",
                            en = "Total incl. VAT",
                            ar = "المجموع شامل الأداء",
                            dz = "المجموع بالأداء"
                        ),
                        "${item.totalPriceTTC} $currency",
                        bold = true,
                        accent = true
                    )
                }
            }

            if (item.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "💡 ${item.description}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    modifier = Modifier.padding(start = 52.dp)
                )
            }
        }
    }
}

@Composable
fun DevisInvoicePreviewDialog(
    summary: CalculationSummary,
    companyHeader: String,
    contractorName: String,
    contractorPhone: String,
    language: AppLanguage,
    clientName: String = "",
    clientPhone: String = "",
    location: String = "",
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val invoiceRef = "KNS-2026-${(1000..9999).random()}"
    // Localized neutral fallback for the worksite field (presentation only; no fake data).
    val effectiveLocation = location.ifEmpty { language.tr("Chantier", "Site", "الموقع", "المكان") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = language.tr(
                            "📄 DEVIS PRO #$invoiceRef",
                            "📄 DEVIS PRO #$invoiceRef",
                            "📄 عرض سعر رقم $invoiceRef",
                            "📄 الديڤيز رقم $invoiceRef"
                        ),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Konstrivo BTP 2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = companyHeader.ifEmpty { "Konstrivo BTP & Placo Pro" },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (contractorName.isNotEmpty()) {
                            Text(
                                text = language.tr(
                                    "👤 Artisan : $contractorName ($contractorPhone)",
                                    "👤 Contractor: $contractorName ($contractorPhone)",
                                    "👤 الحرفي: $contractorName ($contractorPhone)",
                                    "👤 الصنايعي: $contractorName ($contractorPhone)"
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = language.tr(
                                "📅 Date : $dateStr • Chantier : $effectiveLocation",
                                "📅 Date: $dateStr • Site: $effectiveLocation",
                                "📅 التاريخ: $dateStr • الموقع: $effectiveLocation",
                                "📅 التاريخ: $dateStr • المكان: $effectiveLocation"
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (clientName.isNotEmpty()) {
                            Text(
                                text = language.tr(
                                    "🤝 Client : $clientName ($clientPhone)",
                                    "🤝 Client: $clientName ($clientPhone)",
                                    "🤝 العميل: $clientName ($clientPhone)",
                                    "🤝 الحريف: $clientName ($clientPhone)"
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = language.tr(
                        "📦 Détail Quantitatif et Estimatif :",
                        "📦 Quantitative & Estimated Breakdown:",
                        "📦 التفاصيل الكمية والتقديرية:",
                        "📦 تفصيل الكميات والتقدير:"
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Document table: column header (Description | Quantity | Total)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = language.tr("Désignation", "Description", "البيان", "السلعة"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = language.tr("Qté", "Qty", "الكمية", "الكمية"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(56.dp)
                    )
                    Text(
                        text = language.tr("Total", "Total", "الإجمالي", "المجموع"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(88.dp)
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(4.dp))

                // Scrollable Table of Items
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = 240.dp)
                ) {
                    items(summary.items) { item ->
                        val name = when (language) {
                            AppLanguage.DERJA -> item.nameDerja
                            AppLanguage.ARABIC -> item.nameAr
                            AppLanguage.FRENCH -> item.nameFr
                            AppLanguage.ENGLISH -> item.nameFr
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${formatQuantity(item.quantity)} ${item.unit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(56.dp)
                            )
                            Text(
                                text = "${item.totalPrice}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(88.dp)
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Totals
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(KonstrivoRadius.Button),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = language.tr(
                                    "Total Fournitures (HT) :",
                                    "Total Supplies (excl. tax) :",
                                    "إجمالي المواد (بدون ضريبة)",
                                    "كلفة السلعة (بدون ضريبة)"
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text("${summary.totalMaterialCost} ${summary.currency}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = language.tr(
                                    "Main d'œuvre (HT) :",
                                    "Labor (excl. tax) :",
                                    "اليد العاملة (بدون ضريبة)",
                                    "اليد العاملة (بدون ضريبة)"
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text("${summary.estimatedLaborCost} ${summary.currency}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = language.tr(
                                    "TOTAL HT :",
                                    "TOTAL (excl. tax) :",
                                    "الإجمالي بدون ضريبة",
                                    "المجموع بدون ضريبة"
                                ),
                                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold
                            )
                            Text("${summary.grandTotalHT} ${summary.currency}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = language.tr(
                                    "TVA (${friendlyNumber(summary.tvaRate)}%) :",
                                    "VAT (${friendlyNumber(summary.tvaRate)}%) :",
                                    "ضريبة القيمة المضافة (${friendlyNumber(summary.tvaRate)}%) :",
                                    "TVA (${friendlyNumber(summary.tvaRate)}%) :"
                                ),
                                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold
                            )
                            Text("${summary.totalTva} ${summary.currency}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = language.tr(
                                    "TOTAL TTC :",
                                    "TOTAL TTC :",
                                    "الإجمالي شامل الضريبة",
                                    "المجموع الكلي"
                                ),
                                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold
                            )
                            Text("${summary.grandTotalTTC} ${summary.currency}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KonstrivoGold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onShare,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = language.tr(
                        "Envoyer WhatsApp",
                        "Send via WhatsApp",
                        "إرسال عبر واتساب",
                        "صيفط عبر واتساب"
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(language.tr("Fermer", "Close", "إغلاق", "إغلاق"))
            }
        }
    )
}

@Composable
fun EditPriceDialog(
    priceItem: MaterialPriceEntity,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var priceText by remember { mutableStateOf(priceItem.currentPrice.toString()) }
    val title = when (language) {
        AppLanguage.DERJA -> priceItem.nameDerja
        AppLanguage.ARABIC -> priceItem.nameAr
        AppLanguage.FRENCH -> priceItem.nameFr
        AppLanguage.ENGLISH -> priceItem.nameFr
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = language.tr("Modifier le tarif", "Edit price", "تعديل سعر المادة", "تعديل سعر المادة"),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${language.tr("Unité", "Unit", "الوحدة", "الوحدة")}: ${priceItem.unit} | ${language.tr("Tarif par défaut", "Default price", "السعر الافتراضي", "السعر الافتراضي")}: ${priceItem.defaultPrice} ${priceItem.currency}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(language.tr("Nouveau prix (${priceItem.currency})", "New price (${priceItem.currency})", "السعر الجديد (${priceItem.currency})", "السعر الجديد (${priceItem.currency})")) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_price")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull()
                    if (p != null && p >= 0) {
                        onSave(p)
                    }
                },
                modifier = Modifier
                    .testTag("btn_confirm_edit_price")
                    .heightIn(min = 48.dp)
            ) {
                Text(language.tr("Enregistrer", "Save", "حفظ", "حفظ"))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(language.tr("Annuler", "Cancel", "إلغاء", "إلغاء"))
            }
        }
    )
}

@Composable
fun SaveEstimateDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (clientName: String, phone: String, location: String, notes: String) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = language.tr("Enregistrer le Devis","Enregistrer le Devis","حفظ تقرير وعرض السعر","حفظ تقرير وعرض السعر"),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text(language.tr("Nom du Client","Nom du Client","اسم الحريف / العميل","اسم الحريف / العميل")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_devis_client")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(language.tr("Numéro WhatsApp / Tél","Numéro WhatsApp / Tél","رقم الهاتف / الواتساب","رقم الهاتف / الواتساب")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_devis_phone")
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(language.tr("Lieu du Chantier","Lieu du Chantier","موقع الشانتي / المدينة","موقع الشانتي / المدينة")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_devis_location")
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(language.tr("Notes supplémentaires","Notes supplémentaires","ملاحظات وتفاصيل إضافية","ملاحظات وتفاصيل إضافية")) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("input_devis_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(clientName, phone, location, notes) },
                modifier = Modifier.testTag("btn_confirm_save_devis")
            ) {
                Text(language.tr("Enregistrer","Enregistrer","حفظ التقرير","حفظ التقرير"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(language.tr("Annuler","Annuler","إلغاء","إلغاء"))
            }
        }
    )
}
