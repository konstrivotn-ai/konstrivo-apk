package com.example.domain.paint

object PaintPackageCatalog {
    private val packages = mutableListOf<PaintPackage>()

    fun getAll(): List<PaintPackage> = packages.toList()

    fun getById(packageId: String): PaintPackage? = packages.firstOrNull { it.packageId == packageId }

    fun getByProductId(productId: String): List<PaintPackage> = packages.filter { it.productId == productId }

    fun getActiveByCountry(countryCode: String): List<PaintPackage> = packages.filter { it.countryCode.equals(countryCode, ignoreCase = true) && it.active }

    fun addPackage(p: PaintPackage) {
        if (packages.any { it.packageId == p.packageId }) throw IllegalArgumentException("Package with id ${p.packageId} already exists")
        packages.add(p)
    }

    fun clear() {
        packages.clear()
    }

    /**
     * Add verified reference packages only.
     */
    fun addReferenceData() {
        clear()
        // Verified packages for ASTRAL_AQUASTRAL_SATIN
        addPackage(
            PaintPackage(
                packageId = "ASTRAL_AQUASTRAL_SATIN_5KG",
                productId = "ASTRAL_AQUASTRAL_SATIN",
                containerWeightKg = 5.0,
                countryCode = "TN",
                active = true
            )
        )

        addPackage(
            PaintPackage(
                packageId = "ASTRAL_AQUASTRAL_SATIN_20KG",
                productId = "ASTRAL_AQUASTRAL_SATIN",
                containerWeightKg = 20.0,
                countryCode = "TN",
                active = true
            )
        )
    }

    /**
     * Legacy test fixture population (kept empty by default). Tests should call
     * `addReferenceData()` when they need production-like reference entries.
     */
    fun addTestData() {
        // intentionally left empty; use addReferenceData() for verified packages
    }
}
