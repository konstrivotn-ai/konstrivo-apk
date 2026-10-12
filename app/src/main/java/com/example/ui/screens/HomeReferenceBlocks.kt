package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.ProjectEstimateEntity
import com.example.data.model.AppLanguage
import com.example.ui.components.designsystem.KonstrivoPriceStyle
import com.example.ui.components.designsystem.KonstrivoPriceText
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold

@Composable
fun HomeActiveCounters(language: AppLanguage, estimateCount: Int, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(language.tr("Mes Chantiers Actifs", "My Active Projects", "مشاريعي النشطة", "الشانتي تاعي"),
            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.weight(1f).testTag("home_active_quotes")) {
                Column(Modifier.fillMaxWidth().padding(KonstrivoSpacing.CardInner)) {
                    KonstrivoPriceText(text = "$estimateCount", style = KonstrivoPriceStyle.Large, color = KonstrivoGold)
                    Text(language.tr("Devis", "Quotes", "العروض", "الديفي"),
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.weight(1f).testTag("home_active_projects")) {
                Column(Modifier.fillMaxWidth().padding(KonstrivoSpacing.CardInner)) {
                    KonstrivoPriceText(text = "$estimateCount", style = KonstrivoPriceStyle.Large)
                    Text(language.tr("Chantiers", "Projects", "المشاريع", "الشانتي"),
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun HomeNotificationsStrip(
    language: AppLanguage,
    estimates: List<ProjectEstimateEntity>,
    onOpenAll: () -> Unit,
    onOpenEstimate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("home_notifications_card")) {
        Column(Modifier.fillMaxWidth().padding(KonstrivoSpacing.CardInner), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = KonstrivoGold)
                Text(language.tr("Notifications", "Notifications", "الاشعارات", "الاشعارات"),
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f))
                Text(language.tr("Voir tout", "See all", "عرض الكل", "عرض الكل"),
                    style = MaterialTheme.typography.labelMedium, color = KonstrivoGold,
                    modifier = Modifier.clickable(onClick = onOpenAll))
            }
            if (estimates.isEmpty()) {
                Text(language.tr("Aucune notification.", "No notifications.", "لا اشعارات.", "لا اشعارات."),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                estimates.take(2).forEach { e ->
                    Row(Modifier.fillMaxWidth().clickable(onClick = onOpenEstimate),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (e.clientName.isNotBlank()) e.clientName else e.calculationTypeId,
                            style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        KonstrivoPriceText(text = "${e.grandTotal} ${e.currency}", style = KonstrivoPriceStyle.Small, color = KonstrivoGold)
                    }
                }
            }
        }
    }
}
