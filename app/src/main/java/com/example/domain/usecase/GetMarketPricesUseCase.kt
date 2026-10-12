package com.example.domain.usecase

import com.example.data.local.MaterialPriceEntity
import com.example.data.repository.ConstructionRepository
import kotlinx.coroutines.flow.Flow

class GetMarketPricesUseCase(private val repository: ConstructionRepository) {
    operator fun invoke(countryCode: String): Flow<List<MaterialPriceEntity>> {
        return repository.getPricesByCountry(countryCode)
    }

    suspend fun getList(countryCode: String): List<MaterialPriceEntity> {
        return repository.getPricesListByCountry(countryCode)
    }
}
