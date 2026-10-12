package com.example.calculator

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert

class PaintCalculatorTest {

    @Test
    fun testPaintCalculation_example() {
        val area = 50.0
        val openings = 5.0
        val additional = 0.0
        val coverage = 10.0
        val coats = 2
        val surfaceMultiplier = 1.1
        val waste = 10.0

        val netArea = kotlin.math.max(0.0, area - openings + additional)
        assertEquals(45.0, netArea, 0.0001)

        val liters = PaintCalculator.calculatePaintLiters(
            area,
            openings,
            additional,
            coverage,
            coats,
            surfaceMultiplier,
            waste
        )

        assertEquals(10.89, liters, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCoverageZero_throws() {
        PaintCalculator.calculatePaintLiters(
            area = 10.0,
            openingsArea = 0.0,
            additionalArea = 0.0,
            coverageM2PerLiter = 0.0,
            coats = 1,
            surfaceMultiplier = 1.0,
            wastePercent = 0.0
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCoatsZero_throws() {
        PaintCalculator.calculatePaintLiters(
            area = 10.0,
            openingsArea = 0.0,
            additionalArea = 0.0,
            coverageM2PerLiter = 10.0,
            coats = 0,
            surfaceMultiplier = 1.0,
            wastePercent = 0.0
        )
    }

    @Test
    fun testNegativeOpenings_handled() {
        val area = 50.0
        val openings = -5.0
        val additional = 0.0
        val coverage = 10.0
        val coats = 2
        val surfaceMultiplier = 1.1
        val waste = 10.0

        // netArea = 50 - (-5) + 0 = 55.0
        val liters = PaintCalculator.calculatePaintLiters(
            area,
            openings,
            additional,
            coverage,
            coats,
            surfaceMultiplier,
            waste
        )

        assertEquals(13.31, liters, 0.01)
    }

    @Test
    fun testZeroArea_returnsZero() {
        val liters = PaintCalculator.calculatePaintLiters(
            area = 0.0,
            openingsArea = 0.0,
            additionalArea = 0.0,
            coverageM2PerLiter = 10.0,
            coats = 1,
            surfaceMultiplier = 1.0,
            wastePercent = 0.0
        )

        assertEquals(0.0, liters, 0.0001)
    }
}
