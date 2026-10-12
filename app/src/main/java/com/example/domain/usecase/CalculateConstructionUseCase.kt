package com.example.domain.usecase

import com.example.calculator.ConstructionCalculator
import com.example.data.local.MaterialPriceEntity
import com.example.data.model.CalculationSummary
import com.example.data.model.Country

/**
 * UseCase responsible for executing construction calculations by delegating directly
 * to the verified ConstructionCalculator engine.
 */
class CalculateConstructionUseCase {
    operator fun invoke(
        params: ConstructionCalculator.CalculationParams,
        priceMap: Map<String, MaterialPriceEntity>,
        country: Country
    ): CalculationSummary {
        return ConstructionCalculator.calculate(params, priceMap, country)
    }
}
