package com.example.ui.components.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Country
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.components.designsystem.KonstrivoRadius

/**
 * KONSTRIVO PRO - Phase 4.1 system components (ADDITIVE layer).
 *
 * These composables complete the shared design-system registry without touching
 * any existing screen or API. They are pure presentation:
 *  - All labels/messages/currency symbols are supplied by the caller (4-language
 *    resolution stays at call-sites via the established tr() pattern).
 *  - No business logic, no state ownership beyond local UI state.
 */

/**
 * Unified engineering-style input field.
 *
 * Wraps M3 [OutlinedTextField] with our tokens: numeric keyboards, optional
 * unit suffix (m², ml...), error/supporting text and an optional testTag.
 * Minimum touch height respected by default M3 metrics.
 *
 * NOTE: this does NOT replace existing input usages; screens adopt it in
 * their own redesign phases (4.x).
 */
@Composable
fun KonstrivoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffixText: String? = null,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Number,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    tag: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = if (tag != null) modifier.testTag(tag) else modifier,
        label = { Text(label) },
        suffix = {
            if (suffixText != null) {
                Text(
                    text = suffixText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(imageVector = leadingIcon, contentDescription = null)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        supportingText = {
            if (supportingText != null) {
                Text(supportingText, style = MaterialTheme.typography.labelSmall)
            }
        },
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        shape = RoundedCornerShape(KonstrivoRadius.Button)  // R1: inputs 12dp
    )
}

/**
 * Read-only tile for ENGINE-CALCULATED values (surface, perimeter, totals).
 * Makes "computed" visually unmistakable from "editable": filled tinted
 * container, "=" glyph badge, tabular numerals and NO cursor field.
 * Presentation only — value comes pre-computed from the calculation engine.
 */
@Composable
fun KonstrivoCalculatedTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null
) {
    Surface(
        shape = KonstrivoShapes.Medium,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm),
            modifier = Modifier.padding(
                horizontal = KonstrivoSpacing.Md,
                vertical = KonstrivoSpacing.Sm
            )
        ) {
            Surface(
                shape = RoundedCornerShape(KonstrivoRadius.Badge),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "=",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge
                            .copy(fontFeatureSettings = "tnum"),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (unit != null) {
                        Text(
                            text = unit,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Unified procurement-style material line:
 *   NAME (weight)                  SUBTOTAL (navy, tabular)
 *   QUANTITY × UNIT • PRICE / UNIT
 * Data strings arrive pre-formatted from callers — pure presentation.
 */
@Composable
fun KonstrivoMaterialRow(
    name: String,
    quantityWithUnit: String,
    pricePerUnit: String,
    modifier: Modifier = Modifier,
    subtotal: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (subtotal != null) {
                Text(
                    text = subtotal,
                    style = MaterialTheme.typography.titleSmall
                        .copy(fontFeatureSettings = "tnum"),
                    color = KonstrivoNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$quantityWithUnit  •  $pricePerUnit",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
            modifier = Modifier.padding(top = KonstrivoSpacing.Sm)
        )
    }
}

/**
 * Deliberate error state — complements [KonstrivoEmptyState] in the core registry.
 * Explains WHAT failed and offers an optional recovery action (label provided by
 * the caller in the active language). Pure presentation.
 */
@Composable
fun KonstrivoErrorState(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    description: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KonstrivoSpacing.Lg, vertical = KonstrivoSpacing.Xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (actionLabel != null && onAction != null) {
            OutlinedButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}

/**
 * HT / TVA / TTC financial summary — engineering document style.
 * The TTC line dominates (navy surface, largest tabular figure); values are
 * pre-formatted strings from the caller so rounding stays owned by business logic.
 */
@Composable
fun KonstrivoResultSummary(
    htLabel: String,
    htValue: String,
    tvaLabel: String,
    tvaValue: String,
    ttcLabel: String,
    ttcValue: String,
    modifier: Modifier = Modifier,
    currencySymbol: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Xs)
    ) {
        SummaryLine(label = htLabel, value = htValue)
        SummaryLine(label = tvaLabel, value = tvaValue)
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
            modifier = Modifier.padding(vertical = KonstrivoSpacing.Xs)
        )
        Surface(
            shape = RoundedCornerShape(KonstrivoRadius.Card),
            color = KonstrivoNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = KonstrivoSpacing.Md, vertical = KonstrivoSpacing.Md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = ttcLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "$ttcValue${currencySymbol?.let { " $it" } ?: ""}",
                    style = MaterialTheme.typography.headlineSmall
                        .copy(fontFeatureSettings = "tnum"),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ============================================================================
// PHASE 4.1 ADDITIONS — Confirmation & Selection primitives
// Pure display components: every label/title is supplied by the caller
// (resolved via language.tr(...) so all four languages stay supported).
// No business logic, no invented callbacks — callers keep their own.
// ============================================================================

/**
 * Unified confirmation dialog (delete estimate, restore prices, logout ...).
 * @param isDestructive renders the confirm action in [error] color and must be
 * true for irreversible operations, per design-system safety rule.
 */
@Composable
fun KonstrivoConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    isDestructive: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val accent =
        if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = KonstrivoShapes.Dialog,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = accent),
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(text = confirmText, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(text = dismissText)
            }
        }
    )
}

/**
 * Unified country selector ready for Home / Settings / Market Prices.
 * Intentionally NOT wired into screens in Phase 4.1 (per plan) — it exists as
 * the single shared implementation so later phases replace their local copies
 * without touching state management.
 *
 * @param labelOf    display name resolver owned by the caller (language.tr).
 * @param subtitleOf optional secondary line resolver (e.g. currency) — caller-owned.
 */
@Composable
fun KonstrivoCountryPicker(
    countries: List<Country>,
    selectedCode: String?,
    title: String,
    dismissText: String,
    labelOf: (Country) -> String,
    subtitleOf: ((Country) -> String)? = null,
    itemTag: ((Country) -> String)? = null,
    onDismiss: () -> Unit,
    onSelect: (Country) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(text = dismissText)
            }
        },
        shape = KonstrivoShapes.Dialog,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                countries.forEach { country ->
                    val selected = country.code == selectedCode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(country) }
                            .padding(
                                horizontal = KonstrivoSpacing.Md,
                                vertical = KonstrivoSpacing.Sm
                            )
                            .then(if (itemTag != null) Modifier.testTag(itemTag(country)) else Modifier),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = country.flag,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.width(KonstrivoSpacing.Md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = labelOf(country),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            subtitleOf?.let { resolve ->
                                Text(
                                    text = resolve(country),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (selected) {
                            Spacer(modifier = Modifier.width(KonstrivoSpacing.Sm))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}
