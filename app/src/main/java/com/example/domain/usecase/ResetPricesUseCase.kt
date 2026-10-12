package com.example.domain.usecase

import com.example.data.repository.ConstructionRepository

class ResetPricesUseCase(private val repository: ConstructionRepository) {
    suspend operator fun invoke(countryCode: String) {
        repository.resetPricesToDefault(countryCode)
    }
}
