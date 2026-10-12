package com.example.domain.paint

data class PaintProduct(
    val productId: String,
    val materialTypeId: String,
    val brand: String? = null,
    val productName: String,
    val containerVolumeL: Double? = null,
    val containerWeightKg: Double? = null,
    val coverageM2PerLiter: Double? = null,
    val recommendedCoats: Int? = null,
    val isPrimer: Boolean = false,
    val isInterior: Boolean = true,
    val isExterior: Boolean = false,
    val countryCode: String = "",
    val supplierReference: String? = null,
    val active: Boolean = true
) {
    init {
        // Rule: both volume and weight must not be populated simultaneously
        if (containerVolumeL != null && containerWeightKg != null) {
            throw IllegalArgumentException("Only one of containerVolumeL or containerWeightKg may be set")
        }
        // productId and materialTypeId and productName must be non-empty
        if (productId.isBlank()) throw IllegalArgumentException("productId must not be blank")
        if (materialTypeId.isBlank()) throw IllegalArgumentException("materialTypeId must not be blank")
        if (productName.isBlank()) throw IllegalArgumentException("productName must not be blank")
        // recommendedCoats if present must be > 0
        if (recommendedCoats != null && recommendedCoats <= 0) throw IllegalArgumentException("recommendedCoats must be > 0 if provided")
        // coverage if present must be > 0
        if (coverageM2PerLiter != null && coverageM2PerLiter <= 0.0) throw IllegalArgumentException("coverageM2PerLiter must be > 0 if provided")
    }
}
