package com.example.calculator

import com.example.domain.paint.PaintPackage
import com.example.domain.paint.PaintPackageCatalog
import com.example.domain.paint.PaintProductCatalog
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.math.ceil

class PaintEndToEndDomainTest {

    @Before
    fun setup() {
        // Ensure product and package catalogs are in a known state
        PaintProductCatalog.clear()
        PaintProductCatalog.addReferenceData()

        PaintPackageCatalog.clear()
        // add two in-memory packages for ASTRAL_LAKALO
        PaintPackageCatalog.addPackage(
            PaintPackage(
                packageId = "ASTRAL_LAKALO_4L",
                productId = "ASTRAL_LAKALO",
                containerVolumeL = 4.0,
                countryCode = "TN",
                active = true
            )
        )
        PaintPackageCatalog.addPackage(
            PaintPackage(
                packageId = "ASTRAL_LAKALO_075L",
                productId = "ASTRAL_LAKALO",
                containerVolumeL = 0.75,
                countryCode = "TN",
                active = true
            )
        )
    }

    @Test
    fun testEndToEndPaintDomainFlow_M2_PER_L() {
        // Inputs
        val area = 50.0
        val openings = 5.0
        val additional = 0.0
        val netArea = kotlin.math.max(0.0, area - openings + additional)
        assertEquals(45.0, netArea, 0.0001)

        val surfaceMultiplier = 1.0
        val wastePercent = 10.0

        // Product resolution
        val product = PaintProductCatalog.getById("ASTRAL_LAKALO")
        assertNotNull(product)

        // Coverage validation
        val coverageValue = PaintProductCatalog.getCoverageValue("ASTRAL_LAKALO")
        val coverageUnit = PaintProductCatalog.getCoverageUnit("ASTRAL_LAKALO")
        assertNotNull(coverageValue)
        assertEquals("M2_PER_L", coverageUnit)
        assertEquals(13.0, coverageValue!!, 0.0001)

        // Use recommended coats
        val result = PaintCalculator.calculateForProduct(
            product = product!!,
            area = area,
            openingsArea = openings,
            additionalArea = additional,
            surfaceMultiplier = surfaceMultiplier,
            wastePercent = wastePercent,
            useRecommendedCoats = true
        )

        // Expected liters: (45 / 13) * 2 * 1.0 * 1.10
        val expectedLiters = (45.0 / 13.0) * 2.0 * 1.0 * 1.10

        when (result) {
            is PaintCalculator.ProductCalculationResult.Success -> {
                assertEquals("L", result.unit)
                assertEquals(2, result.coats)
                assertEquals(expectedLiters, result.requiredAmount, 0.0001)
            }
            else -> fail("Expected Success liters result for M2_PER_L product")
        }

        // Package catalog finds both packages
        val pkgs = PaintPackageCatalog.getByProductId("ASTRAL_LAKALO")
        assertEquals(2, pkgs.size)

        // Find combinations to cover required liters
        val required = expectedLiters
        var foundCombo = false
        var chosenCombo: Map<String, Int> = emptyMap()
        var supplied = 0.0
        val p4 = 4.0
        val p075 = 0.75

        val max4 = ceil(required / p4).toInt() + 1
        for (n4 in 0..max4) {
            val remaining = required - n4 * p4
            val n075 = if (remaining <= 0.0) 0 else ceil(remaining / p075).toInt()
            val sup = n4 * p4 + n075 * p075
            if (sup >= required) {
                foundCombo = true
                chosenCombo = mapOf("ASTRAL_LAKALO_4L" to n4, "ASTRAL_LAKALO_075L" to n075)
                supplied = sup
                break
            }
        }

        assertTrue(foundCombo)
        // No KG/L conversion: required unit should be L
        assertEquals("L", (result as PaintCalculator.ProductCalculationResult.Success).unit)

        val waste = supplied - required
        // Report result assertions
        assertTrue(supplied >= required)
        assertTrue(waste >= 0.0)

        // No price calculation: MaterialPrice not used — we assert there is no price lookup in this test
        // (Implicit: tests do not access MaterialPrice and no exception thrown)
    }
}
