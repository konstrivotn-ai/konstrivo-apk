package com.example.calculator

import com.example.domain.paint.PaintPackage
import com.example.domain.paint.PaintPackageCatalog
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PaintPackageTest {

    @Before
    fun setup() {
        PaintPackageCatalog.clear()
        PaintPackageCatalog.addReferenceData()
    }

    @Test
    fun testValid5kgPackage() {
        val p = PaintPackageCatalog.getById("ASTRAL_AQUASTRAL_SATIN_5KG")
        assertNotNull(p)
        assertEquals(5.0, p!!.containerWeightKg!!, 0.0001)
    }

    @Test
    fun testValid20kgPackage() {
        val p = PaintPackageCatalog.getById("ASTRAL_AQUASTRAL_SATIN_20KG")
        assertNotNull(p)
        assertEquals(20.0, p!!.containerWeightKg!!, 0.0001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidBothVolumeAndWeight() {
        PaintPackage(
            packageId = "BAD_BOTH",
            productId = "ASTRAL_AQUASTRAL_SATIN",
            containerVolumeL = 4.0,
            containerWeightKg = 5.0
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidNeitherVolumeNorWeight() {
        PaintPackage(
            packageId = "BAD_NONE",
            productId = "ASTRAL_AQUASTRAL_SATIN"
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidZeroSize() {
        PaintPackage(
            packageId = "BAD_ZERO",
            productId = "ASTRAL_AQUASTRAL_SATIN",
            containerWeightKg = 0.0
        )
    }

    @Test
    fun testLookupByProductId() {
        val list = PaintPackageCatalog.getByProductId("ASTRAL_AQUASTRAL_SATIN")
        assertTrue(list.isNotEmpty())
        assertTrue(list.any { it.packageId == "ASTRAL_AQUASTRAL_SATIN_5KG" })
    }

    @Test
    fun testActiveCountryFiltering() {
        val tn = PaintPackageCatalog.getActiveByCountry("TN")
        assertTrue(tn.any { it.packageId == "ASTRAL_AQUASTRAL_SATIN_5KG" })
        assertTrue(tn.any { it.packageId == "ASTRAL_AQUASTRAL_SATIN_20KG" })
    }

    @Test
    fun testPackageIdsUnique() {
        val allIds = PaintPackageCatalog.getAll().map { it.packageId }
        assertEquals(allIds.size, allIds.toSet().size)
    }
}
