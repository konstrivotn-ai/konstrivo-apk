package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorParityDefaultsTest {
    @Test
    fun everyNativeCalculationTypeDefaultsToWebsiteWasteMargin() {
        CalculationType.entries.forEach { type ->
            assertEquals("Unexpected default waste margin for ${type.id}", 1.10, type.defaultWasteMargin, 0.000001)
        }
    }
}
