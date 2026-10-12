package com.example.domain.model

import com.example.data.model.AppLanguage
import com.example.data.model.Country
import java.util.Locale

/**
 * Encapsulates the entire market, regional, economic, and measurement context for a construction calculation.
 * Decouples the calculation engines from direct country enum assumptions.
 */
data class MarketContext(
    val country: Country,
    val region: String = "",
    val currencyCode: String = country.currencyCode,
    val currencySymbol: String = country.currencySymbol,
    val unitSystem: String = "METRIC",
    val defaultLaborRatePerM2: Double = when (country) {
        Country.TUNISIA -> 12.0
        Country.FRANCE -> 28.0
        Country.ALGERIA -> 500.0
        Country.MOROCCO -> 40.0
        Country.CANADA -> 35.0
        Country.SAUDI -> 25.0
        Country.UAE -> 25.0
        Country.EGYPT -> 180.0
        Country.LIBYA -> 20.0
    },
    val defaultWasteMargin: Double = 5.0,
    val hasActiveMarketData: Boolean = true
) {
    fun formatPrice(amount: Double): String {
        return String.format(Locale.US, "%.2f %s", amount, currencySymbol)
    }

    fun formatPriceWithCode(amount: Double): String {
        return String.format(Locale.US, "%.2f %s", amount, currencyCode)
    }

    fun formatArea(area: Double): String {
        return String.format(Locale.US, "%.2f m²", area)
    }

    fun formatPerimeter(perimeter: Double): String {
        return String.format(Locale.US, "%.2f ml", perimeter)
    }

    fun formatQuantity(quantity: Double, unit: StandardUnit, language: AppLanguage): String {
        val qtyStr = if (quantity % 1.0 == 0.0) {
            String.format(Locale.US, "%.0f", quantity)
        } else {
            String.format(Locale.US, "%.2f", quantity)
        }
        return "$qtyStr ${unit.localizedName(language)}"
    }

    companion object {
        fun forCountry(country: Country, region: String = ""): MarketContext {
            return MarketContext(
                country = country,
                region = region
            )
        }

        fun forCountryCode(countryCode: String, region: String = ""): MarketContext {
            val country = Country.fromCode(countryCode)
            return forCountry(country, region)
        }
    }
}
