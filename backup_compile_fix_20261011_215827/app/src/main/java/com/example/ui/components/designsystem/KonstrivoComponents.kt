package com.example.ui.components.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KonstrivoError
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.theme.KonstrivoNumericStyles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * KONSTRIVO PRO - Design System foundation (STEP 1).
 *
 * Reusable UI primitives built strictly on top of the theme tokens:
 *  - Colors -> ui/theme/Color.kt (via MaterialTheme.colorScheme + brand accents)
 *  - Type   -> ui/theme/Type.kt  (Typography + KonstrivoNumericStyles tabular figures)
 *
 * Visual hierarchy rule: the NUMBER is always more prominent than its label.
 */

/** Shared spacing rhythm for all Konstrivo screens. */
object KonstrivoSpacing {
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 24.dp

    /** Large section separation / screen-level breathing room. */
    val Xxl = 32.dp

    /** Standard inner padding of cards (matches existing screens). */
    val CardInner = 14.dp
}

/** Shared corner radius tokens. */
object KonstrivoRadius {
    val Card = 16.dp
    val Badge = 8.dp   // kept (status chips / small bubbles)

    /** R1 Design System: buttons & inputs use a dedicated 12dp corner radius (independent of Badge). */
    val Button = 12.dp

    /** Corner radius of tinted icon containers inside cards (PHASE 2 unification). */
    val IconBox = 12.dp
}

/** Pill shape used by badges / status chips. */
val KonstrivoPillShape = RoundedCornerShape(percent = 50)

/**
 * Base container surface for all Konstrivo cards.
 *
 * @param onClick optional; when set, the whole card is clickable.
 * @param showBorder draws the subtle outline border used for "coming soon"/disabled cards.
 */
@Composable
fun KonstrivoCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    showBorder: Boolean = false,
    contentPadding: Dp = KonstrivoSpacing.Lg,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (showBorder) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        } else {
            null
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm),
            content = content
        )
    }
}
/**
 * Uppercase amber section caption (e.g. "MATÉRIAUX", "APPLICATION").
 */
@Composable
fun KonstrivoSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        color = KonstrivoGold,
        modifier = modifier
    )
}

/** Semantic sizes for money/quantity values, mapped onto [KonstrivoNumericStyles]. */
enum class KonstrivoPriceStyle { Display, Large, Medium, Small }

/**
 * Numeric text (prices, quantities, totals) rendered with tabular figures ("tnum")
 * so columns of numbers stay visually aligned in lists.
 */
