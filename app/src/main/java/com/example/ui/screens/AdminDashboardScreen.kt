package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.MaterialPriceEntity
import com.example.data.local.ProjectEstimateEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationType
import com.example.data.model.Country
import com.example.domain.material.MaterialCatalog
import com.example.domain.model.MaterialCategory
import com.example.ui.theme.KonstrivoAmber
import com.example.ui.theme.KonstrivoEmerald
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.viewmodel.AppTab
import com.example.ui.components.designsystem.KonstrivoMenuCard
import com.example.ui.components.designsystem.tr
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PHASE G — Professional Admin Dashboard (UI / architecture-ready shell only).
 *
 * Sections: Overview, Catalogue (Materials / Systems / Categories / Prices),
 * Suppliers (future-ready), Devis, Users & Companies (future-ready), PRO.
 *
 * Rules enforced:
 *  - Real data only: MaterialCatalog, pricesList, savedEstimates, userSettings.
 *  - No fake numbers: unavailable statistics show a neutral "coming soon" state.
 *  - No new navigation framework: internal section state + BackHandler.
 *  - No new authentication: logout uses the existing viewModel.logoutAdmin().
 *  - Price editing reuses the already-supported updateMaterialPrice / updateUserSettings.
 */
private enum class AdminSection {
    OVERVIEW,
    CATALOGUE,
    MATERIALS,
    PRICES,
    SUPPLIERS,
    DEVIS,
    USERS,
    PRO
}

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.selectedLanguage.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val prices by viewModel.pricesList.collectAsState()
    val estimates by viewModel.savedEstimates.collectAsState()
    val country = viewModel.selectedCountry.collectAsState().value

    var section by remember { mutableStateOf(AdminSection.OVERVIEW) }

    // Internal section navigation only — no second navigation framework.
    BackHandler(enabled = section != AdminSection.OVERVIEW) { section = AdminSection.OVERVIEW }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard"),
        color = KonstrivoNavy
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminHeader(
                title = if (section == AdminSection.OVERVIEW) {
                    language.tr(
                        fr = "Administration Générale",
                        en = "General Administration",
                        ar = "الإدارة العامة",
                        dz = "الإدارة العامة"
                    )
                } else {
                    adminSectionTitle(section, language)
                },
                companyHeader = userSettings.companyHeader,
                language = language,
                showBack = section != AdminSection.OVERVIEW,
                onBack = { section = AdminSection.OVERVIEW },
                onLogout = {
                    viewModel.logoutAdmin()
                    onLogout()
                }
            )

            Box(modifier = Modifier.weight(1f)) {
                when (section) {
                    AdminSection.OVERVIEW -> AdminOverviewSection(
                        language = language,
                        materialsCount = MaterialCatalog.ALL_MATERIALS.size,
                        systemsCount = CalculationType.entries.size,
                        devisCount = estimates.size,
                        pricesCount = prices.size,
                        onOpen = { section = it },
                        onOpenSettings = { viewModel.selectTab(AppTab.SETTINGS) }
                    )
                    AdminSection.CATALOGUE -> AdminCatalogueSection(
                        language = language,
                        onOpen = { section = it }
                    )
                    AdminSection.MATERIALS -> AdminMaterialsSection(language = language)
                    AdminSection.PRICES -> AdminPricesSection(
                        language = language,
                        viewModel = viewModel,
                        prices = prices,
                        country = country,
                        userSettings = userSettings
                    )
                    AdminSection.SUPPLIERS -> AdminSuppliersSection(language = language)
                    AdminSection.DEVIS -> AdminDevisSection(language = language, estimates = estimates)
                    AdminSection.USERS -> AdminUsersSection(language = language, userSettings = userSettings)
                    AdminSection.PRO -> AdminProSection(language = language, userSettings = userSettings)
                }
            }
        }
    }
}

