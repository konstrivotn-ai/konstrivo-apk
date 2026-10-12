package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Notifications
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
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

@Composable
fun NotificationsScreen(viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val language by viewModel.selectedLanguage.collectAsState()
    val estimates by viewModel.savedEstimates.collectAsState()
    BoardScaffold(title = language.tr("Notifications", "Notifications", "الاشعارات", "الاشعارات"), tag = "notifications_screen", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (estimates.isEmpty()) {
                item {
                    Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = KonstrivoGold)
                            Column {
                                Text(language.tr("Aucune notification", "No notifications", "لا اشعارات", "لا اشعارات"),
                                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(language.tr("Vos devis recents apparaitront ici.", "Your recent quotes will appear here.",
                                    "ستظهر عروضك الاخيرة هنا.", "الديفي الجدد يظهرو هنا."),
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                items(estimates.take(20)) { e ->
                    BoardRowCard(
                        BoardRow("Devis ${e.id}", "Quote ${e.id}", "عرض ${e.id}", "ديفي ${e.id}",
                            "${e.clientName} - ${e.grandTotal} ${e.currency}", "${e.clientName} - ${e.grandTotal} ${e.currency}",
                            "${e.clientName} - ${e.grandTotal} ${e.currency}", "${e.clientName} - ${e.grandTotal} ${e.currency}",
                            Icons.AutoMirrored.Filled.ReceiptLong, KonstrivoGold, "notification_estimate_${e.id}",
                            onClick = { it.selectTab(AppTab.ESTIMATES) }),
                        viewModel,
                    )
                }
            }
        }
    }
}