@Composable
fun KonstrivoPriceText(
    text: String,
    modifier: Modifier = Modifier,
    style: KonstrivoPriceStyle = KonstrivoPriceStyle.Medium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = 1
) {
    val numericStyle = when (style) {
        KonstrivoPriceStyle.Display -> KonstrivoNumericStyles.PriceDisplay
        KonstrivoPriceStyle.Large -> KonstrivoNumericStyles.PriceLarge
        KonstrivoPriceStyle.Medium -> KonstrivoNumericStyles.PriceMedium
        KonstrivoPriceStyle.Small -> KonstrivoNumericStyles.NumericSmall
    }
    Text(
        text = text,
        style = numericStyle,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

/**
 * Compact KPI tile: optional icon, big numeric value, small caption.
 */
@Composable
fun KonstrivoMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    KonstrivoCard(modifier = modifier, contentPadding = KonstrivoSpacing.CardInner) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = KonstrivoGold,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = value,
            style = KonstrivoNumericStyles.MetricValue,
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
/**
 * Small pill chip for statuses / counts ("ACTIF", "3 devis", ...).
 */
@Composable
fun KonstrivoBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Surface(shape = KonstrivoPillShape, color = containerColor, modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Placeholder for empty lists / sections.
 */
@Composable
fun KonstrivoEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KonstrivoSpacing.Xl, vertical = KonstrivoSpacing.Xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Navigable list entry: icon + title (+ subtitle) + chevron.
 */
@Composable
fun KonstrivoMenuCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    icon: ImageVector? = null,
    showChevron: Boolean = true,
    testTag: String? = null
) {
    KonstrivoCard(
        modifier = if (testTag != null) modifier.testTag(testTag) else modifier,
        onClick = onClick,
        contentPadding = KonstrivoSpacing.CardInner
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = KonstrivoGold,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (showChevron) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * One line of a totals breakdown: label on the left, numeric value on the right.
 */
@Composable
fun KonstrivoSummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal,
            color = if (emphasized) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        KonstrivoPriceText(
            text = value,
            style = KonstrivoPriceStyle.Small,
            color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ══════════════════════════════════════════════════════════════════════
// PHASE 2.1 — EXTENDED DESIGN SYSTEM
// Corner shapes, branded shell primitives and reusable action/input/
// status components. Built strictly on top of existing theme tokens.
// ══════════════════════════════════════════════════════════════════════

/** Corner-based shape tokens (complement the dp tokens in [KonstrivoRadius]). */
object KonstrivoShapes {
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Card = RoundedCornerShape(16.dp)
    val Large = RoundedCornerShape(20.dp)
    val Dialog = RoundedCornerShape(24.dp)
}

/** Navy brand monogram used across top bars and hero headers. */
@Composable
fun KonstrivoBrandMark(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(KonstrivoRadius.IconBox))
            .background(Color.White.copy(alpha = 0.14f))
            .border(1.dp, KonstrivoGold.copy(alpha = 0.6f), RoundedCornerShape(KonstrivoRadius.IconBox)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "K",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = KonstrivoGold
        )
    }
}

/**
 * Branded navy top bar with status-bar inset handling.
 * `actions` keeps full caller control (language menu, account entry…).
 */
@Composable
fun KonstrivoTopBar(
    title: String,
    modifier: Modifier = Modifier,
    contextLine: String? = null,
    onBrandClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = KonstrivoNavy,
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = KonstrivoSpacing.Md, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md),
                    modifier = Modifier
                        .weight(1f)
                        .then(if (onBrandClick != null) Modifier.clickable(onClick = onBrandClick) else Modifier)
                ) {
                    KonstrivoBrandMark()
                    Column {
                        Text(
                            text = "KONSTRIVO PRO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = KonstrivoGold
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                actions()
            }
            HorizontalDivider(color = KonstrivoGold.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}

/** Section title row with optional leading icon and trailing slot (e.g. "Voir tout"). */
@Composable
fun KonstrivoSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Sm)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        trailing()
    }
}

/** Pill badge with a leading status dot — used for trade/estimate states. */
@Composable
fun KonstrivoStatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = KonstrivoPillShape,
        color = color.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/** Pre-styled numeric input with the shared medium radius. Presentation only. */
@Composable
fun KonstrivoInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = singleLine,
        shape = KonstrivoShapes.Medium,
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

/** Inline busy indicator with an explanatory message. */
@Composable
fun KonstrivoLoadingState(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(KonstrivoSpacing.CardInner),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
    ) {
        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
        Text(
            text = message,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Price block — the strongest numeric element of a panel.
 * `emphasized` renders the navy/amber premium variant for grand totals.
 */
@Composable
fun KonstrivoPriceCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    Surface(
        shape = KonstrivoShapes.Card,
        color = if (emphasized) KonstrivoNavy else MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Xs),
            modifier = Modifier.padding(KonstrivoSpacing.CardInner)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (emphasized) Color.White.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            KonstrivoPriceText(
                text = value,
                style = KonstrivoPriceStyle.Large,
                color = if (emphasized) KonstrivoGold else MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Simple navigation item model for [KonstrivoBottomBar] (decoupled from AppTab). */
data class KonstrivoNavItem(
    val key: String,
    val label: String,
    val icon: ImageVector
)

/** Branded bottom navigation bar with a hairline separator and amber indicator. */
@Composable
fun KonstrivoBottomBar(
    items: List<KonstrivoNavItem>,
    isSelected: (KonstrivoNavItem) -> Boolean,
    onSelect: (KonstrivoNavItem) -> Unit,
    showFabSpacer: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            // Collage layout uses a center FAB. We keep bottom bar items but
            // reserve a slot in the middle to visually match the reference.
            val middleIndex = if (showFabSpacer) items.size / 2 else -1
            items.forEachIndexed { idx, item ->
                if (showFabSpacer && idx == middleIndex) {
                    Spacer(modifier = Modifier.width(28.dp))
                }
                val sel = isSelected(item)
                NavigationBarItem(
                    selected = sel,
                    onClick = { onSelect(item) },
                    icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = KonstrivoGold.copy(alpha = 0.30f)
                    ),
                    modifier = Modifier.testTag("nav_tab_${item.key.lowercase()}")
                )
            }
        }
    }
}

/**
 * Collage-style FAB (gold circle with plus). Intended to sit centered above the
 * bottom navigation, matching `image.jpg`.
 */
@Composable
fun KonstrivoCenterFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = KonstrivoGold,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = modifier.testTag("center_fab"),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}

