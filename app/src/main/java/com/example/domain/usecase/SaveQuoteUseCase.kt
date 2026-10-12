package com.example.domain.usecase

import com.example.data.local.ProjectEstimateEntity
import com.example.data.repository.ConstructionRepository

class SaveQuoteUseCase(private val repository: ConstructionRepository) {
    suspend operator fun invoke(estimate: ProjectEstimateEntity): Long {
        return repository.saveEstimate(estimate)
    }
}
