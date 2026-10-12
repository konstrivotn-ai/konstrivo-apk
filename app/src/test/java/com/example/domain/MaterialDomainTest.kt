package com.example.domain

import com.example.data.local.InitialData
import com.example.data.model.AppLanguage
import com.example.data.model.TradeCategory
import com.example.domain.material.MaterialCatalog
import com.example.domain.model.MaterialCategory
import com.example.domain.model.StandardUnit
import org.junit.Assert.*
import org.junit.Test

class MaterialDomainTest {

    @Test
    fun testMaterialCatalog_allIdsUnique() {
        val ids = MaterialCatalog.ALL_MATERIALS.map { it.id }
        val uniqueIds = ids.toSet()
        assertEquals("All material IDs must be strictly unique", ids.size, uniqueIds.size)
        assertTrue("Material catalog should contain at least 40 materials", ids.size >= 40)
    }

    @Test
    fun testMaterialCatalog_basePricesMatchCatalogIds() {
        val catalogIds = MaterialCatalog.allIds
        for (raw in InitialData.BASE_PRICES) {
            assertTrue("Base price key '${raw.baseKey}' must exist in MaterialCatalog", catalogIds.contains(raw.baseKey))
        }
    }

    @Test
    fun testMaterialCatalog_everyCatalogMaterialHasSeedPrice() {
        // STEP 9A — Reverse parity guard: every MaterialCatalog entry must have a
        // matching InitialData.BASE_PRICES seed so the idempotent top-up seeding
        // in AppDatabase.onOpen can always materialize a priced row for it on
        // devices upgrading from older app versions.
        val seededKeys = InitialData.BASE_PRICES.map { it.baseKey }.toSet()
        val missingSeeds = MaterialCatalog.ALL_MATERIALS.map { it.id }.filterNot(seededKeys::contains)
        assertTrue(
            "Every MaterialCatalog material must have an InitialData seed price. Missing: $missingSeeds",
            missingSeeds.isEmpty()
        )
    }

    @Test
    fun testStandardUnits_allLocalizedProperly() {
        for (unit in StandardUnit.entries) {
            for (lang in AppLanguage.entries) {
                val localized = unit.localizedName(lang)
                assertNotNull("Localized unit must not be null for $unit in $lang", localized)
                assertTrue("Localized unit must not be blank for $unit in $lang", localized.isNotBlank())
            }
            assertTrue("Standard symbol must not be blank for $unit", unit.standardSymbol.isNotBlank())
            assertTrue("Unit ID must not be blank for $unit", unit.id.isNotBlank())
        }
    }

    @Test
    fun testStandardUnit_fromRawParser() {
        assertEquals(StandardUnit.BOX_1000, StandardUnit.fromRaw("علبة 1000 (Boîte)"))
        assertEquals(StandardUnit.BOX_500, StandardUnit.fromRaw("علبة 500 (Boîte)"))
        assertEquals(StandardUnit.BOX_200, StandardUnit.fromRaw("علبة 200 (Boîte)"))
        assertEquals(StandardUnit.BOX_7_2M2, StandardUnit.fromRaw("باكي 7.2m² (Paquet)"))
        assertEquals(StandardUnit.ROLL_90M, StandardUnit.fromRaw("لفة 90m (Rouleau)"))
        assertEquals(StandardUnit.ROLL_50M, StandardUnit.fromRaw("لفة 50m (Rouleau)"))
        assertEquals(StandardUnit.ROLL_30M, StandardUnit.fromRaw("لفة 30m (Rouleau)"))
        assertEquals(StandardUnit.ROLL_15M2, StandardUnit.fromRaw("لفة 15m² (Rouleau)"))
        assertEquals(StandardUnit.ROLL_10M, StandardUnit.fromRaw("لفة 10m (Rouleau)"))
        assertEquals(StandardUnit.BUCKET_20L, StandardUnit.fromRaw("سطال 20L (Bidon)"))
        assertEquals(StandardUnit.BUCKET_15L, StandardUnit.fromRaw("سطال 15L (Bidon)"))
        assertEquals(StandardUnit.BUCKET_5KG, StandardUnit.fromRaw("سطال 5kg (Bidon)"))
        assertEquals(StandardUnit.BAG_50KG, StandardUnit.fromRaw("كيس 50kg (Sac)"))
        assertEquals(StandardUnit.BAG_25KG, StandardUnit.fromRaw("كيس 25kg (Sac)"))
        assertEquals(StandardUnit.BAG_5KG, StandardUnit.fromRaw("كيس 5kg (Sac)"))
        assertEquals(StandardUnit.CARTRIDGE, StandardUnit.fromRaw("خرطوشة (Cartouche)"))
        assertEquals(StandardUnit.AEROSOL_750ML, StandardUnit.fromRaw("قارورة 750ml (Aérosol)"))
        assertEquals(StandardUnit.CUBIC_METER, StandardUnit.fromRaw("متر مكعب (m³)"))
        assertEquals(StandardUnit.PIECE, StandardUnit.fromRaw("لوح (Plaque)"))
        assertEquals(StandardUnit.PIECE, StandardUnit.fromRaw("قطعة 3م (Pièce)"))
    }

    @Test
    fun testMaterialCategory_allLocalizedProperly() {
        for (category in MaterialCategory.entries) {
            for (lang in AppLanguage.entries) {
                val name = category.localizedName(lang)
                assertNotNull("Category name must not be null for $category in $lang", name)
                assertTrue("Category name must not be blank for $category in $lang", name.isNotBlank())
            }
            assertTrue("Category ID must not be blank for $category", category.id.isNotBlank())
            assertTrue("Category icon must not be blank for $category", category.iconEmoji.isNotBlank())
        }
    }

    @Test
    fun testMaterialCatalog_queriesByCategoryAndTrade() {
        val placoMaterials = MaterialCatalog.getByTrade(TradeCategory.PLACO)
        assertTrue("Placo trade should have multiple materials", placoMaterials.size >= 15)

        val paintingMaterials = MaterialCatalog.getByTrade(TradeCategory.PEINTURE)
        assertTrue("Peinture trade should have materials", paintingMaterials.size >= 4)

        val placoPanels = MaterialCatalog.getByCategory(MaterialCategory.PLACO)
        assertTrue("Placo panels category should have items", placoPanels.isNotEmpty())

        val masonryMaterials = MaterialCatalog.getByCategory(MaterialCategory.MACONNERIE)
        assertTrue("Masonry materials should exist", masonryMaterials.isNotEmpty())
    }
}
