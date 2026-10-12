package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.Country
import com.example.data.model.TradeCategory
import com.example.domain.material.MaterialCatalog
import com.example.domain.model.Material
import com.example.domain.model.MaterialCategory
import com.example.domain.repository.EstimateRepository
import com.example.domain.repository.MaterialRepository
import com.example.domain.repository.PriceRepository
import com.example.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow

class ConstructionRepository(
    private val materialPriceDao: MaterialPriceDao,
    private val projectEstimateDao: ProjectEstimateDao,
    private val userSettingsDao: UserSettingsDao
) : MaterialRepository, PriceRepository, EstimateRepository, UserSettingsRepository {

    // ==========================================
    // MATERIAL REPOSITORY IMPLEMENTATION
    // ==========================================
    override fun getAllMaterials(): List<Material> {
        return MaterialCatalog.ALL_MATERIALS
    }

    override fun getMaterialById(id: String): Material? {
        return MaterialCatalog.getById(id)
    }

    override fun getMaterialsByCategory(category: MaterialCategory): List<Material> {
        return MaterialCatalog.getByCategory(category)
    }

    override fun getMaterialsByTrade(trade: TradeCategory): List<Material> {
        return MaterialCatalog.getByTrade(trade)
    }

    // ==========================================
    // PRICE REPOSITORY IMPLEMENTATION
    // ==========================================
    override fun getPricesByCountry(countryCode: String): Flow<List<MaterialPriceEntity>> {
        return materialPriceDao.getPricesByCountry(countryCode)
    }

    override suspend fun getPricesListByCountry(countryCode: String): List<MaterialPriceEntity> {
        return materialPriceDao.getPricesListByCountry(countryCode)
    }

    /**
     * Return a package-aware map keyed by `baseKey`.
     * This method is a minimal service-layer helper and does not change DB schema.
     */
    suspend fun getPriceMapForCountry(countryCode: String, packageId: String? = null): Map<String, MaterialPriceEntity> {
        val prices = materialPriceDao.getPricesListByCountry(countryCode)
        return com.example.domain.PriceSelection.selectPriceMap(prices, packageId)
    }

    override suspend fun updatePrice(id: String, newPrice: Double) {
        materialPriceDao.updatePrice(id, newPrice)
    }

    override suspend fun insertOrUpdatePrice(price: MaterialPriceEntity) {
        materialPriceDao.insertOrUpdate(price)
    }

    override suspend fun resetPricesToDefault(countryCode: String) {
        val country = Country.fromCode(countryCode)
        val defaultPrices = InitialData.generatePricesForCountry(country)
        for (price in defaultPrices) {
            materialPriceDao.insertOrUpdate(price)
        }
    }

    // ==========================================
    // ESTIMATE / QUOTE REPOSITORY IMPLEMENTATION
    // ==========================================
    override fun getAllEstimates(): Flow<List<ProjectEstimateEntity>> {
        return projectEstimateDao.getAllEstimates()
    }

    override suspend fun saveEstimate(estimate: ProjectEstimateEntity): Long {
        return projectEstimateDao.insertEstimate(estimate)
    }

    override suspend fun deleteEstimate(id: Long) {
        projectEstimateDao.deleteEstimateById(id)
    }

    // ==========================================
    // USER SETTINGS REPOSITORY IMPLEMENTATION
    // ==========================================
    override fun getUserSettings(): Flow<UserSettingsEntity?> {
        return userSettingsDao.getSettings()
    }

    override suspend fun updateSettings(settings: UserSettingsEntity) {
        userSettingsDao.insertOrUpdate(settings)
    }
}

