package com.example.calculator

import com.example.domain.paint.PaintPackageCatalog
import com.example.domain.paint.PaintProductCatalog
import org.junit.Assert.*
import org.junit.Test

class ReferenceVsTestDataSeparationTest {

    @Test
    fun testReferenceDataDoesNotContainTestFixtures() {
        PaintProductCatalog.clear()
        PaintProductCatalog.addReferenceData()

        // Reference data should not contain TEST_* demo ids
        val all = PaintProductCatalog.getAll().map { it.productId }
        assertFalse(all.any { it.startsWith("TEST_") })
    }

    @Test
    fun testTestDataDoesNotContainReferenceProducts() {
        PaintProductCatalog.clear()
        PaintProductCatalog.addTestData()

        val all = PaintProductCatalog.getAll().map { it.productId }
        // Test data should not contain verified ASTRAL ids
        assertFalse(all.contains("ASTRAL_AQUASTRAL_SATIN"))
        assertFalse(all.contains("ASTRAL_LAKALO"))
    }

    @Test
    fun testPackageReferenceDataIsolation() {
        PaintPackageCatalog.clear()
        PaintPackageCatalog.addReferenceData()

        val all = PaintPackageCatalog.getAll().map { it.packageId }
        assertTrue(all.contains("ASTRAL_AQUASTRAL_SATIN_5KG"))
        assertFalse(all.contains("ASTRAL_LAKALO_4L"))
    }
}
