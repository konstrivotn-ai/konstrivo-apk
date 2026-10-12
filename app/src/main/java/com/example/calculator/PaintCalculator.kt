package com.example.calculator

import com.example.domain.paint.PaintProduct
import com.example.domain.paint.PaintProductCatalog
import kotlin.math.max

object PaintCalculator {
    /**
     * Calculate required paint liters using the provided parameters.
     * Inputs are explicit and no defaults are assumed here.
     */
    fun calculatePaintLiters(
        area: Double,
        openingsArea: Double,
        additionalArea: Double,
        coverageM2PerLiter: Double,
        coats: Int,
        surfaceMultiplier: Double,
        wastePercent: Double
    ): Double {
        // Basic validation
        if (coverageM2PerLiter <= 0.0) {
            throw IllegalArgumentException("coverageM2PerLiter must be > 0")
        }
        if (coats <= 0) {
            throw IllegalArgumentException("coats must be > 0")
        }

        val rawNetArea = area - openingsArea + additionalArea
        if (rawNetArea < 0.0) {
            throw IllegalArgumentException("net area (area - openingsArea + additionalArea) must not be negative")
        }

        val wasteMultiplier = 1.0 + wastePercent / 100.0
        if (wasteMultiplier <= 0.0) {
            throw IllegalArgumentException("wastePercent must be > -100")
        }

        val netArea = max(0.0, rawNetArea)
        val liters = (netArea / coverageM2PerLiter) * coats * surfaceMultiplier * wasteMultiplier
        return liters
    }

    /**
     * Result model for product-based calculations.
     */
    sealed class ProductCalculationResult {
        data class Success(
            val requiredAmount: Double,
            val unit: String, // "L" or "KG"
            val coats: Int,
            val coverageValue: Double?,
            val coverageUnit: String?
        ) : ProductCalculationResult()

        data class UnsupportedCoverageUnit(val message: String) : ProductCalculationResult()

        data class MissingCoverage(val message: String) : ProductCalculationResult()
    }

    /**
     * Calculate required paint using a `PaintProduct` technical data.
     * - If coverage unit is M2_PER_L, returns liters in Success.unit = "L".
     * - If coverage unit is M2_PER_KG, returns kilograms in Success.unit = "KG".
     * - If coverage is missing, returns MissingCoverage.
     */
    fun calculateForProduct(
        product: PaintProduct,
        area: Double,
        openingsArea: Double,
        additionalArea: Double,
        surfaceMultiplier: Double,
        wastePercent: Double,
        useRecommendedCoats: Boolean = true,
        coatsOverride: Int? = null
    ): ProductCalculationResult {
        // Determine coats to use
        val coats = when {
            coatsOverride != null -> coatsOverride
            useRecommendedCoats && product.recommendedCoats != null -> product.recommendedCoats
            else -> 1
        }

        // Try to obtain coverage info: first check product.coverageM2PerLiter (legacy),
        // then fall back to catalog metadata which preserves unit.
        val legacyCoverageM2PerL = product.coverageM2PerLiter
        if (legacyCoverageM2PerL != null) {
            // legacy value is assumed to be m2 per liter
            val liters = calculatePaintLiters(area, openingsArea, additionalArea, legacyCoverageM2PerL, coats, surfaceMultiplier, wastePercent)
            return ProductCalculationResult.Success(liters, "L", coats, legacyCoverageM2PerL, "M2_PER_L")
        }

        val coverageValue = PaintProductCatalog.getCoverageValue(product.productId)
        val coverageUnit = PaintProductCatalog.getCoverageUnit(product.productId)
        if (coverageValue == null || coverageUnit == null) {
            return ProductCalculationResult.MissingCoverage("No coverage data for product ${product.productId}")
        }

        val netArea = max(0.0, area - openingsArea + additionalArea)
        val wasteMultiplier = 1.0 + wastePercent / 100.0

        return when (coverageUnit) {
            "M2_PER_L" -> {
                val liters = (netArea / coverageValue) * coats * surfaceMultiplier * wasteMultiplier
                ProductCalculationResult.Success(liters, "L", coats, coverageValue, coverageUnit)
            }
            "M2_PER_KG" -> {
                val kgs = (netArea / coverageValue) * coats * surfaceMultiplier * wasteMultiplier
                ProductCalculationResult.Success(kgs, "KG", coats, coverageValue, coverageUnit)
            }
            else -> ProductCalculationResult.UnsupportedCoverageUnit("Unsupported coverage unit: $coverageUnit for product ${product.productId}")
        }
    }
}
