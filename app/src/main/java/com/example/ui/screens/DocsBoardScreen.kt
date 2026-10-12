package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoAmber
import com.example.ui.theme.KonstrivoEmerald
import com.example.ui.theme.KonstrivoGold
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TechnicalDocsScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val language by viewModel.selectedLanguage.collectAsState()
    BoardScaffold(title = language.tr("Technical Documents", "Technical Documents", "الوثائق التقنية", "الوثائق التقنية"), tag = "technical_docs_screen", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val rows = listOf(
                BoardRow("Regulation Standards", "Regulation Standards", "المعايير التنظيمية", "المعايير التنظيمية",
                    "Guide chantier cloison", "Site guide partitions", "دليل الموقع فواصل", "دليل الشانتي فواصل",
                    Icons.AutoMirrored.Filled.MenuBook, KonstrivoGold, "docs_standards",
                    onClick = { it.selectTab(AppTab.GUIDE) }),
                BoardRow("Procedure grids", "Procedure grids", "شبكات الاجراءات", "شبكات الاجراءات",
                    "Joints et finition", "Joints and finishing", "الفواصل والتشطيب", "الفواصل والفينيسيون",
                    Icons.Default.Description, KonstrivoAmber, "docs_procedures",
                    onClick = { it.selectTab(AppTab.GUIDE) }),
                BoardRow("Materials catalog", "Materials catalog", "كتالوج المواد", "كتالوج المواد",
                    "Fiches materiaux", "Material sheets", "بطاقات المواد", "بطاقات المواد",
                    Icons.Default.Inventory2, KonstrivoEmerald, "docs_catalog",
                    onClick = { it.openMaterialsCatalog() }),
            )
            items(rows) { BoardRowCard(it, viewModel) }
        }
    }
}
