package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MaterialPriceEntity
import com.example.data.model.*
import com.example.domain.usecase.CalculateConstructionUseCase
import com.example.calculator.ConstructionCalculator
import kotlinx.coroutines.flow.*

class CalculatorViewModel(
    private val calculateUseCase: CalculateConstructionUseCase = CalculateConstructionUseCase()
) : ViewModel() {

    private val _selectedTrade = MutableStateFlow(TradeCategory.PLACO)
    val selectedTrade: StateFlow<TradeCategory> = _selectedTrade.asStateFlow()

    private val _selectedCalculationType = MutableStateFlow(CalculationType.PLACO_PLAFOND_FIXE)
    val selectedCalculationType: StateFlow<CalculationType> = _selectedCalculationType.asStateFlow()

    private val _inputArea = MutableStateFlow("25.0")
    val inputArea: StateFlow<String> = _inputArea.asStateFlow()

    private val _inputLength = MutableStateFlow("5.0")
    val inputLength: StateFlow<String> = _inputLength.asStateFlow()

    private val _inputWidth = MutableStateFlow("5.0")
    val inputWidth: StateFlow<String> = _inputWidth.asStateFlow()

    private val _inputHeight = MutableStateFlow("2.8")
    val inputHeight: StateFlow<String> = _inputHeight.asStateFlow()

    private val _inputLinearMeters = MutableStateFlow("20.0")
    val inputLinearMeters: StateFlow<String> = _inputLinearMeters.asStateFlow()

    private val _inputUnitsCount = MutableStateFlow("1.0")
    val inputUnitsCount: StateFlow<String> = _inputUnitsCount.asStateFlow()

    // Combined door/window opening area; website deducts only at >= 1 m².
    private val _openingArea = MutableStateFlow("0.0")
    val openingArea: StateFlow<String> = _openingArea.asStateFlow()

    private val _wasteMargin = MutableStateFlow("10.0")
    val wasteMargin: StateFlow<String> = _wasteMargin.asStateFlow()

    private val _selectedBoardType = MutableStateFlow("plaque_ba13_standard")
    val selectedBoardType: StateFlow<String> = _selectedBoardType.asStateFlow()
    private val _boardDimensions = MutableStateFlow("1.2x2.5")
    val boardDimensions: StateFlow<String> = _boardDimensions.asStateFlow()

    private val _selectedMontantType = MutableStateFlow<String?>(null)
    val selectedMontantType: StateFlow<String?> = _selectedMontantType.asStateFlow()

    // Selected horizontal track (rail) profile id. null keeps the legacy choice
    // (rail_48 for framing systems, rail_70 for Aquapanel).
    private val _selectedRailType = MutableStateFlow<String?>(null)
    val selectedRailType: StateFlow<String?> = _selectedRailType.asStateFlow()

    private val _isDoubleSkin = MutableStateFlow(false)
    val isDoubleSkin: StateFlow<Boolean> = _isDoubleSkin.asStateFlow()

    private val _includeInsulation = MutableStateFlow(true)
    val includeInsulation: StateFlow<Boolean> = _includeInsulation.asStateFlow()

    private val _insulationType = MutableStateFlow("laine_de_verre_50mm")
    val insulationType: StateFlow<String> = _insulationType.asStateFlow()

    private val _customLaborRate = MutableStateFlow("")
    val customLaborRate: StateFlow<String> = _customLaborRate.asStateFlow()

    // Website parity: optional tax override (blank means use the country's fiscal profile).
    private val _tvaRateOverride = MutableStateFlow("")
    val tvaRateOverride: StateFlow<String> = _tvaRateOverride.asStateFlow()

    private val _fiscalStampEnabled = MutableStateFlow(true)
    val fiscalStampEnabled: StateFlow<Boolean> = _fiscalStampEnabled.asStateFlow()

    private val _retentionRate = MutableStateFlow("0")
    val retentionRate: StateFlow<String> = _retentionRate.asStateFlow()

    // Entraxe des montants (maillage vertical en mètres). Default = 0.40 m (40 cm)
    // so that legacy calculation behavior is preserved until the user makes a choice.
    private val _entraxeMontants = MutableStateFlow(0.40)
    val entraxeMontants: StateFlow<Double> = _entraxeMontants.asStateFlow()

    // STEP 9C1 — Joints & Finition inputs (PLACO_JOINTS_FINITION).
    // Defaults preserve legacy behaviour: empty strings / quality 1 map to the
    // existing CalculationParams defaults, so nothing changes until the user types.
    private val _inputJointsMeters = MutableStateFlow("")
    val inputJointsMeters: StateFlow<String> = _inputJointsMeters.asStateFlow()

    private val _inputCornersCount = MutableStateFlow("")
    val inputCornersCount: StateFlow<String> = _inputCornersCount.asStateFlow()

    private val _finishQuality = MutableStateFlow(1)
    val finishQuality: StateFlow<Int> = _finishQuality.asStateFlow()

    // STEP 9C2 — Trappes de visite lines (empty => zero impact on calculations).
    private val _trappeLines = MutableStateFlow<List<ConstructionCalculator.TrappeLine>>(emptyList())
    val trappeLines: StateFlow<List<ConstructionCalculator.TrappeLine>> = _trappeLines.asStateFlow()

    fun setTrade(trade: TradeCategory) {
        _selectedTrade.value = trade
        val defaultType = CalculationType.entries.firstOrNull { it.trade == trade } ?: CalculationType.PLACO_PLAFOND_FIXE
        _selectedCalculationType.value = defaultType
        _wasteMargin.value = ((defaultType.defaultWasteMargin - 1.0) * 100).toInt().toString()
        _inputHeight.value = if (defaultType == CalculationType.DEMOLITION_DEPOSE) "15.0" else "2.8"
    }

    fun setCalculationType(type: CalculationType) {
        _selectedTrade.value = type.trade
        _selectedCalculationType.value = type
        _wasteMargin.value = ((type.defaultWasteMargin - 1.0) * 100).toInt().toString()
        _inputHeight.value = if (type == CalculationType.DEMOLITION_DEPOSE) "15.0" else "2.8"
    }

    fun setInputArea(value: String) { _inputArea.value = value }
    fun setInputLength(value: String) {
        _inputLength.value = value
        recalcAreaFromDimensions()
    }
    fun setInputWidth(value: String) {
        _inputWidth.value = value
        recalcAreaFromDimensions()
    }
    fun setInputHeight(value: String) { _inputHeight.value = value }
    fun setInputLinearMeters(value: String) { _inputLinearMeters.value = value }
    fun setInputUnitsCount(value: String) { _inputUnitsCount.value = value }
    fun setOpeningArea(value: String) { _openingArea.value = value }
    fun setWasteMargin(value: String) { _wasteMargin.value = value }
    fun setSelectedBoardType(type: String) { _selectedBoardType.value = type }
    fun setBoardDimensions(value: String) {
        if (value in setOf("1.2x2.5", "1.2x2.8", "1.2x3.0", "0.9x2.0")) _boardDimensions.value = value
    }
    fun setSelectedMontantType(value: String?) { _selectedMontantType.value = value }
    fun setSelectedRailType(value: String?) { _selectedRailType.value = value }
    fun setIsDoubleSkin(doubleSkin: Boolean) { _isDoubleSkin.value = doubleSkin }
    fun setIncludeInsulation(include: Boolean) { _includeInsulation.value = include }
    fun setInsulationType(type: String) { _insulationType.value = type }
    fun setCustomLaborRate(rate: String) { _customLaborRate.value = rate }
    fun setTvaRateOverride(rate: String) { _tvaRateOverride.value = rate }
    fun setFiscalStampEnabled(enabled: Boolean) { _fiscalStampEnabled.value = enabled }
    fun setRetentionRate(rate: String) {
        _retentionRate.value = rate.toDoubleOrNull()?.coerceIn(0.0, 10.0)?.toInt()?.toString() ?: "0"
    }

    fun setInputJointsMeters(value: String) { _inputJointsMeters.value = value }
    fun setInputCornersCount(value: String) { _inputCornersCount.value = value }
    fun setFinishQuality(quality: Int) { _finishQuality.value = quality }

    /** Adds or updates a trappe line; quantity <= 0 removes the line. */
    fun setTrappeQuantity(dimensionKey: String, quantity: Int) {
        val updated = _trappeLines.value
            .filterNot { it.dimensionKey == dimensionKey }
            .toMutableList()
        if (quantity > 0) {
            updated.add(ConstructionCalculator.TrappeLine(dimensionKey, quantity))
        }
        _trappeLines.value = updated
    }

    fun clearTrappeLines() { _trappeLines.value = emptyList() }

    fun setEntraxeMontantsMeters(value: Double) {
        // Only 40 cm (0.40 m) and 60 cm (0.60 m) are supported in Phase C.
        require(value == 0.40 || value == 0.60) {
            "Entraxe must be 0.40m or 0.60m"
        }
        _entraxeMontants.value = value
    }

    private fun recalcAreaFromDimensions() {
        val l = _inputLength.value.toDoubleOrNull() ?: 0.0
        val w = _inputWidth.value.toDoubleOrNull() ?: 0.0
        if (l > 0 && w > 0) {
            _inputArea.value = (l * w).toString()
        }
    }

    fun computeSummaryFlow(
        pricesListFlow: StateFlow<List<MaterialPriceEntity>>,
        countryFlow: StateFlow<Country>
    ): StateFlow<CalculationSummary?> {
        return combine(
            _selectedCalculationType,
            _inputArea,
            _inputLength,
            _inputWidth,
            _inputHeight,
            _inputLinearMeters,
            _inputUnitsCount,
            _openingArea,
            _wasteMargin,
            _selectedBoardType,
            _selectedMontantType,
            _selectedRailType,
            _isDoubleSkin,
            _includeInsulation,
            _insulationType,
            _customLaborRate,
            _entraxeMontants,
            _inputJointsMeters,
            _inputCornersCount,
            _finishQuality,
            _trappeLines,
            pricesListFlow,
            countryFlow,
            _tvaRateOverride,
            _fiscalStampEnabled,
            _retentionRate,
            _boardDimensions
        ) { args ->
            val type = args[0] as CalculationType
            val areaStr = args[1] as String
            val lengthStr = args[2] as String
            val widthStr = args[3] as String
            val heightStr = args[4] as String
            val linearMStr = args[5] as String
            val unitsStr = args[6] as String
            val openingAreaStr = args[7] as String
            val wasteStr = args[8] as String
            val boardType = args[9] as String
            val montantType = args[10] as String?
            val railType = args[11] as String?
            val isDouble = args[12] as Boolean
            val includeIns = args[13] as Boolean
            val insType = args[14] as String
            val customLabor = args[15] as String
            val entraxe = args[16] as Double
            val jointsMetersStr = args[17] as String
            val cornersCountStr = args[18] as String
            val finishQualityVal = args[19] as Int
            val trappes = @Suppress("UNCHECKED_CAST") (args[20] as List<ConstructionCalculator.TrappeLine>)
            @Suppress("UNCHECKED_CAST")
            val prices = args[21] as List<MaterialPriceEntity>
            val country = args[22] as Country
            val tvaRateOverride = (args[23] as String).toDoubleOrNull()?.takeIf { it in listOf(0.0, 7.0, 13.0, 19.0) }
            val fiscalStampEnabled = args[24] as Boolean
            val retentionRate = ((args[25] as String).toDoubleOrNull() ?: 0.0).coerceIn(0.0, 10.0)
            val boardDimensions = args[26] as String
            val boardWidthM = boardDimensions.substringBefore("x").toDoubleOrNull() ?: 1.2
            val boardLengthM = boardDimensions.substringAfter("x").toDoubleOrNull() ?: 2.5

            val length = lengthStr.toDoubleOrNull() ?: 0.0
            val width = widthStr.toDoubleOrNull() ?: 0.0
            val height = heightStr.toDoubleOrNull() ?: 2.8
            val linearM = linearMStr.toDoubleOrNull() ?: 0.0
            val units = unitsStr.toDoubleOrNull() ?: 1.0
            val openingArea = (openingAreaStr.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
            val waste = (wasteStr.toDoubleOrNull() ?: 10.0).coerceIn(0.0, 50.0)
            val customLaborVal = customLabor.toDoubleOrNull()
            val jointsMetersVal = jointsMetersStr.toDoubleOrNull() ?: 0.0
            val cornersCountVal = cornersCountStr.toDoubleOrNull() ?: 0.0

            var area = areaStr.toDoubleOrNull() ?: 0.0
            var perimeter = 0.0

            if (length > 0 && width > 0) {
                area = length * width
                perimeter = 2 * (length + width)
            } else if (area > 0) {
                perimeter = 4 * Math.sqrt(area)
            }

            // Preserve legacy behavior by default (packageId == null).
            // Build a deterministic, package-aware price map using domain helper.
            val priceMap = com.example.domain.PriceSelection.selectPriceMap(prices, null)

            val params = ConstructionCalculator.CalculationParams(
                type = type,
                area = area,
                perimeter = perimeter,
                height = height,
                linearMeters = linearM,
                unitCount = units,
                netOpeningsArea = openingArea,
                wasteMarginPercent = waste,
                boardType = boardType,
                boardWidthM = boardWidthM,
                boardLengthM = boardLengthM,
                isDoubleSkin = isDouble,
                includeInsulation = includeIns,
                insulationType = insType,
                montantType = montantType,
                railType = railType,
                customLaborRatePerM2 = customLaborVal,
                entraxeMontants = entraxe,
                jointsMeters = jointsMetersVal,
                cornersCount = cornersCountVal,
                finishQuality = finishQualityVal,
                trappes = trappes,
                tvaRate = tvaRateOverride,
                fiscalStampAmount = if (fiscalStampEnabled && country == Country.TUNISIA) 1.0 else 0.0,
                retentionRate = retentionRate
            )

            calculateUseCase(params, priceMap, country)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }
}
