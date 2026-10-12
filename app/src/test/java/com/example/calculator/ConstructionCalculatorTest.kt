package com.example.calculator

import com.example.data.local.InitialData
import com.example.data.model.CalculationType
import com.example.data.model.Country
import com.example.domain.usecase.CalculateConstructionUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConstructionCalculatorTest {

    private val calculateUseCase = CalculateConstructionUseCase()

    @Test
    fun testPlafondFixeBA13Calculation() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_PLAFOND_FIXE,
            area = 30.0,
            perimeter = 22.0,
            height = 2.8,
            wasteMarginPercent = 5.0,
            boardType = "plaque_ba13_standard",
            includeInsulation = true,
            insulationType = "laine_de_verre_50mm"
        )

        val result = calculateUseCase(params, prices, country)

        // Area: 30m² * 1.05 = 31.5 / 3 = 10.5 -> 11 plaques
        val plaqueItem = result.items.find { it.id.contains("plaque") }
        assertTrue("Plaque item exists", plaqueItem != null)
        assertEquals(11.0, plaqueItem!!.quantity, 0.01)

        // Fourrure: 30 * 2 / 3 = 20
        val fourrureItem = result.items.find { it.id == "fourrure" }
        assertTrue("Fourrure item exists", fourrureItem != null)
        assertEquals(20.0, fourrureItem!!.quantity, 0.01)

        // Corniere: 22 / 3 = 7.33 -> 8
        val corniereItem = result.items.find { it.id == "corniere_angle" }
        assertTrue("Corniere item exists", corniereItem != null)
        assertEquals(8.0, corniereItem!!.quantity, 0.01)

        // Suspente: 30 * 1.2 = 36
        val suspenteItem = result.items.find { it.id == "suspente" }
        assertTrue("Suspente item exists", suspenteItem != null)
        assertEquals(36.0, suspenteItem!!.quantity, 0.01)

        // Totals sanity check
        assertTrue("Total material cost > 0", result.totalMaterialCost > 0)
        assertTrue("Grand total > 0", result.grandTotalCost > 0)
    }

    @Test
    fun testCloisonCalculation() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_CLOISON,
            area = 20.0,
            perimeter = 18.0,
            height = 2.8,
            wasteMarginPercent = 5.0,
            boardType = "plaque_ba13_standard",
            isDoubleSkin = false,
            includeInsulation = true,
            insulationType = "laine_de_verre_50mm"
        )

        val result = calculateUseCase(params, prices, country)

        // Single skin cloison: (20 * 2.10) / 3 = 14 plaques
        val plaqueItem = result.items.find { it.id.contains("plaque") }
        assertTrue("Plaque item exists", plaqueItem != null)
        assertEquals(14.0, plaqueItem!!.quantity, 0.01)

        // Rails: wallLength = area / height = 20 / 2.8 = 7.143 m
        // rails = ceil((wallLength * 2) / stock length 3.0m) = ceil(14.286 / 3) = ceil(4.762) = 5
        val railItem = result.items.find { it.id == "rail_48" }
        assertTrue("Rail item exists", railItem != null)
        assertEquals(5.0, railItem!!.quantity, 0.01)

        // Montants: studs = floor(wallLength / entraxe 0.40) + 1 = floor(7.143 / 0.40) + 1 = 18
        // montants = ceil(studs * height 2.8 / board length 2.5) = ceil(50.4 / 2.5) = 21
        val montantItem = result.items.find { it.id == "montant_48" }
        assertTrue("Montant item exists", montantItem != null)
        assertEquals(21.0, montantItem!!.quantity, 0.01)
    }

    @Test
    fun testMontantDefaultsTo48WhenNull() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_CLOISON,
            area = 20.0,
            perimeter = 18.0,
            height = 2.8,
            wasteMarginPercent = 5.0,
            boardType = "plaque_ba13_standard",
            isDoubleSkin = false,
            includeInsulation = true,
            insulationType = "laine_de_verre_50mm",
            montantType = null
        )

        val result = calculateUseCase(params, prices, country)
        val montantItem = result.items.find { it.id == "montant_48" }
        assertTrue("Default montant fallback should use montant_48", montantItem != null)
    }

    @Test
    fun testMontantUsesSelectedM70() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_CLOISON,
            area = 20.0,
            perimeter = 18.0,
            height = 2.8,
            wasteMarginPercent = 5.0,
            boardType = "plaque_ba13_standard",
            isDoubleSkin = false,
            includeInsulation = true,
            insulationType = "laine_de_verre_50mm",
            montantType = "montant_70"
        )

        val result = calculateUseCase(params, prices, country)
        val montantItem = result.items.find { it.id == "montant_70" }
        assertTrue("Selected montant should be montant_70", montantItem != null)
        assertTrue("montant_48 should not be used after M70 selection", result.items.none { it.id == "montant_48" })
    }

    @Test
    fun testMontantUsesSelectedM48() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_CLOISON,
            area = 20.0,
            perimeter = 18.0,
            height = 2.8,
            wasteMarginPercent = 5.0,
            boardType = "plaque_ba13_standard",
            isDoubleSkin = false,
            includeInsulation = true,
            insulationType = "laine_de_verre_50mm",
            montantType = "montant_48"
        )

        val result = calculateUseCase(params, prices, country)
        val montantItem = result.items.find { it.id == "montant_48" }
        assertTrue("Selected montant should be montant_48", montantItem != null)
    }

    @Test
    fun testMontantPriceLookupUsesSelectedType() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_CLOISON,
            area = 20.0,
            perimeter = 18.0,
            height = 2.8,
            wasteMarginPercent = 5.0,
            boardType = "plaque_ba13_standard",
            isDoubleSkin = false,
            includeInsulation = true,
            insulationType = "laine_de_verre_50mm",
            montantType = "montant_70"
        )

        val result = calculateUseCase(params, prices, country)
        val montantItem = result.items.find { it.id == "montant_70" }
        assertTrue("Montant item should exist for M70 selection", montantItem != null)
        assertTrue("Selected M70 price should come from the price map", prices["montant_70"] != null)
        assertEquals(prices["montant_70"]!!.currentPrice, montantItem!!.unitPrice, 0.0)
    }

    @Test
    fun testDemountableCeilingCalculation() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_DEMONTABLE,
            area = 100.0,
            perimeter = 40.0,
            wasteMarginPercent = 5.0
        )

        val result = calculateUseCase(params, prices, country)

        // Dalles: 100 * 2.8 * 1.05 = 294 dalles
        val dallesItem = result.items.find { it.id.contains("dalle") }
        assertTrue("Dalles item exists", dallesItem != null)
        assertEquals(294.0, dallesItem!!.quantity, 0.01)
    }
}
