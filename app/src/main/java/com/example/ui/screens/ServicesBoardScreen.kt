package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Person
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
fun ServicesScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val language by viewModel.selectedLanguage.collectAsState()
    BoardScaffold(title = language.tr("Services", "Services", "الخدمات", "الخدمات"), tag = "services_screen", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val rows = listOf(
                BoardRow("Service Contractor", "Service Contractor", "مقاول خدمات", "مقاول خدمات",
                    "Devis et suivi chantiers", "Quotes and site follow-up", "عروض ومتابعة", "عروض ومتابعة",
                    Icons.Default.Engineering, KonstrivoGold, "services_contractor",
                    onClick = { it.selectTab(AppTab.PROJECTS) }),
                BoardRow("Contractor", "Contractor", "المقاول", "المقاول",
                    "Fiche entreprise", "Company profile", "بطاقة الشركة", "بطاقة الشركة",
                    Icons.Default.Person, KonstrivoAmber, "services_contractor_profile",
                    onClick = { it.openProfile() }),
                BoardRow("Service services", "Service services", "خدمات الموقع", "خدمات الشانتي",
                    "Catalogue et prix", "Catalog and prices", "الكتالوج والاسعار", "الكتالوج والاسعار",
                    Icons.Default.Handyman, KonstrivoEmerald, "services_site",
                    onClick = { it.openMaterialsCatalog() }),
                BoardRow("Communication", "Communication", "التواصل", "التواصل",
                    "Partage WhatsApp", "WhatsApp sharing", "مشاركة واتساب", "مشاركة واتساب",
                    Icons.Default.HeadsetMic, KonstrivoSlate, "services_comms",
                    onClick = { it.selectTab(AppTab.ESTIMATES) }),
                BoardRow("Unit contractor", "Unit contractor", "مقاول الوحدات", "مقاول الوحدات",
                    "Guide chantier", "Site guide", "دليل الموقع", "دليل الشانتي",
                    Icons.AutoMirrored.Filled.MenuBook, KonstrivoAmber, "services_units",
                    onClick = { it.selectTab(AppTab.GUIDE) }),
                BoardRow("Mes devis", "My quotes", "عروضي", "الديفي تاعي",
                    "Historique et partage", "History and sharing", "السجل والمشاركة", "السجل والمشاركة",
                    Icons.AutoMirrored.Filled.ReceiptLong, KonstrivoGold, "services_quotes",
                    onClick = { it.selectTab(AppTab.ESTIMATES) }),
            )
            items(rows) { BoardRowCard(it, viewModel) }
        }
    }
}
