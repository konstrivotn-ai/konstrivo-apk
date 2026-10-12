package com.example.domain.paint

object PaintProductCatalog {
    // In-memory store. Start empty; tests may populate demo/test entries.
    private val products = mutableListOf<PaintProduct>()
    // Preserve coverage value+unit metadata when product model cannot express unit directly.
    private val coverageValueByProductId = mutableMapOf<String, Double?>()
    private val coverageUnitByProductId = mutableMapOf<String, String?>()

    fun getAll(): List<PaintProduct> = products.toList()

    fun getById(productId: String): PaintProduct? = products.firstOrNull { it.productId == productId }

    fun getByMaterialType(materialTypeId: String): List<PaintProduct> = products.filter { it.materialTypeId == materialTypeId }

    fun getActiveByCountry(countryCode: String): List<PaintProduct> = products.filter { it.countryCode.equals(countryCode, ignoreCase = true) && it.active }

    // Management APIs for tests/demo only
    fun addProduct(p: PaintProduct) {
        if (products.any { it.productId == p.productId }) {
            throw IllegalArgumentException("Product with id ${p.productId} already exists")
        }
        products.add(p)
    }

    fun setCoverageMetadata(productId: String, value: Double?, unit: String?) {
        coverageValueByProductId[productId] = value
        coverageUnitByProductId[productId] = unit
    }

    fun getCoverageValue(productId: String): Double? = coverageValueByProductId[productId]

    fun getCoverageUnit(productId: String): String? = coverageUnitByProductId[productId]

    fun clear() {
        products.clear()
    }
    /**
     * Add only clearly-marked test/demo entries. These are NOT reference production data.
     */
    fun addTestData() {
        clear()
        addProduct(
            PaintProduct(
                productId = "TEST_PAINT_15L_A",
                materialTypeId = "peinture_finition_satinee",
                brand = "TEST_BRAND",
                productName = "Test Satin 15L (demo)",
                containerVolumeL = 15.0,
                coverageM2PerLiter = null,
                recommendedCoats = null,
                isPrimer = false,
                isInterior = true,
                isExterior = false,
                countryCode = "TN",
                supplierReference = "DEMO-001",
                active = true
            )
        )
        addProduct(
            PaintProduct(
                productId = "TEST_PRIMER_20L_A",
                materialTypeId = "fixateur_primaire",
                brand = "TEST_BRAND",
                productName = "Test Primer 20L (demo)",
                containerVolumeL = 20.0,
                coverageM2PerLiter = null,
                recommendedCoats = 1,
                isPrimer = true,
                isInterior = true,
                isExterior = true,
                countryCode = "TN",
                supplierReference = "DEMO-002",
                active = false // demo inactive entry
            )
        )
    }

    /**
     * Add verified reference products only. Use this in production-like test setups.
     */
    fun addReferenceData() {
        clear()
        // --- Verified Astral products (reference data) ---
        val aquastral = PaintProduct(
            productId = "ASTRAL_AQUASTRAL_SATIN",
            materialTypeId = "peinture_finition_satinee",
            brand = "Astral",
            productName = "Aquastral Satin",
            containerVolumeL = null,
            containerWeightKg = null,
            coverageM2PerLiter = null, // coverage provided in m2/kg
            recommendedCoats = 2,
            isPrimer = false,
            isInterior = true,
            isExterior = false,
            countryCode = "TN",
            supplierReference = null,
            active = true
        )
        addProduct(aquastral)
        setCoverageMetadata(aquastral.productId, 10.0, "M2_PER_KG")

        val vinylastral = PaintProduct(
            productId = "ASTRAL_VINYLASTRAL",
            materialTypeId = "peinture_interieure",
            brand = "Astral",
            productName = "Vinylastral",
            containerVolumeL = null,
            containerWeightKg = null,
            coverageM2PerLiter = null, // coverage provided in m2/kg
            recommendedCoats = 2,
            isPrimer = false,
            isInterior = true,
            isExterior = true,
            countryCode = "TN",
            supplierReference = null,
            active = true
        )
        addProduct(vinylastral)
        setCoverageMetadata(vinylastral.productId, 4.0, "M2_PER_KG")

        val lakalo = PaintProduct(
            productId = "ASTRAL_LAKALO",
            materialTypeId = "peinture_finition",
            brand = "Astral",
            productName = "Lakalo",
            containerVolumeL = null,
            containerWeightKg = null,
            coverageM2PerLiter = 13.0, // coverage provided in m2/L
            recommendedCoats = 2,
            isPrimer = false,
            isInterior = true,
            isExterior = false,
            countryCode = "TN",
            supplierReference = null,
            active = true
        )
        addProduct(lakalo)
        setCoverageMetadata(lakalo.productId, 13.0, "M2_PER_L")
    }
}
