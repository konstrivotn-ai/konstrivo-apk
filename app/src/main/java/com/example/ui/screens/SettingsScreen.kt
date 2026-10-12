package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.example.ui.components.designsystem.KonstrivoCountryPicker
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.localizedName
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.Country
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import com.example.ui.components.designsystem.tr

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.selectedLanguage.collectAsState()
    val country by viewModel.selectedCountry.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    var contractorName by remember(userSettings) { mutableStateOf(userSettings.contractorName) }
    var contractorPhone by remember(userSettings) { mutableStateOf(userSettings.contractorPhone) }
    var companyHeader by remember(userSettings) { mutableStateOf(userSettings.companyHeader) }
    var defaultWasteMargin by remember(userSettings) { mutableStateOf(userSettings.defaultWasteMargin.toString()) }

    var showCountryDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // COMPTE — Accès professionnel (STEP 2: opens standalone LoginScreen / Admin dashboard)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("owner_login_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAdmin) KonstrivoNavy else MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAdmin) Icons.Default.VerifiedUser else Icons.Default.AccountCircle,
                                                                contentDescription = if (isAdmin) language.tr("Administrateur", "Administrator", "مدير", "مدير") else language.tr("Accès professionnel", "Professional Access", "وصول مهني", "وصول مهني"),
                                tint = if (isAdmin) KonstrivoAmber else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = when (language) {
                                    AppLanguage.DERJA -> "الوصول المهني"
                                    AppLanguage.ARABIC -> "الوصول الاحترافي"
                                    AppLanguage.FRENCH -> "Accès professionnel"
                                    AppLanguage.ENGLISH -> "Professional Access"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isAdmin) {
                                    when (language) {
                                        AppLanguage.DERJA -> "جلسة المدير نشطة"
                                        AppLanguage.ARABIC -> "جلسة المدير نشطة"
                                        AppLanguage.FRENCH -> "Session administrateur active"
                                        AppLanguage.ENGLISH -> "Administrator session active"
                                    }
                                } else {
                                    when (language) {
                                        AppLanguage.DERJA -> "سجّل الدخول للوصول إلى أدوات الإدارة"
                                        AppLanguage.ARABIC -> "سجّل الدخول للوصول إلى أدوات الإدارة"
                                        AppLanguage.FRENCH -> "Connectez-vous pour accéder aux outils d’administration"
                                        AppLanguage.ENGLISH -> "Sign in to access administration tools"
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                    }

                    if (isAdmin) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = KonstrivoAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                                                text = language.tr("Outils d’administration disponibles","Administration tools available","أدوات الإدارة متوفرة","أدوات الإدارة متوفرة"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.openAdminDashboard() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_open_admin")
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                                                        Text(language.tr("Administration","Admin Dashboard","الإدارة","الإدارة"))
                        }

                        OutlinedButton(
                            onClick = { viewModel.logoutAdmin() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                                                        Text(language.tr("Se déconnecter","Sign out","تسجيل الخروج","تسجيل الخروج"))
                        }
                    } else {
                        Button(
                            onClick = { viewModel.openLoginScreen() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_open_login")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                                                        Text(language.tr("Se connecter","Sign in","تسجيل الدخول","تسجيل الدخول"))
                        }
                    }
                    // Reference boards: Profile (FREE vs PRO) + Subscription.
                    OutlinedButton(
                        onClick = { viewModel.openProfile() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_open_profile")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(language.tr("Profile / Mon Compte", "Profile / My Account", "الحساب", "الحساب"))
                    }
                    OutlinedButton(
                        onClick = { viewModel.openSubscription() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_open_subscription")
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(language.tr("Subscription", "Subscription", "الاشتراك", "الاشتراك"))
                    }
                }
            }
        }

        // App Header Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(KonstrivoNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = KonstrivoAmber, modifier = Modifier.size(28.dp))
                    }
                    Column {
                        Text(
                            text = language.tr("Paramètres & Profil Chantier","Settings & Site Profile","إعدادات وهوية الحرفي والشانتي","إعدادات وهوية الحرفي والشانتي"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                                                        text = language.tr(
                                "Konstrivo • BTP & Placo Pro 2026",
                                "Konstrivo • BTP & Placo Pro 2026",
                                "كونستريفو • بتبقى وبلاكو برو 2026",
                                "كونستريفو • بتبقى وبلاكو برو 2026"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Section 1: Language Selection
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = when (language) {
                            AppLanguage.DERJA -> "لغة التطبيق والمصطلحات"
                            AppLanguage.ARABIC -> "لغة التطبيق والمصطلحات"
                            AppLanguage.FRENCH -> "Langue de l'application"
                            AppLanguage.ENGLISH -> "App Language & Terms"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Two-column layout keeps all four language labels readable on narrow phones,
                    // including Arabic/Derja, without changing the language state or persistence.
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.entries.chunked(2).forEach { languageRow ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                languageRow.forEach { lang ->
                                    val isSel = lang == language
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.setLanguage(lang) },
                                        label = {
                                            Text(
                                                lang.displayName,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = KonstrivoGold,
                                            selectedLabelColor = KonstrivoNavy
                                        ),
                                        modifier = Modifier.weight(1f).testTag("lang_chip_${lang.code}")
                                    )
                                }
                                if (languageRow.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Country & Market Selection
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = when (language) {
                            AppLanguage.DERJA -> "البلد والعملة وسوق الأسعار"
                            AppLanguage.ARABIC -> "الدولة والعملة وسوق الأسعار"
                            AppLanguage.FRENCH -> "Pays & Devise du Marché"
                            AppLanguage.ENGLISH -> "Country & Market Currency"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        onClick = { showCountryDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().testTag("btn_select_country")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(text = country.flag, style = MaterialTheme.typography.headlineSmall)
                                Column {
                                    Text(
                                        text = country.localizedName(language),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${country.currencyCode} (${country.currencySymbol})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        }
                    }
                }
            }
        }

        // Section 3: Contractor Profile Details
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = language.tr("Profil & Coordonnées Artisan (Devis)","Contractor Profile & Company (Estimates)","بيانات المعلم / المقاول (تظهر في عروض الأسعار)","بيانات المعلم / المقاول (تظهر في عروض الأسعار)"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = contractorName,
                        onValueChange = { contractorName = it },
                                                label = { Text(language.tr("Nom de l'Artisan / Société","Craftsman / Company Name","اسم المعلم أو الشركة","اسم المعلم أو الشركة")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_contractor_name")
                    )

                    OutlinedTextField(
                        value = contractorPhone,
                        onValueChange = { contractorPhone = it },
                                                label = { Text(language.tr("Numéro de Téléphone / WhatsApp","Phone / WhatsApp Number","رقم الهاتف / الواتساب","رقم الهاتف / الواتساب")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_contractor_phone")
                    )

                    OutlinedTextField(
                        value = companyHeader,
                        onValueChange = { companyHeader = it },
                                                label = { Text(language.tr("En-tête des devis WhatsApp","WhatsApp Estimate Header","عنوان ومقدمة عروض الأسعار","عنوان ومقدمة عروض الأسعار")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_company_header")
                    )

                    OutlinedTextField(
                        value = defaultWasteMargin,
                        onValueChange = { defaultWasteMargin = it },
                                                label = { Text(language.tr("Marge de chute par défaut (%)","Default Waste Margin (%)","نسبة الهدر الافتراضية (%)","نسبة الهدر الافتراضية (%)")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_default_waste")
                    )

                    Button(
                        onClick = {
                            val waste = defaultWasteMargin.toDoubleOrNull() ?: 5.0
                            val updated = userSettings.copy(
                                contractorName = contractorName,
                                contractorPhone = contractorPhone,
                                companyHeader = companyHeader,
                                defaultWasteMargin = waste
                            )
                            viewModel.updateUserSettings(updated)
                                                        Toast.makeText(context, language.tr("Profil enregistré avec succès!","Profile saved successfully!","تم حفظ البيانات بنجاح!","تم حفظ البيانات بنجاح!"), Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_save_settings")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(language.tr("Enregistrer les modifications","Save Changes","حفظ التغييرات","حفظ التغييرات"))
                    }
                }
            }
        }

        // Section 4: Data Management & App Info
        item {
            Card(
                shape = RoundedCornerShape(KonstrivoRadius.Card),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = when (language) {
                            AppLanguage.DERJA -> "إدارة البيانات والنظام"
                            AppLanguage.ARABIC -> "إدارة البيانات والنظام"
                            AppLanguage.FRENCH -> "Base de données & Système"
                            AppLanguage.ENGLISH -> "Data Management & System"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = {
                            viewModel.resetPricesToDefault()
                                                        Toast.makeText(context, language.tr("Prix réinitialisés!","Prices reset to defaults!","تمت استعادة أسعار السوق الافتراضية!","تمت استعادة أسعار السوق الافتراضية!"), Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_reset_prices_settings")
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(language.tr("Restaurer les prix d'usine 2026","Restore 2026 Factory Prices","استعادة أسعار مواد السوق لسنة 2026","استعادة أسعار مواد السوق لسنة 2026"))
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                                                text = language.tr(
                            "• 100% Offline-First (Room SQLite)\n• Données et formules certifiées pour chantiers en Tunisie & Maghreb\n• Version 2.0.0 Pro",
                            "• 100% Offline-First (Room SQLite)\n• Certified data & formulas for Tunisia & Maghreb job sites\n• Version 2.0.0 Pro",
                            "• أولاً محليًا بلا اتصال (Room SQLite)\n• بيانات وصيغ معتمدة لمواقع الأعمال في تونس والمغرب\n• الإصدار 2.0.0 برو",
                            "• أولاً محليًا بلا اتصال (Room SQLite)\n• بيانات وصيغ معتمدة لمواقع الأعمال في تونس والمغرب\n• الإصدار 2.0.0 برو"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
        if (showCountryDialog) {
        KonstrivoCountryPicker(
            countries = Country.entries.toList(),
            selectedCode = country?.code,
            title = language.tr(
                "Sélectionner le Pays", "Select Country", "اختر الدولة", "اختر الدولة"
            ),
            dismissText = language.tr("Fermer", "Close", "إغلاق", "إغلاق"),
            labelOf = { c -> c.localizedName(language) },
            subtitleOf = { c -> "${c.currencyCode} (${c.currencySymbol})" },
            itemTag = { c -> "country_${c.code}" },
            onDismiss = { showCountryDialog = false },
            onSelect = { c ->
                viewModel.setCountry(c)
                showCountryDialog = false
            }
        )
    }
}
