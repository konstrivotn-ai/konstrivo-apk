package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.MaterialPriceEntity
import com.example.data.model.AppLanguage
import com.example.data.model.Country
import com.example.ui.components.EditPriceDialog
import com.example.ui.components.designsystem.KonstrivoBadge
import com.example.ui.components.designsystem.KonstrivoCard
import com.example.ui.components.designsystem.KonstrivoConfirmDialog
import com.example.ui.components.designsystem.KonstrivoCountryPicker
import com.example.ui.components.designsystem.KonstrivoEmptyState
import com.example.ui.components.designsystem.KonstrivoPriceStyle
import com.example.ui.components.designsystem.tr
import com.example.ui.components.designsystem.KonstrivoPriceText
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy

import com.example.ui.viewmodel.MainViewModel

/**
 * KONSTRIVO PRO - Phase 4.5: Market Prices UX rework.
 *
 * Presentation layer only:
 *  - All price loading/saving/restoring goes through the existing MainViewModel API.
 *  - Search & category filtering logic is unchanged (same predicates as before).
 *  - Reuses the shared design system (KonstrivoCard/PriceText/Badge/EmptyState,
 *    KonstrivoConfirmDialog, KonstrivoCountryPicker) instead of local copies.
 *  - Complete FR / EN / AR / Derja localization via language.tr(fr, en, ar, dz).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPricesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.selectedLanguage.collectAsState()
    val country by viewModel.selectedCountry.collectAsState()
    val pricesList by viewModel.pricesList.collectAsState()
    val searchQuery by viewModel.priceSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedPriceCategory.collectAsState()

    var editingPriceItem by remember { mutableStateOf<MaterialPriceEntity?>(null) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val categories = listOf(
        "all" to language.tr("Tout", "All", "الكل", "الكل"),
        "placo" to language.tr("Plaques", "Boards", "ألواح البلاكو", "البلاكو"),
        "ossature" to language.tr("Ossature Métal", "Metal framing", "الحديد والهيكل", "الهيكل المعدني"),
        "visserie" to language.tr("Visserie & Accessoires", "Fasteners & accessories", "البراغي والإكسسوارات", "البراغي والإكسسوارات"),
        "enduits" to language.tr("Enduits & Colles", "Plasters & adhesives", "المعجون والغراء", "المعاجين والغراء"),
        "dalles" to language.tr("Dalles 60x60", "Tiles 60x60", "دالات 60×60", "الدالات 60×60"),
        "isolation" to language.tr("Isolation", "Insulation", "العزل والصوت", "العزل والصوت"),
        "peinture" to language.tr("Peinture", "Paint", "الدهان والديكور", "الدهان والديكور"),
        "maconnerie" to language.tr("Maçonnerie", "Masonry", "الآجر والإسمنت", "الآجر والإسمنت"),
        "carrelage" to language.tr("Carrelage", "Tiling", "السيراميك", "السيراميك"),
        "bois_alu" to language.tr("Menuiserie", "Carpentry & aluminium", "النجارة والألمنيوم", "النجارة والألمنيوم")
    )

    // UNCHANGED filtering logic (same predicates as the previous implementation).
    val filteredPrices = remember(pricesList, searchQuery, selectedCategory) {
        pricesList.filter { item ->
            val matchCategory = selectedCategory == "all" || item.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    item.nameFr.contains(searchQuery, ignoreCase = true) ||
                    item.nameAr.contains(searchQuery, ignoreCase = true) ||
                    item.nameDerja.contains(searchQuery, ignoreCase = true) ||
                    item.brandInfo.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("market_prices_screen")
    ) {
        // ── Header: country + currency + material count ──
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KonstrivoSpacing.Lg, vertical = KonstrivoSpacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(KonstrivoRadius.Badge))
                        .clickable { showCountryPicker = true }
                        .padding(horizontal = KonstrivoSpacing.Md, vertical = KonstrivoSpacing.Xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
                ) {
                    Text(text = country.flag, style = MaterialTheme.typography.titleLarge)
                    Column {
                        Text(
                            text = if (language == AppLanguage.FRENCH || language == AppLanguage.ENGLISH) country.nameFr else country.nameAr,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${country.currencyCode} (${country.currencySymbol})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                KonstrivoBadge(
                    text = "${pricesList.size} " + language.tr("matériaux", "materials", "خامات", "خامات"),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = KonstrivoNavy,
                    modifier = Modifier.testTag("market_prices_count")
                )
            }
        }

        TextButton(
            onClick = { showResetConfirm = true },
            modifier = Modifier
                .padding(start = KonstrivoSpacing.Lg, top = KonstrivoSpacing.Sm)
                .heightIn(min = 48.dp)
        ) {
            Icon(
                Icons.Default.Restore,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(KonstrivoSpacing.Xs))
            Text(language.tr("Restaurer les tarifs du marché", "Restore market prices", "استعادة أسعار السوق", "أرجّع أسعار السوق"))
        }

        // ── Search (logic unchanged; clearer affordances) ──
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setPriceSearchQuery(it) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setPriceSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            placeholder = {
                Text(
                    language.tr(
                        "Rechercher un matériau (ex: fourrure, BA13, enduit...)",
                        "Search a material (e.g. furring, BA13, plaster...)",
                        "ابحث عن مادة (مثل: فورور، بلاكو، ياجور...)",
                        "أبحث على مادة (مثلا: فورور، بلاكو...)"
                    )
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(KonstrivoRadius.Button),  // R4: inputs unified to 12dp
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KonstrivoSpacing.Lg)
                .testTag("input_price_search")
        )

        // ── Live results feedback (presentation only) ──
        if (searchQuery.isNotBlank() || selectedCategory != "all") {
            Text(
                text = "${filteredPrices.size} " + language.tr("résultat(s)", "result(s)", "نتيجة", "نتيجة") +
                        " · ${pricesList.size} " + language.tr("au total", "in total", "المجموع", "المجموع"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KonstrivoSpacing.Lg, vertical = KonstrivoSpacing.Xs)
                    .testTag("market_prices_results_count")
            )
        }

        // ── Category filters (selection logic unchanged) ──
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm),
            contentPadding = PaddingValues(horizontal = KonstrivoSpacing.Lg),
            modifier = Modifier.padding(vertical = KonstrivoSpacing.Xs)
        ) {
            items(categories) { (catKey, catTitle) ->
                FilterChip(
                    selected = selectedCategory == catKey,
                    onClick = { viewModel.setSelectedPriceCategory(catKey) },
                    label = { Text(catTitle, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KonstrivoGold,  // R4: unified reference yellow #FFC72C
                        selectedLabelColor = KonstrivoNavy
                    ),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("category_filter_$catKey")
                )
            }
        }

        // ── Price list ──
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md),
            contentPadding = PaddingValues(
                start = KonstrivoSpacing.Lg,
                end = KonstrivoSpacing.Lg,
                top = KonstrivoSpacing.Sm,
                bottom = KonstrivoSpacing.Xxl
            )
        ) {
            // Reference board "Trends" block — REAL data only: top-3 most
            // expensive materials of the current filtered list. No mock prices.
            if (filteredPrices.size >= 3 && searchQuery.isBlank() && selectedCategory == "all") {
                item {
                    androidx.compose.material3.Card(
                        shape = RoundedCornerShape(KonstrivoRadius.Card),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth().testTag("market_trends_card"),
                    ) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.fillMaxWidth().padding(KonstrivoSpacing.CardInner),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = language.tr("Trends", "Trends", "الاتجاهات", "الاتجاهات"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            val trends = remember(filteredPrices) { filteredPrices.sortedByDescending { it.currentPrice }.take(3) }
                            trends.forEach { t ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.FRENCH || language == AppLanguage.ENGLISH) t.nameFr else t.nameAr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f),
                                    )
                                    KonstrivoPriceText(
                                        text = "${t.currentPrice} ${t.currency}",
                                        style = KonstrivoPriceStyle.Small,
                                        color = KonstrivoGold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (filteredPrices.isEmpty()) {
                item {
                    KonstrivoEmptyState(
                        title = language.tr("Aucun matériau trouvé", "No material found", "لا توجد مادة", "ما فماش مادة"),
                        subtitle = language.tr(
                            "Essayez un autre mot-clé ou une autre catégorie.",
                            "Try another keyword or category.",
                            "جرّب كلمة أخرى أو فئة أخرى.",
                            "جرّب كلمة ولا فئة أخرى."
                        ),
                        icon = Icons.Default.Search,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            items(filteredPrices) { item ->
                MarketPriceCard(
                    item = item,
                    language = language,
                    categoryTitle = categories.firstOrNull { it.first == item.category }?.second ?: item.category,
                    onEdit = { editingPriceItem = item }
                )
            }
        }

        // ── Edit price (existing shared dialog, unchanged API) ──
        editingPriceItem?.let { item ->
            EditPriceDialog(
                priceItem = item,
                language = language,
                onDismiss = { editingPriceItem = null },
                onSave = { newPrice ->
                    viewModel.updateMaterialPrice(item.id, newPrice)
                    editingPriceItem = null
                    Toast.makeText(
                        context,
                        language.tr("Prix mis à jour !", "Price updated!", "تم تحديث السعر!", "السعر بدّل!"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        // ── Restore/reset: confirm-gated via the shared design-system dialog ──
        if (showResetConfirm) {
            KonstrivoConfirmDialog(
                title = language.tr("Restaurer les tarifs ?", "Restore prices?", "استعادة الأسعار؟", "أرجّع الأسعار؟"),
                message = language.tr(
                    "Tous les prix modifiés retrouveront les valeurs standard du marché sélectionné.",
                    "All modified prices will be reset to the standard market values.",
                    "ستعود جميع الأسعار المعدّلة إلى القيم القياسية للسوق المحدد.",
                    "كل الأسعار اللي بدّلتهم يرجعو لقيم السوق القياسية."
                ),
                confirmText = language.tr("Restaurer", "Restore", "استعادة", "أرجّع"),
                dismissText = language.tr("Annuler", "Cancel", "إلغاء", "إلغاء"),
                isDestructive = true,
                onDismiss = { showResetConfirm = false },
                onConfirm = {
                    viewModel.resetPricesToDefault()
                    showResetConfirm = false
                    Toast.makeText(
                        context,
                        language.tr("Tarifs restaurés !", "Prices restored!", "تمت الاستعادة!", "الأسعار رجعو!"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        // ── Country picker: single shared design-system component ──
        if (showCountryPicker) {
            KonstrivoCountryPicker(
                countries = Country.entries.toList(),
                selectedCode = country.code,
                title = language.tr("Sélectionner le Pays / Marché", "Select country / market", "اختر الدولة / السوق", "أختار البلاد / السوق"),
                dismissText = language.tr("Fermer", "Close", "إغلاق", "أغلق"),
                labelOf = { c -> if (language == AppLanguage.FRENCH || language == AppLanguage.ENGLISH) c.nameFr else c.nameAr },
                subtitleOf = { c -> "${c.currencyCode} (${c.currencySymbol})" },
                itemTag = { c -> "country_select_${c.code}" },
                onDismiss = { showCountryPicker = false },
                onSelect = { c ->
                    viewModel.setCountry(c)
                    showCountryPicker = false
                }
            )
        }
    }
}

/**
 * Professional price card. The PRICE is the strongest visual element
 * (KonstrivoPriceText/Large + tabular figures); name, unit, category and
 * source are secondary lines. Whole card opens the existing EditPriceDialog.
 */
