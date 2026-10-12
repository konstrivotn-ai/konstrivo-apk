package com.example.domain.usecase

import com.example.data.local.UserSettingsEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationSummary
import java.text.SimpleDateFormat
import java.util.*

/**
 * Display-only formatter for material quantities.
 * Keeps up to 2 decimal places without truncating fractional quantities.
 */
fun formatQuantity(quantity: Double): String {
    val clamped = Math.round(quantity * 100.0) / 100.0
    return if (clamped == clamped.toLong().toDouble()) {
        clamped.toLong().toString()
    } else {
        clamped.toString()
    }
}


class FormatWhatsAppQuoteUseCase {
    operator fun invoke(
        summary: CalculationSummary,
        settings: UserSettingsEntity,
        language: AppLanguage,
        clientName: String = "",
        location: String = ""
    ): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sb = StringBuilder()

        when (language) {
            AppLanguage.DERJA -> {
                sb.append("📋 *عرض أسعار وحسبة سلع الشانتي - ${settings.companyHeader}*\n")
                sb.append("📅 التاريخ: $dateStr\n")
                if (clientName.isNotEmpty()) sb.append("👤 الحريف: $clientName\n")
                if (location.isNotEmpty()) sb.append("📍 موقع الشانتي: $location\n")
                sb.append("🛠️ نوع الخدمة: ${summary.calculationType.nameDerja}\n")
                sb.append("📐 المساحة: ${summary.inputArea} م² (المحيط: ${summary.inputPerimeter.toInt()} م)\n")
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                sb.append("📦 *تفصيل السلعة والمواد المطلوبة:*\n")
                for (item in summary.items) {
                    sb.append("▫️ *${item.nameDerja}*: ${formatQuantity(item.quantity)} ${item.unit} | ${item.totalPrice} ${summary.currency}\n")
                }
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                sb.append("💰 *مجموع كلفة السلعة (HT):* ${summary.totalMaterialCost} ${summary.currency}\n")
                sb.append("👷 *اليد العاملة المقدرة (HT):* ${summary.estimatedLaborCost} ${summary.currency}\n")
                sb.append("💰 *الأداء (TVA ${formatRate(summary.tvaRate)}%):* ${summary.totalTva} ${summary.currency}\n")
                sb.append("✨ *المجموع الكلي شامل الأداء (TTC):* *${summary.grandTotalTTC} ${summary.currency}*\n")
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                if (settings.contractorName.isNotEmpty()) {
                    sb.append("📞 المعلم: ${settings.contractorName} (${settings.contractorPhone})\n")
                }
                sb.append("✅ محسوبة بدقة وتطبيق Konstrivo Pro 2026")
            }
            AppLanguage.ARABIC -> {
                sb.append("📋 *عرض سعر وتقدير مواد البناء - ${settings.companyHeader}*\n")
                sb.append("📅 التاريخ: $dateStr\n")
                if (clientName.isNotEmpty()) sb.append("👤 العميل: $clientName\n")
                if (location.isNotEmpty()) sb.append("📍 الموقع: $location\n")
                sb.append("🛠️ نوع العمل: ${summary.calculationType.nameAr}\n")
                sb.append("📐 المساحة الإجمالية: ${summary.inputArea} م²\n")
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                sb.append("📦 *جدول المواد والكميات:*\n")
                for (item in summary.items) {
                    sb.append("▫️ *${item.nameAr}*: ${formatQuantity(item.quantity)} ${item.unit} | ${item.totalPrice} ${summary.currency}\n")
                }
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                sb.append("💰 *إجمالي تكلفة المواد (HT):* ${summary.totalMaterialCost} ${summary.currency}\n")
                sb.append("👷 *تكلفة المصنعية / اليد العاملة (HT):* ${summary.estimatedLaborCost} ${summary.currency}\n")
                sb.append("💰 *الضريبة (TVA ${formatRate(summary.tvaRate)}%):* ${summary.totalTva} ${summary.currency}\n")
                sb.append("✨ *المبلغ الإجمالي شامل الضريبة (TTC):* *${summary.grandTotalTTC} ${summary.currency}*\n")
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                if (settings.contractorName.isNotEmpty()) {
                    sb.append("📞 المقاول المسؤول: ${settings.contractorName} (${settings.contractorPhone})\n")
                }
            }
            AppLanguage.FRENCH -> {
                sb.append("📋 *DEVIS ESTIMATIF ET QUANTITATIF - ${settings.companyHeader}*\n")
                sb.append("📅 Date: $dateStr\n")
                if (clientName.isNotEmpty()) sb.append("👤 Client: $clientName\n")
                if (location.isNotEmpty()) sb.append("📍 Chantier: $location\n")
                sb.append("🛠️ Prestation: ${summary.calculationType.nameFr}\n")
                sb.append("📐 Surface: ${summary.inputArea} m² (Périmètre: ${summary.inputPerimeter.toInt()} m)\n")
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                sb.append("📦 *DÉTAIL DES FOURNITURES & MATÉRIAUX:*\n")
                for (item in summary.items) {
                    sb.append("▫️ *${item.nameFr}*: ${formatQuantity(item.quantity)} ${item.unit} (${item.totalPrice} ${summary.currency})\n")
                }
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                sb.append("💰 *Total Fournitures (HT):* ${summary.totalMaterialCost} ${summary.currency}\n")
                sb.append("👷 *Main d'œuvre estimée (HT):* ${summary.estimatedLaborCost} ${summary.currency}\n")
                sb.append("💰 *TVA (${formatRate(summary.tvaRate)}%):* ${summary.totalTva} ${summary.currency}\n")
                sb.append("✨ *TOTAL TTC:* *${summary.grandTotalTTC} ${summary.currency}*\n")
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                if (settings.contractorName.isNotEmpty()) {
                    sb.append("📞 Artisan / Contact: ${settings.contractorName} (${settings.contractorPhone})\n")
                }
            }
            AppLanguage.ENGLISH -> {
                sb.append("📋 *CONSTRUCTION ESTIMATE & BILL OF QUANTITIES - ${settings.companyHeader}*\n")
                sb.append("📅 Date: $dateStr\n")
                if (clientName.isNotEmpty()) sb.append("👤 Client: $clientName\n")
                if (location.isNotEmpty()) sb.append("📍 Site Location: $location\n")
                sb.append("🛠️ Work Type: ${summary.calculationType.nameFr}\n")
                sb.append("📐 Total Area: ${summary.inputArea} m² (Perimeter: ${summary.inputPerimeter.toInt()} m)\n")
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                sb.append("📦 *BILL OF MATERIALS & SUPPLIES:*\n")
                for (item in summary.items) {
                    sb.append("▫️ *${item.nameFr}*: ${formatQuantity(item.quantity)} ${item.unit} (${item.totalPrice} ${summary.currency})\n")
                }
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                sb.append("💰 *Total Materials Cost (HT):* ${summary.totalMaterialCost} ${summary.currency}\n")
                sb.append("👷 *Estimated Labor Cost (HT):* ${summary.estimatedLaborCost} ${summary.currency}\n")
                sb.append("💰 *VAT (${formatRate(summary.tvaRate)}%):* ${summary.totalTva} ${summary.currency}\n")
                sb.append("✨ *GRAND TOTAL TTC:* *${summary.grandTotalTTC} ${summary.currency}*\n")
                sb.append("━━━━━━━━━━━━━━━━━━\n")
                if (settings.contractorName.isNotEmpty()) {
                    sb.append("📞 Contractor: ${settings.contractorName} (${settings.contractorPhone})\n")
                }
            }
        }
        return sb.toString()
    }

    private fun formatRate(rate: Double): String =
        if (rate % 1.0 == 0.0) rate.toInt().toString() else rate.toString()
}

