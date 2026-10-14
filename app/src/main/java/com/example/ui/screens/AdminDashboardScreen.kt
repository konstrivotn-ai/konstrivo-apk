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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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
                    language.tr(fr = "Administration Générale", en = "General Administration", ar = "الإدارة العامة", dz = "الإدارة العامة")
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
                    AdminSection.USERS -> AdminUsersSection(
                        language = language,
                        userSettings = userSettings,
                        viewModel = viewModel
                    )
                    AdminSection.PRO -> AdminProSection(language = language, userSettings = userSettings)
                }
            }
        }
    }
}

private fun adminSectionTitle(section: AdminSection, language: AppLanguage): String = when (section) {
    AdminSection.OVERVIEW -> language.tr(fr = "Administration Générale", en = "General Administration", ar = "الإدارة العامة", dz = "الإدارة العامة")
    AdminSection.CATALOGUE -> language.tr(fr = "Catalogue", en = "Catalogue", ar = "الكتالوج", dz = "الكتالوج")
    AdminSection.MATERIALS -> language.tr(fr = "Matériaux", en = "Materials", ar = "المواد", dz = "المواد")
    AdminSection.PRICES -> language.tr(fr = "Gestion des Prix", en = "Price Management", ar = "إدارة الأسعار", dz = "إدارة الأسعار")
    AdminSection.SUPPLIERS -> language.tr(fr = "Fournisseurs", en = "Suppliers", ar = "الموردون", dz = "الموردين")
    AdminSection.DEVIS -> language.tr(fr = "Devis", en = "Estimates", ar = "عروض الأسعار", dz = "عروض الأسعار")
    AdminSection.USERS -> language.tr(fr = "Utilisateurs & Sociétés", en = "Users & Companies", ar = "المستخدمون والشركات", dz = "المستخدمين والشركات")
    AdminSection.PRO -> language.tr(fr = "PRO / Abonnement", en = "PRO / Subscription", ar = "PRO / الاشتراك", dz = "PRO / الاشتراك")
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
                    contentDescription = language.tr(fr = "Retour", en = "Back", ar = "رجوع", dz = "رجوع"),
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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(language.tr(fr = "Quitter", en = "Exit", ar = "خروج", dz = "خروج"))
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
                label = language.tr(fr = "Matériaux", en = "Materials", ar = "المواد", dz = "المواد"),
                value = "$materialsCount",
                icon = Icons.Default.Layers,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                label = language.tr(fr = "Systèmes", en = "Systems", ar = "الأنظمة", dz = "الأنظمة"),
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
                label = language.tr(fr = "Entrées de prix", en = "Price entries", ar = "أسعار مسجلة", dz = "أسعار مسجلة"),
                value = "$pricesCount",
                icon = Icons.Default.Sell,
                modifier = Modifier.weight(1f)
            )
        }

        AdminSectionLabel(language.tr(fr = "GESTION", en = "MANAGEMENT", ar = "الإدارة", dz = "الإدارة"))

        KonstrivoMenuCard(
            title = language.tr(fr = "Catalogue", en = "Catalogue", ar = "الكتالوج", dz = "الكتالوج"),
            onClick = { onOpen(AdminSection.CATALOGUE) },
            subtitle = language.tr(fr = "Matériaux, systèmes, catégories", en = "Materials, systems, categories", ar = "المواد والأنظمة والفئات", dz = "المواد والأنظمة والفئات"),
            icon = Icons.Default.Inventory2,
            testTag = "admin_catalogue"
        )

        KonstrivoMenuCard(
            title = language.tr(fr = "Gestion des Prix", en = "Price Management", ar = "إدارة الأسعار", dz = "إدارة الأسعار"),
            onClick = { onOpen(AdminSection.PRICES) },
            subtitle = language.tr(fr = "Tarifs courants & sources de prix", en = "Current prices & sources", ar = "الأسعار الحالية ومصادرها", dz = "الأسعار الحالية ومصادرها"),
            icon = Icons.Default.Sell,
            testTag = "admin_prices"
        )

        KonstrivoMenuCard(
            title = language.tr(fr = "Fournisseurs", en = "Suppliers", ar = "الموردون", dz = "الموردون"),
            onClick = { onOpen(AdminSection.SUPPLIERS) },
            subtitle = language.tr(fr = "Import catalogue", en = "Catalogue import", ar = "استيراد الكتالوج", dz = "استيراد الكتالوج"),
            icon = Icons.Default.LocalShipping,
            testTag = "admin_suppliers"
        )

        KonstrivoMenuCard(
            title = language.tr(fr = "Devis", en = "Estimates", ar = "عروض الأسعار", dz = "عروض الأسعار"),
            onClick = { onOpen(AdminSection.DEVIS) },
            subtitle = language.tr(fr = "Suivi des devis enregistrés", en = "Track saved estimates", ar = "متابعة عروض الأسعار المسجلة", dz = "متابعة عروض الأسعار المسجلة"),
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            testTag = "admin_devis"
        )

        KonstrivoMenuCard(
            title = language.tr(fr = "Utilisateurs & Sociétés", en = "Users & Companies", ar = "المستخدمون والشركات", dz = "المستخدمين والشركات"),
            onClick = { onOpen(AdminSection.USERS) },
            subtitle = language.tr(fr = "Gestion des comptes et Google Auth", en = "Account management & Google Auth", ar = "إدارة الحسابات وربط Google", dz = "إدارة الحسابات وربط Google"),
            icon = Icons.Default.Groups,
            testTag = "admin_users"
        )

        KonstrivoMenuCard(
            title = language.tr(fr = "PRO / Abonnement", en = "PRO / Subscription", ar = "PRO / الاشتراك", dz = "PRO / الاشتراك"),
            onClick = { onOpen(AdminSection.PRO) },
            subtitle = language.tr(fr = "Statut du compte et fonctions PRO", en = "Account status & PRO features", ar = "حالة الحساب وميزات PRO", dz = "حالة الحساب وميزات PRO"),
            icon = Icons.Default.WorkspacePremium,
            testTag = "admin_pro"
        )

        AdminSectionLabel(language.tr(fr = "APPLICATION", en = "APPLICATION", ar = "التطبيق", dz = "التطبيق"))

        KonstrivoMenuCard(
            title = language.tr(fr = "Paramètres", en = "Settings", ar = "الإعدادات", dz = "الإعدادات"),
            onClick = { onOpenSettings() },
            subtitle = language.tr(fr = "Langue, pays, profil artisan", en = "Language, country, contractor profile", ar = "اللغة والبلد وبيانات الحرفي", dz = "اللغة والبلد وبيانات الحرفي"),
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
            title = language.tr(fr = "Matériaux", en = "Materials", ar = "المواد", dz = "المواد"),
            onClick = { onOpen(AdminSection.MATERIALS) },
            subtitle = "${MaterialCatalog.ALL_MATERIALS.size} " + language.tr(fr = "références au catalogue", en = "catalogue references", ar = "مرجعاً في الكتالوج", dz = "مرجع في الكتالوج"),
            icon = Icons.Default.Layers,
            testTag = "admin_materials"
        )
    }
}

