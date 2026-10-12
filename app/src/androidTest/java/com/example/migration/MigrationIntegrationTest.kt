package com.example.migration

import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.example.data.local.MIGRATION_1_2
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * Instrumented integration test for Room migration 1 -> 2.
 * Uses real SupportSQLiteDatabase via FrameworkSQLiteOpenHelperFactory.
 */
class MigrationIntegrationTest {

    private val TEST_DB = "migration-test.db"

    @After
    fun cleanup() {
        val f = File(androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>().getDatabasePath(TEST_DB).absolutePath)
        if (f.exists()) f.delete()
    }

    @Test
    fun migration_1_to_2_adds_packageId_and_preserves_data() {
        // Step 1: create legacy DB using raw SQL (schema as in version=1)
        val helperFactory = FrameworkSQLiteOpenHelperFactory()
        val config = androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(
            androidx.test.core.app.ApplicationProvider.getApplicationContext()
        )
            .name(TEST_DB)
            .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS `material_prices` (`id` TEXT NOT NULL, `baseKey` TEXT NOT NULL, `countryCode` TEXT NOT NULL, `category` TEXT NOT NULL, `nameFr` TEXT NOT NULL, `nameAr` TEXT NOT NULL, `nameDerja` TEXT NOT NULL, `unit` TEXT NOT NULL, `defaultPrice` REAL NOT NULL, `currentPrice` REAL NOT NULL, `currency` TEXT NOT NULL, `isCustom` INTEGER NOT NULL, `brandInfo` TEXT NOT NULL, PRIMARY KEY(`id`))")
                }

                override fun onUpgrade(db: androidx.sqlite.db.SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                }
            }).build()

        val openHelper = helperFactory.create(config)
        val db = openHelper.writableDatabase

        // Insert a legacy material price row
        val insertSql = "INSERT INTO material_prices (id, baseKey, countryCode, category, nameFr, nameAr, nameDerja, unit, defaultPrice, currentPrice, currency, isCustom, brandInfo) VALUES ('TN_paint_base','paint_base','TN','peinture','Peinture Base','','','L',100.0,100.0,'TND',0,'Brand')"
        db.execSQL(insertSql)
        db.close()

        // Step 2: re-open and run migration
        val helperFactory2 = FrameworkSQLiteOpenHelperFactory()
        val config2 = androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(
            androidx.test.core.app.ApplicationProvider.getApplicationContext()
        )
            .name(TEST_DB)
            .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {}
                override fun onUpgrade(db: androidx.sqlite.db.SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            }).build()
        val openHelper2 = helperFactory2.create(config2)
        val supportDb = openHelper2.writableDatabase

        // Execute Migration 1 -> 2 directly against the SupportSQLiteDatabase
        MIGRATION_1_2.migrate(supportDb)

        // Verify: packageId column exists
        val cursor = supportDb.query("PRAGMA table_info('material_prices')")
        var hasPackageColumn = false
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            if (name == "packageId") {
                hasPackageColumn = true
                break
            }
        }
        cursor.close()
        assertTrue("packageId column should exist after migration", hasPackageColumn)

        // Verify legacy row preserved and packageId is null
        val cursor2 = supportDb.query("SELECT id, baseKey, countryCode, packageId FROM material_prices WHERE id = ?", arrayOf("TN_paint_base"))
        assertTrue(cursor2.moveToFirst())
        assertEquals("TN_paint_base", cursor2.getString(cursor2.getColumnIndexOrThrow("id")))
        assertEquals("paint_base", cursor2.getString(cursor2.getColumnIndexOrThrow("baseKey")))
        assertEquals("TN", cursor2.getString(cursor2.getColumnIndexOrThrow("countryCode")))
        val pkgIndex = cursor2.getColumnIndex("packageId")
        assertTrue(cursor2.isNull(pkgIndex))
        cursor2.close()

        // Insert a new package-specific row after migration
        supportDb.execSQL("INSERT INTO material_prices (id, baseKey, countryCode, category, nameFr, nameAr, nameDerja, unit, defaultPrice, currentPrice, currency, isCustom, brandInfo, packageId) VALUES ('TN_paint_pkg1','paint_base','TN','peinture','Peinture Pkg','','','L',95.0,95.0,'TND',0,'Brand','TEST_PACKAGE_5KG')")

        val cursor3 = supportDb.query("SELECT id, packageId FROM material_prices WHERE packageId = ?", arrayOf("TEST_PACKAGE_5KG"))
        assertTrue(cursor3.moveToFirst())
        assertEquals("TEST_PACKAGE_5KG", cursor3.getString(cursor3.getColumnIndexOrThrow("packageId")))
        cursor3.close()

        openHelper2.close()
    }
}
