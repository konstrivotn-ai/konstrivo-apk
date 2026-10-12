package com.example.domain

import com.example.data.local.InitialData
import com.example.data.model.AppLanguage
import com.example.data.model.Country
import com.example.domain.currency.DefaultExchangeRateProvider
import com.example.domain.model.MarketContext
import com.example.domain.model.MaterialPrice
import com.example.domain.model.PriceHistoryRecord
import com.example.domain.model.PriceSource
import com.example.domain.model.StandardUnit
import org.junit.Assert.*
import org.junit.Test

class PriceDomainTest {

    @Test
    fun testPriceSource_definitionsAndLocalization() {
        for (source in PriceSource.entries) {
            for (lang in AppLanguage.entries) {
                val name = source.localizedName(lang)
                assertNotNull("PriceSource name must not be null for $source in $lang", name)
                assertTrue("PriceSource name must not be blank for $source in $lang", name.isNotBlank())
            }
            assertTrue("PriceSource ID must not be blank for $source", source.id.isNotBlank())
        }
        assertTrue("Official BTP must be flagged as official", PriceSource.OFFICIAL_BTP_MERCATO.isOfficial)
        assertFalse("Market 2026 is benchmark, not government official", PriceSource.MARKET_BENCHMARK_2026.isOfficial)
        assertFalse("Supplier quote is not government official", PriceSource.SUPPLIER_QUOTE.isOfficial)
    }

    @Test
    fun testMaterialPrice_dataModelIntegrity() {
        val price = MaterialPrice(
            id = "TN_plaque_ba13_standard",
            materialId = "plaque_ba13_standard",
            countryCode = "TN",
            region = "Grand Tunis",
            currency = "TND",
            defaultPrice = 30.0,
            currentPrice = 30.0,
            unit = StandardUnit.PIECE,
            source = PriceSource.MARKET_BENCHMARK_2026,
            supplierName = "Quincaillerie Ben Salem",
            effectiveFrom = 1735689600000L // 2026-01-01
        )

        assertEquals("TN_plaque_ba13_standard", price.id)
        assertEquals("plaque_ba13_standard", price.materialId)
        assertEquals("TN", price.countryCode)
        assertEquals("Grand Tunis", price.region)
        assertEquals(30.0, price.currentPrice, 0.001)
        assertEquals(StandardUnit.PIECE, price.unit)
        assertEquals(PriceSource.MARKET_BENCHMARK_2026, price.source)
    }

    @Test
    fun testPriceHistoryRecord_dataModel() {
        val historyRecord = PriceHistoryRecord(
            id = 1L,
            materialId = "plaque_ba13_standard",
            countryCode = "TN",
            region = "Sfax",
            recordedPrice = 32.0,
            currency = "TND",
            source = PriceSource.USER_OVERRIDE,
            recordedTimestamp = System.currentTimeMillis(),
            note = "Price update on site"
        )

        assertEquals(1L, historyRecord.id)
        assertEquals("plaque_ba13_standard", historyRecord.materialId)
        assertEquals(32.0, historyRecord.recordedPrice, 0.001)
        assertEquals(PriceSource.USER_OVERRIDE, historyRecord.source)
    }

    @Test
    fun testMarketContext_formattingAndDefaults() {
        val tnContext = MarketContext.forCountry(Country.TUNISIA, "Ariana")
        assertEquals("TN", tnContext.country.code)
        assertEquals("Ariana", tnContext.region)
        assertEquals("TND", tnContext.currencyCode)
        assertEquals("د.ت", tnContext.currencySymbol)
        assertEquals(12.0, tnContext.defaultLaborRatePerM2, 0.001)
        assertEquals("150.00 د.ت", tnContext.formatPrice(150.0))
        assertEquals("25.00 m²", tnContext.formatArea(25.0))
        assertEquals("18.00 ml", tnContext.formatPerimeter(18.0))

        val frContext = MarketContext.forCountry(Country.FRANCE, "Lyon")
        assertEquals("FR", frContext.country.code)
        assertEquals("Lyon", frContext.region)
        assertEquals("EUR", frContext.currencyCode)
        assertEquals("€", frContext.currencySymbol)
        assertEquals(28.0, frContext.defaultLaborRatePerM2, 0.001)
    }

    @Test
    fun testExchangeRateProvider_conversionLogic() {
        val converter = DefaultExchangeRateProvider
        // 100 TND in EUR (100 * 1.0 / 3.3333 ~ 30.0 EUR)
        val inEur = converter.convert(100.0, "TND", "EUR")
        assertEquals(30.0, inEur, 0.5)

        // Same currency returns exact same amount
        assertEquals(150.0, converter.convert(150.0, "TND", "TND"), 0.001)
        assertEquals(50.0, converter.convert(50.0, "EUR", "EUR"), 0.001)
    }
}
