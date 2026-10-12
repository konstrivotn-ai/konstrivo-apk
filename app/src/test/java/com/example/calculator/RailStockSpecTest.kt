package com.example.calculator

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.ceil

class RailStockSpecTest {

    @Test
    fun railStockPieces_for_14_6m_with_3m_stock_is_5() {
        val totalRun = 7.3 * 2.0 // 14.6 m
        val stock = 3.0
        val pieces = ceil(totalRun / stock)
        assertEquals(5.0, pieces, 0.0)
    }
}
