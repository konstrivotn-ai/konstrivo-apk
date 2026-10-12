package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialPriceDao {
    @Query("SELECT * FROM material_prices WHERE countryCode = :countryCode ORDER BY category, nameFr")
    fun getPricesByCountry(countryCode: String): Flow<List<MaterialPriceEntity>>

    @Query("SELECT * FROM material_prices WHERE countryCode = :countryCode AND category = :category ORDER BY nameFr")
    fun getPricesByCountryAndCategory(countryCode: String, category: String): Flow<List<MaterialPriceEntity>>

    @Query("SELECT * FROM material_prices WHERE countryCode = :countryCode")
    suspend fun getPricesListByCountry(countryCode: String): List<MaterialPriceEntity>

    @Query("SELECT * FROM material_prices WHERE countryCode = :countryCode AND packageId = :packageId")
    suspend fun getPricesByCountryAndPackage(countryCode: String, packageId: String): List<MaterialPriceEntity>

    @Query("SELECT * FROM material_prices WHERE id = :id")
    suspend fun getPriceById(id: String): MaterialPriceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(price: MaterialPriceEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialPrices(prices: List<MaterialPriceEntity>)

    @Query("UPDATE material_prices SET currentPrice = :newPrice WHERE id = :id")
    suspend fun updatePrice(id: String, newPrice: Double)

    @Query("UPDATE material_prices SET currentPrice = defaultPrice WHERE countryCode = :countryCode")
    suspend fun resetCountryPricesToDefault(countryCode: String)

    @Delete
    suspend fun deletePrice(price: MaterialPriceEntity)
}

@Dao
interface ProjectEstimateDao {
    @Query("SELECT * FROM project_estimates ORDER BY createdAt DESC")
    fun getAllEstimates(): Flow<List<ProjectEstimateEntity>>

    @Query("SELECT * FROM project_estimates WHERE id = :id")
    suspend fun getEstimateById(id: Long): ProjectEstimateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimate(estimate: ProjectEstimateEntity): Long

    @Delete
    suspend fun deleteEstimate(estimate: ProjectEstimateEntity)

    @Query("DELETE FROM project_estimates WHERE id = :id")
    suspend fun deleteEstimateById(id: Long)
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getSettingsSync(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: UserSettingsEntity)
}
