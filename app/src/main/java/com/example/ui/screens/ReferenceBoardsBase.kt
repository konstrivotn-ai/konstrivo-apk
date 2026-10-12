package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.KonstrivoSpacing
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.viewmodel.MainViewModel

internal data class BoardRow(
    val titleFr: String, val titleEn: String, val titleAr: String, val titleDz: String,
    val subFr: String, val subEn: String, val subAr: String, val subDz: String,
    val icon: ImageVector, val accent: Color, val tag: String,
    val onClick: (MainViewModel) -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BoardScaffold(
    title: String, tag: String, onBack: () -> Unit, content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("${tag}_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KonstrivoNavy),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize().testTag(tag),
    ) { pad -> Box(Modifier.fillMaxSize().padding(pad)) { content() } }
}

@Composable
internal fun BoardRowCard(row: BoardRow, viewModel: MainViewModel) {
    val language by viewModel.selectedLanguage.collectAsState()
    Card(
        onClick = { row.onClick(viewModel) },
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth().testTag(row.tag),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(KonstrivoSpacing.CardInner),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(KonstrivoRadius.IconBox))
                    .background(row.accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) { Icon(row.icon, contentDescription = null, tint = row.accent, modifier = Modifier.size(24.dp)) }
            Column(Modifier.weight(1f)) {
                Text(language.tr(row.titleFr, row.titleEn, row.titleAr, row.titleDz),
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(language.tr(row.subFr, row.subEn, row.subAr, row.subDz),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}
