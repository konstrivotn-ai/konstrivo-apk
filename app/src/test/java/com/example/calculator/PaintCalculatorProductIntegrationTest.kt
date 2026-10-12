package com.example.calculator

import com.example.domain.paint.PaintProductCatalog
import com.example.domain.paint.PaintProduct
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PaintCalculatorProductIntegrationTest {

    @Before
    fun setup() {
        PaintProductCatalog.clear()
        PaintProductCatalog.addReferenceData()
    }

    @Test
    fun testM2PerL_ProductCalculatesLiters() {
        val product = PaintProductCatalog.getById("ASTRAL_LAKALO")!! // coverage 13 M2/L

        val result = PaintCalculator.calculateForProduct(
            product = product,
            area = 50.0,
            openingsArea = 5.0,
            additionalArea = 0.0,
            surfaceMultiplier = 1.1,
            wastePercent = 10.0
        )

        when (result) {
            is PaintCalculator.ProductCalculationResult.Success -> {
                assertEquals("L", result.unit)
                // expected liters ~ 8.377
                assertEquals(8.377, result.requiredAmount, 0.01)
                assertEquals(2, result.coats)
                assertEquals(13.0, result.coverageValue!!, 0.0001)
                assertEquals("M2_PER_L", result.coverageUnit)
            }
            else -> fail("Expected success result for M2_PER_L product")
        }
    }

    @Test
    fun testM2PerKg_ProductCalculatesKilograms() {
        val product = PaintProductCatalog.getById("ASTRAL_AQUASTRAL_SATIN")!! // coverage 10 M2/KG

        val result = PaintCalculator.calculateForProduct(
            product = product,
            area = 50.0,
            openingsArea = 5.0,
            additionalArea = 0.0,
            surfaceMultiplier = 1.1,
            wastePercent = 10.0
        )

        when (result) {
            is PaintCalculator.ProductCalculationResult.Success -> {
                assertEquals("KG", result.unit)
                // expected kg = 10.89 (see earlier tests parity)
                assertEquals(10.89, result.requiredAmount, 0.01)
                assertEquals(2, result.coats)
                assertEquals(10.0, result.coverageValue!!, 0.0001)
                assertEquals("M2_PER_KG", result.coverageUnit)
            }
            else -> fail("Expected success result for M2_PER_KG product")
        }
    }

    @Test
    fun testNoIllegalKgToLConversion() {
        val product = PaintProductCatalog.getById("ASTRAL_AQUASTRAL_SATIN")!!
        val result = PaintCalculator.calculateForProduct(
            product = product,
            area = 10.0,
            openingsArea = 0.0,
            additionalArea = 0.0,
            surfaceMultiplier = 1.0,
            wastePercent = 0.0
        )
        when (result) {
            is PaintCalculator.ProductCalculationResult.Success -> assertEquals("KG", result.unit)
            else -> fail("Expected KG success result; no kg->L conversion allowed")
        }
    }

    @Test
    fun testUnknownCoverage_returnsMissing() {
        val p = PaintProduct(
            productId = "UNKNOWN_COV",
            materialTypeId = "peinture_finition",
            productName = "NoCov",
            containerVolumeL = 4.0,
            countryCode = "TN"
        )

        val result = PaintCalculator.calculateForProduct(
            product = p,
            area = 10.0,
            openingsArea = 0.0,
            additionalArea = 0.0,
            surfaceMultiplier = 1.0,
            wastePercent = 0.0
        )

        when (result) {
            is PaintCalculator.ProductCalculationResult.MissingCoverage -> {
                // expected
            }
            else -> fail("Expected MissingCoverage for product without coverage metadata")
        }
    }

    @Test
    fun testRecommendedCoatsUsedWhenRequested() {
        val product = PaintProductCatalog.getById("ASTRAL_LAKALO")!! // recommendedCoats = 2
        val result = PaintCalculator.calculateForProduct(
            product = product,
            area = 10.0,
            openingsArea = 0.0,
            additionalArea = 0.0,
            surfaceMultiplier = 1.0,
            wastePercent = 0.0,
            useRecommendedCoats = true
        )
        when (result) {
            is PaintCalculator.ProductCalculationResult.Success -> assertEquals(2, result.coats)
            else -> fail("Expected success and use of recommended coats")
        }
    }
}
