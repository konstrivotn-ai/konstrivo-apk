package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.remote.CloudCatalogClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale

/**
 * Live site catalogue inside the native calculator.
 * New active website trades, services, metric definitions, materials and current
 * prices are loaded from /api/v1; the app does not need a release to discover them.
 */
@Composable
fun SiteDynamicCalculatorCard(language: AppLanguage, modifier: Modifier = Modifier) {
    var refreshKey by remember { mutableStateOf(0) }
    var trades by remember { mutableStateOf(emptyList<CloudCatalogClient.TradeItem>()) }
    var selectedTradeId by remember { mutableStateOf("") }
    var bundle by remember { mutableStateOf<CloudCatalogClient.TradeBundle?>(null) }
    var selectedServiceId by remember { mutableStateOf("") }
    var selectedMetreId by remember { mutableStateOf("") }
    var selectedMaterialId by remember { mutableStateOf("") }
    var serviceQuantity by remember { mutableStateOf("1") }
    val dimensionValues = remember { mutableStateMapOf<String, String>() }
    var isLoadingTrades by remember { mutableStateOf(true) }
    var isLoadingBundle by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isArabic = language == AppLanguage.ARABIC || language == AppLanguage.DERJA
    fun text(fr: String, ar: String): String = if (isArabic) ar else fr
    val selectedService = bundle?.services?.firstOrNull { it.id == selectedServiceId }
    val selectedMetre = bundle?.metreItems?.firstOrNull { it.id == selectedMetreId }
    val selectedMaterial = bundle?.materials?.firstOrNull { it.id == selectedMaterialId }

    LaunchedEffect(refreshKey) {
        isLoadingTrades = true
        errorMessage = null
        try {
            val loaded = withContext(Dispatchers.IO) { CloudCatalogClient.loadTrades() }
            trades = loaded
            if (loaded.none { it.id == selectedTradeId }) selectedTradeId = loaded.firstOrNull()?.id.orEmpty()
            if (loaded.isEmpty()) errorMessage = text("Aucun métier disponible dans le catalogue du site.", "ما فما حتى صنعة متاحة في كاتالوغ الموقع.")
        } catch (e: Exception) {
            errorMessage = text("Catalogue du site indisponible. Vérifiez la connexion puis actualisez.", "كاتالوغ الموقع موش متاح. تثبّت من الإنترنت وعاود حدّث.")
        } finally {
            isLoadingTrades = false
        }
    }

    LaunchedEffect(selectedTradeId, refreshKey) {
        val trade = trades.firstOrNull { it.id == selectedTradeId } ?: return@LaunchedEffect
        isLoadingBundle = true
        errorMessage = null
        bundle = null
        selectedServiceId = ""
        selectedMetreId = ""
        selectedMaterialId = ""
        dimensionValues.clear()
        try {
            val loaded = withContext(Dispatchers.IO) { CloudCatalogClient.loadTradeBundle(trade) }
            bundle = loaded
            selectedServiceId = loaded.services.firstOrNull()?.id.orEmpty()
            selectedMetreId = loaded.metreItems.firstOrNull()?.id.orEmpty()
            selectedMaterialId = loaded.materials.firstOrNull()?.id.orEmpty()
        } catch (e: Exception) {
            errorMessage = text("Impossible de charger les prestations et matériaux de ce métier.", "ما نجّمش نحمّل الخدمات والمواد متاع الصنعة هاذي.")
        } finally {
            isLoadingBundle = false
        }
    }

    LaunchedEffect(selectedMetreId, bundle) {
        dimensionValues.clear()
        val element = bundle?.metreItems?.firstOrNull { it.id == selectedMetreId } ?: return@LaunchedEffect
        element.dimensions.forEach { dim -> dimensionValues[dim.key] = dim.defaultValue?.toString().orEmpty() }
    }

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        androidx.compose.material3.Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text("Catalogue du site en direct", "كاتالوغ الموقع مباشر"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text("Les ajouts du site apparaissent ici automatiquement.", "أي صنعة ولا مادة جديدة في الموقع تظهر لهنا آليًا."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { refreshKey++ }, enabled = !isLoadingTrades && !isLoadingBundle) {
                    androidx.compose.material3.Icon(Icons.Default.Refresh, contentDescription = null)
                    Text(text("Actualiser", "تحديث"))
                }
            }

            if (isLoadingTrades) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CircularProgressIndicator()
                    Text(text("Chargement du catalogue partagé…", "قاعدين نحمّلو في الكاتالوغ المشترك…"))
                }
            } else if (trades.isNotEmpty()) {
                Text(text("MÉTIERS / Trades", "المهن"), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    trades.forEach { trade ->
                        FilterChip(
                            selected = selectedTradeId == trade.id,
                            onClick = { selectedTradeId = trade.id },
                            label = { Text(if (isArabic && trade.labelAr.isNotBlank()) trade.labelAr else trade.labelFr) }
                        )
                    }
                }
            }

            if (isLoadingBundle) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CircularProgressIndicator()
                    Text(text("Chargement des options, matériaux et tarifs…", "قاعدين نحمّلو الخيارات والمواد والأسعار…"))
                }
            }
            errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

            bundle?.let { data ->
                if (data.services.isNotEmpty()) {
                    HorizontalDivider()
                    Text(text("Prestations du site", "الخدمات الموجودة في الموقع"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.services.forEach { service ->
                            FilterChip(
                                selected = selectedServiceId == service.id,
                                onClick = { selectedServiceId = service.id },
                                label = { Text(if (isArabic && service.nameAr.isNotBlank()) service.nameAr else service.nameFr) }
                            )
                        }
                    }
                    selectedService?.let { service ->
                        Text("Unité : ${service.unit}  •  Tarif indicatif : ${service.suggestedRate?.let { String.format(Locale.FRANCE, "%.3f", it) + " DT" } ?: text("non défini", "موش محدّد")}", style = MaterialTheme.typography.bodySmall)
                        OutlinedTextField(
                            value = serviceQuantity,
                            onValueChange = { serviceQuantity = it },
                            label = { Text(text("Quantité (${service.unit})", "الكمية (${service.unit})")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        val q = serviceQuantity.replace(',', '.').toDoubleOrNull()
                        val rate = service.suggestedRate
                        if (q != null && q >= 0 && rate != null && rate >= 0) {
                            Text(
                                text = "Total indicatif de la prestation : ${String.format(Locale.FRANCE, "%.3f", q * rate)} DT",
                                style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (data.metreItems.isNotEmpty()) {
                    HorizontalDivider()
                    Text(text("Métré dynamique", "المترية الديناميكية"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.metreItems.forEach { item ->
                            FilterChip(
                                selected = selectedMetreId == item.id,
                                onClick = { selectedMetreId = item.id },
                                label = { Text(if (isArabic && item.labelAr.isNotBlank()) item.labelAr else item.labelFr) }
                            )
                        }
                    }
                    selectedMetre?.let { element ->
                        Text("Unité de résultat : ${element.unit} • Méthode : ${element.method}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        element.dimensions.forEach { dim ->
                            OutlinedTextField(
                                value = dimensionValues[dim.key].orEmpty(),
                                onValueChange = { dimensionValues[dim.key] = it },
                                label = { Text("${if (isArabic && dim.labelAr.isNotBlank()) dim.labelAr else dim.labelFr} (${dim.unit})") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        val calculated = evaluateSiteElement(element, dimensionValues)
                        when {
                            element.hasAdvancedRules -> Text(
                                text("Cette formule contient des règles supplémentaires (ouvertures/déductions/rendement) : résultat masqué pour éviter un métré incorrect.", "الفورمول فيها قواعد إضافية (فتحات/خصم/مردودية)، ما نظهروش نتيجة باش ما نعطيوش مترية غالطة."),
                                color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall
                            )
                            calculated != null -> Text(
                                text = "Quantité calculée : ${String.format(Locale.FRANCE, "%.3f", calculated)} ${element.unit}",
                                style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            else -> Text(text("Complétez les dimensions requises pour calculer.", "كمّل القياسات اللازمة باش تتحسب الكمية."), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                if (data.materials.isNotEmpty()) {
                    HorizontalDivider()
                    Text(text("Matériaux du site et prix disponibles", "مواد الموقع والأسعار المتوفرة"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.materials.forEach { material ->
                            AssistChip(
                                onClick = { selectedMaterialId = material.id },
                                label = { Text("${if (isArabic && material.nameAr.isNotBlank()) material.nameAr else material.nameFr} · ${material.price?.let { String.format(Locale.FRANCE, "%.3f", it) + " " + material.currency } ?: text("prix non publié", "السعر موش منشور")}") }
                            )
                        }
                    }
                    selectedMaterial?.let { material ->
                        Text("${material.code} • ${material.category} • ${material.unit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (material.specs.isNotBlank()) Text(material.specs, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (data.services.isEmpty() && data.metreItems.isEmpty() && data.materials.isEmpty()) {
                    Text(text("Ce métier existe sur le site, mais ses prestations, éléments de métré et matériaux ne sont pas encore configurés.", "الصنعة موجودة في الموقع أما خدماتها وموادها ومترّيتها ما زال ما تجهزوش."), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun evaluateSiteElement(
    element: CloudCatalogClient.MetreItem,
    values: Map<String, String>
): Double? {
    if (element.hasAdvancedRules) return null
    fun dim(key: String): Double? = values[key]?.replace(',', '.')?.toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0 }
    val calc = element.calc
    val gross = when (calc.optString("op")) {
        "single" -> dim(calc.optString("dim")) ?: return null
        "product" -> {
            val keys = calc.optJSONArray("dims") ?: return null
            if (keys.length() == 0) return null
            var result = 1.0
            for (i in 0 until keys.length()) result *= dim(keys.optString(i)) ?: return null
            result
        }
        "perimeter_h" -> {
            val a = dim(calc.optString("planeA")) ?: return null
            val b = dim(calc.optString("planeB")) ?: return null
            val h = dim(calc.optString("height")) ?: return null
            (a + b) * 2.0 * h
        }
        else -> return null
    }
    val layers = element.layers ?: 1.0
    val waste = element.wastePercent ?: 0.0
    if (layers < 1 || !layers.isFinite() || waste < 0 || !waste.isFinite()) return null
    val result = gross * layers * (1.0 + waste / 100.0)
    return result.takeIf { it.isFinite() && it >= 0 }
}
