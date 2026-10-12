package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MaterialPriceEntity
import com.example.domain.usecase.GetMarketPricesUseCase
import com.example.domain.usecase.ResetPricesUseCase
import com.example.domain.usecase.UpdateMaterialPriceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PricesViewModel(
    private val getMarketPricesUseCase: GetMarketPricesUseCase,
    private val updateMaterialPriceUseCase: UpdateMaterialPriceUseCase,
    private val resetPricesUseCase: ResetPricesUseCase
) : ViewModel() {

    private val _pricesList = MutableStateFlow<List<MaterialPriceEntity>>(emptyList())
    val pricesList: StateFlow<List<MaterialPriceEntity>> = _pricesList.asStateFlow()

    private val _priceSearchQuery = MutableStateFlow("")
    val priceSearchQuery: StateFlow<String> = _priceSearchQuery.asStateFlow()

    private val _selectedPriceCategory = MutableStateFlow("all")
    val selectedPriceCategory: StateFlow<String> = _selectedPriceCategory.asStateFlow()

    fun loadPricesForCountry(countryCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getMarketPricesUseCase(countryCode).collect { list ->
                _pricesList.value = list
            }
        }
    }

    fun setPriceSearchQuery(query: String) {
        _priceSearchQuery.value = query
    }

    fun setSelectedPriceCategory(category: String) {
        _selectedPriceCategory.value = category
    }

    fun updateMaterialPrice(id: String, newPrice: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            updateMaterialPriceUseCase(id, newPrice)
        }
    }

    fun resetPricesToDefault(countryCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            resetPricesUseCase(countryCode)
        }
    }
}
