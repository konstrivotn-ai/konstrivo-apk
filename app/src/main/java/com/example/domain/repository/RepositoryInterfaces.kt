package com.example.domain.repository

import com.example.data.local.MaterialPriceEntity
import com.example.data.local.ProjectEstimateEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.model.TradeCategory
import com.example.domain.model.Material
import com.example.domain.model.MaterialCategory
import kotlinx.coroutines.flow.Flow

/**
 * Clean domain interface for querying construction materials catalog.
 */
interface MaterialRepository {
    fun getAllMaterials(): List<Material>
    fun getMaterialById(id: String): Material?
    fun getMaterialsByCategory(category: MaterialCategory): List<Material>
    fun getMaterialsByTrade(trade: TradeCategory): List<Material>
}

/**
 * Clean domain interface for querying and updating market prices.
 */
interface PriceRepository {
    fun getPricesByCountry(countryCode: String): Flow<List<MaterialPriceEntity>>
    suspend fun getPricesListByCountry(countryCode: String): List<MaterialPriceEntity>
    suspend fun updatePrice(id: String, newPrice: Double)
    suspend fun insertOrUpdatePrice(price: MaterialPriceEntity)
    suspend fun resetPricesToDefault(countryCode: String)
}

/**
 * Clean domain interface for saving and managing client quotes and project estimates.
 */
interface EstimateRepository {
    fun getAllEstimates(): Flow<List<ProjectEstimateEntity>>
    suspend fun saveEstimate(estimate: ProjectEstimateEntity): Long
    suspend fun deleteEstimate(id: Long)
}

/**
 * Clean domain interface for managing user preferences and contractor profile settings.
 */
interface UserSettingsRepository {
    fun getUserSettings(): Flow<UserSettingsEntity?>
    suspend fun updateSettings(settings: UserSettingsEntity)
}
