package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "material_prices")
data class MaterialPriceEntity(
    @PrimaryKey val id: String, // e.g. "TN_plaque_ba13_standard"
    val baseKey: String, // e.g. "plaque_ba13_standard"
    val countryCode: String, // "TN", "DZ", "MA", "FR", "SA", etc.
    val category: String, // "placo", "ossature", "visserie", "enduits", "dalles", "isolation", "peinture", "maconnerie", "carrelage", "bois_alu"
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val unit: String, // "pièce", "m²", "sac 25kg", "boîte 1000", "rouleau", "m³", "bidon"
    val defaultPrice: Double,
    val currentPrice: Double,
    val currency: String,
    val isCustom: Boolean = false,
    val brandInfo: String = "",
    // Nullable package SKU identifier. Added in DB migration 1 -> 2.
    val packageId: String? = null
)

@Entity(tableName = "project_estimates")
data class ProjectEstimateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientName: String,
    val clientPhone: String,
    val location: String,
    val calculationTypeId: String,
    val tradeCategory: String,
    val inputArea: Double,
    val inputPerimeter: Double,
    val inputHeight: Double,
    val inputQuantity: Double,
    val wasteMargin: Double,
    val materialsJson: String, // serialized items
    val totalMaterialCost: Double,
    val laborCost: Double,
    val grandTotal: Double,
    val currency: String,
    val countryCode: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val selectedCountry: String = "TN",
    val selectedLanguage: String = "derja",
    val defaultWasteMargin: Double = 5.0, // 5%
    val defaultLaborRatePerM2: Double = 12.0, // in selected currency
    val contractorName: String = "",
    val contractorPhone: String = "",
    val workshopLocation: String = "",
    val companyHeader: String = "Placo & Travaux BTP"
)