@Composable
private fun AdminMaterialsSection(language: AppLanguage) {
    val materials = MaterialCatalog.ALL_MATERIALS
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
    val ba13Id = "${country.code}_plaque_ba13_standard"
    val hydroId = "${country.code}_plaque_ba13_hydrofuge"
    val ba13Price = prices.find { it.id == ba13Id }?.currentPrice ?: 0.0
    val hydroPrice = prices.find { it.id == hydroId }?.currentPrice ?: 0.0
    
    var ba13Value by remember(ba13Price) { mutableStateOf(ba13Price.toString()) }
    var hydroValue by remember(hydroPrice) { mutableStateOf(hydroPrice.toString()) }
    var laborValue by remember(userSettings.defaultLaborRatePerM2) {
        mutableStateOf(userSettings.defaultLaborRatePerM2.toString())
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = language.tr(fr = "Mise à jour des Prix & Tarifs", en = "Price & Rate Updates", ar = "تحديث الأسعار والتعريفات", dz = "تحديث الأسعار والتعريفات"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                OutlinedTextField(
                    value = ba13Value,
                    onValueChange = { ba13Value = it },
                    label = { Text("BA13 Standard Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hydroValue,
                    onValueChange = { hydroValue = it },
                    label = { Text("BA13 Hydrofuge Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = laborValue,
                    onValueChange = { laborValue = it },
                    label = { Text("Labor Rate per m²") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val bVal = ba13Value.toDoubleOrNull() ?: ba13Price
                        val hVal = hydroValue.toDoubleOrNull() ?: hydroPrice
                        val lVal = laborValue.toDoubleOrNull() ?: userSettings.defaultLaborRatePerM2

                        viewModel.updateMaterialPrice(ba13Id, bVal)
                        viewModel.updateMaterialPrice(hydroId, hVal)
                        viewModel.updateUserSettings(userSettings.copy(defaultLaborRatePerM2 = lVal))

                        Toast.makeText(context, language.tr(fr = "Mis à jour avec succès", en = "Successfully updated", ar = "تم التحديث بنجاح", dz = "تحدث بنجاح"), Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KonstrivoAmber)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = KonstrivoNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = language.tr(fr = "Enregistrer les modifications", en = "Save changes", ar = "حفظ التغييرات", dz = "حفظ التغييرات"), color = KonstrivoNavy, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminSuppliersSection(language: AppLanguage) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = language.tr(fr = "Fournisseurs & Partenaires", en = "Suppliers & Partners", ar = "الموردون والشركاء", dz = "الموردين والشركاء"), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminDevisSection(language: AppLanguage, estimates: List<ProjectEstimateEntity>) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(estimates) { est ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Devis ID: ${est.id}", fontWeight = FontWeight.Bold)
                        Text(text = "Total: ${est.totalEstimatedCost} TND", color = KonstrivoEmerald)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersSection(
    language: AppLanguage,
    userSettings: UserSettingsEntity,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var companyName by remember(userSettings.companyHeader) { mutableStateOf(userSettings.companyHeader) }
    var contractorName by remember(userSettings.contractorName) { mutableStateOf(userSettings.contractorName ?: "") }
    var userEmail by remember(userSettings.contractorEmail) { mutableStateOf(userSettings.contractorEmail ?: "") }

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
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color = KonstrivoAmber.copy(alpha = 0.2f), shape = RoundedCornerShape(40.dp))
                        .border(2.dp, KonstrivoAmber, RoundedCornerShape(40.dp))
                        .clickable {
                            Toast.makeText(context, language.tr(fr = "Sélection de photo bientôt disponible", en = "Photo selection coming soon", ar = "اختيار الصورة قريباً", dz = "اختيار الصورة قريباً"), Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Modifier la photo",
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = language.tr(fr = "Modifier le Profil & la Société", en = "Edit Profile & Company", ar = "تعديل الملف الشخصي والشركة", dz = "تعديل الملف الشخصي والشركة"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text(language.tr(fr = "Nom de la Société", en = "Company Name", ar = "اسم الشركة", dz = "اسم الشركة")) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contractorName,
                    onValueChange = { contractorName = it },
                    label = { Text(language.tr(fr = "Nom de l'Artisan / Responsable", en = "Contractor Name", ar = "اسم الحرفي / المسؤول", dz = "اسم الحرفي")) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("Email (Google Auth)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val updatedSettings = userSettings.copy(
                            companyHeader = companyName,
                            contractorName = contractorName,
                            contractorEmail = userEmail
                        )
                        viewModel.updateUserSettings(updatedSettings)
                        Toast.makeText(context, language.tr(fr = "Profil mis à jour avec succès", en = "Profile successfully updated", ar = "تم تحديث الملف الشخصي بنجاح", dz = "تحدث الملف الشخصي بنجاح"), Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KonstrivoAmber)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = KonstrivoNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = language.tr(fr = "Enregistrer les modifications", en = "Save changes", ar = "حفظ التغييرات", dz = "حفظ التغييرات"), color = KonstrivoNavy, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminProSection(language: AppLanguage, userSettings: UserSettingsEntity) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = language.tr(fr = "Statut PRO & Licence", en = "PRO Status & License", ar = "حالة PRO والترخيص", dz = "حالة PRO والترخيص"), fontWeight = FontWeight.Bold, color = KonstrivoAmber)
            }
        }
    }
}