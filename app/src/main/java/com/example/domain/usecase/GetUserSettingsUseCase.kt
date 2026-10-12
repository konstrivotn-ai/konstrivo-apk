package com.example.domain.usecase

import com.example.data.local.UserSettingsEntity
import com.example.data.repository.ConstructionRepository
import kotlinx.coroutines.flow.Flow

class GetUserSettingsUseCase(private val repository: ConstructionRepository) {
    operator fun invoke(): Flow<UserSettingsEntity?> {
        return repository.getUserSettings()
    }
}
