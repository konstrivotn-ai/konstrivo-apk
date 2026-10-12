package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Country
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSectionLabel
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MoreScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.selectedLanguage.collectAsState()
    val country by viewModel.selectedCountry.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    // PHASE 5 — Navigation unification: this screen is a pure launch hub.
    // PRICES / GUIDE / SETTINGS / CALCULATOR / ESTIMATES open through the SAME
    // tab mechanism used by HomeScreen and the top account button, so every
    // entry point renders the identical header & back behavior.
    // PHASE 4.6 — Services & Outils hub: tools grouped by intent (CALCUL /
    // CHANTIER / DOCUMENTS / RÉFÉRENCES), solid professional header (no
    // gradient), Coming Soon tools rendered disabled & non-clickable (no fake
    // navigation callbacks). Pure UI/UX phase — no architecture change.
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("more_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
                // Header — solid brand Navy identity (no decorative gradient)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            shape = RoundedCornerShape(KonstrivoRadius.Card),
                            colors = CardDefaults.cardColors(containerColor = KonstrivoDarkNavy),
                            border = BorderStroke(1.dp, KonstrivoGold.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(KonstrivoSpacing.Lg),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                                        .background(Color.White.copy(alpha = 0.10f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = null,
                                        tint = KonstrivoAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = language.tr(
                                            "Services & Outils BTP",
                                            "BTP Services & Tools",
                                            "خدمات وأدوات الموقع",
                                            "خدمات وأدوات الشانتي"
                                        ),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "KONSTRIVO PRO",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                        Text(
                            text = language.tr(
                                "Votre boîte à outils professionnel : calculs, guides, devis et prix du marché.",
                                "Your professional toolbox: calculations, guides, quotes and market prices.",
                                "مجموعة أدواتك المهنية: الحسابات، الأدلة، العروض وأسعار السوق.",
                                "مجموعة أدواتك المهنية: الحسابات، الأدلة، العروض وأسعار السوق."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ═══ Section CALCUL ═══
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MoreSectionHeader(
                            icon = Icons.Default.Calculate,
                            text = language.tr("CALCUL", "CALCULATION", "الحسابات", "الحسابات")
                        )
                        MoreNavCard(
                            title = language.tr("Calculatrice", "Calculator", "الآلة الحاسبة", "الآلة الحاسبة"),
                            subtitle = language.tr(
                                "Métré & calcul de matériaux par métier",
                                "Take-off & material calculations per trade",
                                "حساب الكميات والمواد لكل مهنة",
                                "حساب الكميات والمواد لكل مهنة"
                            ),
                            icon = Icons.Default.Calculate,
                            accentColor = KonstrivoGold,
                            onClick = { viewModel.selectTab(AppTab.CALCULATOR) },
                            modifier = Modifier.testTag("more_nav_calculator")
                        )
                        MoreNavCard(
                            title = language.tr("Material et Price Calculator", "Material and Price Calculator", "حاسبة المواد والاسعار", "حاسبة المواد والاسعار"),
                            subtitle = language.tr(
                                "Quantite et prix unitaire",
                                "Quantity and unit price",
                                "الكمية وسعر الوحدة",
                                "الكمية وسعر الوحدة"
                            ),
                            icon = Icons.Default.MonetizationOn,
                            accentColor = KonstrivoGold,
                            onClick = { viewModel.openMaterialCalc() },
                            modifier = Modifier.testTag("more_nav_material_calc")
                        )
                        MoreComingSoonCard(
                            title = language.tr("Convertisseur d'unités", "Unit Converter", "محوّل الوحدات", "محوّل الوحدات"),
                            subtitle = language.tr(
                                "m², m³, ml, kg… conversions rapides",
                                "m², m³, linear m, kg — quick conversions",
                                "تحويلات سريعة بين الوحدات",
                                "تحويلات سريعة بين الوحدات"
                            ),
                            icon = Icons.Default.Straighten,
                            comingSoonLabel = language.tr("BIENTÔT", "COMING SOON", "قريباً", "قريباً"),
                            modifier = Modifier.testTag("more_soon_unit_converter")
                        )
                        MoreComingSoonCard(
                            title = language.tr("Estimateur de quantités", "Quantity Estimator", "مُقدّر الكميات", "مُقدّر الكميات"),
                            subtitle = language.tr(
                                "Quantités indicatives par ouvrage",
                                "Indicative quantities per work item",
                                "تقدير الكميات لكل عمل",
                                "تقدير الكميات لكل عمل"
                            ),
                            icon = Icons.Default.SquareFoot,
                            comingSoonLabel = language.tr("BIENTÔT", "COMING SOON", "قريباً", "قريباً"),
                            modifier = Modifier.testTag("more_soon_quantity_estimator")
                        )
                    }
                }

                // ═══ Section CATALOGUE ═══
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MoreSectionHeader(
                            icon = Icons.Default.Inventory2,
                            text = language.tr("CATALOGUE", "CATALOG", "الكتالوج", "الكتالوج")
                        )
                        MoreNavCard(
                            title = language.tr("Matériaux / Catalog", "Materials / Catalog", "المواد / الكتالوج", "المواد / الكتالوج"),
                            subtitle = language.tr(
                                "Liste des matériaux (offline)",
                                "Materials list (offline)",
                                "قائمة المواد (بدون إنترنت)",
                                "قائمة المواد (بدون إنترنت)"
                            ),
                            icon = Icons.Default.Inventory2,
                            accentColor = KonstrivoGold,
                            onClick = { viewModel.openMaterialsCatalog() },
                            modifier = Modifier.testTag("more_nav_materials_catalog")
                        )
                    }
                }

                // ═══ Section CHANTIER ═══
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MoreSectionHeader(
                            icon = Icons.Default.Construction,
                            text = language.tr("CHANTIER", "ON-SITE", "الشانتي والموقع", "الشانتي")
                        )
                        MoreNavCard(
                            title = language.tr(
                                "Guide Chantier & Normes DTU",
                                "Site Guide & DTU Standards",
                                "دليل الموقع والمواصفات DTU",
                                "دليل الشانتي ومراحل التركيب"
                            ),
                            subtitle = language.tr(
                                "Placo, ossatures, enduits, sécurité chantier",
                                "Drywall, framing, coatings, site safety",
                                "البلاكو، الهياكل، الدهانات، العزل والسلامة",
                                "بلاكو، حديد، دهان، عزل، إجراءات السلامة"
                            ),
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            accentColor = KonstrivoGold,
                            onClick = { viewModel.selectTab(AppTab.GUIDE) },
                            modifier = Modifier.testTag("more_nav_guide")
                        )
                        MoreNavCard(
                            title = language.tr("Services", "Services", "الخدمات", "الخدمات"),
                            subtitle = language.tr(
                                "Contractors et services chantier",
                                "Contractors and site services",
                                "المقاولون وخدمات الموقع",
                                "المقاولون وخدمات الشانتي"
                            ),
                            icon = Icons.Default.Handyman,
                            accentColor = KonstrivoGold,
                            onClick = { viewModel.openServices() },
                            modifier = Modifier.testTag("more_nav_services")
                        )
                        MoreNavCard(
                            title = language.tr("Outils de chantier", "Site Tools", "ادوات الموقع", "ادوات الشانتي"),
                            subtitle = language.tr(
                                "Calculs et references rapides",
                                "Quick calculations and references",
                                "حسابات ومراجع سريعة",
                                "حسابات ومراجع سريعة"
                            ),
                            icon = Icons.Default.Construction,
                            accentColor = KonstrivoAmber,
                            onClick = { viewModel.openTools() },
                            modifier = Modifier.testTag("more_nav_site_tools")
                        )
                    }
                }

                // ═══ Section DOCUMENTS ═══
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MoreSectionHeader(
                            icon = Icons.AutoMirrored.Filled.ReceiptLong,
                            text = language.tr("DOCUMENTS", "DOCUMENTS", "المستندات", "الوثائق")
                        )
                        MoreNavCard(
                            title = language.tr("Mes Devis", "My Quotes", "عروض الأسعار المحفوظة", "عروض الأسعار المحفوظة"),
                            subtitle = language.tr(
                                "Historique, détail & partage WhatsApp",
                                "History & WhatsApp sharing",
                                "السجل والمشاركة عبر واتساب",
                                "السجل والمشاركة عبر واتساب"
                            ),
                            icon = Icons.AutoMirrored.Filled.ReceiptLong,
                            accentColor = KonstrivoGold,
                            onClick = { viewModel.selectTab(AppTab.ESTIMATES) },
                            modifier = Modifier.testTag("more_nav_estimates")
                        )
                        MoreNavCard(
                            title = language.tr("Archives & Documents", "Archives & Documents", "الارشيف والمستندات", "الارشيف والوثائق"),
                            subtitle = language.tr(
                                "Classement des devis et documents",
                                "Quote and document filing",
                                "تنظيم العروض والمستندات",
                                "تنظيم العروض والوثائق"
                            ),
                            icon = Icons.Default.Inventory2,
                            accentColor = KonstrivoSlate,
                            onClick = { viewModel.openDocs() },
                            modifier = Modifier.testTag("more_nav_archives")
                        )
                        MoreNavCard(
                            title = language.tr("Notifications", "Notifications", "الاشعارات", "الاشعارات"),
                            subtitle = language.tr(
                                "Devis recents et messages",
                                "Recent quotes and messages",
                                "العروض الاخيرة والرسائل",
                                "العروض الاخيرة والرسائل"
                            ),
                            icon = Icons.Default.Notifications,
                            accentColor = KonstrivoGold,
                            onClick = { viewModel.openNotifications() },
                            modifier = Modifier.testTag("more_nav_notifications")
                        )
                    }
                }

                // ═══ Section RÉFÉRENCES ═══
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MoreSectionHeader(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            text = language.tr("RÉFÉRENCES", "REFERENCES", "المراجع", "المراجع")
                        )
                        MoreNavCard(
                            title = language.tr(
                                "Tarifs des Matériaux & Prix",
                                "Material Prices & Tariffs",
                                "أسعار مواد البناء والسوق",
                                "أسعار سلع البناء والمواد"
                            ),
                            subtitle = "${country.flag} ${country.currencyCode} (${country.currencySymbol}) • 2026",
                            icon = Icons.Default.MonetizationOn,
                            accentColor = KonstrivoAmber,
                            onClick = { viewModel.selectTab(AppTab.PRICES) },
                            modifier = Modifier.testTag("more_nav_prices")
                        )
                        // NOTE (Phase 4.6): no dedicated Trades destination exists from
                        // More (trades are only reachable from Home / TradeDetails
                        // overlay). We do NOT invent navigation — the Métiers entry
                        // stays a non-clickable Coming Soon card.
                        MoreComingSoonCard(
                            title = language.tr("Métiers & Travaux", "Trades & Works", "المهن والأعمال", "المهن والتخصصات"),
                            subtitle = language.tr(
                                "Répertoire des corps d'état",
                                "Construction trade directory",
                                "دليل المهن والتخصصات",
                                "دليل المهن والتخصصات"
                            ),
                            icon = Icons.Default.Engineering,
                            comingSoonLabel = language.tr("BIENTÔT", "COMING SOON", "قريباً", "قريباً"),
                            modifier = Modifier.testTag("more_soon_trades")
                        )
                    }
                }

                // ═══ Section CONFIGURATION (existing destination — preserved) ═══
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MoreSectionHeader(
                            icon = Icons.Default.Settings,
                            text = language.tr(
                                "CONFIGURATION & PROFIL",
                                "SETUP & COMPANY PROFILE",
                                "الإعدادات وهوية المؤسسة",
                                "الإعدادات والهوية المهنية"
                            )
                        )
                        MoreNavCard(
                            title = language.tr(
                                "Paramètres & Profil Entreprise",
                                "Settings & Company Profile",
                                "الإعدادات وهوية المؤسسة",
                                "الإعدادات وملف المقاول"
                            ),
                            subtitle = if (userSettings.contractorName.isNotEmpty()) "${userSettings.contractorName} • ${language.displayName}" else language.tr(
                                "Langue, pays, marge de chute, devis",
                                "Language, country, waste margin, quotes",
                                "اللغة، الدولة، هدر المواد، العروض",
                                "اللغة، البلد، نسبة الهدر، الـ Devis"
                            ),
                            icon = Icons.Default.Settings,
                            accentColor = KonstrivoSlate,
                            onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                            modifier = Modifier.testTag("more_nav_settings")
                        )
                    }
                }

                // App Info & Version Footer
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "KONSTRIVO PRO v2.6.0",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = language.tr(
                                    "Fonctionne 100% Hors-ligne (Offline-First)",
                                    "Works 100% Offline (Offline-First)",
                                    "يعمل بدون إنترنت بالكامل (Offline-First)",
                                    "يخدم بدون إنترنت (Offline-First)"
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = KonstrivoEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
}