private fun adminSectionTitle(section: AdminSection, language: AppLanguage): String = when (section) {
    AdminSection.OVERVIEW -> language.tr(
        fr = "Administration Générale", en = "General Administration", ar = "الإدارة العامة", dz = "الإدارة العامة"
    )
    AdminSection.CATALOGUE -> language.tr(
        fr = "Catalogue", en = "Catalogue", ar = "الكتالوج", dz = "الكتالوج"
    )
    AdminSection.MATERIALS -> language.tr(
        fr = "Matériaux", en = "Materials", ar = "المواد", dz = "المواد"
    )
    AdminSection.PRICES -> language.tr(
        fr = "Gestion des Prix", en = "Price Management", ar = "إدارة الأسعار", dz = "إدارة الأسعار"
    )
    AdminSection.SUPPLIERS -> language.tr(
        fr = "Fournisseurs", en = "Suppliers", ar = "الموردون", dz = "الموردين"
    )
    AdminSection.DEVIS -> language.tr(
        fr = "Devis", en = "Estimates", ar = "عروض الأسعار", dz = "عروض الأسعار"
    )
    AdminSection.USERS -> language.tr(
        fr = "Utilisateurs & Sociétés", en = "Users & Companies", ar = "المستخدمون والشركات", dz = "المستخدمين والشركات"
    )
    AdminSection.PRO -> language.tr(
        fr = "PRO / Abonnement", en = "PRO / Subscription", ar = "PRO / الاشتراك", dz = "PRO / الاشتراك"
    )
}

@Composable
private fun AdminHeader(
    title: String,
    companyHeader: String,
    language: AppLanguage,
    showBack: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showBack) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = language.tr(
                        fr = "Retour", en = "Back", ar = "رجوع", dz = "رجوع"
                    ),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = KonstrivoAmber.copy(alpha = 0.18f)) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (companyHeader.isNotBlank()) companyHeader else language.tr(
                            fr = "Compte PRO", en = "PRO Account", ar = "حساب PRO", dz = "حساب PRO"
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = KonstrivoAmber,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                language.tr(fr = "Quitter", en = "Exit", ar = "خروج", dz = "خروج")
            )
        }
    }
}

