package com.example.calculator

import com.example.data.local.InitialData
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationType
import com.example.data.model.Country
import com.example.data.model.TradeCategory
import com.example.domain.usecase.CalculateConstructionUseCase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PeintureCalculatorTest {

    private lateinit var calculateUseCase: CalculateConstructionUseCase
    private val country = Country.TUNISIA
    private lateinit var prices: Map<String, com.example.data.local.MaterialPriceEntity>

    @Before
    fun setUp() {
        calculateUseCase = CalculateConstructionUseCase()
        prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }
    }

    // ==========================================
    // 1. PEINTURE INTERIEURE TESTS
    // ==========================================

    @Test
    fun testPeintureInterieure_smallArea_12m2() {
        // Small room (12 m²)
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PEINTURE_INTERIEURE,
            area = 12.0,
            perimeter = 14.0,
            wasteMarginPercent = 8.0 // default waste margin
        )

        val result = calculateUseCase(params, prices, country)

        assertEquals(CalculationType.PEINTURE_INTERIEURE, result.calculationType)
        assertEquals(12.0, result.inputArea, 0.001)
        assertEquals(3, result.items.size)

        // Enduit: ceil((12 * 1.2) / 25.0) = ceil(14.4 / 25) = 1 sac
        val enduitItem = result.items.find { it.id == "enduit_lissage_peinture_25kg" }
        assertNotNull("Enduit item exists", enduitItem)
        assertEquals(1.0, enduitItem!!.quantity, 0.001)
        assertEquals(35.0, enduitItem.unitPrice, 0.001)
        assertEquals(35.0, enduitItem.totalPrice, 0.001)

        // Fixateur: ceil((12 * 0.15) / 20.0) = ceil(1.8 / 20) = 1 bidon
        val fixateurItem = result.items.find { it.id == "fixateur_primaire_20l" }
        assertNotNull("Fixateur item exists", fixateurItem)
        assertEquals(1.0, fixateurItem!!.quantity, 0.001)
        assertEquals(68.0, fixateurItem.unitPrice, 0.001)
        assertEquals(68.0, fixateurItem.totalPrice, 0.001)

        // Peinture Finition: ceil((12 * 0.25) / 15.0) = ceil(3.0 / 15) = 1 bidon
        val peintureItem = result.items.find { it.id == "peinture_finition_satinee_15l" }
        assertNotNull("Peinture finish item exists", peintureItem)
        assertEquals(1.0, peintureItem!!.quantity, 0.001)
        assertEquals(145.0, peintureItem.unitPrice, 0.001)
        assertEquals(145.0, peintureItem.totalPrice, 0.001)

        // Total materials: 35 + 68 + 145 = 248.0 TND
        assertEquals(248.0, result.totalMaterialCost, 0.001)

        // Labor: 12.0 * 8.0 = 96.0 TND
        assertEquals(96.0, result.estimatedLaborCost, 0.001)

        // Grand Total: 248.0 + 96.0 = 344.0 TND
        assertEquals(344.0, result.grandTotalCost, 0.001)
    }

    @Test
    fun testPeintureInterieure_mediumArea_50m2() {
        // Medium apartment wall area (50 m²)
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PEINTURE_INTERIEURE,
            area = 50.0,
            perimeter = 30.0,
            wasteMarginPercent = 8.0
        )

        val result = calculateUseCase(params, prices, country)

        // Enduit: ceil((50 * 1.2) / 25.0) = ceil(60.0 / 25) = 3 sacs
        val enduitItem = result.items.find { it.id == "enduit_lissage_peinture_25kg" }!!
        assertEquals(3.0, enduitItem.quantity, 0.001)
        assertEquals(3.0 * 35.0, enduitItem.totalPrice, 0.001)

        // Fixateur: ceil((50 * 0.15) / 20.0) = ceil(7.5 / 20) = 1 bidon
        val fixateurItem = result.items.find { it.id == "fixateur_primaire_20l" }!!
        assertEquals(1.0, fixateurItem.quantity, 0.001)

        // Peinture: ceil((50 * 0.25) / 15.0) = ceil(12.5 / 15) = 1 bidon
        val peintureItem = result.items.find { it.id == "peinture_finition_satinee_15l" }!!
        assertEquals(1.0, peintureItem.quantity, 0.001)

        // Total materials: (3 * 35) + 68 + 145 = 105 + 68 + 145 = 318.0 TND
        assertEquals(318.0, result.totalMaterialCost, 0.001)

        // Labor: 50.0 * 8.0 = 400.0 TND
        assertEquals(400.0, result.estimatedLaborCost, 0.001)

        // Grand Total: 318.0 + 400.0 = 718.0 TND
        assertEquals(718.0, result.grandTotalCost, 0.001)
    }

    @Test
    fun testPeintureInterieure_largeArea_200m2_withCustomLabor() {
        // Large site (200 m²) with custom contractor rate of 10.0 TND/m²
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PEINTURE_INTERIEURE,
            area = 200.0,
            perimeter = 60.0,
            wasteMarginPercent = 8.0,
            customLaborRatePerM2 = 10.0
        )

        val result = calculateUseCase(params, prices, country)

        // Enduit: ceil((200 * 1.2) / 25.0) = ceil(240 / 25) = 10 sacs
        val enduitItem = result.items.find { it.id == "enduit_lissage_peinture_25kg" }!!
        assertEquals(10.0, enduitItem.quantity, 0.001)
        assertEquals(350.0, enduitItem.totalPrice, 0.001)

        // Fixateur: ceil((200 * 0.15) / 20.0) = ceil(30 / 20) = 2 bidons
        val fixateurItem = result.items.find { it.id == "fixateur_primaire_20l" }!!
        assertEquals(2.0, fixateurItem.quantity, 0.001)
        assertEquals(136.0, fixateurItem.totalPrice, 0.001)

        // Peinture: ceil((200 * 0.25) / 15.0) = ceil(50 / 15) = 4 bidons
        val peintureItem = result.items.find { it.id == "peinture_finition_satinee_15l" }!!
        assertEquals(4.0, peintureItem.quantity, 0.001)
        assertEquals(4.0 * 145.0, peintureItem.totalPrice, 0.001) // 580.0

        // Total materials: 350 + 136 + 580 = 1066.0 TND
        assertEquals(1066.0, result.totalMaterialCost, 0.001)

        // Custom Labor: 200.0 * 10.0 = 2000.0 TND
        assertEquals(2000.0, result.estimatedLaborCost, 0.001)

        // Grand Total: 1066 + 2000 = 3066.0 TND
        assertEquals(3066.0, result.grandTotalCost, 0.001)
    }

    // ==========================================
    // 2. PEINTURE SABLE / DECOR TESTS
    // ==========================================

    @Test
    fun testPeintureSable_smallArea_10m2() {
        // Small feature decorative wall (10 m²)
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PEINTURE_SABLE,
            area = 10.0,
            perimeter = 13.0,
            wasteMarginPercent = 5.0
        )

        val result = calculateUseCase(params, prices, country)

        assertEquals(CalculationType.PEINTURE_SABLE, result.calculationType)
        assertEquals(10.0, result.inputArea, 0.001)
        assertEquals(2, result.items.size)

        // Fixateur: ceil((10 * 0.15) / 20.0) = ceil(1.5 / 20) = 1 bidon
        val fixateurItem = result.items.find { it.id == "fixateur_primaire_20l" }
        assertNotNull("Fixateur item exists", fixateurItem)
        assertEquals(1.0, fixateurItem!!.quantity, 0.001)
        assertEquals(68.0, fixateurItem.totalPrice, 0.001)

        // Peinture Sablé: ceil((10 * 0.25) / 5.0) = ceil(2.5 / 5) = 1 bidon
        val sableItem = result.items.find { it.id == "peinture_sable_decoratif_5kg" }
        assertNotNull("Sablé decorative item exists", sableItem)
        assertEquals(1.0, sableItem!!.quantity, 0.001)
        assertEquals(55.0, sableItem.totalPrice, 0.001)

        // Total materials: 68 + 55 = 123.0 TND
        assertEquals(123.0, result.totalMaterialCost, 0.001)

        // Labor: 10.0 * 14.0 = 140.0 TND
        assertEquals(140.0, result.estimatedLaborCost, 0.001)

        // Grand Total: 123 + 140 = 263.0 TND
        assertEquals(263.0, result.grandTotalCost, 0.001)
    }

    @Test
    fun testPeintureSable_mediumArea_35m2() {
        // Feature walls across living room (35 m²)
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PEINTURE_SABLE,
            area = 35.0,
            perimeter = 24.0,
            wasteMarginPercent = 5.0
        )

        val result = calculateUseCase(params, prices, country)

        // Fixateur: ceil((35 * 0.15) / 20.0) = ceil(5.25 / 20) = 1 bidon
        val fixateurItem = result.items.find { it.id == "fixateur_primaire_20l" }!!
        assertEquals(1.0, fixateurItem.quantity, 0.001)

        // Peinture Sablé: ceil((35 * 0.25) / 5.0) = ceil(8.75 / 5) = 2 bidons (5kg each)
        val sableItem = result.items.find { it.id == "peinture_sable_decoratif_5kg" }!!
        assertEquals(2.0, sableItem.quantity, 0.001)
        assertEquals(110.0, sableItem.totalPrice, 0.001)

        // Total materials: 68 + 110 = 178.0 TND
        assertEquals(178.0, result.totalMaterialCost, 0.001)

        // Labor: 35.0 * 14.0 = 490.0 TND
        assertEquals(490.0, result.estimatedLaborCost, 0.001)

        // Grand Total: 178 + 490 = 668.0 TND
        assertEquals(668.0, result.grandTotalCost, 0.001)
    }

    @Test
    fun testPeintureSable_largeArea_100m2() {
        // Large hall decorative work (100 m²)
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PEINTURE_SABLE,
            area = 100.0,
            perimeter = 40.0,
            wasteMarginPercent = 5.0
        )

        val result = calculateUseCase(params, prices, country)

        // Fixateur: ceil((100 * 0.15) / 20.0) = ceil(15 / 20) = 1 bidon
        val fixateurItem = result.items.find { it.id == "fixateur_primaire_20l" }!!
        assertEquals(1.0, fixateurItem.quantity, 0.001)

        // Peinture Sablé: ceil((100 * 0.25) / 5.0) = ceil(25 / 5) = 5 bidons
        val sableItem = result.items.find { it.id == "peinture_sable_decoratif_5kg" }!!
        assertEquals(5.0, sableItem.quantity, 0.001)
        assertEquals(275.0, sableItem.totalPrice, 0.001)

        // Total materials: 68 + 275 = 343.0 TND
        assertEquals(343.0, result.totalMaterialCost, 0.001)

        // Labor: 100.0 * 14.0 = 1400.0 TND
        assertEquals(1400.0, result.estimatedLaborCost, 0.001)

        // Grand Total: 343 + 1400 = 1743.0 TND
        assertEquals(1743.0, result.grandTotalCost, 0.001)
    }

    // ==========================================
    // 3. INTERNATIONAL / MULTI-CURRENCY CHECKS
    // ==========================================

    @Test
    fun testPeinture_franceCountryConversion() {
        val frCountry = Country.FRANCE
        val frPrices = InitialData.generatePricesForCountry(frCountry).associateBy { it.baseKey }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PEINTURE_INTERIEURE,
            area = 50.0,
            perimeter = 30.0,
            wasteMarginPercent = 8.0
        )

        val result = calculateUseCase(params, frPrices, frCountry)

        assertEquals("€", result.currency)
        assertEquals("FR", result.countryCode)
        assertTrue("Total material in EUR > 0", result.totalMaterialCost > 0)
        // Default labor for France: 8.0 * 0.30 = 2.4 EUR/m² -> 50 * 2.4 = 120.0 EUR
        assertEquals(120.0, result.estimatedLaborCost, 0.01)
    }

    @Test
    fun testPeintureInterieure_finish_regression_matchesLegacyFormula() {
        // Regression: verify PaintCalculator-based finish produces same 15L can count
        val area = 50.0
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PEINTURE_INTERIEURE,
            area = area,
            perimeter = 30.0,
            wasteMarginPercent = 8.0
        )

        val result = calculateUseCase(params, prices, country)

        // Legacy calculation: ceil((area * 0.25) / 15.0)
        val legacyBidons = kotlin.math.ceil((area * 0.25) / 15.0)

        val peintureItem = result.items.find { it.id == "peinture_finition_satinee_15l" }!!
        assertEquals(legacyBidons, peintureItem.quantity, 0.001)
    }
}
