package com.example.calculator

import com.example.domain.paint.PaintProduct
import com.example.domain.paint.PaintProductCatalog
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PaintProductTest {

    @Before
    fun setup() {
        PaintProductCatalog.clear()
        PaintProductCatalog.addTestData()
    }

    @Test
    fun testProductIdUniqueness() {
        val p = PaintProduct(
            productId = "UNIQUE_ID",
            materialTypeId = "peinture_finition_satinee",
            productName = "Unique Test",
            containerVolumeL = 15.0,
            countryCode = "TN"
        )
        PaintProductCatalog.addProduct(p)

        try {
            PaintProductCatalog.addProduct(p)
            fail("Expected exception for duplicate productId")
        } catch (ex: IllegalArgumentException) {
            // expected
        }
    }

    @Test
    fun testValidProductCanBeCreated() {
        val p = PaintProduct(
            productId = "P_VALID",
            materialTypeId = "peinture_finition_satinee",
            productName = "Valid Paint",
            containerVolumeL = 15.0,
            coverageM2PerLiter = 4.0,
            recommendedCoats = 2,
            countryCode = "TN"
        )
        PaintProductCatalog.addProduct(p)
        val fetched = PaintProductCatalog.getById("P_VALID")
        assertNotNull(fetched)
        assertEquals("P_VALID", fetched!!.productId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidContainerConfigurationRejected() {
        PaintProduct(
            productId = "BAD",
            materialTypeId = "peinture_finition_satinee",
            productName = "Bad Product",
            containerVolumeL = 15.0,
            containerWeightKg = 5.0,
            countryCode = "TN"
        )
    }

    @Test
    fun testLookupByProductId() {
        val p = PaintProductCatalog.getById("TEST_PAINT_15L_A")
        assertNotNull(p)
        assertEquals("TEST_PAINT_15L_A", p!!.productId)
    }

    @Test
    fun testLookupByMaterialType() {
        val list = PaintProductCatalog.getByMaterialType("peinture_finition_satinee")
        assertTrue(list.isNotEmpty())
        assertTrue(list.any { it.productId == "TEST_PAINT_15L_A" })
    }

    @Test
    fun testCountryFilteringAndActiveExclusion() {
        val activeTN = PaintProductCatalog.getActiveByCountry("TN")
        // TEST_PAINT_15L_A is active TN, TEST_PRIMER_20L_A is inactive TN
        assertTrue(activeTN.any { it.productId == "TEST_PAINT_15L_A" })
        assertFalse(activeTN.any { it.productId == "TEST_PRIMER_20L_A" })
    }

    @Test
    fun testUnknownCoverageAndCoatsRemainNull() {
        val p = PaintProduct(
            productId = "NULLS",
            materialTypeId = "peinture_finition_satinee",
            productName = "Nulls",
            containerVolumeL = 15.0,
            countryCode = "TN"
        )
        assertNull(p.coverageM2PerLiter)
        assertNull(p.recommendedCoats)
    }
}