@Composable
fun MoreNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KonstrivoSpacing.CardInner),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 * PHASE 4.6 — Section header for the Services & Outils hub.
 * Small functional icon + the existing [KonstrivoSectionLabel] (same
 * composition pattern as CalculatorScreen sections).
 */
@Composable
private fun MoreSectionHeader(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        KonstrivoSectionLabel(text = text)
    }
}

/**
 * PHASE 4.6 — Coming Soon tool card.
 *
 * Strictly follows the Coming Soon rules:
 *  - NOT clickable (non-clickable Card overload, no onClick, no navigation)
 *  - visually disabled / ~60% emphasis (muted onSurfaceVariant tones)
 *  - localized "COMING SOON" badge, no chevron
 *  - mirrors the MoreNavCard visual family (same radii/spacing/icon-box), so
 *    the distinction stays purely about availability.
 */
@Composable
private fun MoreComingSoonCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    comingSoonLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KonstrivoSpacing.CardInner),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    MoreComingSoonBadge(label = comingSoonLabel)
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }
        }
    }
}

/** Small localized "COMING SOON" pill used by [MoreComingSoonCard]. */
@Composable
private fun MoreComingSoonBadge(label: String) {
    Surface(
        shape = RoundedCornerShape(KonstrivoRadius.Badge),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
