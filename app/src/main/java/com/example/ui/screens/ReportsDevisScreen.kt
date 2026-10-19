package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEstimateEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationType
import com.example.ui.components.designsystem.KonstrivoPrimaryButton
import com.example.ui.components.designsystem.KonstrivoPriceStyle
import com.example.ui.components.designsystem.KonstrivoPriceText
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSectionLabel
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsDevisScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val language by viewModel.selectedLanguage.collectAsState()
    val savedEstimates by viewModel.savedEstimates.collectAsState()

    var expandedEstimateId by remember { mutableStateOf<Long?>(null) }
    val dateFormatter = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    val shortDateFormatter = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    // PHASE 2.4 — Workspace KPIs are derived strictly from already-loaded estimates.
    // No new business logic: same flows, same repository data, presentation only.
    val totalEstimated = remember(savedEstimates) { savedEstimates.sumOf { it.grandTotal } }
    val latestEstimate = remember(savedEstimates) { savedEstimates.maxByOrNull { it.createdAt } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("reports_devis_screen")
    ) {
        DevisWorkspaceHero(
            language = language,
            estimateCount = savedEstimates.size,
            hasData = latestEstimate != null,
            totalEstimated = totalEstimated,
            currency = latestEstimate?.currency ?: "",
            latestDateText = latestEstimate?.let { shortDateFormatter.format(Date(it.createdAt)) }
        )

        if (savedEstimates.isEmpty()) {
            EstimatesEmptyState(
                language = language,
                onStartCalculation = { viewModel.selectTab(AppTab.CALCULATOR) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("estimates_empty_state")
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = KonstrivoSpacing.Lg,
                    end = KonstrivoSpacing.Lg,
                    top = KonstrivoSpacing.Xs,
                    bottom = KonstrivoSpacing.Lg
                ),
                verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
            ) {
                items(savedEstimates, key = { it.id }) { estimate ->
                    val isExpanded = expandedEstimateId == estimate.id
                    val calcType = CalculationType.entries.firstOrNull { it.id == estimate.calculationTypeId }
                    val typeTitle = when {
                        estimate.calculationTypeId.startsWith("site_material:") -> when (language) {
                            AppLanguage.DERJA -> "مادة من كاتالوغ الموقع"
                            AppLanguage.ARABIC -> "مادة من كتالوج الموقع"
                            AppLanguage.FRENCH -> "Matériau du catalogue du site"
                            AppLanguage.ENGLISH -> "Website catalog material"
                        }
                        estimate.calculationTypeId.startsWith("site_service:") -> when (language) {
                            AppLanguage.DERJA -> "خدمة من كاتالوغ الموقع"
                            AppLanguage.ARABIC -> "خدمة من كتالوج الموقع"
                            AppLanguage.FRENCH -> "Prestation du catalogue du site"
                            AppLanguage.ENGLISH -> "Website catalog service"
                        }
                        else -> when (language) {
                            AppLanguage.DERJA -> calcType?.nameDerja ?: estimate.calculationTypeId
                            AppLanguage.ARABIC -> calcType?.nameAr ?: estimate.calculationTypeId
                            AppLanguage.FRENCH -> calcType?.nameFr ?: estimate.calculationTypeId
                            AppLanguage.ENGLISH -> calcType?.nameFr ?: estimate.calculationTypeId
                        }
                    }

                    EstimateDocumentCard(
                        estimate = estimate,
                        language = language,
                        typeTitle = typeTitle,
                        dateText = dateFormatter.format(Date(estimate.createdAt)),
                        isExpanded = isExpanded,
                        onToggleExpand = { expandedEstimateId = if (isExpanded) null else estimate.id },
                        onShareWhatsApp = {
                            val text = formatEstimateToWhatsApp(estimate, language)
                            viewModel.shareWhatsApp(context, text, estimate.clientPhone)
                        },
                        onCopy = {
                            val text = formatEstimateToWhatsApp(estimate, language)
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(
                                context,
                                when (language) {
                                    AppLanguage.DERJA, AppLanguage.ARABIC -> "تم نسخ التقرير!"
                                    AppLanguage.FRENCH -> "Devis copié !"
                                    AppLanguage.ENGLISH -> "Report copied!"
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onExportPdf = {
                            runCatching { EstimatePdfExporter.share(context, estimate) }
                                .onFailure {
                                    Toast.makeText(context, "Impossible de créer le PDF : ${it.localizedMessage ?: "erreur"}", Toast.LENGTH_LONG).show()
                                }
                        },
                        onDelete = {
                            viewModel.deleteEstimate(estimate.id)
                            Toast.makeText(
                                context,
                                when (language) {
                                    AppLanguage.DERJA, AppLanguage.ARABIC -> "تم حذف التقرير"
                                    AppLanguage.FRENCH -> "Devis supprimé"
                                    AppLanguage.ENGLISH -> "Quote deleted"
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}
/**
 * PHASE 2.4 — Workspace hero. Navy command banner introducing the Estimates /
 * Devis workspace and surfacing only REAL KPIs already available in app state:
 * number of saved estimates, last quote date, estimated total (native currency).
 */
@Composable
private fun DevisWorkspaceHero(
    language: AppLanguage,
    estimateCount: Int,
    hasData: Boolean,
    totalEstimated: Double,
    currency: String,
    latestDateText: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KonstrivoSpacing.Lg, vertical = KonstrivoSpacing.Sm),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KonstrivoNavy),
        border = BorderStroke(1.dp, KonstrivoAmber.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(KonstrivoNavy, Color(0xFF1E293B), KonstrivoDarkNavy)
                    )
                )
                .padding(KonstrivoSpacing.Lg)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(
                                1.dp,
                                KonstrivoAmber.copy(alpha = 0.55f),
                                RoundedCornerShape(KonstrivoRadius.IconBox)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = KonstrivoAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (language) {
                                AppLanguage.DERJA -> "تقارير وعروض الأسعار"
                                AppLanguage.ARABIC -> "عروض الأسعار والتقارير"
                                AppLanguage.FRENCH -> "DEVIS & RAPPORTS"
                                AppLanguage.ENGLISH -> "ESTIMATES & REPORTS"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.DERJA -> "كل عروض الأسعار والتقارير المتعلقة بالشانتي"
                                AppLanguage.ARABIC -> "عروض وتقديرات مشاريعك"
                                AppLanguage.FRENCH -> "Vos estimations et devis de chantier"
                                AppLanguage.ENGLISH -> "Your project estimates and quotes"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)) {
                    WorkspaceStatChip(
                        value = "$estimateCount",
                        label = when (language) {
                            AppLanguage.DERJA -> "عرض مسجل"
                            AppLanguage.ARABIC -> "عرض محفوظ"
                            AppLanguage.FRENCH -> "devis enregistrés"
                            AppLanguage.ENGLISH -> "saved estimates"
                        },
                        modifier = Modifier.weight(1f)
                    )

                    if (hasData && latestDateText != null) {
                        WorkspaceStatChip(
                            value = latestDateText,
                            label = when (language) {
                                AppLanguage.DERJA -> "آخر عرض"
                                AppLanguage.ARABIC -> "آخر عرض"
                                AppLanguage.FRENCH -> "dernier devis"
                                AppLanguage.ENGLISH -> "last quote"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        WorkspaceStatChip(
                            // Same 2-decimal truncation rule previously used by the header badge.
                            value = "${(totalEstimated * 100).toLong() / 100.0} $currency",
                            label = when (language) {
                                AppLanguage.DERJA -> "المجموع الكلي"
                                AppLanguage.ARABIC -> "إجمالي التقديرات"
                                AppLanguage.FRENCH -> "total estimé"
                                AppLanguage.ENGLISH -> "estimated total"
                            },
                            modifier = Modifier.weight(1.15f)
                        )
                    }
                }
            }
        }
    }
}
/** Compact navy-tinted stat tile used inside the workspace hero. */
@Composable
private fun WorkspaceStatChip(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(KonstrivoRadius.IconBox),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp,
                color = KonstrivoAmber.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * PHASE 2.4 — Premium empty state for the estimates workspace, wired to the
 * EXISTING callback (selectTab(CALCULATOR)). No new workflow introduced.
 */
@Composable
private fun EstimatesEmptyState(
    language: AppLanguage,
    onStartCalculation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KonstrivoSpacing.Xl),
            shape = RoundedCornerShape(KonstrivoRadius.Card),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KonstrivoSpacing.Lg, vertical = KonstrivoSpacing.Xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(KonstrivoNavy.copy(alpha = if (MaterialTheme.colorScheme.surface.luminance() > 0.5f) 0.06f else 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.height(KonstrivoSpacing.Xs))
                Text(
                    text = when (language) {
                        AppLanguage.DERJA -> "ماكاش حتى عرض مسجل"
                        AppLanguage.ARABIC -> "لا توجد عروض محفوظة"
                        AppLanguage.FRENCH -> "Aucun devis enregistré"
                        AppLanguage.ENGLISH -> "No saved estimates"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = when (language) {
                        AppLanguage.DERJA -> "دير الحساب من الحاسبة الذكية و سجل الـ Devis تاعك هون"
                        AppLanguage.ARABIC -> "ابدأ عملية حسابية ثم احفظ العرض ليظهر هنا"
                        AppLanguage.FRENCH -> "Lancez un calcul puis enregistrez le devis pour le retrouver ici"
                        AppLanguage.ENGLISH -> "Run a calculation and save the quote to see it here"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(KonstrivoSpacing.Sm))
                KonstrivoPrimaryButton(
                    text = when (language) {
                        AppLanguage.DERJA -> "ابدأ الحساب الآن"
                        AppLanguage.ARABIC -> "بدء حساب جديد"
                        AppLanguage.FRENCH -> "Commencer un calcul"
                        AppLanguage.ENGLISH -> "Start a calculation"
                    },
                    onClick = onStartCalculation,
                    icon = Icons.Default.Calculate
                )
            }
        }
    }
}

/**
 * PHASE 2.4 — Estimate rendered as a technical document card:
 * navy document header → client/project block → engineering specs →
 * amber TTC totals band → actions. Data sources are 100% unchanged.
 */
@Composable
private fun EstimateDocumentCard(
    estimate: ProjectEstimateEntity,
    language: AppLanguage,
    typeTitle: String,
    dateText: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onShareWhatsApp: () -> Unit,
    onCopy: () -> Unit,
    onExportPdf: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("estimate_item_${estimate.id}"),
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // ── Document header band (navy): service type + date ──────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KonstrivoNavy)
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = KonstrivoSpacing.Md, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                        .background(Color.White.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = typeTitle.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = when (language) {
                            AppLanguage.DERJA -> "الديڤيز رقم ${estimate.id}"
                            AppLanguage.ARABIC -> "عرض السعر رقم ${estimate.id}"
                            AppLanguage.FRENCH -> "Devis #${estimate.id}"
                            AppLanguage.ENGLISH -> "Quote #${estimate.id}"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(shape = RoundedCornerShape(KonstrivoRadius.IconBox), color = Color.White.copy(alpha = 0.12f)) {
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = KonstrivoAmber
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(KonstrivoSpacing.CardInner),
                verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
            ) {
                // ── Client / project identity ─────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(KonstrivoGold.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (if (estimate.clientName.isNotEmpty()) estimate.clientName else "?")
                                .take(1).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = KonstrivoNavy
                        )
                    }
                    Text(
                        text = if (estimate.clientName.isNotEmpty()) estimate.clientName
                        else when (language) {
                            AppLanguage.DERJA -> "حريف"
                            AppLanguage.ARABIC -> "عميل خاص"
                            AppLanguage.FRENCH -> "Client Particulier"
                            AppLanguage.ENGLISH -> "Private Client"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (estimate.clientPhone.isNotEmpty()) {
                    Text(
                        text = "☎ ${estimate.clientPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (estimate.location.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = KonstrivoGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = estimate.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // ── Engineering specs row ─────────────────────────────────────
                if (estimate.inputArea > 0.0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)) {
                        SpecChip(
                            label = when (language) {
                                AppLanguage.DERJA -> "المساحة"
                                AppLanguage.ARABIC -> "المساحة"
                                AppLanguage.FRENCH -> "Surface"
                                AppLanguage.ENGLISH -> "Area"
                            },
                            value = "${estimate.inputArea} m²"
                        )
                    }
                }
                // ── Subtotals (existing data, existing wording) ────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KonstrivoPriceText(
                        text = "${when (language) {
                            AppLanguage.DERJA -> "السلعة"
                            AppLanguage.ARABIC -> "المواد"
                            AppLanguage.FRENCH -> "Fournitures"
                            AppLanguage.ENGLISH -> "Supplies"
                        }}: ${estimate.totalMaterialCost} ${estimate.currency}",
                        style = KonstrivoPriceStyle.Small
                    )
                    KonstrivoPriceText(
                        text = "${when (language) {
                            AppLanguage.DERJA -> "اليد العاملة"
                            AppLanguage.ARABIC -> "المصنعية"
                            AppLanguage.FRENCH -> "Main d'œuvre"
                            AppLanguage.ENGLISH -> "Labor"
                        }}: ${estimate.laborCost} ${estimate.currency}",
                        style = KonstrivoPriceStyle.Small,
                        color = KonstrivoBlue
                    )
                }

                // ── Expanded technical detail ─────────────────────────────────
                AnimatedVisibility(visible = isExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)) {
                        if (estimate.location.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = KonstrivoGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = estimate.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        val detailRows = buildList {
                            add(
                                when (language) {
                                    AppLanguage.DERJA -> "الخدمة"
                                    AppLanguage.ARABIC -> "الخدمة"
                                    AppLanguage.FRENCH -> "Service"
                                    AppLanguage.ENGLISH -> "Service"
                                } to typeTitle
                            )
                            if (estimate.inputArea > 0.0) {
                                add(
                                    when (language) {
                                        AppLanguage.DERJA -> "المساحة"
                                        AppLanguage.ARABIC -> "المساحة"
                                        AppLanguage.FRENCH -> "Surface"
                                        AppLanguage.ENGLISH -> "Area"
                                    } to "${estimate.inputArea} m²"
                                )
                            }
                        }
                        detailRows.forEach { (label, value) ->
                            DetailRow(label = label, value = value)
                        }

                        // Materials breakdown — same JSON source as the WhatsApp report.
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                        Text(
                            text = when (language) {
                                AppLanguage.DERJA -> "تفصيل السلعة:"
                                AppLanguage.ARABIC -> "تفاصيل المواد المسجلة:"
                                AppLanguage.FRENCH -> "Détail des Matériaux:"
                                AppLanguage.ENGLISH -> "Materials Detail:"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        val materialsArray = try {
                            JSONArray(estimate.materialsJson)
                        } catch (e: Exception) {
                            JSONArray()
                        }
                        for (i in 0 until materialsArray.length()) {
                            val obj = materialsArray.getJSONObject(i)
                            val mName = when (language) {
                                AppLanguage.DERJA -> obj.optString("nameDerja", obj.optString("nameFr"))
                                AppLanguage.ARABIC -> obj.optString("nameAr", obj.optString("nameFr"))
                                AppLanguage.FRENCH -> obj.optString("nameFr")
                                AppLanguage.ENGLISH -> obj.optString("nameFr")
                            }
                            val qty = obj.optDouble("quantity", 0.0)
                            val unit = obj.optString("unit", "")
                            val tot = obj.optDouble("totalPrice", 0.0)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "• $mName (${qty.toInt()} $unit)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                KonstrivoPriceText(
                                    text = "$tot ${estimate.currency}",
                                    style = KonstrivoPriceStyle.Small
                                )
                            }
                        }

                        if (estimate.notes.isNotEmpty()) {
                            Text(
                                text = "${when (language) {
                                    AppLanguage.DERJA, AppLanguage.ARABIC -> "ملاحظات"
                                    AppLanguage.FRENCH -> "Notes"
                                    AppLanguage.ENGLISH -> "Notes"
                                }}: ${estimate.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                // ── Financial section ─────────────────────────────────────────
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.DERJA -> "المجموع الكلي"
                                AppLanguage.ARABIC -> "الإجمالي شامل الضريبة"
                                AppLanguage.FRENCH -> "TOTAL TTC"
                                AppLanguage.ENGLISH -> "TOTAL TTC"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        KonstrivoPriceText(
                            text = "${estimate.grandTotal} ${estimate.currency}",
                            style = KonstrivoPriceStyle.Large,
                            color = KonstrivoAmber
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // ── Actions: existing behaviors only (share / copy / delete) ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                        color = KonstrivoEmerald.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, KonstrivoEmerald.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable(onClick = onShareWhatsApp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 9.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = KonstrivoEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.DERJA -> "واتساب"
                                    AppLanguage.ARABIC -> "مشاركة واتساب"
                                    AppLanguage.FRENCH -> "WhatsApp"
                                    AppLanguage.ENGLISH -> "WhatsApp"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = KonstrivoEmerald
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        TextButton(onClick = onExportPdf, modifier = Modifier.height(48.dp)) {
                            Text("PDF", fontWeight = FontWeight.Bold, color = KonstrivoNavy)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        IconButton(onClick = onCopy, modifier = Modifier.size(48.dp)) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = KonstrivoGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(KonstrivoRadius.IconBox),
                        color = KonstrivoError.copy(alpha = 0.10f)
                    ) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(48.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = KonstrivoError,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
/**
 * PHASE 2.4 — VERBATIM CARRY-OVER (unchanged formatting contract).
 * Builds the WhatsApp quote text exactly as before this redesign:
 * same headers, prices, quantities, currency, emojis and language branches.
 */
/** Technical spec pill: muted caption + bold blue value (surface, quantity…). */
@Composable
private fun SpecChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(KonstrivoRadius.IconBox),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = KonstrivoGold
            )
        }
    }
}

/** Label/value row used inside the expanded technical detail block. */
@Composable
private fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatEstimateToWhatsApp(estimate: ProjectEstimateEntity, language: AppLanguage): String {
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(estimate.createdAt))
    val sb = StringBuilder()

    when (language) {
        AppLanguage.DERJA -> {
            sb.append("📋 *عرض أسعار الشانتي - Placo Tunisie*\n")
            sb.append("📅 التاريخ: $dateStr\n")
            sb.append("👤 الحريف: ${estimate.clientName}\n")
            sb.append("📍 المكان: ${estimate.location}\n")
            sb.append("📐 المساحة: ${estimate.inputArea} م²\n")
            sb.append("━━━━━━━━━━━━━━━━━━\n")
            sb.append("📦 *تفصيل السلعة والمواد المطلوبة:*\n")
            try {
                val array = JSONArray(estimate.materialsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val mName = obj.optString("nameDerja", obj.optString("nameFr"))
                    val qty = obj.optDouble("quantity", 0.0)
                    val unit = obj.optString("unit", "")
                    val tot = obj.optDouble("totalPrice", 0.0)
                    sb.append("▫️ $mName: ${qty.toInt()} $unit | $tot ${estimate.currency}\n")
                }
            } catch (e: Exception) {}
            sb.append("━━━━━━━━━━━━━━━━━━\n")
            sb.append("💰 *كلفة السلعة:* ${estimate.totalMaterialCost} ${estimate.currency}\n")
            sb.append("👷 *اليد العاملة (الخدمة):* ${estimate.laborCost} ${estimate.currency}\n")
            sb.append("✨ *المجموع الكلي التقديري:* *${estimate.grandTotal} ${estimate.currency}*\n")
            if (estimate.notes.isNotEmpty()) {
                sb.append("📝 ملاحظات: ${estimate.notes}\n")
            }
        }
        AppLanguage.ARABIC -> {
            sb.append("📋 *عرض سعر وتقدير مشروع بناء*\n")
            sb.append("📅 التاريخ: $dateStr\n")
            sb.append("👤 العميل: ${estimate.clientName}\n")
            sb.append("📍 الموقع: ${estimate.location}\n")
            sb.append("📐 المساحة: ${estimate.inputArea} م²\n")
            sb.append("━━━━━━━━━━━━━━━━━━\n")
            sb.append("📦 *قائمة المواد:*\n")
            try {
                val array = JSONArray(estimate.materialsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val mName = obj.optString("nameAr", obj.optString("nameFr"))
                    val qty = obj.optDouble("quantity", 0.0)
                    val unit = obj.optString("unit", "")
                    val tot = obj.optDouble("totalPrice", 0.0)
                    sb.append("▫️ $mName: ${qty.toInt()} $unit | $tot ${estimate.currency}\n")
                }
            } catch (e: Exception) {}
            sb.append("━━━━━━━━━━━━━━━━━━\n")
            sb.append("💰 *إجمالي المواد:* ${estimate.totalMaterialCost} ${estimate.currency}\n")
            sb.append("👷 *المصنعية / اليد العاملة:* ${estimate.laborCost} ${estimate.currency}\n")
            sb.append("✨ *المجموع الإجمالي:* *${estimate.grandTotal} ${estimate.currency}*\n")
        }
        AppLanguage.FRENCH -> {
            sb.append("📋 *DEVIS ESTIMATIF ET QUANTITATIF*\n")
            sb.append("📅 Date: $dateStr\n")
            sb.append("👤 Client: ${estimate.clientName}\n")
            sb.append("📍 Chantier: ${estimate.location}\n")
            sb.append("📐 Surface: ${estimate.inputArea} m²\n")
            sb.append("━━━━━━━━━━━━━━━━━━\n")
            sb.append("📦 *DÉTAIL DES FOURNITURES:*\n")
            try {
                val array = JSONArray(estimate.materialsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val mName = obj.optString("nameFr")
                    val qty = obj.optDouble("quantity", 0.0)
                    val unit = obj.optString("unit", "")
                    val tot = obj.optDouble("totalPrice", 0.0)
                    sb.append("▫️ $mName: ${qty.toInt()} $unit (${tot} ${estimate.currency})\n")
                }
            } catch (e: Exception) {}
            sb.append("━━━━━━━━━━━━━━━━━━\n")
            sb.append("💰 *Total Fournitures:* ${estimate.totalMaterialCost} ${estimate.currency}\n")
            sb.append("👷 *Main d'œuvre:* ${estimate.laborCost} ${estimate.currency}\n")
            sb.append("✨ *TOTAL ESTIMÉ:* *${estimate.grandTotal} ${estimate.currency}*\n")
        }
        AppLanguage.ENGLISH -> {
            sb.append("📋 *CONSTRUCTION ESTIMATE & BILL OF QUANTITIES*\n")
            sb.append("📅 Date: $dateStr\n")
            sb.append("👤 Client: ${estimate.clientName}\n")
            sb.append("📍 Location: ${estimate.location}\n")
            sb.append("📐 Surface: ${estimate.inputArea} m²\n")
            sb.append("━━━━━━━━━━━━━━━━━━\n")
            sb.append("📦 *BILL OF MATERIALS & SUPPLIES:*\n")
            try {
                val array = JSONArray(estimate.materialsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val mName = obj.optString("nameFr")
                    val qty = obj.optDouble("quantity", 0.0)
                    val unit = obj.optString("unit", "")
                    val tot = obj.optDouble("totalPrice", 0.0)
                    sb.append("▫️ $mName: ${qty.toInt()} $unit (${tot} ${estimate.currency})\n")
                }
            } catch (e: Exception) {}
            sb.append("━━━━━━━━━━━━━━━━━━\n")
            sb.append("💰 *Total Supplies:* ${estimate.totalMaterialCost} ${estimate.currency}\n")
            sb.append("👷 *Labor Cost:* ${estimate.laborCost} ${estimate.currency}\n")
            sb.append("✨ *GRAND TOTAL ESTIMATED:* *${estimate.grandTotal} ${estimate.currency}*\n")
        }
    }

    return sb.toString()
}