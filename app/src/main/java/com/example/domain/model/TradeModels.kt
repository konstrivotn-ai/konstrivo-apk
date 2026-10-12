package com.example.domain.model

import com.example.data.model.AppLanguage
import com.example.data.model.CalculationType
import com.example.data.model.TradeCategory

/**
 * Status of a construction trade in the KONSTRIVO platform.
 */
enum class TradeStatus {
    ACTIVE,
    IN_DEVELOPMENT,
    COMING_SOON
}

/**
 * Status of an individual work type / prestation under a trade.
 */
enum class WorkTypeStatus {
    ACTIVE,
    IN_DEVELOPMENT,
    COMING_SOON
}

/**
 * Domain model representing a specific work type (prestation) under a trade.
 */
data class WorkType(
    val id: String,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val nameEn: String,
    val descriptionFr: String,
    val descriptionAr: String,
    val descriptionDerja: String,
    val descriptionEn: String,
    val iconEmoji: String,
    val unit: String,
    val status: WorkTypeStatus,
    val calculationType: CalculationType? = null
) {
    fun localizedName(language: AppLanguage): String = when (language) {
        AppLanguage.FRENCH -> nameFr
        AppLanguage.ARABIC -> nameAr
        AppLanguage.DERJA -> nameDerja
        AppLanguage.ENGLISH -> nameEn
    }

    fun localizedDescription(language: AppLanguage): String = when (language) {
        AppLanguage.FRENCH -> descriptionFr
        AppLanguage.ARABIC -> descriptionAr
        AppLanguage.DERJA -> descriptionDerja
        AppLanguage.ENGLISH -> descriptionEn
    }

    fun localizedStatusLabel(language: AppLanguage): String = when (status) {
        WorkTypeStatus.ACTIVE -> when (language) {
            AppLanguage.FRENCH -> "Calculateur Actif"
            AppLanguage.ARABIC -> "حساب فوري متاح"
            AppLanguage.DERJA -> "حاسبة جاهزة"
            AppLanguage.ENGLISH -> "Active Calculator"
        }
        WorkTypeStatus.IN_DEVELOPMENT -> when (language) {
            AppLanguage.FRENCH -> "En Standardisation DTU"
            AppLanguage.ARABIC -> "قيد التجهيز الفني"
            AppLanguage.DERJA -> "قاعدين نجهزو فيها"
            AppLanguage.ENGLISH -> "In Development"
        }
        WorkTypeStatus.COMING_SOON -> when (language) {
            AppLanguage.FRENCH -> "Bientôt (Roadmap)"
            AppLanguage.ARABIC -> "قريبًا"
            AppLanguage.DERJA -> "قريباً"
            AppLanguage.ENGLISH -> "Coming Soon"
        }
    }
}

/**
 * Domain model representing a full Construction Trade (Corps d'état / مهنة بناء).
 */
data class Trade(
    val id: String,
    val category: TradeCategory?,
    val nameFr: String,
    val nameAr: String,
    val nameDerja: String,
    val nameEn: String,
    val iconEmoji: String,
    val descriptionFr: String,
    val descriptionAr: String,
    val descriptionDerja: String,
    val descriptionEn: String,
    val status: TradeStatus,
    val workTypes: List<WorkType>
) {
    val isAvailable: Boolean get() = status == TradeStatus.ACTIVE

    fun localizedName(language: AppLanguage): String = when (language) {
        AppLanguage.FRENCH -> nameFr
        AppLanguage.ARABIC -> nameAr
        AppLanguage.DERJA -> nameDerja
        AppLanguage.ENGLISH -> nameEn
    }

    fun localizedDescription(language: AppLanguage): String = when (language) {
        AppLanguage.FRENCH -> descriptionFr
        AppLanguage.ARABIC -> descriptionAr
        AppLanguage.DERJA -> descriptionDerja
        AppLanguage.ENGLISH -> descriptionEn
    }

    fun localizedStatusLabel(language: AppLanguage): String = when (status) {
        TradeStatus.ACTIVE -> when (language) {
            AppLanguage.FRENCH -> "Complet & Actif"
            AppLanguage.ARABIC -> "مكتمل ومتاح"
            AppLanguage.DERJA -> "حاضر ويخدم"
            AppLanguage.ENGLISH -> "Complete & Active"
        }
        TradeStatus.IN_DEVELOPMENT -> when (language) {
            AppLanguage.FRENCH -> "En Développement"
            AppLanguage.ARABIC -> "قيد الإعداد الهندسي"
            AppLanguage.DERJA -> "قيد التجهيز"
            AppLanguage.ENGLISH -> "In Development"
        }
        TradeStatus.COMING_SOON -> when (language) {
            AppLanguage.FRENCH -> "Bientôt (Roadmap 2026)"
            AppLanguage.ARABIC -> "قريبًا (Roadmap 2026)"
            AppLanguage.DERJA -> "مبرمج لـ 2026"
            AppLanguage.ENGLISH -> "Coming Soon (2026)"
        }
    }
}
