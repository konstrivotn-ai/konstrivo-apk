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
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SubscriptionScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val language by viewModel.selectedLanguage.collectAsState()
    BoardScaffold(title = language.tr("Subscription", "Subscription", "الاشتراك", "الاشتراك"), tag = "subscription_screen", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text(language.tr("Upgrade to PRO", "Upgrade to PRO", "الترقية الى برو", "الترقية الى برو"),
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(Triple("Monthly", "شهري", "subscription_monthly"), Triple("Annual", "سنوي", "subscription_annual")).forEach { (en, ar, tag) ->
                        val label = language.tr(en, en, ar, ar)
                        Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KonstrivoGold.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = KonstrivoGold)
                                Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("380", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = KonstrivoGold)
                            }
                        }
                    }
                }
            }
            item {
                Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()) {
                    Text(language.tr("Paiement non configure dans cette version. Aucun debit effectue.",
                        "Payment is not configured in this build. No charge is made.",
                        "الدفع غير مفعل في هذه النسخة. لن يتم خصم اي مبلغ.",
                        "الدفع غير مفعل في هذه النسخة. لن يتم خصم اي مبلغ."),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
