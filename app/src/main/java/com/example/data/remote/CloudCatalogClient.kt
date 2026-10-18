package com.example.data.remote

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Read-only bridge to the website's live catalog; dynamic trades are data, not enum values. */
object CloudCatalogClient {
    private const val API_BASE = "https://konstrivo.konstrivo.blitz.cloud/api/v1/"
    private const val CONNECT_TIMEOUT_MS = 7000
    private const val READ_TIMEOUT_MS = 9000
    private const val PRICE_CACHE_TTL_MS = 120_000L
    @Volatile private var cachedPrices: Map<String, Pair<Double, String>> = emptyMap()
    @Volatile private var cachedPricesAtMs: Long = 0L

    data class TradeItem(val id: String, val code: String, val labelFr: String, val labelAr: String, val labelDerja: String, val icon: String)
    data class ServiceItem(val id: String, val nameFr: String, val nameAr: String, val unit: String, val suggestedRate: Double?)
    data class DimensionItem(val key: String, val labelFr: String, val labelAr: String, val unit: String, val defaultValue: Double?)
    data class MetreItem(
        val id: String, val code: String, val labelFr: String, val labelAr: String, val unit: String,
        val method: String, val dimensions: List<DimensionItem>, val calc: JSONObject,
        val layers: Double?, val wastePercent: Double?, val hasAdvancedRules: Boolean
    )
    data class MaterialItem(
        val id: String, val code: String, val nameFr: String, val nameAr: String,
        val category: String, val unit: String, val specs: String, val price: Double?, val currency: String
    )
    data class TradeBundle(val services: List<ServiceItem>, val metreItems: List<MetreItem>, val materials: List<MaterialItem>)

    fun loadTrades(): List<TradeItem> {
        val root = request("trades?officialOnly=false&limit=200")
        return rows(root).mapNotNull { row ->
            val id = row.optString("id").trim()
            val code = row.optString("code").ifBlank { id }
            val label = row.optString("labelFr").ifBlank { row.optString("nameFr") }
            if (id.isBlank() || label.isBlank()) null else TradeItem(
                id, code, label, row.optString("labelAr"), row.optString("labelDerja"), row.optString("icon")
            )
        }.sortedBy { it.labelFr.lowercase() }
    }

    fun loadTradeBundle(trade: TradeItem): TradeBundle {
        val servicesRoot = request("trades/${segment(trade.id)}/services")
        val metreRoot = request("trades/${segment(trade.id)}/metre-elements")
        val materialRoot = request("materials?trade=${query(trade.code)}&page=1&limit=200")
        val prices = loadPriceMap()

        val services = rows(servicesRoot).mapNotNull { row ->
            val id = row.optString("id")
            val name = row.optString("nameFr")
            if (id.isBlank() || name.isBlank()) null else ServiceItem(
                id, name, row.optString("nameAr"), row.optString("defaultUnit").ifBlank { "m²" },
                row.numberOrNull("suggestedRateTnd")
            )
        }
        val metreItems = rows(metreRoot).mapNotNull { row ->
            val id = row.optString("id")
            val label = row.optString("labelFr")
            if (id.isBlank() || label.isBlank()) null else {
                val dimsJson = row.optJSONArray("dims") ?: JSONArray()
                val dims = (0 until dimsJson.length()).mapNotNull dimLoop@ { index ->
                    val d = dimsJson.optJSONObject(index) ?: return@dimLoop null
                    val key = d.optString("key")
                    if (key.isBlank()) null else DimensionItem(
                        key, d.optString("labelFr").ifBlank { key }, d.optString("labelAr"),
                        d.optString("unit"), d.numberOrNull("default")
                    )
                }
                val advanced = listOf("openings", "deductions", "yieldPerUnit", "coveragePerProductUnit")
                    .any { row.has(it) && !row.isNull(it) }
                MetreItem(
                    id, row.optString("elementCode"), label, row.optString("labelAr"),
                    row.optString("qtyUnit").ifBlank { "unit" }, row.optString("method"), dims,
                    row.optJSONObject("calc") ?: JSONObject(), row.numberOrNull("layers"),
                    row.numberOrNull("wastePercent"), advanced
                )
            }
        }
        val materials = rows(materialRoot).mapNotNull { row ->
            val id = row.optString("id")
            val name = row.optString("nameFr")
            if (id.isBlank() || name.isBlank()) null else {
                val price = prices[id]
                MaterialItem(
                    id, row.optString("code"), name, row.optString("nameAr"), row.optString("category"),
                    row.optString("baseUnit").ifBlank { "unit" }, row.optString("technicalSpecs"),
                    price?.first, price?.second ?: "TND"
                )
            }
        }
        return TradeBundle(services, metreItems, materials)
    }

    @Synchronized
    private fun loadPriceMap(): Map<String, Pair<Double, String>> {
        val now = System.currentTimeMillis()
        if (cachedPrices.isNotEmpty() && now - cachedPricesAtMs < PRICE_CACHE_TTL_MS) return cachedPrices
        val result = linkedMapOf<String, Pair<Double, String>>()
        for (page in 1..10) {
            val root = try {
                request("prices?currency=TND&market=tn&page=$page&limit=200")
            } catch (_: Exception) {
                break // Missing price data must not hide the trade/material catalog.
            }
            val pageRows = rows(root)
            pageRows.forEach { row ->
                val id = row.optString("materialId")
                val price = row.numberOrNull("price")
                if (id.isNotBlank() && price != null) result[id] = price to row.optString("currency").ifBlank { "TND" }
            }
            val container = root.optJSONObject("data")
            val total = container?.optInt("total", -1) ?: -1
            val limit = container?.optInt("limit", 200) ?: 200
            if (pageRows.isEmpty() || (total >= 0 && page * limit >= total) || pageRows.size < 200) break
        }
        if (result.isNotEmpty()) {
            cachedPrices = result.toMap()
            cachedPricesAtMs = now
        }
        return result
    }

    private fun request(path: String): JSONObject {
        val connection = (URL(API_BASE + path).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Cache-Control", "no-cache")
        }
        try {
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (status !in 200..299) throw IllegalStateException("KONSTRIVO API returned HTTP $status")
            return JSONObject(body)
        } finally { connection.disconnect() }
    }

    private fun rows(root: JSONObject): List<JSONObject> {
        val data = root.opt("data")
        val array = when (data) {
            is JSONArray -> data
            is JSONObject -> data.optJSONArray("data") ?: data.optJSONArray("items")
            else -> root.optJSONArray("items")
        } ?: return emptyList()
        return (0 until array.length()).mapNotNull { array.optJSONObject(it) }
    }

    private fun JSONObject.numberOrNull(key: String): Double? {
        if (!has(key) || isNull(key)) return null
        return optDouble(key, Double.NaN).takeIf { it.isFinite() }
    }
    private fun segment(value: String): String = URLEncoder.encode(value, "UTF-8")
    private fun query(value: String): String = URLEncoder.encode(value, "UTF-8")
}
