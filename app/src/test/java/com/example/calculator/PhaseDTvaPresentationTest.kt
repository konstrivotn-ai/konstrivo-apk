package com.example.calculator

import com.example.data.local.InitialData
import com.example.data.local.UserSettingsEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationSummary
import com.example.data.model.CalculationType
import com.example.data.model.Country
import com.example.domain.usecase.CalculateConstructionUseCase
import com.example.domain.usecase.FormatWhatsAppQuoteUseCase
import com.example.ui.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Phase D — TVA / HT / TTC presentation tests.
 *
 * These tests verify that the fiscal fields already computed by ConstructionCalculator
 * (and FiscalCatalog) are present, consistent, and surfaced correctly — without any
 * re-computation in the UI layer.
 */
class PhaseDTvaPresentationTest {

    private val calculateUseCase = CalculateConstructionUseCase()

    private fun buildCloisonSummary(
        montantType: String? = null,
        entraxe: Double = 0.40
    ): CalculationSummary {
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
            montantType = montantType,
            entraxeMontants = entraxe
        )
        return calculateUseCase(params, prices, country)
    }

    // Test A — grandTotalCost is the HT total; grandTotalTTC is the correct TTC to display.
    @Test
    fun testA_grandTotalCostIsLegacyHT_andTTCIsTheDisplayedTotal() {
        val summary = buildCloisonSummary()
        assertEquals(summary.grandTotalHT, summary.grandTotalCost, 0.0001)
        assertTrue("TVA rate must be > 0 for Tunisia", summary.tvaRate > 0.0)
        assertTrue(
            "grandTotalTTC must differ from the HT grandTotal when tvaRate > 0",
            summary.grandTotalTTC != summary.grandTotalCost
        )
        assertTrue(
            "grandTotalTTC > grandTotalCost (HT)",
            summary.grandTotalTTC > summary.grandTotalCost
        )
    }

    // Test B — TVA fields exist in CalculationSummary and are internally consistent.
    @Test
    fun testB_tvaFieldsPresentAndConsistent() {
        val summary = buildCloisonSummary()
        assertEquals(
            summary.materialTva + summary.laborTva + summary.transportTva +
                summary.equipmentTva + summary.otherTva,
            summary.totalTva,
            0.0001
        )
        assertEquals(summary.totalMaterialCost + summary.materialTva, summary.materialTotalTTC, 0.0001)
        assertEquals(summary.estimatedLaborCost + summary.laborTva, summary.laborTotalTTC, 0.0001)
        assertEquals(
            summary.totalMaterialCost + summary.estimatedLaborCost +
                summary.transportCostHT + summary.equipmentCostHT + summary.otherCostHT,
            summary.grandTotalHT,
            0.0001
        )
    }

    // Test C — CalculatedItem carries its own fiscal data (unitPriceHT / tvaAmount / totalPriceTTC).
    @Test
    fun testC_calculatedItemFiscalFields() {
        val summary = buildCloisonSummary(montantType = "montant_70")
        val montant = summary.items.firstOrNull { it.id == "montant_70" }
        assertNotNull("montant_70 item exists", montant)
        assertEquals("unitPriceHT defaults to HT unit price", montant!!.unitPrice, montant.unitPriceHT, 0.0001)
        assertEquals("item TVA rate matches summary TVA rate", summary.tvaRate, montant.tvaRate, 0.0001)
        assertEquals("TTC = HT + TVA per item", montant.totalPrice + montant.tvaAmount, montant.totalPriceTTC, 0.01)
    }

    // Test D — M70 + 60 cm remain selected together and reach the calculation.
    @Test
    fun testD_m70_60cmParameterKeepBothValues() {
        val vm = CalculatorViewModel()
        vm.setSelectedMontantType("montant_70")
        vm.setEntraxeMontantsMeters(0.60)
        assertEquals("montant_70", vm.selectedMontantType.value)
        assertEquals(0.60, vm.entraxeMontants.value, 0.0001)

        val summary = buildCloisonSummary(montantType = "montant_70", entraxe = 0.60)
        assertTrue("M70 item is present", summary.items.any { it.id == "montant_70" })
        assertTrue("M48 is not used", summary.items.none { it.id == "montant_48" })
    }

    // Test E — M48 + 40 cm remain correct (explicit selection).
    @Test
    fun testE_m48_40cmStillWorks() {
        val vm = CalculatorViewModel()
        vm.setSelectedMontantType("montant_48")
        vm.setEntraxeMontantsMeters(0.40)
        assertEquals("montant_48", vm.selectedMontantType.value)
        assertEquals(0.40, vm.entraxeMontants.value, 0.0001)

        val summary = buildCloisonSummary(montantType = "montant_48", entraxe = 0.40)
        assertTrue("M48 item is present", summary.items.any { it.id == "montant_48" })
    }

    // Test F — FormatWhatsAppQuoteUseCase uses grandTotalTTC & TVA without changing the price engine.
    @Test
    fun testF_whatsAppShowsTTCAndTVA() {
        val summary = buildCloisonSummary()
        val settings = UserSettingsEntity(companyHeader = "Test BTP")
        val text = FormatWhatsAppQuoteUseCase()(summary, settings, AppLanguage.FRENCH)

        assertTrue("shows TOTAL TTC label", text.contains("TOTAL TTC"))
        assertTrue("shows TVA rate from FiscalCatalog", text.contains("TVA (19%)"))
        assertTrue("shows totalTva amount", text.contains("${summary.totalTva}"))
        assertTrue("shows grandTotalTTC amount", text.contains("${summary.grandTotalTTC}"))
        assertFalse("no longer uses the misleading legacy label", text.contains("TOTAL GÉNÉRAL ESTIMÉ"))
    }
}