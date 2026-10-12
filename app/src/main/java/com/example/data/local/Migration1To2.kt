package com.example.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room Migration 1 -> 2: Add nullable packageId column to material_prices.
 */
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add nullable packageId column. Existing rows default to NULL.
        database.execSQL("ALTER TABLE material_prices ADD COLUMN packageId TEXT")
        // Optional index to speed package lookups by country. Add only if needed.
        database.execSQL("CREATE INDEX IF NOT EXISTS idx_material_prices_country_package ON material_prices(countryCode, packageId)")
    }
}
