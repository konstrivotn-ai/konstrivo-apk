package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationType
import com.example.data.model.TradeCategory
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold

private data class CalcTile(
    val trade: TradeCategory, val type: CalculationType,
    val fr: String, val en: String, val ar: String, val dz: String,
    val icon: ImageVector, val tag: String,
)

/**
 * Reference "Selection calculator" grid (ref_top_05) — six calculator shortcuts
 * in a 2-column card grid. Each tile routes into the SAME existing engine by
 * selecting the trade + calculation type; no new calculation path is added.
 */
@Composable
fun ReferenceCalculatorGrid(
    language: AppLanguage,
    onSelect: (TradeCategory, CalculationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tiles = listOf(
        CalcTile(TradeCategory.MACONNERIE, CalculationType.MACONNERIE_BRIQUE, "Brique", "Brick", "ياجور", "ياجور", Icons.Default.GridView, "ref_calc_brick"),
        CalcTile(TradeCategory.PLACO, CalculationType.PLACO_CLOISON, "Cloison", "Partition", "فاصل", "فاصل", Icons.Default.Layers, "ref_calc_partition"),
        CalcTile(TradeCategory.PLACO, CalculationType.PLACO_PLAFOND_FIXE, "Plafond", "Ceiling", "سقف", "سقف", Icons.Default.Home, "ref_calc_ceiling"),
        CalcTile(TradeCategory.PEINTURE, CalculationType.PEINTURE_INTERIEURE, "Peinture", "Paint", "دهان", "دهان", Icons.Default.FormatPaint, "ref_calc_paint"),
        CalcTile(TradeCategory.CARRELAGE, CalculationType.CARRELAGE_SOL, "Carrelage", "Tiling", "سيراميك", "سيراميك", Icons.Default.ViewModule, "ref_calc_tiling"),
        CalcTile(TradeCategory.CREPISSAGE, CalculationType.CREPISSAGE_CIMENT, "Crépissage", "Plastering", "ترطيبة", "ترطيبة", Icons.Default.Castle, "ref_calc_plaster"),
    )
    Column(modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(language.tr("Selection calculator", "Calculator selection", "اختيار الحاسبة", "اختيار الحاسبة"),
            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Column(Modifier.fillMaxWidth().testTag("ref_calc_grid"), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            tiles.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { t ->
                        Card(onClick = { onSelect(t.trade, t.type) }, shape = RoundedCornerShape(KonstrivoRadius.Card),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            modifier = Modifier.weight(1f).testTag(t.tag)) {
                            Column(Modifier.fillMaxWidth().padding(vertical = KonstrivoSpacing.CardInner, horizontal = KonstrivoSpacing.Sm),
                                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(KonstrivoGold.copy(alpha = 0.12f), RoundedCornerShape(KonstrivoRadius.IconBox)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(t.icon, contentDescription = null, tint = KonstrivoGold, modifier = Modifier.size(26.dp))
                                }
                                Text(language.tr(t.fr, t.en, t.ar, t.dz), style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    // Keep rows balanced when the tile count is odd.
                    if (row.size == 1) {
                        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
