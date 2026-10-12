package com.example.domain.usecase

import com.example.data.repository.ConstructionRepository

class UpdateMaterialPriceUseCase(private val repository: ConstructionRepository) {
    suspend operator fun invoke(id: String, newPrice: Double) {
        repository.updatePrice(id, newPrice)
    }
}
