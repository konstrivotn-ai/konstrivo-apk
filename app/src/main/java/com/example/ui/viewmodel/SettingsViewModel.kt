package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserSettingsEntity
import com.example.data.model.AppLanguage
import com.example.data.model.Country
import com.example.domain.usecase.GetUserSettingsUseCase
import com.example.domain.usecase.UpdateUserSettingsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    val userSettings: StateFlow<UserSettingsEntity> = getUserSettingsUseCase()
        .map { it ?: UserSettingsEntity() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserSettingsEntity()
        )

    private val _selectedCountry = MutableStateFlow(Country.TUNISIA)
    val selectedCountry: StateFlow<Country> = _selectedCountry.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.DERJA)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            userSettings.collect { settings ->
                _selectedCountry.value = Country.fromCode(settings.selectedCountry)
                _selectedLanguage.value = AppLanguage.fromCode(settings.selectedLanguage)
            }
        }
    }

    fun setCountry(country: Country, onCountryChanged: ((String) -> Unit)? = null) {
        _selectedCountry.value = country
        viewModelScope.launch(Dispatchers.IO) {
            val cur = userSettings.value
            updateUserSettingsUseCase(cur.copy(selectedCountry = country.code))
            onCountryChanged?.invoke(country.code)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _selectedLanguage.value = lang
        viewModelScope.launch(Dispatchers.IO) {
            val cur = userSettings.value
            updateUserSettingsUseCase(cur.copy(selectedLanguage = lang.code))
        }
    }

    fun updateUserSettings(settings: UserSettingsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserSettingsUseCase(settings)
        }
    }
}
