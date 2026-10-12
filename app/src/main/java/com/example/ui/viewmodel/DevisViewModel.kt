package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ProjectEstimateEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationSummary
import com.example.domain.usecase.DeleteQuoteUseCase
import com.example.domain.usecase.FormatWhatsAppQuoteUseCase
import com.example.domain.usecase.GetSavedQuotesUseCase
import com.example.domain.usecase.SaveQuoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class DevisViewModel(
    private val getSavedQuotesUseCase: GetSavedQuotesUseCase,
    private val saveQuoteUseCase: SaveQuoteUseCase,
    private val deleteQuoteUseCase: DeleteQuoteUseCase,
    private val formatWhatsAppQuoteUseCase: FormatWhatsAppQuoteUseCase = FormatWhatsAppQuoteUseCase()
) : ViewModel() {

    val savedEstimates: StateFlow<List<ProjectEstimateEntity>> = getSavedQuotesUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun saveEstimate(
        summary: CalculationSummary,
        clientName: String,
        clientPhone: String,
        location: String,
        notes: String,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val jsonArray = JSONArray()
            for (item in summary.items) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("nameFr", item.nameFr)
                obj.put("nameAr", item.nameAr)
                obj.put("nameDerja", item.nameDerja)
                obj.put("quantity", item.quantity)
                obj.put("unit", item.unit)
                obj.put("unitPrice", item.unitPrice)
                obj.put("totalPrice", item.totalPrice)
                jsonArray.put(obj)
            }

            val entity = ProjectEstimateEntity(
                clientName = clientName.ifEmpty { "عميل جديد" },
                clientPhone = clientPhone,
                location = location.ifEmpty { "الشانتي" },
                calculationTypeId = summary.calculationType.id,
                tradeCategory = summary.calculationType.trade.id,
                inputArea = summary.inputArea,
                inputPerimeter = summary.inputPerimeter,
                inputHeight = summary.inputHeight,
                inputQuantity = summary.inputQuantity,
                wasteMargin = summary.wasteMarginPercent,
                materialsJson = jsonArray.toString(),
                totalMaterialCost = summary.totalMaterialCost,
                laborCost = summary.estimatedLaborCost,
                grandTotal = summary.grandTotalCost,
                currency = summary.currency,
                countryCode = summary.countryCode,
                notes = notes
            )
            saveQuoteUseCase(entity)
            launch(Dispatchers.Main) {
                onSaved()
            }
        }
    }

    fun deleteEstimate(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteQuoteUseCase(id)
        }
    }

    fun generateWhatsAppText(
        summary: CalculationSummary,
        settings: UserSettingsEntity,
        language: AppLanguage,
        clientName: String = "",
        location: String = ""
    ): String {
        return formatWhatsAppQuoteUseCase(summary, settings, language, clientName, location)
    }

    fun shareWhatsApp(context: Context, text: String, phone: String = "") {
        try {
            val uri = if (phone.isNotEmpty()) {
                val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
                Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(text)}")
            } else {
                Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(text)}")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "مشاركة تقرير الشانتي")
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(shareIntent)
        }
    }
}
