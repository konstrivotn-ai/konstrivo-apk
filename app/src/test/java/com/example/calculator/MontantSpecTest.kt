package com.example.calculator

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.ceil
import kotlin.math.floor

class MontantSpecTest {

    @Test
    fun studs_positions_and_pieces_with_various_stock_lengths() {
        val length = 7.3
        val height = 3.8
        val entraxe = 0.40

        // positions (number of studs locations)
        val studsPositions = floor(length / entraxe).toInt() + 1
        assertEquals(19, studsPositions)

        val tallCoupling = if (height > 3.0) 1.1 else 1.0
        val montantLinearMeters = studsPositions * height * tallCoupling // linear meters required

        // pieces if stock length is 3.0 m
        val piecesFor3m = ceil(montantLinearMeters / 3.0)
        // pieces if using current code divisor (2.5 m)
        val piecesFor2_5m = ceil(montantLinearMeters / 2.5)

        // Expected from calculation
        assertEquals(27.0, piecesFor3m, 0.0)
        assertEquals(32.0, piecesFor2_5m, 0.0)
    }
}
