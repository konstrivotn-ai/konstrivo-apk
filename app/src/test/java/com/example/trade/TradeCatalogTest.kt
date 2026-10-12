package com.example.trade

import com.example.data.model.AppLanguage
import com.example.data.model.CalculationType
import com.example.data.model.TradeCategory
import com.example.domain.model.TradeStatus
import com.example.domain.model.WorkTypeStatus
import com.example.domain.trade.TradeCatalog
import org.junit.Assert.*
import org.junit.Test

class TradeCatalogTest {

    @Test
    fun testTradeCatalog_allTradesHaveUniqueIds() {
        val trades = TradeCatalog.getAllTrades()
        val ids = trades.map { it.id }
        assertEquals("Trade IDs must be distinct", ids.size, ids.toSet().size)
    }

    @Test
    fun testTradeCatalog_allTradesHaveNonEmptyLocalizedFields() {
        val trades = TradeCatalog.getAllTrades()
        for (trade in trades) {
            assertTrue("Trade ${trade.id} nameFr is empty", trade.nameFr.isNotBlank())
            assertTrue("Trade ${trade.id} nameAr is empty", trade.nameAr.isNotBlank())
            assertTrue("Trade ${trade.id} nameDerja is empty", trade.nameDerja.isNotBlank())
            assertTrue("Trade ${trade.id} nameEn is empty", trade.nameEn.isNotBlank())
            assertTrue("Trade ${trade.id} iconEmoji is empty", trade.iconEmoji.isNotBlank())

            for (lang in AppLanguage.entries) {
                assertTrue("Trade ${trade.id} localizedName for $lang is blank", trade.localizedName(lang).isNotBlank())
                assertTrue("Trade ${trade.id} localizedDescription for $lang is blank", trade.localizedDescription(lang).isNotBlank())
                assertTrue("Trade ${trade.id} localizedStatusLabel for $lang is blank", trade.localizedStatusLabel(lang).isNotBlank())
            }
        }
    }

    @Test
    fun testTradeCatalog_allWorkTypesHaveUniqueIdsAndValidFields() {
        val trades = TradeCatalog.getAllTrades()
        val allWorkTypes = trades.flatMap { it.workTypes }
        val workTypeIds = allWorkTypes.map { it.id }
        assertEquals("WorkType IDs must be distinct across catalog", workTypeIds.size, workTypeIds.toSet().size)

        for (wt in allWorkTypes) {
            assertTrue("WorkType ${wt.id} nameFr is empty", wt.nameFr.isNotBlank())
            assertTrue("WorkType ${wt.id} nameAr is empty", wt.nameAr.isNotBlank())
            assertTrue("WorkType ${wt.id} nameDerja is empty", wt.nameDerja.isNotBlank())
            assertTrue("WorkType ${wt.id} unit is empty", wt.unit.isNotBlank())

            if (wt.status == WorkTypeStatus.ACTIVE) {
                assertNotNull("Active WorkType ${wt.id} must define a CalculationType", wt.calculationType)
            }

            for (lang in AppLanguage.entries) {
                assertTrue("WorkType ${wt.id} localizedName for $lang is blank", wt.localizedName(lang).isNotBlank())
                assertTrue("WorkType ${wt.id} localizedDescription for $lang is blank", wt.localizedDescription(lang).isNotBlank())
            }
        }
    }

    @Test
    fun testPlacoTrade_hasAllSixStandardWorkTypes() {
        val placo = TradeCatalog.getTradeById("placo")
        assertNotNull("Placo trade must exist in catalog", placo)
        assertEquals(TradeStatus.ACTIVE, placo!!.status)
        assertEquals(TradeCategory.PLACO, placo.category)
        assertEquals(6, placo.workTypes.size)

        val placoCalcTypes = placo.workTypes.mapNotNull { it.calculationType }
        assertTrue(placoCalcTypes.contains(CalculationType.PLACO_PLAFOND_FIXE))
        assertTrue(placoCalcTypes.contains(CalculationType.PLACO_CLOISON))
        assertTrue(placoCalcTypes.contains(CalculationType.PLACO_DEMONTABLE))
        assertTrue(placoCalcTypes.contains(CalculationType.PLACO_CACHE_LUMIERE))
        assertTrue(placoCalcTypes.contains(CalculationType.PLACO_AQUAPANEL))
        assertTrue(placoCalcTypes.contains(CalculationType.PLACO_PVC))
    }

    @Test
    fun testPeintureTrade_hasActiveCalculationTypes() {
        val peinture = TradeCatalog.getTradeById("peinture")
        assertNotNull("Peinture trade must exist in catalog", peinture)
        assertEquals(TradeStatus.ACTIVE, peinture!!.status)
        assertEquals(TradeCategory.PEINTURE, peinture.category)

        val activeWorkTypes = peinture.workTypes.filter { it.status == WorkTypeStatus.ACTIVE }
        assertEquals(2, activeWorkTypes.size)

        val activeCalcTypes = activeWorkTypes.map { it.calculationType }
        assertTrue(activeCalcTypes.contains(CalculationType.PEINTURE_INTERIEURE))
        assertTrue(activeCalcTypes.contains(CalculationType.PEINTURE_SABLE))
    }

    @Test
    fun testTradeCatalog_lookups() {
        val placoByCategory = TradeCatalog.getTradeByCategory(TradeCategory.PLACO)
        assertNotNull(placoByCategory)
        assertEquals("placo", placoByCategory!!.id)

        val activeTrades = TradeCatalog.getActiveTrades()
        assertTrue("Active trades must include at least Placo and Peinture", activeTrades.size >= 2)
        assertTrue(activeTrades.any { it.id == "placo" })
        assertTrue(activeTrades.any { it.id == "peinture" })
    }
}