@Composable
private fun MarketPriceCard(
    item: MaterialPriceEntity,
    language: AppLanguage,
    categoryTitle: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isModified = item.currentPrice != item.defaultPrice
    val displayName = when (language) {
        AppLanguage.DERJA -> item.nameDerja
        AppLanguage.ARABIC -> item.nameAr
        else -> item.nameFr // EN falls back to FR: protected data model has no nameEn
    }

    KonstrivoCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("price_item_${item.id}"),
        onClick = onEdit,
        contentPadding = KonstrivoSpacing.CardInner
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Xs)
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isModified) {
                        KonstrivoBadge(text = language.tr("Modifié", "Modified", "معدّل", "معدّل"))
                    }
                }
                Text(
                    text = "${language.tr("Unité", "Unit", "الوحدة", "الوحدة")}: ${item.unit} · " +
                            "${language.tr("Catégorie", "Category", "الفئة", "الفئة")}: $categoryTitle",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.brandInfo.isNotEmpty()) {
                    Text(
                        text = "${language.tr("Source", "Source", "المصدر", "المصدر")}: ${item.brandInfo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // PRICE = strongest visual element on the card
                KonstrivoPriceText(
                    text = "${item.currentPrice} ${item.currency}",
                    style = KonstrivoPriceStyle.Large,
                    color = MaterialTheme.colorScheme.primary
                )
                if (isModified) {
                    Text(
                        text = "${language.tr("Standard", "Standard", "القياسي", "القياسي")}: ${item.defaultPrice} ${item.currency}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = language.tr("Modifier", "Edit", "تعديل", "بدّل"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
