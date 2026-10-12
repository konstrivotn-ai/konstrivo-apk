package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.theme.KonstrivoSlate
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(viewModel: MainViewModel, onBack: () -> Unit, onUpgrade: () -> Unit, modifier: Modifier = Modifier) {
    val language by viewModel.selectedLanguage.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    BoardScaffold(title = language.tr("Profile / Mon Compte", "Profile / My Account", "الحساب", "الحساب"), tag = "profile_screen", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                    colors = CardDefaults.cardColors(containerColor = KonstrivoNavy),
                    modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(language.tr("FREE vs PRO", "FREE vs PRO", "مجاني vs برو", "مجاني vs برو"),
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KonstrivoGold)
                        val rows = listOf(
                            Triple(language.tr("Devis illimites", "Unlimited quotes", "عروض غير محدودة", "ديفي بلا حدود"), false, isAdmin),
                            Triple(language.tr("Catalogue complet", "Full catalog", "كتالوج كامل", "كتالوج كامل"), true, true),
                            Triple(language.tr("Support prioritaire", "Priority support", "دعم ذو اولوية", "دعم ذو اولوية"), false, isAdmin),
                        )
                        rows.forEach { (label, free, pro) ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                Text("FREE ${if (free) "OK" else "--"}  -  PRO ${if (pro) "OK" else "--"}",
                                    style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                        Button(onClick = onUpgrade,
                            colors = ButtonDefaults.buttonColors(containerColor = KonstrivoGold, contentColor = KonstrivoNavy),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(language.tr("Upgrade to PRO", "Upgrade to PRO", "الترقية الى برو", "الترقية الى برو"))
                        }
                    }
                }
            }
            item {
                BoardRowCard(BoardRow("Parametres", "Settings", "الاعدادات", "الاعدادات",
                    "Langue, pays, entreprise", "Language, country, company", "اللغة والدولة والشركة", "اللغة والبلد والشركة",
                    Icons.Default.Settings, KonstrivoSlate, "profile_settings",
                    onClick = { it.selectTab(AppTab.SETTINGS) }), viewModel)
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = KonstrivoGold)
                    Text("  KONSTRIVO PRO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
