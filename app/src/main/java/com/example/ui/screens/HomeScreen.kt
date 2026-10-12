package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEstimateEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.model.*
import com.example.domain.model.Trade
import com.example.domain.model.TradeStatus
import com.example.domain.trade.TradeCatalog
import com.example.ui.components.RoomPresetsRow
import com.example.ui.components.designsystem.KonstrivoBadge
import com.example.ui.components.designsystem.KonstrivoBrandMark
import com.example.ui.components.designsystem.KonstrivoCard
import com.example.ui.components.designsystem.KonstrivoEmptyState
import com.example.ui.components.designsystem.KonstrivoPrimaryButton
import com.example.ui.components.designsystem.KonstrivoPriceStyle
import com.example.ui.components.designsystem.KonstrivoPriceText
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSectionHeader
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.ui.components.designsystem.localizedName
import com.example.ui.components.designsystem.tr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.selectedLanguage.collectAsState()
    val country by viewModel.selectedCountry.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val savedEstimates by viewModel.savedEstimates.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }

    var showCountryDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Professional Header
        item {
            ProfessionalHeader(
                language = language,
                country = country,
                userSettings = userSettings,
                isAdmin = isAdmin,
                currentYear = currentYear,
                onProfileClick = { showProfileDialog = true },
                onCountryClick = { showCountryDialog = true }
            )
        }

        // 2. Hero Command Center
        item {
            HeroCommandCenter(
                language = language,
                savedEstimatesCount = savedEstimates.size,
                onNewCalculation = { viewModel.navigateToCalculator() },
                onViewEstimates = { viewModel.selectTab(AppTab.ESTIMATES) },
                onMarketPrices = { viewModel.selectTab(AppTab.PRICES) }
            )
        }

        // Reference board: Mes Chantiers Actifs (REAL counts only).
        item {
            HomeActiveCounters(language = language, estimateCount = savedEstimates.size)
        }

        // Reference board: Notifications strip (REAL quotes only).
        item {
            HomeNotificationsStrip(
                language = language,
                estimates = savedEstimates,
                onOpenAll = { viewModel.openNotifications() },
                onOpenEstimate = { viewModel.selectTab(AppTab.ESTIMATES) },
            )
        }

        // 3. Quick Actions Grid
        item {
            QuickActionsGrid(
                language = language,
                country = country,
                currentYear = currentYear,
                savedEstimatesCount = savedEstimates.size,
                onCalculatorClick = { viewModel.navigateToCalculator() },
                onPricesClick = { viewModel.selectTab(AppTab.PRICES) },
                onEstimatesClick = { viewModel.selectTab(AppTab.ESTIMATES) },
                onGuideClick = { viewModel.selectTab(AppTab.GUIDE) },
                onProfileClick = { showProfileDialog = true },
                onSettingsClick = { viewModel.selectTab(AppTab.SETTINGS) }
            )
        }

        // 4. Quick Room Presets
        item {
            RoomPresetsRow(
                onPresetSelected = { length, width ->
                    viewModel.setInputLength(length.toString())
                    viewModel.setInputWidth(width.toString())
                    viewModel.navigateToCalculator(TradeCategory.PLACO, CalculationType.PLACO_PLAFOND_FIXE)
                },
                language = language
            )
        }

        // 5. Trades Section
        item {
            TradesSection(
                language = language,
                onTradeSelected = { trade -> viewModel.openTradeDetails(trade) }
            )
        }

        // 6. Recent Activity
        item {
            RecentActivitySection(
                language = language,
                savedEstimates = savedEstimates,
                onViewAll = { viewModel.selectTab(AppTab.ESTIMATES) },
                onShareWhatsApp = { estimate ->
                    val text = "📋 *${estimate.calculationTypeId}* - ${estimate.totalMaterialCost + estimate.laborCost} ${estimate.currency}\n👤 ${estimate.clientName} (${estimate.clientPhone})"
                    viewModel.shareWhatsApp(context, text, estimate.clientPhone)
                },
                onViewEstimates = { viewModel.selectTab(AppTab.ESTIMATES) }
            )
        }
    }

    // Dialogs
    if (showCountryDialog) {
        CountrySelectionDialog(
            language = language,
            currentCountry = country,
            onCountrySelected = { viewModel.setCountry(it) },
            onDismiss = { showCountryDialog = false }
        )
    }

    if (showProfileDialog) {
        ProfileDialog(
            language = language,
            userSettings = userSettings,
            onEditInSettings = {
                showProfileDialog = false
                viewModel.selectTab(AppTab.SETTINGS)
            },
            onDismiss = { showProfileDialog = false }
        )
    }
}

