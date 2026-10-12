package com.example.domain

import com.example.data.local.MaterialPriceEntity

/**
 * Helper utilities for selecting package-aware material prices.
 * This logic is intentionally conservative and deterministic so legacy behaviour
 * (packageId == null) remains unchanged unless an explicit packageId is requested.
 */
object PriceSelection {
    private fun deterministicPick(candidates: List<MaterialPriceEntity>): MaterialPriceEntity {
        // Deterministic ordering: prefer isCustom=true, then lower currentPrice, then id lexicographically
        return candidates.sortedWith(compareByDescending<MaterialPriceEntity> { it.isCustom }
            .thenBy { it.currentPrice }
            .thenBy { it.id })
            .first()
    }

    /**
     * Build a mapping baseKey -> selected MaterialPriceEntity applying package rules.
     * - If packageId != null and package-specific row exists for that packageId, use it.
     * - Otherwise fallback to legacy row (packageId == null) when present.
     * - If none of the above, pick a deterministic candidate from available rows.
     * - Invalid prices (currentPrice <= 0) are ignored.
     */
    fun selectPriceMap(prices: List<MaterialPriceEntity>, packageId: String?): Map<String, MaterialPriceEntity> {
        val grouped = prices.groupBy { it.baseKey }
        val result = mutableMapOf<String, MaterialPriceEntity>()
        for ((baseKey, list) in grouped) {
            val valid = list.filter { it.currentPrice > 0.0 }
            if (valid.isEmpty()) continue

            if (packageId != null) {
                // Determine target country for this baseKey. Prefer legacy row country when present.
                val legacyCountry = list.firstOrNull { it.packageId == null }?.countryCode
                    ?: list.firstOrNull()?.countryCode
                val pkMatches = valid.filter { it.packageId != null && it.packageId == packageId && (legacyCountry == null || it.countryCode == legacyCountry) }
                if (pkMatches.isNotEmpty()) {
                    result[baseKey] = deterministicPick(pkMatches)
                    continue
                }
            }

            val legacy = valid.filter { it.packageId == null }
            if (legacy.isNotEmpty()) {
                result[baseKey] = deterministicPick(legacy)
                continue
            }

            // No package-specific or legacy row, pick deterministic from remaining
            result[baseKey] = deterministicPick(valid)
        }
        return result
    }
}
