package com.example.domain

import com.example.data.local.MaterialPriceEntity
import org.junit.Assert.*
import org.junit.Test

class PriceSelectionUnitTest {

    @Test
    fun scenarioA_packagePriceExists_packageWins() {
        val prod = MaterialPriceEntity(id = "TN_paint_base", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "P", nameAr = "A", nameDerja = "D", unit = "L", defaultPrice = 100.0, currentPrice = 100.0, currency = "TND")
        val pkg = MaterialPriceEntity(id = "TN_pkg_42", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "PK", nameAr = "PA", nameDerja = "PD", unit = "L", defaultPrice = 90.0, currentPrice = 90.0, currency = "TND", packageId = "pkg-42")
        val map = PriceSelection.selectPriceMap(listOf(prod, pkg), "pkg-42")
        assertTrue(map.containsKey("paint_base"))
        assertEquals("pkg-42", map["paint_base"]!!.packageId)
    }

    @Test
    fun scenarioB_packageProvidedButMissing_fallbackToProduct() {
        val prod = MaterialPriceEntity(id = "TN_paint_base", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "P", nameAr = "A", nameDerja = "D", unit = "L", defaultPrice = 100.0, currentPrice = 100.0, currency = "TND")
        val map = PriceSelection.selectPriceMap(listOf(prod), "pkg-missing")
        assertTrue(map.containsKey("paint_base"))
        assertNull(map["paint_base"]!!.packageId)
    }

    @Test
    fun scenarioC_packageNull_legacyBehaviorUnchanged() {
        val prod = MaterialPriceEntity(id = "TN_paint_base", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "P", nameAr = "A", nameDerja = "D", unit = "L", defaultPrice = 120.0, currentPrice = 120.0, currency = "TND")
        val map = PriceSelection.selectPriceMap(listOf(prod), null)
        assertTrue(map.containsKey("paint_base"))
        assertNull(map["paint_base"]!!.packageId)
    }

    @Test
    fun scenarioD_packageForOtherCountry_mustNotMatch() {
        val prod = MaterialPriceEntity(id = "TN_paint_base", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "P", nameAr = "A", nameDerja = "D", unit = "L", defaultPrice = 100.0, currentPrice = 100.0, currency = "TND")
        val pkgOther = MaterialPriceEntity(id = "FR_pkg_42", baseKey = "paint_base", countryCode = "FR", category = "peinture", nameFr = "PK", nameAr = "PA", nameDerja = "PD", unit = "L", defaultPrice = 30.0, currentPrice = 30.0, currency = "EUR", packageId = "pkg-42")
        // selectPriceMap works on provided list; to simulate country filtering we pass only TN rows
        val map = PriceSelection.selectPriceMap(listOf(prod, pkgOther), "pkg-42")
        // package row is FR — it won't be selected as it has different countryCode from the product
        assertTrue(map.containsKey("paint_base"))
        // fallback to product row because package entry does not match country semantics
        assertNull(map["paint_base"]!!.packageId)
    }

    @Test
    fun scenarioE_packageExistsButInvalid_fallbackAccordingToRules() {
        val prod = MaterialPriceEntity(id = "TN_paint_base", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "P", nameAr = "A", nameDerja = "D", unit = "L", defaultPrice = 100.0, currentPrice = 100.0, currency = "TND")
        val pkgInvalid = MaterialPriceEntity(id = "TN_pkg_bad", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "PK", nameAr = "PA", nameDerja = "PD", unit = "L", defaultPrice = 0.0, currentPrice = 0.0, currency = "TND", packageId = "pkg-bad")
        val map = PriceSelection.selectPriceMap(listOf(prod, pkgInvalid), "pkg-bad")
        // invalid package (currentPrice <= 0) should be ignored and fallback to product
        assertTrue(map.containsKey("paint_base"))
        assertNull(map["paint_base"]!!.packageId)
    }

    @Test
    fun scenarioF_deterministicSelection_ifMultiplePackageRows() {
        val pkgA = MaterialPriceEntity(id = "TN_pkgA_1", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "A1", nameAr = "A1", nameDerja = "A1", unit = "L", defaultPrice = 70.0, currentPrice = 70.0, currency = "TND", packageId = "pkg-A", isCustom = false)
        val pkgB = MaterialPriceEntity(id = "TN_pkgA_2", baseKey = "paint_base", countryCode = "TN", category = "peinture", nameFr = "A2", nameAr = "A2", nameDerja = "A2", unit = "L", defaultPrice = 85.0, currentPrice = 85.0, currency = "TND", packageId = "pkg-A", isCustom = true)
        val map = PriceSelection.selectPriceMap(listOf(pkgA, pkgB), "pkg-A")
        // deterministic: isCustom=true preferred
        assertTrue(map.containsKey("paint_base"))
        assertEquals("TN_pkgA_2", map["paint_base"]!!.id)
    }
}
