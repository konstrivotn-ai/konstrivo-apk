package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoAmber
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy
import kotlinx.coroutines.delay

/**
 * Splash screen that matches the reference board:
 * - Dark navy background
 * - Simple construction blueprint lines
 * - Center brand "KONSTRIVO PRO" + subtitle
 *
 * Offline-first: purely timed; does not touch backend or repositories.
 */
@Composable
fun SplashScreen(
    language: AppLanguage,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(durationMillis = 520))
        // Reference splash holds briefly.
        delay(900)
        onFinished()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_screen"),
        color = KonstrivoNavy
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Match ref_top_01: blueprint lines, plus a prominent bottom-right
            // construction plan silhouette.
            BlueprintBackdrop()

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 28.dp)
                    .alpha(alpha.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ref splash has a thin blueprint/house icon, not an app icon.
                // We draw it with strokes to avoid bitmap dependencies.
                BlueprintHouseMark(modifier = Modifier.size(74.dp))

                Text(
                    text = "KONSTRIVO PRO",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = language.tr(
                        fr = "Construction & Bâtiment",
                        en = "Construction & Building",
                        ar = "البناء والتشييد",
                        dz = "البناء والتشييد"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom subtle copyright line
            Text(
                text = language.tr(
                    fr = "© KONSTRIVO • PRO",
                    en = "© KONSTRIVO • PRO",
                    ar = "© كونستريفو • برو",
                    dz = "© كونستريفو • برو"
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 22.dp)
                    .alpha(0.8f)
            )
        }
    }
}

@Composable
private fun BlueprintHouseMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.testTag("splash_house_mark")) {
        val w = size.width
        val stroke = Stroke(width = (w * 0.035f).coerceAtLeast(2.0f), cap = StrokeCap.Round)
        val c = KonstrivoGold.copy(alpha = 0.65f)

        // House outline
        drawLine(c, androidx.compose.ui.geometry.Offset(w*0.2f, w*0.55f), androidx.compose.ui.geometry.Offset(w*0.5f, w*0.25f), stroke.width, stroke.cap)
        drawLine(c, androidx.compose.ui.geometry.Offset(w*0.8f, w*0.55f), androidx.compose.ui.geometry.Offset(w*0.5f, w*0.25f), stroke.width, stroke.cap)
        drawLine(c, androidx.compose.ui.geometry.Offset(w*0.25f, w*0.55f), androidx.compose.ui.geometry.Offset(w*0.25f, w*0.85f), stroke.width, stroke.cap)
        drawLine(c, androidx.compose.ui.geometry.Offset(w*0.75f, w*0.55f), androidx.compose.ui.geometry.Offset(w*0.75f, w*0.85f), stroke.width, stroke.cap)
        drawLine(c, androidx.compose.ui.geometry.Offset(w*0.25f, w*0.85f), androidx.compose.ui.geometry.Offset(w*0.75f, w*0.85f), stroke.width, stroke.cap)

        // Simple door
        drawLine(c, androidx.compose.ui.geometry.Offset(w*0.48f, w*0.85f), androidx.compose.ui.geometry.Offset(w*0.48f, w*0.68f), stroke.width, stroke.cap)
        drawLine(c, androidx.compose.ui.geometry.Offset(w*0.52f, w*0.85f), androidx.compose.ui.geometry.Offset(w*0.52f, w*0.68f), stroke.width, stroke.cap)
        drawLine(c, androidx.compose.ui.geometry.Offset(w*0.48f, w*0.68f), androidx.compose.ui.geometry.Offset(w*0.52f, w*0.68f), stroke.width, stroke.cap)
    }
}

@Composable
private fun BlueprintBackdrop(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_blueprint")
    ) {
        val w = size.width
        val h = size.height
        val grid = 56.dp.toPx()
        val stroke = Stroke(width = 1.5f, cap = StrokeCap.Round)

        // soft diagonal lines
        var x = -h
        while (x < w) {
            drawLine(
                color = KonstrivoAmber.copy(alpha = 0.12f),
                start = androidx.compose.ui.geometry.Offset(x, 0f),
                end = androidx.compose.ui.geometry.Offset(x + h, h),
                strokeWidth = stroke.width
            )
            x += grid
        }

        // top-left corner technical mark (like the reference)
        val markW = w * 0.25f
        val markH = h * 0.18f
        drawLine(
            color = KonstrivoGold.copy(alpha = 0.18f),
            start = androidx.compose.ui.geometry.Offset(markW * 0.15f, markH * 0.10f),
            end = androidx.compose.ui.geometry.Offset(markW * 0.85f, markH * 0.10f),
            strokeWidth = stroke.width
        )
        drawLine(
            color = KonstrivoGold.copy(alpha = 0.18f),
            start = androidx.compose.ui.geometry.Offset(markW * 0.15f, markH * 0.10f),
            end = androidx.compose.ui.geometry.Offset(markW * 0.15f, markH * 0.70f),
            strokeWidth = stroke.width
        )
        drawCircle(
            color = KonstrivoGold.copy(alpha = 0.18f),
            radius = 10.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(markW * 0.15f, markH * 0.70f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