@Composable
private fun AdminOverviewSection(
    language: AppLanguage,
    materialsCount: Int,
    systemsCount: Int,
    devisCount: Int,
    pricesCount: Int,
    onOpen: (AdminSection) -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminMetricCard(
                label = language.tr(
                    fr = "Matériaux", en = "Materials", ar = "المواد", dz = "المواد"
                ),
                value = "$materialsCount",
                icon = Icons.Default.Layers,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                label = language.tr(
                    fr = "Systèmes", en = "Systems", ar = "الأنظمة", dz = "الأنظمة"
                ),
                value = "$systemsCount",
                icon = Icons.Default.Architecture,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminMetricCard(
                label = "Devis",
                value = "$devisCount",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                label = language.tr(
                    fr = "Entrées de prix", en = "Price entries", ar = "أسعار مسجلة", dz = "أسعار مسجلة"
                ),
                value = "$pricesCount",
                icon = Icons.Default.Sell,
                modifier = Modifier.weight(1f)
            )
        }

        AdminSectionLabel(language.tr(
            fr = "GESTION", en = "MANAGEMENT", ar = "الإدارة", dz = "الإدارة"
        ))

        KonstrivoMenuCard(
            title = language.tr(
                fr = "Catalogue", en = "Catalogue", ar = "الكتالوج", dz = "الكتالوج"
            ),
            onClick = { onOpen(AdminSection.CATALOGUE) },
            subtitle = language.tr(
                fr = "Matériaux, systèmes, catégories",
                en = "Materials, systems, categories",
                ar = "المواد والأنظمة والفئات",
                dz = "المواد والأنظمة والفئات"
            ),
            icon = Icons.Default.Inventory2,
            testTag = "admin_catalogue"
        )

        KonstrivoMenuCard(
            title = language.tr(
                fr = "Gestion des Prix", en = "Price Management", ar = "إدارة الأسعار", dz = "إدارة الأسعار"
            ),
            onClick = { onOpen(AdminSection.PRICES) },
            subtitle = language.tr(
                fr = "Tarifs courants & sources de prix",
                en = "Current prices & sources",
                ar = "الأسعار الحالية ومصادرها",
                dz = "الأسعار الحالية ومصادرها"
            ),
            icon = Icons.Default.Sell,
            testTag = "admin_prices"
        )

        KonstrivoMenuCard(
            title = language.tr(
                fr = "Fournisseurs", en = "Suppliers", ar = "الموردون", dz = "الموردون"
            ),
            onClick = { onOpen(AdminSection.SUPPLIERS) },
            subtitle = language.tr(
                fr = "Import catalogue — bientôt disponible",
                en = "Catalogue import — coming soon",
                ar = "استيراد الكتالوج — قريباً",
                dz = "استيراد الكتالوج — قريباً"
            ),
            icon = Icons.Default.LocalShipping,
            testTag = "admin_suppliers"
        )

        KonstrivoMenuCard(
            title = language.tr(
                fr = "Devis", en = "Estimates", ar = "عروض الأسعار", dz = "عروض الأسعار"
            ),
            onClick = { onOpen(AdminSection.DEVIS) },
            subtitle = language.tr(
                fr = "Suivi des devis enregistrés",
                en = "Track saved estimates",
                ar = "متابعة عروض الأسعار المسجلة",
                dz = "متابعة عروض الأسعار المسجلة"
            ),
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            testTag = "admin_devis"
        )

        KonstrivoMenuCard(
            title = language.tr(
                fr = "Utilisateurs & Sociétés", en = "Users & Companies", ar = "المستخدمون والشركات", dz = "المستخدمين والشركات"
            ),
            onClick = { onOpen(AdminSection.USERS) },
            subtitle = language.tr(
                fr = "Gestion future des comptes",
                en = "Future account management",
                ar = "إدارة الحسابات مستقبلاً",
                dz = "إدارة الحسابات مستقبلاً"
            ),
            icon = Icons.Default.Groups,
            testTag = "admin_users"
        )

        KonstrivoMenuCard(
            title = language.tr(
                fr = "PRO / Abonnement", en = "PRO / Subscription", ar = "PRO / الاشتراك", dz = "PRO / الاشتراك"
            ),
            onClick = { onOpen(AdminSection.PRO) },
            subtitle = language.tr(
                fr = "Statut du compte et fonctions PRO",
                en = "Account status & PRO features",
                ar = "حالة الحساب وميزات PRO",
                dz = "حالة الحساب وميزات PRO"
            ),
            icon = Icons.Default.WorkspacePremium,
            testTag = "admin_pro"
        )

        AdminSectionLabel(language.tr(
            fr = "APPLICATION", en = "APPLICATION", ar = "التطبيق", dz = "التطبيق"
        ))

        KonstrivoMenuCard(
            title = language.tr(
                fr = "Paramètres", en = "Settings", ar = "الإعدادات", dz = "الإعدادات"
            ),
            onClick = { onOpenSettings() },
            subtitle = language.tr(
                fr = "Langue, pays, profil artisan",
                en = "Language, country, contractor profile",
                ar = "اللغة والبلد وبيانات الحرفي",
                dz = "اللغة والبلد وبيانات الحرفي"
            ),
            icon = Icons.Default.Settings,
            testTag = "admin_settings"
        )
    }
}

@Composable
private fun AdminMetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = KonstrivoAmber,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AdminSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        color = KonstrivoAmber
    )
}

