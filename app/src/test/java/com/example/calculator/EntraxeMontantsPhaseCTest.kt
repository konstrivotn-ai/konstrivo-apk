package com.example.calculator

import com.example.data.local.InitialData
import com.example.data.local.MaterialPriceEntity
import com.example.data.model.CalculationType
import com.example.data.model.Country
import com.example.domain.usecase.CalculateConstructionUseCase
import com.example.ui.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Phase C — Entraxe des montants wiring tests.
 *
 * Verifies that the Entraxe (40 cm / 60 cm) chosen in the UI actually flows into
 * the calculation and changes the real montant quantity, without breaking the
 * independent Montant Type (Phase B) selection.
 */
class EntraxeMontantsPhaseCTest {

    private val calculateUseCase = CalculateConstructionUseCase()

    // Test A — default value is 0.40 m (40 cm) when nothing is explicitly chosen.
    @Test
    fun testEntraxe_default_equals040() {
        val viewModel = CalculatorViewModel()
        assertEquals(0.40, viewModel.entraxeMontants.value, 0.0001)
    }

    // Test B — setting 40 cm keeps the ViewModel value at 0.40 m.
    @Test
    fun testEntraxe_set40cm_keeps040() {
        val viewModel = CalculatorViewModel()
        viewModel.setEntraxeMontantsMeters(0.40)
        assertEquals(0.40, viewModel.entraxeMontants.value, 0.0001)
    }

    // Test C — setting 60 cm keeps the ViewModel value at 0.60 m.
    @Test
    fun testEntraxe_set60cm_keeps060() {
        val viewModel = CalculatorViewModel()
        viewModel.setEntraxeMontantsMeters(0.60)
        assertEquals(0.60, viewModel.entraxeMontants.value, 0.0001)
    }

    // Test D — an invalid value (0.50 m) must be rejected and never reach the calculation.
    @Test(expected = IllegalArgumentException::class)
    fun testEntraxe_rejectsInvalidValue_050() {
        val viewModel = CalculatorViewModel()
        viewModel.setEntraxeMontantsMeters(0.50)
    }

    // Test E — the same inputs but a tighter / looser entraxe change the montant quantity.
    @Test
    fun testEntraxe_changesMontantQuantity() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }

        val qty40 = cloisonMontantQty(prices, country, 0.40, "montant_48")
        val qty60 = cloisonMontantQty(prices, country, 0.60, "montant_48")

        // Cloison area 20m², height 2.8m => wallLength = area/height = 7.1429 m.
        // 40 cm: studs = floor(7.1429/0.40)+1 = 18 -> montants = ceil(18*2.8/2.5) = 21
        // 60 cm: studs = floor(7.1429/0.60)+1 = 12 -> montants = ceil(12*2.8/2.5) = 14
        assertEquals(21.0, qty40, 0.0001)
        assertEquals(14.0, qty60, 0.0001)
        assertTrue("Montant quantity must change between 40cm and 60cm", qty40 != qty60)
    }

    // Test F — Phase B (Montant) and Phase C (Entraxe) work independently: M70 + 60 cm.
    @Test
    fun testMontantAndEntraxeCombination_m70_60cm() {
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
            montantType = "montant_70",
            entraxeMontants = 0.60
        )
        val result = calculateUseCase(params, prices, country)

        val montant70 = result.items.find { it.id == "montant_70" }
        assertNotNull("M70 montant item should exist", montant70)
        assertTrue(
            "montant_48 should not be used after selecting M70 + 60cm",
            result.items.none { it.id == "montant_48" }
        )
        // Same 60cm formula as Test E: 14 pieces of the selected montant_70.
        assertEquals(14.0, montant70!!.quantity, 0.0001)
        assertEquals(prices["montant_70"]!!.currentPrice, montant70!!.unitPrice, 0.0)
    }

    private fun cloisonMontantQty(
        prices: Map<String, MaterialPriceEntity>,
        country: Country,
        entraxe: Double,
        montantType: String
    ): Double {
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
            montantType = montantType,
            entraxeMontants = entraxe
        )
        val result = calculateUseCase(params, prices, country)
        val montant = result.items.find { it.id == montantType }
        assertNotNull("Montant item exists", montant)
        return montant!!.quantity
    }
}