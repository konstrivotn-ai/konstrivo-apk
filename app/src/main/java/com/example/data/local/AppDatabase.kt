package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MaterialPriceEntity::class,
        ProjectEstimateEntity::class,
        UserSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materialPriceDao(): MaterialPriceDao
    abstract fun projectEstimateDao(): ProjectEstimateDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "placo_tunisie_btp.db"
                )
                        .addCallback(DatabaseCallback(scope))
                        .addMigrations(MIGRATION_1_2)
                        .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        // STEP 9A — Idempotent price top-up.
                        // insertInitialPrices uses OnConflictStrategy.IGNORE, so
                        // re-running it on EVERY open only ADDS rows that are
                        // missing from databases created by older app versions.
                        // Existing rows — including any user-modified prices —
                        // are never overwritten or deleted, and repeating this
                        // on every open is safe and cheap.
                        database.materialPriceDao()
                            .insertInitialPrices(InitialData.getAllInitialPrices())
                    }
                }
            }

            private suspend fun populateDatabase(db: AppDatabase) {
                val priceDao = db.materialPriceDao()
                val settingsDao = db.userSettingsDao()

                val initialPrices = InitialData.getAllInitialPrices()
                priceDao.insertInitialPrices(initialPrices)

                if (settingsDao.getSettingsSync() == null) {
                    settingsDao.insertOrUpdate(
                        UserSettingsEntity(
                            id = 1,
                            selectedCountry = "TN",
                            selectedLanguage = "derja",
                            defaultWasteMargin = 5.0,
                            defaultLaborRatePerM2 = 12.0,
                            contractorName = "معلم بلاكو بلاتر والشانتي",
                            contractorPhone = "",
                            workshopLocation = "تونس (Tunisie)",
                            companyHeader = "Placo Tunisie - خدمات الجبس والديكور"
                        )
                    )
                }
            }
        }
    }
}
