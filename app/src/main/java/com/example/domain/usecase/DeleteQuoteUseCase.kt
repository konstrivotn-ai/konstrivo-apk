package com.example.domain.usecase

import com.example.data.repository.ConstructionRepository

class DeleteQuoteUseCase(private val repository: ConstructionRepository) {
    suspend operator fun invoke(id: Long) {
        repository.deleteEstimate(id)
    }
}
