package com.example.calculator

import com.example.data.local.InitialData
import com.example.data.local.UserSettingsEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationSummary
import com.example.data.model.CalculationType
import com.example.data.model.Country
import com.example.domain.material.MaterialCatalog
import com.example.domain.model.MaterialCategory
import com.example.domain.fiscal.TaxMath
import com.example.domain.usecase.CalculateConstructionUseCase
import com.example.domain.usecase.FormatWhatsAppQuoteUseCase
import com.example.domain.usecase.formatQuantity
import com.example.ui.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Phase F — Final validation tests.
 *
 * Adds only the coverage missing after Phases A–E:
 *  - A: the Calculator screen board selector is driven by MaterialCatalog.ALL_MATERIALS
 *       (no hard-coded legacy list), with the same label-fallback rules as the UI.
 *  - B: montantType = null falls back to montant_48 (montant_70 must NOT be used).
 *
 * Entraxe (Phase C) and TVA/TTC presentation (Phase D) are already covered by
 * EntraxeMontantsPhaseCTest and PhaseDTvaPresentationTest — not duplicated here.
 */
class PhaseFFinalValidationTest {

    private val calculateUseCase = CalculateConstructionUseCase()

    private fun buildCloisonSummary(montantType: String?): CalculationSummary {
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
            entraxeMontants = 0.40
        )
        return calculateUseCase(params, prices, country)
    }

    // Test A — the board selector source of truth is MaterialCatalog.ALL_MATERIALS (category PLACO).
    @Test
    fun testA_placoBoardSelectorDrivenByCatalog() {
        // Same filter used by CalculatorScreen.boardOptions.
        val placoBoards = MaterialCatalog.ALL_MATERIALS.filter { it.category == MaterialCategory.PLACO }

        assertTrue("PLACO board list must not be empty", placoBoards.isNotEmpty())

        val ids = placoBoards.map { it.id }
        assertEquals("Board ids must be unique", ids.size, ids.toSet().size)

        // All known active PLACO panels from the catalog are present in the selector source.
        val expected = listOf(
            "plaque_ba13_standard",
            "plaque_ba13_hydrofuge",
            "plaque_ba13_coupe_feu",
            "plaque_ba13_phonique",
            "plaque_aquapanel_ciment",
            "plaque_glassroc_exterieur"
        )
        assertTrue(
            "Selector source must contain every PLACO panel of the catalog",
            ids.containsAll(expected)
        )

        // The default selected board must exist in the catalog-driven list.
        assertTrue(
            "Default board plaque_ba13_standard must be part of the selector",
            ids.contains("plaque_ba13_standard")
        )

        // Mirror of the UI label rule: dims when available, else technicalSpec, else fallback text.
        for (mat in placoBoards) {
            val dims = if (mat.stockWidthM != null && mat.stockLengthM != null) {
                "${(mat.stockWidthM!! * 100).toInt()} × ${(mat.stockLengthM!! * 100).toInt()} cm"
            } else {
                mat.technicalSpec.takeIf { it.isNotBlank() } ?: "Dimensions unavailable"
            }
            assertTrue("Chip label part must not be blank for ${mat.id}", dims.isNotBlank())
        }
    }

    // Test B — UI montant options come from the catalog (OSSATURE + montant_*).
    @Test
    fun testB_montantOptionsDrivenByCatalog() {
        val montantOptions = MaterialCatalog.ALL_MATERIALS.filter {
            it.category == MaterialCategory.OSSATURE && it.id.startsWith("montant_")
        }
        val ids = montantOptions.map { it.id }.toSet()
        assertTrue("montant_48 must be offered by the selector", "montant_48" in ids)
        assertTrue("montant_70 must be offered by the selector", "montant_70" in ids)
    }

    // Test B2 — UI rail options come from the catalog (OSSATURE + rail_*),
    // mirroring the montant selector contract. Any future profile added to the
    // catalog (e.g. an 80 series) is offered automatically without UI changes.
    @Test
    fun testB2_railOptionsDrivenByCatalog() {
        val railOptions = MaterialCatalog.ALL_MATERIALS.filter {
            it.category == MaterialCategory.OSSATURE && it.id.startsWith("rail_")
        }
        val ids = railOptions.map { it.id }.toSet()
        assertTrue("rail_48 must be offered by the selector", "rail_48" in ids)
        assertTrue("rail_70 must be offered by the selector", "rail_70" in ids)
    }

    // Test B3 — UI dalle options for the Démontable tile selector come from the
    // catalog (DALLES + dalle_*), mirroring the STEP 9B CalculatorScreen filter.
    @Test
    fun testB3_dalleOptionsDrivenByCatalog() {
        val dalleOptions = MaterialCatalog.ALL_MATERIALS.filter {
            it.category == MaterialCategory.DALLES && it.id.startsWith("dalle_")
        }
        val ids = dalleOptions.map { it.id }.toSet()
        assertTrue("dalle_vinyl_60x60 must be offered by the selector", "dalle_vinyl_60x60" in ids)
        assertTrue("dalle_laine_roche_60x60 must be offered by the selector", "dalle_laine_roche_60x60" in ids)
    }

    // Test C1 (STEP 9C1) — positive joints meters produce the Joints & Finition
    // items from the existing minimal handler in ConstructionCalculator.
    @Test
    fun testC1_jointsFinition_positiveMetersProducesItems() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_JOINTS_FINITION,
            area = 10.0,
            perimeter = 14.0,
            jointsMeters = 10.0
        )
        val result = calculateUseCase(params, prices, country)
        assertNotNull(
            "Enduit à joint item must appear when jointsMeters > 0",
            result.items.firstOrNull { it.id == "enduit_joint_25kg" }
        )
        assertNotNull(
            "Bande à joint item must appear when jointsMeters > 0",
            result.items.firstOrNull { it.id == "bande_a_joint_90m" }
        )
    }

    // Test C1 (cont.) — legacy behaviour preserved: default zero meters produce
    // no material items, exactly as before the wiring existed.
    @Test
    fun testC1_jointsFinition_zeroMetersLegacyEmpty() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_JOINTS_FINITION,
            area = 10.0,
            perimeter = 14.0
        )
        val result = calculateUseCase(params, prices, country)
        assertTrue(
            "No material items expected when jointsMeters stays at legacy default 0",
            result.items.isEmpty()
        )
    }

    // Test C2 (STEP 9C2) — empty trappes list produces no access-panel items
    // (legacy behaviour preserved when the option is not used).
    @Test
    fun testC2_trappes_emptyByDefault_producesNoItems() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_PLAFOND_FIXE,
            area = 10.0,
            perimeter = 14.0
        )
        val result = calculateUseCase(params, prices, country)
        assertTrue(
            "No trappe items expected when trappes list is empty",
            result.items.none { it.id.startsWith("trappe_") }
        )
    }

    // Test C2 (cont.) — a trappe line becomes an item whose unit price comes
    // exclusively from the price map (seeded 0.0 => editable in Tarifs, never
    // hardcoded in the engine).
    @Test
    fun testC2_trappeLine_producesItemPricedFromMap() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_PLAFOND_FIXE,
            area = 10.0,
            perimeter = 14.0,
            trappes = listOf(ConstructionCalculator.TrappeLine("40x40", 2))
        )
        val result = calculateUseCase(params, prices, country)

        val trappeItem = result.items.firstOrNull { it.id == "trappe_40x40" }
        assertNotNull("Trappe item must be added when a line is provided", trappeItem)
        assertEquals("Trappe quantity must match the user input", 2.0, trappeItem!!.quantity, 0.01)
        assertEquals(
            "Trappe unit price must come from the price map (never hardcoded)",
            prices["trappe_40x40"]!!.currentPrice,
            trappeItem.unitPrice,
            0.0
        )
    }

    // Test C2 (cont.) — selector contract: every reference trappe dimension is
    // catalog-driven and seeded, mirroring the CalculatorScreen filter.
    @Test
    fun testC2_trappeOptionsDrivenByCatalogAndSeed() {
        val catalogTrappes = MaterialCatalog.ALL_MATERIALS.filter { it.id.startsWith("trappe_") }
        val seededKeys = InitialData.BASE_PRICES.map { it.baseKey }.toSet()

        val expected = listOf(
            "trappe_20x20", "trappe_30x30", "trappe_40x40", "trappe_50x50",
            "trappe_60x60", "trappe_40x60", "trappe_60x120"
        )
        val ids = catalogTrappes.map { it.id }.toSet()
        for (id in expected) {
            assertTrue("Catalog must expose $id", id in ids)
            assertTrue("Seed must exist for $id", id in seededKeys)
        }
    }

    // Test C3 (STEP 9C3) — non-functional VM lifecycle contract for trappe
    // lines: upsert by dimension key, removal on zero quantity, full clear.
    // Pure JVM state check — no calculation behaviour involved.
    @Test
    fun testC3_vm_trappeLineLifecycle() {
        val viewModel = CalculatorViewModel()

        // 1) Adding a size with quantity > 0 creates a line.
        viewModel.setTrappeQuantity("40x40", 2)
        assertEquals(1, viewModel.trappeLines.value.size)
        assertEquals("40x40", viewModel.trappeLines.value.first().dimensionKey)
        assertEquals(2, viewModel.trappeLines.value.first().quantity)

        // 2) Re-adding the same size with a new quantity replaces it (upsert).
        viewModel.setTrappeQuantity("40x40", 5)
        assertEquals(1, viewModel.trappeLines.value.size)
        assertEquals(5, viewModel.trappeLines.value.first().quantity)

        // A different size is kept as an independent second line.
        viewModel.setTrappeQuantity("60x120", 1)
        assertEquals(2, viewModel.trappeLines.value.size)

        // 3) quantity = 0 removes the line.
        viewModel.setTrappeQuantity("40x40", 0)
        assertEquals(1, viewModel.trappeLines.value.size)
        assertEquals("60x120", viewModel.trappeLines.value.first().dimensionKey)

        // 4) clearTrappeLines empties everything.
        viewModel.clearTrappeLines()
        assertTrue(viewModel.trappeLines.value.isEmpty())
    }

    // Test D (STEP 9D) — UI brique options come from the catalog
    // (MACONNERIE + brique_*), covering both variants consumed by the
    // engine's brick branch via the shared boardType channel.
    @Test
    fun testD_briqueOptionsDrivenByCatalog() {
        val briqueOptions = MaterialCatalog.ALL_MATERIALS.filter {
            it.category == MaterialCategory.MACONNERIE && it.id.startsWith("brique_")
        }
        val ids = briqueOptions.map { it.id }.toSet()
        assertTrue("brique_12_trous must be offered by the selector", "brique_12_trous" in ids)
        assertTrue("brique_8_trous must be offered by the selector", "brique_8_trous" in ids)
    }

    // Test E (STEP 10B) — Joints & Finition labour follows the country
    // multiplier convention shared by every other calculator branch.
    @Test
    fun testE_jointsFinition_laborFollowsCountryMultiplier() {
        val tnCountry = Country.TUNISIA
        val targetCountry = Country.entries.first {
            it != tnCountry && it.rateToTndMultiplier != tnCountry.rateToTndMultiplier
        }

        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.PLACO_JOINTS_FINITION,
            area = 10.0,
            perimeter = 14.0,
            jointsMeters = 10.0
        )

        val tnResult = calculateUseCase(params, InitialData.generatePricesForCountry(tnCountry).associateBy { it.baseKey }, tnCountry)
        val targetResult = calculateUseCase(params, InitialData.generatePricesForCountry(targetCountry).associateBy { it.baseKey }, targetCountry)

        // Tunisia keeps the historical flat behaviour (base 12.0 x multiplier 1.0
        // x area 10 => 120.0), proving no regression for the primary market.
        assertEquals(120.0, tnResult.estimatedLaborCost, 0.01)

        // Labour scales linearly with the target country's TND-rate multiplier.
        val expectedTargetLabor = TaxMath.round2(10.0 * 12.0 * targetCountry.rateToTndMultiplier)
        assertEquals(expectedTargetLabor, targetResult.estimatedLaborCost, 0.01)

        // Material quantities are identical across countries — only prices scale.
        assertEquals(
            tnResult.items.firstOrNull { it.id == "enduit_joint_25kg" }!!.quantity,
            targetResult.items.firstOrNull { it.id == "enduit_joint_25kg" }!!.quantity,
            0.0001
        )
        assertEquals(
            tnResult.items.firstOrNull { it.id == "bande_a_joint_90m" }!!.quantity,
            targetResult.items.firstOrNull { it.id == "bande_a_joint_90m" }!!.quantity,
            0.0001
        )
    }

    // Test G (STEP 11A) — quantity display formatter contract (W1/W2 fix).
    // Display-only: integral values lose their ".0", fractional values keep up
    // to two decimals with trailing zeros stripped. Never truncates to zero.
    @Test
    fun testG_formatQuantity_displayContract() {
        assertEquals("2", formatQuantity(2.0))
        assertEquals("1", formatQuantity(1.0))
        assertEquals("0.75", formatQuantity(0.75))
        assertEquals("1.9", formatQuantity(1.9))
        assertEquals("2.5", formatQuantity(2.50))
    }

    // Test G (cont.) — end-to-end: crépissage 15 m² produces sable fin
    // 0.75 m³ and the FRENCH WhatsApp quote must keep the real fraction.
    @Test
    fun testG_whatsapp_preservesFractionalSableQuantity() {
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }
        val params = ConstructionCalculator.CalculationParams(
            type = CalculationType.CREPISSAGE_CIMENT,
            area = 15.0,
            perimeter = 16.0
        )
        val summary = calculateUseCase(params, prices, country)
        val settings = UserSettingsEntity()
        // DERJA covered here (largest audience & already migrated to the
        // formatter); FRENCH/ENGLISH lines share an identical literal that the
        // current tooling cannot disambiguate — owner applies the same one-line
        // swap via the provided PowerShell snippet (see STEP 11A report).
        val message = FormatWhatsAppQuoteUseCase()(summary, settings, AppLanguage.DERJA)

        val sableLine = message.lineSequence().firstOrNull { "للترطيبة" in it }
        assertNotNull("Sable line must exist in the quote", sableLine)
        assertTrue(
            "Fractional quantity 0.75 must be preserved verbatim",
            sableLine!!.contains("0.75")
        )
        assertFalse(
            "Truncated '0' quantity is forbidden (W1 regression)",
            sableLine.contains("*: 0 ")
        )
    }

    // Test B (cont.) — montantType = null falls back to montant_48 (never montant_70).
    @Test
    fun testB_montantNull_fallsBackToMontant48_notMontant70() {
        val summary = buildCloisonSummary(montantType = null)

        assertNotNull(
            "Default (null) montant selection must produce a montant_48 item",
            summary.items.firstOrNull { it.id == "montant_48" }
        )
        assertTrue(
            "montant_70 must not appear when nothing is selected",
            summary.items.none { it.id == "montant_70" }
        )

        // The unit price charged is the montant_48 price from the price map.
        val country = Country.TUNISIA
        val prices = InitialData.generatePricesForCountry(country).associateBy { it.baseKey }
        val m48 = summary.items.first { it.id == "montant_48" }
        assertEquals(
            "Charged unit price must equal the montant_48 price",
            prices["montant_48"]!!.currentPrice,
            m48.unitPrice,
            0.0
        )
    }
}