@Composable
private fun AdminComingSoonCard(text: String, language: AppLanguage, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = language.tr(
                        fr = "Disponible prochainement",
                        en = "Coming soon",
                        ar = "قريباً",
                        dz = "قريباً"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AdminFutureRow(text: String, language: AppLanguage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = language.tr(
                fr = "À venir", en = "Planned", ar = "قريباً", dz = "قريباً"
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AdminCatalogueSection(
    language: AppLanguage,
    onOpen: (AdminSection) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        KonstrivoMenuCard(
            title = language.tr(
                fr = "Matériaux", en = "Materials", ar = "المواد", dz = "المواد"
            ),
            onClick = { onOpen(AdminSection.MATERIALS) },
            subtitle = "${MaterialCatalog.ALL_MATERIALS.size} " +
                language.tr(
                    fr = "références au catalogue",
                    en = "catalogue references",
                    ar = "مرجعاً في الكتالوج",
                    dz = "مرجع في الكتالوج"
                ),
            icon = Icons.Default.Layers,
            testTag = "admin_materials"
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = language.tr(
                            fr = "Systèmes de construction",
                            en = "Construction systems",
                            ar = "أنظمة البناء",
                            dz = "أنظمة البناء"
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${CalculationType.entries.size} " +
                        language.tr(
                            fr = "systèmes de calcul disponibles",
                            en = "calculation systems available",
                            ar = "نظام حساب متاح",
                            dz = "نظام حساب متاح"
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = language.tr(
                            fr = "Catégories", en = "Categories", ar = "الفئات", dz = "الفئات"
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                MaterialCategory.entries.forEach { cat ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = cat.localizedName(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${MaterialCatalog.getByCategory(cat).size}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        KonstrivoMenuCard(
            title = language.tr(
                fr = "Gestion des Prix", en = "Price Management", ar = "إدارة الأسعار", dz = "إدارة الأسعار"
            ),
            onClick = { onOpen(AdminSection.PRICES) },
            subtitle = language.tr(
                fr = "Tarifs courants & sources de prix",
                en = "Current prices & sources",
                ar = "الأسعار الحالية ومصادرها",
                dz = "الأسعار الحالية ومصادرها"
            ),
            icon = Icons.Default.Sell,
            testTag = "admin_prices"
        )
    }
}

@Composable
private fun AdminMaterialsSection(language: AppLanguage) {
    val materials = MaterialCatalog.ALL_MATERIALS
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${materials.size} " +
                        language.tr(
                            fr = "références catalogue",
                            en = "catalogue references",
                            ar = "مرجعاً في الكتالوج",
                            dz = "مرجع في الكتالوج"
                        ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = language.tr(
                        fr = "Consultation — l'édition complète arrivera dans une phase future.",
                        en = "Read-only — full editing will arrive in a future phase.",
                        ar = "عرض فقط — التعديل الكامل سيأتي في مرحلة لاحقة.",
                        dz = "عرض برك — التعديل الكامل جاي في مرحلة لاحقة."
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(materials) { mat ->
                val name = when (language) {
                    AppLanguage.DERJA -> mat.nameDerja
                    AppLanguage.ARABIC -> mat.nameAr
                    AppLanguage.FRENCH -> mat.nameFr
                    AppLanguage.ENGLISH -> mat.nameEn
                }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${mat.id} • ${mat.category.localizedName(language)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = mat.standardUnit.standardSymbol,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    color = if (mat.isActive) KonstrivoEmerald
                                    else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminPricesSection(
    language: AppLanguage,
    viewModel: MainViewModel,
    prices: List<MaterialPriceEntity>,
    country: Country,
    userSettings: UserSettingsEntity
) {
    val context = LocalContext.current

    // Preserved real price-editing capability (existing supported operations).
    val ba13Id = "${country.code}_plaque_ba13_standard"
    val hydroId = "${country.code}_plaque_ba13_hydrofuge"
    val ba13Price = prices.find { it.id == ba13Id }?.currentPrice ?: 0.0
    val hydroPrice = prices.find { it.id == hydroId }?.currentPrice ?: 0.0
    var ba13Value by remember(ba13Price) { mutableStateOf(ba13Price) }
    var hydroValue by remember(hydroPrice) { mutableStateOf(hydroPrice) }
    var laborValue by remember(userSettings.defaultLaborRatePerM2) {
        mutableStateOf(userSettings.defaultLaborRatePerM2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sell,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = language.tr(
                            fr = "Tarifs — ${country.nameFr} (${country.currencySymbol})",
                            en = "Prices — ${country.nameFr} (${country.currencySymbol})",
                            ar = "الأسعار — ${country.nameFr} (${country.currencySymbol})",
                            dz = "الأسعار — ${country.nameFr} (${country.currencySymbol})"
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${prices.size} " +
                        language.tr(
                            fr = "entrées de prix • tarification package-aware active",
                            en = "price entries • package-aware pricing active",
                            ar = "مدخل سعر • تسعير الحزم مفعّل",
                            dz = "مدخل سعر • تسعير الحزم مفعّل"
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = language.tr(
                        fr = "Ajustement rapide des tarifs",
                        en = "Quick price adjustment",
                        ar = "تعديل سريع للأسعار",
                        dz = "تعديل سريع للأسعار"
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = ba13Value.toString(),
                        onValueChange = { v -> ba13Value = v.toDoubleOrNull() ?: 0.0 },
                        label = { Text(language.tr(
                            fr = "BA13 / m²", en = "BA13 / m²", ar = "BA13 / م²", dz = "BA13 / م²"
                        )) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = hydroValue.toString(),
                        onValueChange = { v -> hydroValue = v.toDoubleOrNull() ?: 0.0 },
                        label = { Text(language.tr(
                            fr = "Hydrofuge / m²", en = "Waterproof / m²", ar = "هيدروفوج / م²", dz = "هيدروفوج / م²"
                        )) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = laborValue.toString(),
                        onValueChange = { v -> laborValue = v.toDoubleOrNull() ?: 0.0 },
                        label = { Text(language.tr(
                            fr = "Main d'œuvre / m²", en = "Labor / m²", ar = "اليد العاملة / م²", dz = "اليد العاملة / م²"
                        )) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = {
                        viewModel.updateMaterialPrice(ba13Id, ba13Value)
                        viewModel.updateMaterialPrice(hydroId, hydroValue)
                        viewModel.updateUserSettings(
                            userSettings.copy(defaultLaborRatePerM2 = laborValue)
                        )
                        Toast.makeText(
                            context,
                            language.tr(
                                fr = "Tarifs enregistrés",
                                en = "Prices saved",
                                ar = "تم حفظ الأسعار",
                                dz = "تسجلت الأسعار"
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(language.tr(
                        fr = "Enregistrer les tarifs",
                        en = "Save prices",
                        ar = "حفظ الأسعار",
                        dz = "حفظ الأسعار"
                    ))
                }
                Text(
                    text = language.tr(
                        fr = "Ces ajustements mettent à jour les prix locaux utilisés par les calculateurs.",
                        en = "These adjustments update the local prices used by the calculators.",
                        ar = "هذه التعديلات تحدّث الأسعار المحلية المستعملة في الحاسبات.",
                        dz = "هذه التعديلات تحدّث الأسعار المحلية المستعملة في الحاسبات."
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AdminSuppliersSection(language: AppLanguage) {
    val steps = listOf(
        language.tr(fr = "Fournisseur", en = "Supplier", ar = "المورد", dz = "المورد"),
        language.tr(fr = "Fichier catalogue (Excel / CSV)", en = "Catalogue file (Excel / CSV)", ar = "ملف الكتالوج (Excel / CSV)", dz = "ملف الكتالوج (Excel / CSV)"),
        language.tr(fr = "Import", en = "Import", ar = "الاستيراد", dz = "الاستيراد"),
        language.tr(fr = "Validation", en = "Validation", ar = "التحقق", dz = "التحقق"),
        language.tr(fr = "Aperçu", en = "Preview", ar = "المعاينة", dz = "المعاينة"),
        language.tr(fr = "Approbation admin", en = "Admin approval", ar = "موافقة المدير", dz = "موافقة المدير"),
        language.tr(fr = "Publication", en = "Publication", ar = "النشر", dz = "النشر")
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = language.tr(
                            fr = "Import catalogue fournisseur",
                            en = "Supplier catalogue import",
                            ar = "استيراد كتالوج المورد",
                            dz = "استيراد كتالوج المورد"
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = language.tr(
                        fr = "Le futur import permettra de récupérer les catalogues fournisseurs, de les " +
                            "valider, de les prévisualiser puis de les publier après approbation.",
                        en = "The upcoming import will let you fetch supplier catalogues, validate and " +
                            "preview them, then publish after approval.",
                        ar = "سيسمح الاستيراد القادم بجلب كتالوجات الموردين والتحقق منها ومعاينتها ثم نشرها بعد الموافقة.",
                        dz = "الاستيراد الجاي يخليك تجيب كتالوجات الموردين وتتحقق منها وتعاينها وبعد تنشرها بموافقة."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                steps.forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = 1.dp,
                                    color = KonstrivoAmber,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = KonstrivoAmber
                            )
                        }
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        AdminComingSoonCard(
            text = language.tr(
                fr = "Import fournisseurs (Excel / CSV)",
                en = "Supplier import (Excel / CSV)",
                ar = "استيراد الموردين (Excel / CSV)",
                dz = "استيراد الموردين (Excel / CSV)"
            ),
            language = language
        )
    }
}

@Composable
private fun AdminDevisSection(language: AppLanguage, estimates: List<ProjectEstimateEntity>) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = KonstrivoAmber,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "${estimates.size} " +
                            language.tr(
                                fr = "devis enregistrés sur l'appareil",
                                en = "estimates saved on this device",
                                ar = "عرض سعر مسجل على الجهاز",
                                dz = "عرض سعر مسجل على التيليفون"
                            ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = language.tr(
                            fr = "Données locales — aucune donnée inventée",
                            en = "Local data — no invented figures",
                            ar = "بيانات محلية — دون أي بيانات مفترضة",
                            dz = "معلومات محلية — بلا أي أرقام مفترضة"
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (estimates.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = language.tr(
                        fr = "Aucun devis enregistré pour le moment.",
                        en = "No estimates saved yet.",
                        ar = "لا توجد عروض أسعار مسجلة حالياً.",
                        dz = "ما فماش عروض أسعار مسجلة توّا."
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = language.tr(
                            fr = "Devis récents",
                            en = "Recent estimates",
                            ar = "أحدث عروض الأسعار",
                            dz = "آخر عروض الأسعار"
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    estimates
                        .sortedByDescending { it.createdAt }
                        .take(10)
                        .forEach { est ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = est.clientName.ifBlank {
                                            language.tr(
                                                fr = "Client non précisé",
                                                en = "Unnamed client",
                                                ar = "عميل غير محدد",
                                                dz = "كليان موش محدد"
                                            )
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = dateFormat.format(Date(est.createdAt)) +
                                            if (est.location.isNotBlank()) " • ${est.location}" else "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${est.grandTotal} ${est.currency}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = KonstrivoAmber
                                )
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersSection(language: AppLanguage, userSettings: UserSettingsEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = language.tr(
                            fr = "Société actuelle",
                            en = "Current company",
                            ar = "الشركة الحالية",
                            dz = "الشركة الحالية"
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = userSettings.companyHeader.ifBlank {
                        language.tr(
                            fr = "Non renseignée",
                            en = "Not provided",
                            ar = "غير محددة",
                            dz = "مو محددة"
                        )
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (userSettings.contractorName.isNotBlank()) {
                    Text(
                        text = userSettings.contractorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (userSettings.contractorPhone.isNotBlank()) {
                    Text(
                        text = userSettings.contractorPhone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AdminComingSoonCard(
            text = language.tr(
                fr = "Gestion des utilisateurs & sociétés",
                en = "User & company management",
                ar = "إدارة المستخدمين والشركات",
                dz = "إدارة المستخدمين والشركات"
            ),
            language = language
        )
    }
}

@Composable
private fun AdminProSection(language: AppLanguage, userSettings: UserSettingsEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = KonstrivoAmber,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "KONSTRIVO PRO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = language.tr(
                            fr = "Accès administrateur actif sur cet appareil",
                            en = "Administrator access active on this device",
                            ar = "وصول المدير نشط على هذا الجهاز",
                            dz = "دخول المدير نشط على التيليفون هذا"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (userSettings.companyHeader.isNotBlank()) {
                        Text(
                            text = userSettings.companyHeader,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = KonstrivoAmber
                        )
                    }
                }
            }
        }

        AdminSectionLabel(
            language.tr(
                fr = "FONCTIONS À VENIR",
                en = "UPCOMING FEATURES",
                ar = "ميزات قادمة",
                dz = "حوايج جايين"
            )
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AdminFutureRow(
                    text = language.tr(
                        fr = "Abonnements FREE / PRO / PRO+", en = "FREE / PRO / PRO+ subscriptions",
                        ar = "اشتراكات FREE / PRO / PRO+", dz = "اشتراكات FREE / PRO / PRO+"
                    ),
                    language = language
                )
                AdminFutureRow(
                    text = language.tr(
                        fr = "Facturation & paiements", en = "Invoicing & payments",
                        ar = "الفواتير والمدفوعات", dz = "الفاتورة والخلاص"
                    ),
                    language = language
                )
                AdminFutureRow(
                    text = language.tr(
                        fr = "Fonctions PRO avancées pour les devis", en = "Advanced PRO features for estimates",
                        ar = "ميزات PRO متقدمة لعروض الأسعار", dz = "ميزات PRO متقدمة لعروض الأسعار"
                    ),
                    language = language
                )
            }
        }

        Text(
            text = language.tr(
                fr = "Aucun système de facturation n'est actif — ces fonctions seront introduites dans une phase future.",
                en = "No billing system is active yet — these features will be introduced in a future phase.",
                ar = "لا يوجد نظام فوترة نشط — ستُضاف هذه الميزات في مرحلة لاحقة.",
                dz = "ما فماش نظام فوترة نشط — هذي الميزات يجيو في مرحلة لاحقة."
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}










