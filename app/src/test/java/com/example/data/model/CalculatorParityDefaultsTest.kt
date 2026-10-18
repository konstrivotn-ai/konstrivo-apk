package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.domain.trade.TradeCatalog
import com.example.calculator.ConstructionCalculator

class CalculatorParityDefaultsTest {
    @Test
    fun everyNativeCalculationTypeDefaultsToWebsiteWasteMargin() {
        CalculationType.entries.forEach { type ->
            assertEquals("Unexpected default waste margin for ${type.id}", 1.10, type.defaultWasteMargin, 0.000001)
        }
    }

    @Test
    fun websiteTradeCategoriesAreRegisteredAndActive() {
        val expectedIds = setOf(
            "placo", "peinture", "carrelage", "maconnerie", "plomberie",
            "electricite", "etancheite", "isolation", "menuiserie", "sols",
            "facade", "demolition"
        )
        val trades = TradeCatalog.getAllTrades()
        expectedIds.forEach { id ->
            val trade = trades.firstOrNull { it.id == id }
            assertEquals("Missing website trade: $id", true, trade != null)
            assertEquals("Website trade should be active: $id", true, trade!!.isAvailable)
            assertEquals("Website trade should have a mapped calculation: $id", true,
                trade.workTypes.any { it.calculationType != null && it.status.name == "ACTIVE" })
        }
    }

    @Test
    fun newlyAddedWebsiteTradeCalculatorsProduceMaterialLines() {
        val types = listOf(
            CalculationType.PLOMBERIE_INSTALLATION,
            CalculationType.ELECTRICITE_INSTALLATION,
            CalculationType.ETANCHEITE_TOITURE,
            CalculationType.ISOLATION_THERMIQUE,
            CalculationType.SOLS_PARQUET,
            CalculationType.FACADE_FINITION,
            CalculationType.DEMOLITION_DEPOSE
        )
        types.forEach { type ->
            val result = ConstructionCalculator.calculate(
                params = ConstructionCalculator.CalculationParams(
                    type = type,
                    area = 25.0,
                    perimeter = 20.0,
                    linearMeters = 20.0,
                    unitCount = 4.0,
                    wasteMarginPercent = 10.0
                ),
                priceMap = emptyMap(),
                country = Country.TUNISIA
            )
            assertEquals("Incorrect calculation type for ${type.id}", type, result.calculationType)
            assertEquals("Website trade should return material rows: ${type.id}", true, result.items.isNotEmpty())
            assertEquals("Waste margin should be 10%: ${type.id}", 10.0, result.wasteMarginPercent, 0.000001)
        }
    }

}
