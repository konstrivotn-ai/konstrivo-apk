package com.example.calculator

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.ceil

class BoardWasteSpecTest {

    @Test
    fun boardCount_with_5percent_waste_is_20_and_with_10percent_is_21() {
        val length = 7.3
        val height = 3.8
        val area = length * height // 27.74
        val faces = 2.0
        val boardArea = 3.0 // 1.2 x 2.5

        val with5 = ceil((area * faces * 1.05) / boardArea)
        val with10 = ceil((area * faces * 1.10) / boardArea)

        assertEquals(20.0, with5, 0.0)
        assertEquals(21.0, with10, 0.0)
    }
}
