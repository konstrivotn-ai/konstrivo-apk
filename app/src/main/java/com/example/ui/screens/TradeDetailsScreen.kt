package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationType
import com.example.data.model.Country
import com.example.domain.model.Trade
import com.example.domain.model.TradeStatus
import com.example.domain.model.WorkType
import com.example.domain.model.WorkTypeStatus
import com.example.ui.components.designsystem.KonstrivoBadge
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.theme.*
import com.example.ui.components.designsystem.localizedName
import com.example.ui.components.designsystem.tr

/**
 * Generic, reusable Trade Details Screen (شاشة تفاصيل المهنة والتخصص).
 * Displays full trade overview, technical work types, and instant calculator actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDetailsScreen(
    trade: Trade,
    language: AppLanguage,
    country: Country,
    onBack: () -> Unit,
    onNavigateToCalculator: (CalculationType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedWorkTypeForInfo by remember { mutableStateOf<WorkType?>(null) }

    val activeWorkTypesCount = remember(trade) {
        trade.workTypes.count { it.status == WorkTypeStatus.ACTIVE }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = trade.localizedName(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("trade_details_btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = language.tr("Retour", "Back", "رجوع", "رجوع")
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("trade_details_screen_${trade.id}")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Trade Hero Header Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = KonstrivoNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        KonstrivoNavy,
                                        Color(0xFF1E293B),
                                        KonstrivoDarkNavy
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .border(1.dp, KonstrivoAmber.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = trade.iconEmoji,
                                        fontSize = 28.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (trade.status) {
                                        TradeStatus.ACTIVE -> KonstrivoEmerald.copy(alpha = 0.2f)
                                        TradeStatus.IN_DEVELOPMENT -> KonstrivoAmber.copy(alpha = 0.2f)
                                        TradeStatus.COMING_SOON -> Color.White.copy(alpha = 0.15f)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (trade.status) {
                                                        TradeStatus.ACTIVE -> KonstrivoEmerald
                                                        TradeStatus.IN_DEVELOPMENT -> KonstrivoAmber
                                                        TradeStatus.COMING_SOON -> Color.White
                                                    }
                                                )
                                        )
                                        Text(
                                            text = trade.localizedStatusLabel(language),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Text(
                                text = trade.localizedName(language),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Text(
                                text = trade.localizedDescription(language),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 20.sp
                            )

                            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = language.tr("Corps d'état: ${trade.workTypes.size} prestations","Corps d'état: ${trade.workTypes.size} prestations","التخصص: ${trade.workTypes.size} خدمات وأعمال","التخصص: ${trade.workTypes.size} خدمات وأعمال"),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.75f)
                                )

                                Text(
                                    text = language.tr("$activeWorkTypesCount calculateurs actifs","$activeWorkTypesCount calculateurs actifs","$activeWorkTypesCount حاسبة نشطة","$activeWorkTypesCount حاسبة نشطة"),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = KonstrivoAmber
                                )
                            }
                        }
                    }
                }
            }

            // 2. Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (language) {
                            AppLanguage.DERJA -> "🛠️ تفصيل الخدمات والأعمال (Les Travaux)"
                            AppLanguage.ARABIC -> "🛠️ أنواع الأعمال والخدمات الفنية"
                            AppLanguage.FRENCH -> "🛠️ Types de Travaux & Prestations"
                            AppLanguage.ENGLISH -> "🛠️ Work Types & Operations"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3. List of Work Types
            items(trade.workTypes, key = { it.id }) { workType ->
                WorkTypeCard(
                    workType = workType,
                    language = language,
                    country = country,
                    onCalculate = { calcType ->
                        onNavigateToCalculator(calcType)
                    },
                    onInfoClick = {
                        selectedWorkTypeForInfo = workType
                    }
                )
            }
        }
    }

    // Informative Sheet for In-Development or Coming Soon items
    selectedWorkTypeForInfo?.let { wt ->
        AlertDialog(
            onDismissRequest = { selectedWorkTypeForInfo = null },
            icon = { Text(wt.iconEmoji, fontSize = 32.sp) },
            title = {
                Text(
                    text = wt.localizedName(language),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    KonstrivoBadge(
                        text = wt.localizedStatusLabel(language),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    Text(
                        text = wt.localizedDescription(language),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    HorizontalDivider()

                    Text(
                        text = language.tr("Les règles de calcul et de dosage pour cette prestation font l'objet d'une standardisation technique rigoureuse (DTU et pratiques du marché local ${country.nameFr}) afin de garantir des résultats certifiés sans approximation.","Les règles de calcul et de dosage pour cette prestation font l'objet d'une standardisation technique rigoureuse (DTU et pratiques du marché local ${country.nameFr}) afin de garantir des résultats certifiés sans approximation.","يتم إعداد معادلات الاستهلاك والجرعات الفنية لهذه الخدمة وفقًا للمعايير المهنية المعمول بها في ${country.nameAr} لضمان دقة كاملة دون أرقام تقديرية عشوائية.","يتم إعداد معادلات الاستهلاك والجرعات الفنية لهذه الخدمة وفقًا للمعايير المهنية المعمول بها في ${country.nameAr} لضمان دقة كاملة دون أرقام تقديرية عشوائية."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedWorkTypeForInfo = null },
                    colors = ButtonDefaults.buttonColors(containerColor = KonstrivoNavy)
                ) {
                    Text(language.tr("Compris","Compris","حسناً","حسناً"))
                }
            }
        )
    }
}

@Composable
private fun WorkTypeCard(
    workType: WorkType,
    language: AppLanguage,
    country: Country,
    onCalculate: (CalculationType) -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = workType.status == WorkTypeStatus.ACTIVE && workType.calculationType != null

    Card(
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 2.dp else 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("work_type_card_${workType.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = workType.iconEmoji,
                        fontSize = 24.sp
                    )
                    Column {
                        Text(
                            text = workType.localizedName(language),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = language.tr("Unité de mesure: ${workType.unit}","Unité de mesure: ${workType.unit}","وحدة القياس: ${workType.unit}","وحدة القياس: ${workType.unit}"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                KonstrivoBadge(
                    text = workType.localizedStatusLabel(language),
                    containerColor = when (workType.status) {
                        WorkTypeStatus.ACTIVE -> KonstrivoEmerald.copy(alpha = 0.15f)
                        WorkTypeStatus.IN_DEVELOPMENT -> KonstrivoAmber.copy(alpha = 0.15f)
                        WorkTypeStatus.COMING_SOON -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    },
                    contentColor = when (workType.status) {
                        WorkTypeStatus.ACTIVE -> KonstrivoEmerald
                        WorkTypeStatus.IN_DEVELOPMENT -> KonstrivoAmber
                        WorkTypeStatus.COMING_SOON -> MaterialTheme.colorScheme.outline
                    }
                )
            }

            Text(
                text = workType.localizedDescription(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isActive && workType.calculationType != null) {
                    Button(
                        onClick = { onCalculate(workType.calculationType) },
                        colors = ButtonDefaults.buttonColors(containerColor = KonstrivoAmber),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_calculate_${workType.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = language.tr("Lancer le Calcul","Lancer le Calcul","بدء الحساب الفوري","بدء الحساب الفوري"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onInfoClick,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_info_${workType.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = language.tr("Détails Techniques","Détails Techniques","التفاصيل الفنية","التفاصيل الفنية"),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
