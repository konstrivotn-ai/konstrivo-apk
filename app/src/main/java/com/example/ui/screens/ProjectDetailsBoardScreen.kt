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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
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
import com.example.ui.components.designsystem.KonstrivoPriceStyle
import com.example.ui.components.designsystem.KonstrivoPriceText
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold
import com.example.ui.viewmodel.MainViewModel
import java.util.Date
import java.util.Locale
import org.json.JSONArray

@Composable
fun ProjectDetailsScreen(estimateId: Long, viewModel: MainViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val language by viewModel.selectedLanguage.collectAsState()
    val estimates by viewModel.savedEstimates.collectAsState()
    val estimate = estimates.firstOrNull { it.id == estimateId }
    val fmt = java.text.SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    BoardScaffold(title = language.tr("Project Details", "Project Details", "تفاصيل المشروع", "تفاصيل المشروع"), tag = "project_details_screen", onBack = onBack) {
        if (estimate == null) {
            LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
                item {
                    Card(shape = RoundedCornerShape(KonstrivoRadius.Card), modifier = Modifier.fillMaxWidth()) {
                        Text(language.tr("Devis introuvable.", "Quote not found.", "العرض غير موجود.", "الديفي غير موجود."),
                            modifier = Modifier.padding(16.dp))
                    }
                }
            }
            return@BoardScaffold
        }
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val title = if (estimate.clientName.isNotBlank()) estimate.clientName else language.tr("Projet sans nom", "Unnamed project", "مشروع بلا اسم", "مشروع بلا اسم")
                        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                        Text("${fmt.format(Date(estimate.createdAt))} - ${estimate.location}",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        KonstrivoPriceText(text = "${estimate.grandTotal} ${estimate.currency}", style = KonstrivoPriceStyle.Large, color = KonstrivoGold)
                    }
                }
            }
            item {
                Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = KonstrivoGold)
                            Text(language.tr("Travaux", "Works", "الاعمال", "الاعمال"),
                                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        val items = try { JSONArray(estimate.materialsJson) } catch (e: Exception) { JSONArray() }
                        if (items.length() == 0) Text(estimate.calculationTypeId, style = MaterialTheme.typography.bodySmall)
                        for (i in 0 until items.length()) {
                            val o = items.optJSONObject(i)
                            Text("- ${o.optString("nameFr")} : ${o.optDouble("totalPrice")} ${estimate.currency}",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = KonstrivoGold)
                            Text(language.tr("Task documents", "Task documents", "مستندات المهام", "مستندات المهام"),
                                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        val note = if (estimate.notes.isNotBlank()) estimate.notes else language.tr("Aucune note.", "No notes.", "لا ملاحظات.", "لا ملاحظات.")
                        Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(language.tr("Fichiers : partage via WhatsApp depuis Devis.", "Files: share via WhatsApp from Quotes.",
                                "الملفات : مشاركة عبر واتساب من العروض.", "الملفات : مشاركة عبر واتساب من العروض."),
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
