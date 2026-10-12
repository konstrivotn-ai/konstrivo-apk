package com.example.calculator

import com.example.domain.paint.PaintProductCatalog
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VerifiedAstralProductsTest {

    @Before
    fun setup() {
        PaintProductCatalog.clear()
        PaintProductCatalog.addReferenceData()
    }

    @Test
    fun testProductsExistAndUnique() {
        val ids = listOf("ASTRAL_AQUASTRAL_SATIN", "ASTRAL_VINYLASTRAL", "ASTRAL_LAKALO")
        val fetched = ids.map { PaintProductCatalog.getById(it) }
        assertTrue(fetched.all { it != null })
        // uniqueness
        val allIds = PaintProductCatalog.getAll().map { it.productId }
        assertEquals(allIds.size, allIds.toSet().size)
    }

    @Test
    fun testCountryFilteringTN() {
        val tn = PaintProductCatalog.getActiveByCountry("TN")
        assertTrue(tn.any { it.productId == "ASTRAL_AQUASTRAL_SATIN" })
        assertTrue(tn.any { it.productId == "ASTRAL_VINYLASTRAL" })
        assertTrue(tn.any { it.productId == "ASTRAL_LAKALO" })
    }

    @Test
    fun testCoverageMetadataPreserved() {
        val aCoverage = PaintProductCatalog.getCoverageValue("ASTRAL_AQUASTRAL_SATIN")
        val aUnit = PaintProductCatalog.getCoverageUnit("ASTRAL_AQUASTRAL_SATIN")
        assertEquals(10.0, aCoverage!!, 0.0001)
        assertEquals("M2_PER_KG", aUnit)

        val vCoverage = PaintProductCatalog.getCoverageValue("ASTRAL_VINYLASTRAL")
        val vUnit = PaintProductCatalog.getCoverageUnit("ASTRAL_VINYLASTRAL")
        assertEquals(4.0, vCoverage!!, 0.0001)
        assertEquals("M2_PER_KG", vUnit)

        val lCoverage = PaintProductCatalog.getCoverageValue("ASTRAL_LAKALO")
        val lUnit = PaintProductCatalog.getCoverageUnit("ASTRAL_LAKALO")
        assertEquals(13.0, lCoverage!!, 0.0001)
        assertEquals("M2_PER_L", lUnit)
    }
}
