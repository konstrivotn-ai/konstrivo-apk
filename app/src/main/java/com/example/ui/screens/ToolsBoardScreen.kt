package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoAmber
import com.example.ui.theme.KonstrivoEmerald
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoSlate
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ConstructionToolsScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val language by viewModel.selectedLanguage.collectAsState()
    BoardScaffold(title = language.tr("Construction Tools", "Construction Tools", "ادوات البناء", "ادوات الشانتي"), tag = "construction_tools_screen", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val rows = listOf(
                BoardRow("Wall Calculator", "Wall Calculator", "حاسبة الجدران", "حاسبة الحيطان",
                    "Maconnerie et placo", "Masonry and boards", "بناء وبلاكو", "بناء وبلاكو",
                    Icons.Default.Calculate, KonstrivoGold, "tools_wall",
                    onClick = { it.navigateToCalculator() }),
                BoardRow("Level", "Level", "الميزان", "الميزان",
                    "Guide de mise en oeuvre", "Installation guide", "دليل التركيب", "دليل التركيب",
                    Icons.Default.Engineering, KonstrivoAmber, "tools_level",
                    onClick = { it.selectTab(AppTab.GUIDE) }),
                BoardRow("Reference walls", "Reference walls", "جدران مرجعية", "حيطان مرجعية",
                    "Documents techniques", "Technical docs", "وثائق تقنية", "وثائق تقنية",
                    Icons.AutoMirrored.Filled.MenuBook, KonstrivoSlate, "tools_ref_walls",
                    onClick = { it.openDocs() }),
                BoardRow("Market prices", "Market prices", "اسعار السوق", "اسعار السلعة",
                    "Tarifs et tendances", "Tariffs and trends", "الاسعار والاتجاهات", "الاسعار والاتجاهات",
                    Icons.Default.MonetizationOn, KonstrivoEmerald, "tools_prices",
                    onClick = { it.selectTab(AppTab.PRICES) }),
                BoardRow("My quotes", "My quotes", "عروضي", "الديفي تاعي",
                    "Devis et partage", "Quotes and sharing", "العروض والمشاركة", "العروض والمشاركة",
                    Icons.AutoMirrored.Filled.ReceiptLong, KonstrivoGold, "tools_quotes",
                    onClick = { it.selectTab(AppTab.ESTIMATES) }),
            )
            items(rows) { BoardRowCard(it, viewModel) }
        }
    }
}