@Composable
fun ProfessionalHeader(
    language: AppLanguage,
    country: Country,
    userSettings: UserSettingsEntity,
    isAdmin: Boolean,
    currentYear: Int,
    onProfileClick: () -> Unit,
    onCountryClick: () -> Unit
) {
    Surface(color = KonstrivoNavy, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = KonstrivoSpacing.Md, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
        ) {
            KonstrivoBrandMark(size = 36.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "KONSTRIVO PRO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = KonstrivoAmber
                )
                Text(
                    text = "$currentYear · ${language.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
            if (isAdmin) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = language.tr("Administrateur", "Admin", "مدير", "مدير"),
                    tint = KonstrivoAmber,
                    modifier = Modifier.size(18.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .clickable(onClick = onCountryClick)
                    .heightIn(min = 48.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = country.flag, fontSize = 14.sp)
                    Text(
                        text = country.currencyCode,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            IconButton(onClick = onProfileClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = language.tr("Profil de l'entreprise","Company profile","ملف الشركة","ملف الشركة"),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun HeroCommandCenter(
    language: AppLanguage,
    savedEstimatesCount: Int,
    onNewCalculation: () -> Unit,
    onViewEstimates: () -> Unit,
    onMarketPrices: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF1E2B4E), Color(0xFF131C33)))) // Reference: navy hero card (no blue)
                .padding(KonstrivoSpacing.Lg)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
                ) {
                    Surface(shape = RoundedCornerShape(50), color = KonstrivoAmber.copy(alpha = 0.22f)) {
                        Text(
                            text = "PRO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = KonstrivoAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = language.tr("Calculateur Pro","Pro Calculator","الحاسبة الاحترافية","الحاسبة الاحترافية"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = language.tr(
                        "Estimez matériaux, main d'œuvre et prix marché en quelques secondes.",
                        "Estimate materials, labor and market prices in seconds.",
                        "قدّر المواد وأجور العمل وأسعار السوق في ثوانٍ.",
                        "قدّر المواد وأجور العمل وأسعار السوق في ثوانٍ."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.82f)
                )
                KonstrivoPrimaryButton(
                    text = language.tr("Nouveau calcul","New calculation","حساب جديد","حساب جديد"),
                    onClick = onNewCalculation,
                    icon = Icons.Default.Calculate,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)) {
                    HeroQuickLink(
                        label = language.tr("Tarifs Marché","Market Prices","أسعار السوق","أسعار السوق"),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        onClick = onMarketPrices,
                        modifier = Modifier.weight(1f)
                    )
                    HeroQuickLink(
                        label = if (savedEstimatesCount > 0) {
                            "${language.tr("Devis","Estimates","التقديرات","التقديرات")} ($savedEstimatesCount)"
                        } else {
                            language.tr("Devis","Estimates","التقديرات","التقديرات")
                        },
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        onClick = onViewEstimates,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroQuickLink(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(KonstrivoRadius.Badge),
        color = Color.White.copy(alpha = 0.14f),
        modifier = modifier
            .clickable(onClick = onClick)
            .heightIn(min = 48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = KonstrivoAmber,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun CountrySelectionDialog(
    language: AppLanguage,
    currentCountry: Country,
    onCountrySelected: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
            title = {
                Text(
                    text = language.tr("Sélectionner le Pays & Marché","Sélectionner le Pays & Marché","اختيار الدولة وسوق الأسعار","اختيار الدولة وسوق الأسعار"),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Country.entries) { item ->
                        val isSelected = item == currentCountry
                        Surface(
                            onClick = {
                                onCountrySelected(item)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(item.flag, fontSize = 22.sp)
                                    Column {
                                        Text(
                                            text = item.localizedName(language),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                        Text(
                                            text = "${item.currencyCode} (${item.currencySymbol})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(language.tr("Fermer","Fermer","إغلاق","إغلاق"))
                }
            }
        )
}

@Composable
fun ProfileDialog(
    language: AppLanguage,
    userSettings: UserSettingsEntity,
    onEditInSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
            title = {
                Text(
                    text = language.tr("Profil de l'Entreprise / Artisan","Profil de l'Entreprise / Artisan","بيانات وهوية المقاول والشركة","بيانات وهوية المقاول والشركة"),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = language.tr(
                            fr = "Les coordonnées ci-dessous apparaissent sur vos devis et messages WhatsApp partagés avec vos clients.",
                            en = "The details below appear on your estimates and WhatsApp messages shared with clients.",
                            ar = "هذه البيانات تظهر في ترويسة عروض الأسعار والرسائل الموجهة للزبائن.",
                            dz = "هذه البيانات تظهر في ترويسة عروض الأسعار والرسائل الموجهة للزبائن."
                        )
                    )
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "🏢 ${if (userSettings.companyHeader.isNotEmpty()) userSettings.companyHeader else "Non défini"}",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "👤 ${if (userSettings.contractorName.isNotEmpty()) userSettings.contractorName else "Non défini"}")
                            Text(text = "📞 ${if (userSettings.contractorPhone.isNotEmpty()) userSettings.contractorPhone else "Non défini"}")
                        }
                    }
                }
            },
        confirmButton = {
            Button(onClick = onEditInSettings) {
                Text(language.tr("Modifier dans Paramètres","Modifier dans Paramètres","تعديل في الإعدادات","تعديل في الإعدادات"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(language.tr("Fermer","Fermer","إغلاق","إغلاق"))
            }
        }
    )
}

private data class QuickAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val badge: String? = null,
    val isPrimary: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun QuickActionsGrid(
    language: AppLanguage,
    country: Country,
    currentYear: Int,
    savedEstimatesCount: Int,
    onCalculatorClick: () -> Unit,
    onPricesClick: () -> Unit,
    onEstimatesClick: () -> Unit,
    onGuideClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val actions = listOf(
        QuickAction(
            title = language.tr("Calculateur","Calculator","الحاسبة","الحاسبة"),
            subtitle = language.tr("Devis matériaux complet","Full material estimate","تقدير شامل للمواد","تقدير شامل للمواد"),
            icon = Icons.Default.Calculate,
            accent = KonstrivoAmber,
            isPrimary = true,
            onClick = onCalculatorClick
        ),
        QuickAction(
            title = language.tr("Tarifs Marché","Market Prices","أسعار السوق","أسعار السوق"),
            subtitle = country.localizedName(language),
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            accent = KonstrivoGold,
            onClick = onPricesClick
        ),
        QuickAction(
            title = language.tr("Devis","Estimates","التقديرات","التقديرات"),
            subtitle = language.tr("Historique & partage","History & sharing","السجل والمشاركة","السجل والمشاركة"),
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            accent = KonstrivoGold,
            badge = if (savedEstimatesCount > 0) savedEstimatesCount.toString() else null,
            onClick = onEstimatesClick
        ),
        QuickAction(
            title = language.tr("Guide Chantier","Site Guide","دليل الورش","دليل الورش"),
            subtitle = language.tr("Méthodes & techniques","Methods & techniques","الطرق والتقنيات","الطرق والتقنيات"),
            icon = Icons.AutoMirrored.Filled.MenuBook,
            accent = KonstrivoGold,
            onClick = onGuideClick
        ),
        QuickAction(
            title = language.tr("Profil Entreprise","Company Profile","ملف الشركة","ملف الشركة"),
            subtitle = language.tr("Identité sur les devis","Identity on estimates","الهوية في التقديرات","الهوية في التقديرات"),
            icon = Icons.Default.Business,
            accent = KonstrivoNavy,
            onClick = onProfileClick
        ),
        QuickAction(
            title = language.tr("Paramètres","Settings","الإعدادات","الإعدادات"),
            subtitle = language.tr("Langue, pays, devise","Language, country, currency","اللغة والبلد والعملة","اللغة والبلد والعملة"),
            icon = Icons.Default.Settings,
            accent = KonstrivoAmber,
            onClick = onSettingsClick
        )
    )
    Column(verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)) {
        KonstrivoSectionHeader(
            title = language.tr("Outils & Services","Tools & Services","الأدوات والخدمات","الأدوات والخدمات"),
            icon = Icons.Default.Apps
        )
        actions.chunked(2).forEach { rowActions ->
            Row(horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)) {
                rowActions.forEach { action ->
                    HubActionCard(
                        title = action.title,
                        subtitle = action.subtitle,
                        icon = action.icon,
                        badge = action.badge,
                        accentColor = action.accent,
                        onClick = action.onClick,
                        isPrimary = action.isPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowActions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TradesSection(
    language: AppLanguage,
    onTradeSelected: (Trade) -> Unit
) {
    val trades = remember { TradeCatalog.getAllTrades() }
    Column(verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)) {
        KonstrivoSectionHeader(
            title = language.tr("Métiers & Corps d'état","Trades & Disciplines","المهن والتخصصات","المهن والتخصصات"),
            icon = Icons.Default.Engineering
        )
        trades.forEach { trade ->
            TradeItemCard(
                trade = trade,
                language = language,
                onClick = { onTradeSelected(trade) }
            )
        }
    }
}

@Composable
fun RecentActivitySection(
    language: AppLanguage,
    savedEstimates: List<ProjectEstimateEntity>,
    onViewAll: () -> Unit,
    onShareWhatsApp: (ProjectEstimateEntity) -> Unit,
    onViewEstimates: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)) {
        KonstrivoSectionHeader(
            title = language.tr("Activité Récente","Recent Activity","النشاط الأخير","النشاط الأخير"),
            icon = Icons.Default.History,
            trailing = {
                if (savedEstimates.isNotEmpty()) {
                    TextButton(onClick = onViewAll) {
                        Text(
                            text = language.tr("Voir tout","View all","عرض الكل","عرض الكل"),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        )
        if (savedEstimates.isEmpty()) {
            KonstrivoEmptyState(
                title = language.tr("Aucun devis pour le moment","No estimates yet","لا توجد تقديرات بعد","لا توجد تقديرات بعد"),
                subtitle = language.tr(
                    "Start your first calculation with “New estimate”.",
                    "Start your first calculation with “New estimate”.",
                    "ابدأ أول عملية حساب من زر « تقدير جديد ».",
                    "ابدأ أول عملية حساب من زر « تقدير جديد »."
                ),
                icon = Icons.Default.Description,
                modifier = Modifier.testTag("home_estimates_empty_state")
            )
        } else {
            savedEstimates.take(3).forEach { estimate ->
                RecentEstimateCard(
                    estimate = estimate,
                    language = language,
                    onShareWhatsApp = { onShareWhatsApp(estimate) },
                    onClick = onViewEstimates
                )
            }
        }
    }
}

@Composable
fun HubActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String?,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            // PHASE 2A — Primary tool module gets the brand accent ring.
            if (isPrimary) KonstrivoAmber.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPrimary) 2.dp else 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KonstrivoSpacing.CardInner),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                badge?.let {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PlacoFlagshipCard(
    language: AppLanguage,
    onTypeSelected: (CalculationType) -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, KonstrivoGold.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("placo_flagship_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row of Placo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewDetails() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(KonstrivoGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏗️", fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.DERJA -> "البلاكو والأسقف المعلقة (Placo)"
                                AppLanguage.ARABIC -> "البلاكو والأسقف المعلقة (Placo BA13)"
                                AppLanguage.FRENCH -> "Placo & Faux Plafonds (BA13)"
                                AppLanguage.ENGLISH -> "Placo & Drywall Ceilings"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = language.tr("Calcul complet : Plaques, Rails, Montants, Vis, Enduit","Calcul complet : Plaques, Rails, Montants, Vis, Enduit","حساب دقيق: ألواح، سكك، قوائم، براغي، معجون","حساب دقيق: ألواح، سكك، قوائم، براغي، معجون"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KonstrivoGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = language.tr("COMPLET","COMPLET","مكتمل","مكتمل"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = KonstrivoNavy,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Sub-types Grid / Chips (All existing Placo types)
            Text(
                text = language.tr("Choisissez le type d'ouvrage :","Choisissez le type d'ouvrage :","اختر نوع التركيب المطلوب:","اختر نوع التركيب المطلوب:"),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val placoTypes = listOf(
                CalculationType.PLACO_PLAFOND_FIXE to ("🏠 " + language.tr("Faux Plafond Simple BA13","Faux Plafond Simple BA13","سقف معلق مستوي BA13","سقف معلق مستوي BA13")),
                CalculationType.PLACO_CLOISON to ("🧱 " + language.tr("Cloison de Séparation","Cloison de Séparation","جدار فاصل كلووزون","جدار فاصل كلووزون")),
                CalculationType.PLACO_DEMONTABLE to ("🔲 " + language.tr("Plafond Démontable 60x60","Plafond Démontable 60x60","سقف تفكيكي 60×60","سقف تفكيكي 60×60")),
                CalculationType.PLACO_CACHE_LUMIERE to ("💡 " + language.tr("Caisson / Cache-Lumière","Caisson / Cache-Lumière","كرتوش وديكور ليد","كرتوش وديكور ليد")),
                CalculationType.PLACO_AQUAPANEL to ("🛡️ " + language.tr("Habillage Aquapanel Ciment","Habillage Aquapanel Ciment","أكوابانيل خارجي إسمنتي","أكوابانيل خارجي إسمنتي")),
                CalculationType.PLACO_PVC to ("💧 " + language.tr("Plafond PVC Anti-Humidité","Plafond PVC Anti-Humidité","سقف PVC للحمام والمطبخ","سقف PVC للحمام والمطبخ"))
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                placoTypes.chunked(2).forEach { rowPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowPair.forEach { (type, label) ->
                            Surface(
                                onClick = { onTypeSelected(type) },
                                shape = RoundedCornerShape(KonstrivoRadius.Button),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_placo_${type.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.outline
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

@Composable
fun TradeItemCard(
    trade: Trade,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAvailable = trade.isAvailable
    KonstrivoCard(
        onClick = onClick,
        containerColor = if (isAvailable) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        showBorder = true,
        contentPadding = KonstrivoSpacing.CardInner, // 14dp — same as previous inner padding
        modifier = modifier
            .fillMaxWidth()
            .testTag("trade_card_${trade.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(trade.iconEmoji, fontSize = 24.sp)
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = trade.localizedName(language),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (trade.status) {
                                TradeStatus.ACTIVE -> KonstrivoEmerald.copy(alpha = 0.15f)
                                TradeStatus.IN_DEVELOPMENT -> KonstrivoGold.copy(alpha = 0.15f)
                                TradeStatus.COMING_SOON -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = trade.localizedStatusLabel(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = when (trade.status) {
                                    TradeStatus.ACTIVE -> KonstrivoEmerald
                                    TradeStatus.IN_DEVELOPMENT -> KonstrivoGold
                                    TradeStatus.COMING_SOON -> MaterialTheme.colorScheme.outline
                                },
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = trade.localizedDescription(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RecentEstimateCard(
    estimate: ProjectEstimateEntity,
    language: AppLanguage,
    onShareWhatsApp: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(estimate.createdAt) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(estimate.createdAt))
    }

    // PHASE 2C — BTP document hierarchy: Service → Date → Client → Surface/Location → Total → Action.
    // Uses only fields already present on ProjectEstimateEntity (no model changes).
    val typeLabel = remember(estimate.calculationTypeId, language) {
        val calcType = CalculationType.entries.firstOrNull { it.id == estimate.calculationTypeId }
        when (language) {
            AppLanguage.DERJA -> calcType?.nameDerja
            AppLanguage.ARABIC -> calcType?.nameAr
            AppLanguage.FRENCH -> calcType?.nameFr
            AppLanguage.ENGLISH -> calcType?.nameFr
        } ?: estimate.calculationTypeId
    }

    KonstrivoCard(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface,
        showBorder = true,
        contentPadding = KonstrivoSpacing.CardInner,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1) Meta row — service type + date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = KonstrivoGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 2) Client / project title
            Text(
                text = if (estimate.clientName.isNotEmpty()) estimate.clientName else language.tr("Client Particulier","Client Particulier","حريف","حريف"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 3) Surface & location line
            if (estimate.inputArea > 0.0 || estimate.location.isNotEmpty()) {
                Text(
                    text = buildString {
                        if (estimate.inputArea > 0.0) append("${estimate.inputArea} m²")
                        if (estimate.location.isNotEmpty()) {
                            if (isNotEmpty()) append(" • ")
                            append(estimate.location)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            // 4) Total + action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = language.tr("Total TTC","Total TTC","المجموع شامل الأداء","المجموع شامل الأداء"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    KonstrivoPriceText(
                        text = "${estimate.grandTotal} ${estimate.currency}",
                        style = KonstrivoPriceStyle.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = KonstrivoEmerald.copy(alpha = 0.12f)
                ) {
                    IconButton(onClick = onShareWhatsApp, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = language.tr("Partager", "Share", "مشاركة", "شارك"),
                            tint = KonstrivoEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
