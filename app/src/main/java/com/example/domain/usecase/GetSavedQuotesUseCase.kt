package com.example.domain.usecase

import com.example.data.local.ProjectEstimateEntity
import com.example.data.repository.ConstructionRepository
import kotlinx.coroutines.flow.Flow

class GetSavedQuotesUseCase(private val repository: ConstructionRepository) {
    operator fun invoke(): Flow<List<ProjectEstimateEntity>> {
        return repository.getAllEstimates()
    }
}
