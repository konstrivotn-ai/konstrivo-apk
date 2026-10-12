package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.MaterialPriceEntity
import com.example.ui.components.designsystem.KonstrivoPriceStyle
import com.example.ui.components.designsystem.KonstrivoPriceText
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.viewmodel.MainViewModel

/**
 * Material & Price Calculator (ref_top_06).
 *
 * Presentation layer only:
 *  - Reads the SAME Room-backed price list used by Market Prices.
 *  - Segmented category chips + material dropdown + quantity/unit-price inputs.
 *  - Live result rows (real prices) and a gold "Prix total" bar.
 *  - No fake data: everything derives from MaterialPriceEntity rows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialPriceCalculatorScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val language by viewModel.selectedLanguage.collectAsState()
    val prices by viewModel.pricesList.collectAsState()
    var qtyText by remember { mutableStateOf("10") }
    var selectedId by remember(prices) { mutableStateOf(prices.firstOrNull()?.id ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("all") }

    val categories = remember(prices) { listOf("all") + prices.map { it.category }.distinct() }
    val selected = prices.firstOrNull { it.id == selectedId } ?: prices.firstOrNull()

    // Category filter drives both the dropdown pool and the result rows.
    val categoryPrices = remember(prices, selectedCategory) {
        if (selectedCategory == "all") prices else prices.filter { it.category == selectedCategory }
    }
    val resultRows: List<MaterialPriceEntity> = remember(categoryPrices, selected) {
        // Selected material first, then its category neighbours.
        listOfNotNull(selected) + categoryPrices.filter { it.id != selected?.id }.take(6)
    }

    val qty = qtyText.toDoubleOrNull() ?: 0.0
    val total = (selected?.currentPrice ?: 0.0) * qty

    BoardScaffold(
        title = language.tr("Material et Price Calculator", "Material and Price Calculator", "حاسبة المواد والاسعار", "حاسبة المواد والاسعار"),
        tag = "material_price_calculator",
        onBack = onBack
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Segmented category selector (ref: Ciment | Sable / Bloc | Parpaing rows) ──
            item {
                Card(
                    shape = RoundedCornerShape(KonstrivoRadius.Card),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth().testTag("mpc_category_card")
                ) {
                    Column(Modifier.fillMaxWidth().padding(KonstrivoSpacing.CardInner), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            language.tr("Catégorie de matériaux", "Material category", "فئة المواد", "فئة المواد"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { cat ->
                                val label = when (cat) {
                                    "all" -> language.tr("Tout", "All", "الكل", "الكل")
                                    else -> cat.replace('_', ' ').replaceFirstChar { it.uppercase() }
                                }
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = KonstrivoGold,
                                        selectedLabelColor = KonstrivoNavy
                                    ),
                                    modifier = Modifier.testTag("mpc_cat_$cat")
                                )
                            }
                        }
                    }
                }
            }
            // ── Material + quantity + unit price inputs ──
            item {
                Card(
                    shape = RoundedCornerShape(KonstrivoRadius.Card),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth().testTag("mpc_inputs_card")
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = KonstrivoGold)
                            Text(
                                language.tr("Quantite et prix unitaire", "Quantity and unit price", "الكمية وسعر الوحدة", "الكمية وسعر الوحدة"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = selected?.nameFr ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(language.tr("Materiau", "Material", "المادة", "المادة")) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor().testTag("mpc_material_dropdown"),
                                shape = RoundedCornerShape(KonstrivoRadius.Button)
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                categoryPrices.take(100).forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text("${p.nameFr} - ${p.currentPrice} ${p.currency}") },
                                        onClick = { selectedId = p.id; expanded = false }
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = qtyText,
                                onValueChange = { qtyText = it },
                                label = { Text(language.tr("Quantite", "Quantity", "الكمية", "الكمية")) },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("mpc_quantity"),
                                singleLine = true,
                                shape = RoundedCornerShape(KonstrivoRadius.Button)
                            )
                            OutlinedTextField(
                                value = selected?.let { "${it.currentPrice}" } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(language.tr("Prix unitaire", "Unit price", "سعر الوحدة", "سعر الوحدة")) },
                                modifier = Modifier.weight(1f).testTag("mpc_unit_price"),
                                singleLine = true,
                                shape = RoundedCornerShape(KonstrivoRadius.Button)
                            )
                        }
                    }
                }
            }
            // ── Result rows with real prices (ref: Ciments / Blocs / Béton rows) ──
            if (resultRows.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(KonstrivoRadius.Card),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth().testTag("mpc_results_card")
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                language.tr("Resultats", "Results", "النتائج", "النتائج"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            resultRows.forEach { row ->
                                val rowQty = if (row.id == selected?.id) qty else 1.0
                                Row(
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(row.nameFr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        Text(
                                            "${row.unit} · ${row.category}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        KonstrivoPriceText(
                                            text = "${"%.2f".format(row.currentPrice * rowQty)} ${row.currency}",
                                            style = KonstrivoPriceStyle.Medium,
                                            color = KonstrivoGold
                                        )
                                        Text(
                                            "${"%.0f".format(rowQty)} × ${"%.2f".format(row.currentPrice)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Gold total bar (ref: "Prix total" button) ──
            item {
                Card(
                    shape = RoundedCornerShape(KonstrivoRadius.Button),
                    colors = CardDefaults.cardColors(containerColor = KonstrivoGold),
                    modifier = Modifier.fillMaxWidth().testTag("mpc_total_bar")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            language.tr("Prix total", "Total price", "السعر الإجمالي", "السعر الإجمالي"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = KonstrivoNavy
                        )
                        KonstrivoPriceText(
                            text = "${"%.2f".format(total)} ${selected?.currency ?: ""}",
                            style = KonstrivoPriceStyle.Large,
                            color = KonstrivoNavy
                        )
                    }
                }
            }
        }
    }
}
