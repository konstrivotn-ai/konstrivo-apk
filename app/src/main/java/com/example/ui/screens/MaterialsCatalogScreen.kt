package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.domain.material.MaterialCatalog
import com.example.domain.model.Material
import com.example.domain.model.MaterialCategory
import com.example.ui.components.designsystem.KonstrivoCard
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy

import com.example.ui.viewmodel.MainViewModel

/**
 * Materials / Catalog screen (ref_bot_08).
 *
 * Offline-first: uses the immutable [MaterialCatalog] domain list.
 * No prices here (prices are handled in MarketPricesScreen).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsCatalogScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.selectedLanguage.collectAsState()

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<MaterialCategory?>(null) }

    val categories = remember { MaterialCategory.entries.filter { it != MaterialCategory.OTHER } }
    val filtered = remember(query, selectedCategory, language) {
        MaterialCatalog.ALL_MATERIALS.filter { m ->
            val catOk = selectedCategory == null || m.category == selectedCategory
            val q = query.trim()
            val qOk = q.isBlank() ||
                    m.nameFr.contains(q, true) ||
                    m.nameEn.contains(q, true) ||
                    m.nameAr.contains(q, true) ||
                    m.nameDerja.contains(q, true) ||
                    m.brandInfo.contains(q, true)
            catOk && qOk
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("materials_catalog_screen")
    ) {
        // Top mini header like the ref (simple back + title + count)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KonstrivoSpacing.Lg, vertical = KonstrivoSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_materials_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = language.tr("Materials / Catalog", "Materials / Catalog", "المواد / الكتالوج", "المواد / الكتالوج"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "${filtered.size} / ${MaterialCatalog.ALL_MATERIALS.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = {
                Text(language.tr("Search…", "Search…", "بحث…", "بحث…"))
            },
            singleLine = true,
            shape = RoundedCornerShape(KonstrivoRadius.Button),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KonstrivoSpacing.Lg)
                .testTag("materials_search")
        )

        LazyRow(
            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = KonstrivoSpacing.Lg)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text(language.tr("ALL", "ALL", "الكل", "الكل")) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KonstrivoGold,
                        selectedLabelColor = KonstrivoNavy
                    )
                )
            }
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat.localizedName(language)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KonstrivoGold,
                        selectedLabelColor = KonstrivoNavy
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = KonstrivoSpacing.Lg,
                end = KonstrivoSpacing.Lg,
                top = 6.dp,
                bottom = 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id }) { material ->
                MaterialRow(material = material, language = language)
            }
        }
    }
}

@Composable
private fun MaterialRow(material: Material, language: AppLanguage) {
    KonstrivoCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = null,
        showBorder = true,
        contentPadding = KonstrivoSpacing.CardInner
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.localizedName(language),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = material.brandInfo.ifBlank { material.category.localizedName(language) },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = KonstrivoGold.copy(alpha = 0.12f)
            ) {
                Text(
                    text = material.standardUnit.localizedName(language),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = KonstrivoGold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
