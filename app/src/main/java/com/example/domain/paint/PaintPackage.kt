package com.example.domain.paint

data class PaintPackage(
    val packageId: String,
    val productId: String,
    val containerVolumeL: Double? = null,
    val containerWeightKg: Double? = null,
    val supplierReference: String? = null,
    val countryCode: String = "",
    val active: Boolean = true
) {
    init {
        if (packageId.isBlank()) throw IllegalArgumentException("packageId must not be blank")
        if (productId.isBlank()) throw IllegalArgumentException("productId must not be blank")

        val hasVolume = containerVolumeL != null
        val hasWeight = containerWeightKg != null

        if (hasVolume == hasWeight) {
            // both true or both false
            throw IllegalArgumentException("Exactly one of containerVolumeL or containerWeightKg must be provided")
        }

        if (hasVolume) {
            if (containerVolumeL!! <= 0.0) throw IllegalArgumentException("containerVolumeL must be > 0")
        }

        if (hasWeight) {
            if (containerWeightKg!! <= 0.0) throw IllegalArgumentException("containerWeightKg must be > 0")
        }
    }
}
