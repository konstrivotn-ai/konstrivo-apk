package com.example.domain.usecase

import com.example.data.local.UserSettingsEntity
import com.example.data.repository.ConstructionRepository

class UpdateUserSettingsUseCase(private val repository: ConstructionRepository) {
    suspend operator fun invoke(settings: UserSettingsEntity) {
        repository.updateSettings(settings)
    }
}
