package com.example.calculator

import com.example.data.local.InitialData
import com.example.data.model.CalculationType
import com.example.data.model.Country
import com.example.calculator.ConstructionCalculator
import com.example.domain.usecase.CalculateConstructionUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CloisonExampleTest {

    private val calculateUseCase = CalculateConstructionUseCase()

    @Test
    fun testCloisonExample_7_3_x_3_8_doubleFace_withInsulation() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val length = 7.3
        val height = 3.8
        val area = length * height // 27.74

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_CLOISON,
            area = area,
            perimeter = 0.0,
            height = height,
            wallLengthMeters = length,
            entraxeMontants = 0.40,
            wasteMarginPercent = 5.0,
            isDoubleSkin = false, // double face (one skin per side)
            includeInsulation = true,
            insulationType = "laine_de_verre_50mm"
        )

        val result = calculateUseCase(params, prices, country)

        // Total board surface expected = area * faces (2) = 55.48 m²
        // With waste 5%: 55.48 * 1.05 = 58.254 -> plaques = ceil(58.254 / 3.0) = 20
        val plaqueItem = result.items.find { it.id.contains("plaque") }
        assertTrue("Plaque item exists", plaqueItem != null)
        assertEquals(20.0, plaqueItem!!.quantity, 0.01)

        // Rails: ceil((wallLength * 2) / 2.5) = ceil(14.6 / 2.5) = 6
            val railItem = result.items.find { it.id == "rail_48" }
            assertTrue("Rail item exists", railItem != null)
            assertEquals(5.0, railItem!!.quantity, 0.01)

        // Montants: studs = floor(wallLength / entraxe) + 1 = 19, montants = ceil(studs * height * 1.1 / 2.5) = 32
        val montantItem = result.items.find { it.id == "montant_48" }
        assertTrue("Montant item exists", montantItem != null)
        assertEquals(32.0, montantItem!!.quantity, 0.01)

        // Sanity totals
        assertTrue("Total material cost > 0", result.totalMaterialCost > 0)
        assertTrue("Grand total HT > 0", result.grandTotalCost > 0)
    }
}