// =============================================================================
// PHASE 2.1 — Button System
// Professional button variants for consistent actions across all screens.
// =============================================================================

/**
 * Primary Button — main call-to-action (Calculer, Enregistrer, Continuer).
 * Uses brand Amber on Navy for maximum contrast and professional appearance.
 */
@Composable
fun KonstrivoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = KonstrivoGold,
            contentColor = Color.White,
            disabledContainerColor = KonstrivoGold.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(KonstrivoRadius.Button)  // R1: 12dp button corners
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            icon?.let {
                Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Secondary Button — actions with reduced emphasis.
 * Uses brand Navy for professional secondary actions.
 */
@Composable
fun KonstrivoSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = KonstrivoNavy,
            contentColor = Color.White,
            disabledContainerColor = KonstrivoNavy.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(KonstrivoRadius.Button)  // R1: 12dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Outlined Button — alternative actions with border only.
 */
@Composable
fun KonstrivoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = KonstrivoNavy,
            disabledContentColor = KonstrivoNavy.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.5.dp, if (enabled) KonstrivoNavy else KonstrivoNavy.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(KonstrivoRadius.Button)  // R1: 12dp
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Ghost/Text Button — low-emphasis actions (cancel, dismiss).
 */
@Composable
fun KonstrivoGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = KonstrivoNavy,
            disabledContentColor = KonstrivoNavy.copy(alpha = 0.4f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Destructive Button — dangerous actions (delete, reset).
 */
@Composable
fun KonstrivoDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = KonstrivoError,
            contentColor = Color.White,
            disabledContainerColor = KonstrivoError.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(KonstrivoRadius.Button)  // R1: 12dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// =============================================================================
// PHASE 2.1 — Input System
// Specialized input fields for professional data entry.
// =============================================================================

/**
 * Number Field — optimized for numeric input (dimensions, quantities).
 */
@Composable
fun KonstrivoNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffixText: String? = null,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    tag: String? = null
) {
    KonstrivoTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        suffixText = suffixText,
        leadingIcon = leadingIcon,
        keyboardType = KeyboardType.Decimal,
        isError = isError,
        supportingText = supportingText,
        enabled = enabled,
        tag = tag
    )
}

/**
 * Password Field — secure input with visibility toggle.
 */
@Composable
fun KonstrivoPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    tag: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = if (tag != null) modifier.testTag(tag) else modifier,
        label = { Text(label) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = isError,
        supportingText = if (supportingText != null) {
            { Text(supportingText, style = MaterialTheme.typography.labelSmall) }
        } else null,
        enabled = enabled,
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        }
    )
}

/**
 * Search Field — optimized for search with clear button.
 */
@Composable
fun KonstrivoSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    tag: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = if (tag != null) modifier.testTag(tag) else modifier,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            }
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(KonstrivoRadius.Button)  // R1: inputs 12dp
    )
}

// =============================================================================
// PHASE 2.1 — Error State
// Unified error display for consistent error handling.
// =============================================================================

/**
 * Error State — displays error with optional retry action.
 */
@Composable
fun KonstrivoErrorState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    retryText: String,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KonstrivoSpacing.Xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KonstrivoSpacing.Md)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = KonstrivoError,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            KonstrivoOutlinedButton(
                text = retryText,
                onClick = onRetry
            )
        }
    }
}

// =============================================================================
// PHASE 2.1 — Success Feedback
// Snackbar-based success feedback for action confirmation.
// =============================================================================

/**
 * Success Feedback — displays success message via Snackbar.
 * Call from composable with SnackbarHostState.
 */
@Composable
fun KonstrivoSuccessFeedback(
    message: String,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }
}
