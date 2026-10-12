package com.example.domain

import com.example.domain.model.MaterialPrice
import com.example.domain.model.PriceSource
import com.example.domain.model.StandardUnit
import org.junit.Assert.*
import org.junit.Test

/**
 * Test-only specification for package-aware MaterialPrice selection.
 * This file is pure test/domain logic and does NOT modify any production code,
 * DB entities, DAOs, or APIs. It simulates package-aware rows via test maps.
 */
class PricePackageSelectionSpecTest {

    private fun chooseBest(candidates: List<MaterialPrice>, now: Long): MaterialPrice? {
        return candidates
            .filter { it.effectiveFrom <= now && (it.effectiveTo == null || it.effectiveTo!! >= now) }
            .sortedWith(compareByDescending<MaterialPrice> { it.effectiveFrom }.thenBy { it.currentPrice })
            .firstOrNull()
    }

    /**
     * Selection helper used in tests. packageCandidates simulates package-aware DB rows
     * keyed by packageId (test-only). No production structures are changed.
     */
    private fun selectPrice(
        packageId: String?,
        materialId: String,
        countryCode: String,
        productCandidates: List<MaterialPrice>,
        packageCandidates: Map<String, List<MaterialPrice>>,
        now: Long
    ): MaterialPrice? {
        if (packageId != null) {
            val pk = packageCandidates[packageId]
            val pkMatches = pk?.filter { it.countryCode == countryCode } ?: emptyList()
            val bestPackage = chooseBest(pkMatches, now)
            if (bestPackage != null) return bestPackage
        }

        val prodMatches = productCandidates.filter { it.materialId == materialId && it.countryCode == countryCode }
        return chooseBest(prodMatches, now)
    }

    private val baseNow = 1735689600000L // 2026-01-01 deterministic reference

    @Test
    fun scenarioA_packagePriceExists_packageWins() {
        val productPrice = MaterialPrice(
            id = "TN_paint_base",
            materialId = "paint_base",
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 100.0,
            currentPrice = 100.0,
            unit = StandardUnit.LITER,
            source = PriceSource.MARKET_BENCHMARK_2026,
            supplierName = "S1",
            effectiveFrom = baseNow - 1000
        )

        val packagePrice = MaterialPrice(
            id = "TN_pkg_42",
            materialId = "paint_base", // still references product material
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 90.0,
            currentPrice = 90.0,
            unit = StandardUnit.LITER,
            source = PriceSource.SUPPLIER_QUOTE,
            supplierName = "PkgSupplier",
            effectiveFrom = baseNow - 500
        )

        val selected = selectPrice("pkg-42", "paint_base", "TN", listOf(productPrice), mapOf("pkg-42" to listOf(packagePrice)), baseNow)
        assertNotNull(selected)
        assertEquals("TN_pkg_42", selected!!.id)
    }

    @Test
    fun scenarioB_packageProvidedButMissing_fallbackToProduct() {
        val productPrice = MaterialPrice(
            id = "TN_paint_base",
            materialId = "paint_base",
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 100.0,
            currentPrice = 100.0,
            unit = StandardUnit.LITER,
            source = PriceSource.MARKET_BENCHMARK_2026,
            supplierName = "S1",
            effectiveFrom = baseNow - 1000
        )

        val selected = selectPrice("pkg-missing", "paint_base", "TN", listOf(productPrice), emptyMap(), baseNow)
        assertNotNull(selected)
        assertEquals("TN_paint_base", selected!!.id)
    }

    @Test
    fun scenarioC_packageNull_legacyBehaviorUnchanged() {
        val productPrice = MaterialPrice(
            id = "TN_paint_base",
            materialId = "paint_base",
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 120.0,
            currentPrice = 120.0,
            unit = StandardUnit.LITER,
            source = PriceSource.MARKET_BENCHMARK_2026,
            supplierName = "S1",
            effectiveFrom = baseNow - 1000
        )

        val selected = selectPrice(null, "paint_base", "TN", listOf(productPrice), emptyMap(), baseNow)
        assertNotNull(selected)
        assertEquals("TN_paint_base", selected!!.id)
    }

    @Test
    fun scenarioD_packageForOtherCountry_mustNotMatch() {
        val productPrice = MaterialPrice(
            id = "TN_paint_base",
            materialId = "paint_base",
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 100.0,
            currentPrice = 100.0,
            unit = StandardUnit.LITER,
            source = PriceSource.MARKET_BENCHMARK_2026,
            supplierName = "S1",
            effectiveFrom = baseNow - 1000
        )

        val packagePriceOtherCountry = MaterialPrice(
            id = "FR_pkg_42",
            materialId = "paint_base",
            countryCode = "FR",
            region = "",
            currency = "EUR",
            defaultPrice = 30.0,
            currentPrice = 30.0,
            unit = StandardUnit.LITER,
            source = PriceSource.SUPPLIER_QUOTE,
            supplierName = "PkgSupplierFR",
            effectiveFrom = baseNow - 500
        )

        val selected = selectPrice("pkg-42", "paint_base", "TN", listOf(productPrice), mapOf("pkg-42" to listOf(packagePriceOtherCountry)), baseNow)
        assertNotNull(selected)
        // must fall back to TN product price because package row is FR only
        assertEquals("TN_paint_base", selected!!.id)
    }

    @Test
    fun scenarioE_packageExistsButInactive_fallbackAccordingToRules() {
        val productPrice = MaterialPrice(
            id = "TN_paint_base",
            materialId = "paint_base",
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 100.0,
            currentPrice = 100.0,
            unit = StandardUnit.LITER,
            source = PriceSource.MARKET_BENCHMARK_2026,
            supplierName = "S1",
            effectiveFrom = baseNow - 1000
        )

        // package price effective in future -> inactive for baseNow
        val packageFuture = MaterialPrice(
            id = "TN_pkg_future",
            materialId = "paint_base",
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 80.0,
            currentPrice = 80.0,
            unit = StandardUnit.LITER,
            source = PriceSource.SUPPLIER_QUOTE,
            supplierName = "PkgSupplier",
            effectiveFrom = baseNow + 1000000
        )

        val selected = selectPrice("pkg-future", "paint_base", "TN", listOf(productPrice), mapOf("pkg-future" to listOf(packageFuture)), baseNow)
        assertNotNull(selected)
        assertEquals("TN_paint_base", selected!!.id)
    }

    @Test
    fun scenarioF_twoValidPackagePrices_deterministicSelection() {
        // two package prices: one older but cheaper, one newer but more expensive
        val packageOldCheap = MaterialPrice(
            id = "TN_pkgA_old",
            materialId = "paint_base",
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 70.0,
            currentPrice = 70.0,
            unit = StandardUnit.LITER,
            source = PriceSource.SUPPLIER_QUOTE,
            supplierName = "S-A",
            effectiveFrom = baseNow - 2000
        )

        val packageNewExpensive = MaterialPrice(
            id = "TN_pkgA_new",
            materialId = "paint_base",
            countryCode = "TN",
            region = "",
            currency = "TND",
            defaultPrice = 85.0,
            currentPrice = 85.0,
            unit = StandardUnit.LITER,
            source = PriceSource.SUPPLIER_QUOTE,
            supplierName = "S-B",
            effectiveFrom = baseNow - 500
        )

        val selected = selectPrice("pkg-A", "paint_base", "TN", emptyList(), mapOf("pkg-A" to listOf(packageOldCheap, packageNewExpensive)), baseNow)
        assertNotNull(selected)
        // deterministic: prefer latest effectiveFrom (packageNewExpensive)
        assertEquals("TN_pkgA_new", selected!!.id)
    }
}
