package com.example.domain.currency

import java.util.Locale

/**
 * Interface for providing currency exchange rates.
 * Extensible for future live REST API or PostgreSQL synchronization.
 */
interface ExchangeRateProvider {
    fun getRateToTnd(currencyCode: String): Double
    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double
}

/**
 * Default offline baseline exchange rate provider for the 2026 economic context.
 * Decouples raw prices from user display preferences.
 */
object DefaultExchangeRateProvider : ExchangeRateProvider {

    // Base rates relative to 1.0 TND (Tunisian Dinar)
    private val ratesToTnd = mapOf(
        "TND" to 1.0,
        "EUR" to 3.3333,   // 1 EUR ~ 3.33 TND (or 1 TND ~ 0.30 EUR)
        "DZD" to 0.023,    // 1 DZD ~ 0.023 TND (or 1 TND ~ 43.5 DZD)
        "MAD" to 0.3077,   // 1 MAD ~ 0.308 TND (or 1 TND ~ 3.25 MAD)
        "CAD" to 2.2727,   // 1 CAD ~ 2.27 TND (or 1 TND ~ 0.44 CAD)
        "SAR" to 0.8333,   // 1 SAR ~ 0.83 TND (or 1 TND ~ 1.20 SAR)
        "AED" to 0.8475,   // 1 AED ~ 0.85 TND (or 1 TND ~ 1.18 AED)
        "EGP" to 0.0641,   // 1 EGP ~ 0.064 TND (or 1 TND ~ 15.6 EGP)
        "LYD" to 0.6452,   // 1 LYD ~ 0.65 TND (or 1 TND ~ 1.55 LYD)
        "USD" to 3.1000    // 1 USD ~ 3.10 TND
    )

    override fun getRateToTnd(currencyCode: String): Double {
        return ratesToTnd[currencyCode.uppercase(Locale.ROOT)] ?: 1.0
    }

    override fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val from = fromCurrency.uppercase(Locale.ROOT)
        val to = toCurrency.uppercase(Locale.ROOT)
        if (from == to) return amount

        val fromInTnd = amount * (ratesToTnd[from] ?: 1.0)
        val toRate = ratesToTnd[to] ?: 1.0
        val converted = fromInTnd / toRate
        return (converted * 100).toLong() / 100.0
    }
}
