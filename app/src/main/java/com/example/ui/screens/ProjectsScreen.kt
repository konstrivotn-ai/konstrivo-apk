package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.ui.components.designsystem.KonstrivoCard
import com.example.ui.components.designsystem.KonstrivoEmptyState
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Projects / Chantiers screen shown in the reference.
 *
 * NOTE: the current offline-first DB schema in this ZIP does not contain
 * project entities (only quotes and prices). We therefore:
 *  - Implement the UI to match the reference
 *  - Show an honest empty-state
 *  - Provide an "Add" CTA as disabled/coming-soon (no fake persistence)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.selectedLanguage.collectAsState()
    val dateFmt = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("projects_screen")
    ) {
        // Banner header matching the reference: title + small description
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KonstrivoSpacing.Lg, vertical = KonstrivoSpacing.Sm),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = language.tr("Projets / Chantiers", "Projects / Sites", "المشاريع / الشانتي", "المشاريع / الشانتي"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = language.tr(
                            "Suivi des chantiers, documents et étapes.",
                            "Track sites, documents and milestones.",
                            "تتبع المشاريع، الملفات، والمراحل.",
                            "تتبع المشاريع، الملفات، والمراحل."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledIconButton(
                    onClick = { /* coming soon */ },
                    enabled = false,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = KonstrivoGold,
                        disabledContainerColor = KonstrivoGold.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("btn_add_project")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }

        // Reference "Projects / Chantiers": REAL saved quotes act as projects.
        // No fake projects: empty-state stays honest when there are no quotes.
        val savedProjects by viewModel.savedEstimates.collectAsState()
        if (savedProjects.isEmpty()) {
            KonstrivoEmptyState(
                title = language.tr(
                    "Aucun chantier enregistré",
                    "No projects yet",
                    "لا توجد مشاريع محفوظة",
                    "لا توجد مشاريع محفوظة"
                ),
                subtitle = language.tr(
                    "Cette version conserve le calcul et les devis hors ligne. La gestion complète des chantiers arrive avec la synchronisation backend.",
                    "This build keeps offline calculation and quotes. Full project tracking will be enabled once backend sync is configured.",
                    "هذا الإصدار يحافظ على الحسابات والعروض بدون إنترنت. إدارة المشاريع الكاملة ستعمل بعد إعداد المزامنة.",
                    "هذا الإصدار يخدم الحسابات والديفي بلا إنترنت. إدارة المشاريع الكاملة تزيد بعد إعداد المزامنة."
                ),
                icon = Icons.Default.Folder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KonstrivoSpacing.Lg)
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = KonstrivoSpacing.Lg, vertical = KonstrivoSpacing.Md),
            verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
        ) {
            items(savedProjects, key = { it.id }) { project ->
                KonstrivoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_card_${project.id}"),
                    showBorder = true,
                    onClick = { viewModel.openProjectDetails(project.id) },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = KonstrivoGold)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (project.clientName.isNotBlank()) project.clientName else project.calculationTypeId,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = if (project.location.isNotBlank()) project.location else language.tr("Lieu", "Location", "الموقع", "الموقع"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = dateFmt.format(Date(project.createdAt)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${project.grandTotal} ${project.currency}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = KonstrivoGold,
                            )
                        }
                    }
                }
            }
        }
    }
}

