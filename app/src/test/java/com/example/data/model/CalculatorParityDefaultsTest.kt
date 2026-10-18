package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.domain.trade.TradeCatalog

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
}
