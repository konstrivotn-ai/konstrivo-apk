package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MaterialPriceEntity
import com.example.data.local.ProjectEstimateEntity
import com.example.data.local.UserSettingsEntity
import com.example.BuildConfig
import com.example.calculator.ConstructionCalculator
import com.example.data.model.*
import com.example.data.repository.ConstructionRepository
import com.example.domain.security.AdminAuth
import com.example.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val titleFr: String, val titleAr: String, val titleDerja: String, val titleEn: String) {
    HOME("Accueil", "الرئيسية", "الرئيسية", "Home"),
    CALCULATOR("Calculer", "الحاسبة", "الحاسبة", "Calculate"),
    PRICES("Tarifs", "أسعار السوق", "أسعار السلعة", "Prices"),
    ESTIMATES("Devis", "الـ Devis", "الـ Devis", "Quotes"),
    PROJECTS("Chantiers", "المشاريع", "الشانتي", "Projects"),
    MORE("Plus", "المزيد", "المزيد", "More"),
    GUIDE("Guide Chantier", "دليل الشانتي", "دليل الشانتي", "Guide"),
    SETTINGS("Paramètres", "الإعدادات", "الإعدادات", "Settings")
}

/**
 * MainViewModel coordinates global app state, top-level navigation, and delegates
 * domain operations to dedicated feature ViewModels and UseCases.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // ─────────────────────────────────────────────────────────────────────────────
    // Splash / App entry
    // ─────────────────────────────────────────────────────────────────────────────
    // Pure UI state: we keep the app offline-first; splash is timed and does
    // not depend on network or backend initialization.
    private val _showSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean> = _showSplash.asStateFlow()

    fun dismissSplash() {
        _showSplash.value = false
    }

    // Repository & Database
    private val repository: ConstructionRepository

    // Domain Use Cases
    private val calculateUseCase = CalculateConstructionUseCase()
    private val getMarketPricesUseCase: GetMarketPricesUseCase
    private val updateMaterialPriceUseCase: UpdateMaterialPriceUseCase
    private val resetPricesUseCase: ResetPricesUseCase
    private val getSavedQuotesUseCase: GetSavedQuotesUseCase
    private val saveQuoteUseCase: SaveQuoteUseCase
    private val deleteQuoteUseCase: DeleteQuoteUseCase
    private val getUserSettingsUseCase: GetUserSettingsUseCase
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
    private val formatWhatsAppQuoteUseCase = FormatWhatsAppQuoteUseCase()

    // Sub-ViewModels (Modular architecture)
    val calculatorViewModel = CalculatorViewModel(calculateUseCase)
    val pricesViewModel: PricesViewModel
    val devisViewModel: DevisViewModel
    val settingsViewModel: SettingsViewModel

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = ConstructionRepository(
            db.materialPriceDao(),
            db.projectEstimateDao(),
            db.userSettingsDao()
        )

        getMarketPricesUseCase = GetMarketPricesUseCase(repository)
        updateMaterialPriceUseCase = UpdateMaterialPriceUseCase(repository)
        resetPricesUseCase = ResetPricesUseCase(repository)
        getSavedQuotesUseCase = GetSavedQuotesUseCase(repository)
        saveQuoteUseCase = SaveQuoteUseCase(repository)
        deleteQuoteUseCase = DeleteQuoteUseCase(repository)
        getUserSettingsUseCase = GetUserSettingsUseCase(repository)
        updateUserSettingsUseCase = UpdateUserSettingsUseCase(repository)

        pricesViewModel = PricesViewModel(getMarketPricesUseCase, updateMaterialPriceUseCase, resetPricesUseCase)
        devisViewModel = DevisViewModel(getSavedQuotesUseCase, saveQuoteUseCase, deleteQuoteUseCase, formatWhatsAppQuoteUseCase)
        settingsViewModel = SettingsViewModel(getUserSettingsUseCase, updateUserSettingsUseCase)
    }

    // Top-Level UI Navigation State
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Trade Details Navigation State (Generic Métier Details)
    private val _selectedTradeForDetails = MutableStateFlow<com.example.domain.model.Trade?>(null)
    val selectedTradeForDetails: StateFlow<com.example.domain.model.Trade?> = _selectedTradeForDetails.asStateFlow()

    fun openTradeDetails(trade: com.example.domain.model.Trade) {
        _selectedTradeForDetails.value = trade
    }

    fun closeTradeDetails() {
        _selectedTradeForDetails.value = null
    }

    // Delegated Global State Flows
    val userSettings: StateFlow<UserSettingsEntity> get() = settingsViewModel.userSettings
    val selectedCountry: StateFlow<Country> get() = settingsViewModel.selectedCountry
    val selectedLanguage: StateFlow<AppLanguage> get() = settingsViewModel.selectedLanguage

    // Delegated Prices State Flows
    val pricesList: StateFlow<List<MaterialPriceEntity>> get() = pricesViewModel.pricesList
    val priceSearchQuery: StateFlow<String> get() = pricesViewModel.priceSearchQuery
    val selectedPriceCategory: StateFlow<String> get() = pricesViewModel.selectedPriceCategory

    // Delegated Calculator State Flows
    val selectedTrade: StateFlow<TradeCategory> get() = calculatorViewModel.selectedTrade
    val selectedCalculationType: StateFlow<CalculationType> get() = calculatorViewModel.selectedCalculationType
    val inputArea: StateFlow<String> get() = calculatorViewModel.inputArea
    val inputLength: StateFlow<String> get() = calculatorViewModel.inputLength
    val inputWidth: StateFlow<String> get() = calculatorViewModel.inputWidth
    val inputHeight: StateFlow<String> get() = calculatorViewModel.inputHeight
    val inputLinearMeters: StateFlow<String> get() = calculatorViewModel.inputLinearMeters
    val inputUnitsCount: StateFlow<String> get() = calculatorViewModel.inputUnitsCount
    val openingArea: StateFlow<String> get() = calculatorViewModel.openingArea
    val wasteMargin: StateFlow<String> get() = calculatorViewModel.wasteMargin
    val selectedBoardType: StateFlow<String> get() = calculatorViewModel.selectedBoardType
    val selectedMontantType: StateFlow<String?> get() = calculatorViewModel.selectedMontantType
    val selectedRailType: StateFlow<String?> get() = calculatorViewModel.selectedRailType
    val isDoubleSkin: StateFlow<Boolean> get() = calculatorViewModel.isDoubleSkin
    val includeInsulation: StateFlow<Boolean> get() = calculatorViewModel.includeInsulation
    val insulationType: StateFlow<String> get() = calculatorViewModel.insulationType
    val customLaborRate: StateFlow<String> get() = calculatorViewModel.customLaborRate
    val tvaRateOverride: StateFlow<String> get() = calculatorViewModel.tvaRateOverride
    val fiscalStampEnabled: StateFlow<Boolean> get() = calculatorViewModel.fiscalStampEnabled
    val retentionRate: StateFlow<String> get() = calculatorViewModel.retentionRate
    val entraxeMontants: StateFlow<Double> get() = calculatorViewModel.entraxeMontants
    val inputJointsMeters: StateFlow<String> get() = calculatorViewModel.inputJointsMeters
    val inputCornersCount: StateFlow<String> get() = calculatorViewModel.inputCornersCount
    val finishQuality: StateFlow<Int> get() = calculatorViewModel.finishQuality
    val trappeLines: StateFlow<List<ConstructionCalculator.TrappeLine>> get() = calculatorViewModel.trappeLines

    // Delegated Quotes State Flows
    val savedEstimates: StateFlow<List<ProjectEstimateEntity>> get() = devisViewModel.savedEstimates

    // Admin mode (in-memory flag, no DB changes)
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // Live Calculation Summary Flow
    val calculationSummary: StateFlow<CalculationSummary?> by lazy {
        calculatorViewModel.computeSummaryFlow(pricesViewModel.pricesList, settingsViewModel.selectedCountry)
    }

    init {
        viewModelScope.launch {
            settingsViewModel.userSettings.collect { settings ->
                pricesViewModel.loadPricesForCountry(settings.selectedCountry)
            }
        }
    }

    // Admin actions (very small, local-authority guarded)
    fun loginAdmin(email: String, password: String): Boolean {
        // R-001: local authority gate. Credentials come from BuildConfig (untracked
        // .env via secrets plugin); PBKDF2-HMAC-SHA256 verification; fail-closed.
        val ok = AdminAuth.verify(
            email = email,
            password = password,
            expectedEmail = BuildConfig.ADMIN_EMAIL,
            credential = BuildConfig.ADMIN_PASSWORD_CREDENTIAL,
        )
        _isAdmin.value = ok
        return ok
    }

    fun logoutAdmin() {
        _isAdmin.value = false
        adminFromCloudSession = false
    }

    // ── Cloud account session (shared website /api/v1 identity) ───────────────
    // Single source of truth for the logged-in customer UI. The session is
    // restored at startup, and the role used for admin routing is always the
    // one validated by the backend (GET /users/me) — never the locally stored
    // preference value alone.
    data class CloudAuthUiState(
        val isRestoring: Boolean = true,
        val session: com.example.data.remote.CloudAuthClient.Session? = null,
        val isVerified: Boolean = false,
    )

    private val _cloudAuthState = MutableStateFlow(CloudAuthUiState())
    val cloudAuthState: StateFlow<CloudAuthUiState> = _cloudAuthState.asStateFlow()

    // Tracks whether admin mode currently comes from a server-validated cloud
    // role, so signing out of the cloud account never clears a local PBKDF2
    // admin login (and vice versa).
    private var adminFromCloudSession = false

    fun beginCloudSessionRestore() {
        _cloudAuthState.value = CloudAuthUiState(isRestoring = true)
    }

    /** Apply a cloud session (restored at startup or obtained by login) to the UI state. */
    fun applyCloudSession(session: com.example.data.remote.CloudAuthClient.Session?, verified: Boolean) {
        _cloudAuthState.value = CloudAuthUiState(isRestoring = false, session = session, isVerified = verified)
        if (session != null && verified && session.role.equals("admin", ignoreCase = true)) {
            adminFromCloudSession = true
            _isAdmin.value = true
        } else if (session == null && adminFromCloudSession) {
            adminFromCloudSession = false
            _isAdmin.value = false
        }
    }

    /** Sign out of the shared website account on this device only. */
    fun signOutCloudSession(context: Context) {
        com.example.data.remote.CloudAuthClient.logout(context)
        applyCloudSession(null, verified = false)
    }

    // STEP 2: full-screen overlay navigation (state-based, mirroring the existing
    // selectedTradeForDetails mechanism). No navigation library is introduced.
    private val _showLoginScreen = MutableStateFlow(false)
    val showLoginScreen: StateFlow<Boolean> = _showLoginScreen.asStateFlow()

    private val _showCreateAccountScreen = MutableStateFlow(false)
    val showCreateAccountScreen: StateFlow<Boolean> = _showCreateAccountScreen.asStateFlow()

    private val _showMaterialsCatalog = MutableStateFlow(false)
    val showMaterialsCatalog: StateFlow<Boolean> = _showMaterialsCatalog.asStateFlow()

    private val _showAdminDashboard = MutableStateFlow(false)
    val showAdminDashboard: StateFlow<Boolean> = _showAdminDashboard.asStateFlow()

    // Reference-board overlay destinations (state-based, same pattern as login).
    // Pure navigation flags — no business logic, no new data sources.
    private val _showServices = MutableStateFlow(false)
    val showServices: StateFlow<Boolean> = _showServices.asStateFlow()
    private val _showTools = MutableStateFlow(false)
    val showTools: StateFlow<Boolean> = _showTools.asStateFlow()
    private val _showDocs = MutableStateFlow(false)
    val showDocs: StateFlow<Boolean> = _showDocs.asStateFlow()
    private val _showNotifications = MutableStateFlow(false)
    val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()
    private val _showProfile = MutableStateFlow(false)
    val showProfile: StateFlow<Boolean> = _showProfile.asStateFlow()
    private val _showSubscription = MutableStateFlow(false)
    val showSubscription: StateFlow<Boolean> = _showSubscription.asStateFlow()
    private val _showProjectDetailsForEstimate = MutableStateFlow<Long?>(null)
    val showProjectDetailsForEstimate: StateFlow<Long?> = _showProjectDetailsForEstimate.asStateFlow()
    private val _showMaterialCalc = MutableStateFlow(false)
    val showMaterialCalc: StateFlow<Boolean> = _showMaterialCalc.asStateFlow()

    fun openLoginScreen() {
        _showLoginScreen.value = true
    }

    fun closeLoginScreen() {
        _showLoginScreen.value = false
    }

    fun openCreateAccountScreen() {
        _showCreateAccountScreen.value = true
    }

    fun closeCreateAccountScreen() {
        _showCreateAccountScreen.value = false
    }

    fun openMaterialsCatalog() {
        _showMaterialsCatalog.value = true
    }

    fun closeMaterialsCatalog() {
        _showMaterialsCatalog.value = false
    }

    fun openAdminDashboard() {
        _showAdminDashboard.value = true
    }

    fun closeAdminDashboard() {
        _showAdminDashboard.value = false
    }

    fun openServices() { _showServices.value = true }
    fun closeServices() { _showServices.value = false }
    fun openTools() { _showTools.value = true }
    fun closeTools() { _showTools.value = false }
    fun openDocs() { _showDocs.value = true }
    fun closeDocs() { _showDocs.value = false }
    fun openNotifications() { _showNotifications.value = true }
    fun closeNotifications() { _showNotifications.value = false }
    fun openProfile() { _showProfile.value = true }
    fun closeProfile() { _showProfile.value = false }
    fun openSubscription() { _showSubscription.value = true }
    fun closeSubscription() { _showSubscription.value = false }
    fun openProjectDetails(estimateId: Long) { _showProjectDetailsForEstimate.value = estimateId }
    fun closeProjectDetails() { _showProjectDetailsForEstimate.value = null }
    fun openMaterialCalc() { _showMaterialCalc.value = true }
    fun closeMaterialCalc() { _showMaterialCalc.value = false }

    // Navigation methods
    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun navigateToCalculator(trade: TradeCategory? = null, type: CalculationType? = null) {
        if (trade != null) {
            calculatorViewModel.setTrade(trade)
        }
        if (type != null) {
            calculatorViewModel.setCalculationType(type)
        }
        _currentTab.value = AppTab.CALCULATOR
    }

    // Settings actions
    fun setCountry(country: Country) {
        settingsViewModel.setCountry(country) { countryCode ->
            pricesViewModel.loadPricesForCountry(countryCode)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        settingsViewModel.setLanguage(lang)
    }

    fun updateUserSettings(settings: UserSettingsEntity) {
        settingsViewModel.updateUserSettings(settings)
    }

    // Prices actions
    fun setPriceSearchQuery(query: String) = pricesViewModel.setPriceSearchQuery(query)
    fun setSelectedPriceCategory(category: String) = pricesViewModel.setSelectedPriceCategory(category)
    fun updateMaterialPrice(id: String, newPrice: Double) = pricesViewModel.updateMaterialPrice(id, newPrice)
    fun resetPricesToDefault() = pricesViewModel.resetPricesToDefault(selectedCountry.value.code)

    // Calculator actions
    fun setTrade(trade: TradeCategory) = calculatorViewModel.setTrade(trade)
    fun setCalculationType(type: CalculationType) = calculatorViewModel.setCalculationType(type)
    fun setInputArea(value: String) = calculatorViewModel.setInputArea(value)
    fun setInputLength(value: String) = calculatorViewModel.setInputLength(value)
    fun setInputWidth(value: String) = calculatorViewModel.setInputWidth(value)
    fun setInputHeight(value: String) = calculatorViewModel.setInputHeight(value)
    fun setInputLinearMeters(value: String) = calculatorViewModel.setInputLinearMeters(value)
    fun setInputUnitsCount(value: String) = calculatorViewModel.setInputUnitsCount(value)
    fun setOpeningArea(value: String) = calculatorViewModel.setOpeningArea(value)
    fun setWasteMargin(value: String) = calculatorViewModel.setWasteMargin(value)
    fun setSelectedBoardType(type: String) = calculatorViewModel.setSelectedBoardType(type)
    fun setSelectedMontantType(type: String?) = calculatorViewModel.setSelectedMontantType(type)
    fun setSelectedRailType(type: String?) = calculatorViewModel.setSelectedRailType(type)
    fun setIsDoubleSkin(doubleSkin: Boolean) = calculatorViewModel.setIsDoubleSkin(doubleSkin)
    fun setIncludeInsulation(include: Boolean) = calculatorViewModel.setIncludeInsulation(include)
    fun setInsulationType(type: String) = calculatorViewModel.setInsulationType(type)
    fun setCustomLaborRate(rate: String) = calculatorViewModel.setCustomLaborRate(rate)
    fun setTvaRateOverride(rate: String) = calculatorViewModel.setTvaRateOverride(rate)
    fun setFiscalStampEnabled(enabled: Boolean) = calculatorViewModel.setFiscalStampEnabled(enabled)
    fun setRetentionRate(rate: String) = calculatorViewModel.setRetentionRate(rate)

    fun setEntraxeMontantsMeters(value: Double) = calculatorViewModel.setEntraxeMontantsMeters(value)
    fun setInputJointsMeters(value: String) = calculatorViewModel.setInputJointsMeters(value)
    fun setInputCornersCount(value: String) = calculatorViewModel.setInputCornersCount(value)
    fun setFinishQuality(quality: Int) = calculatorViewModel.setFinishQuality(quality)
    fun setTrappeQuantity(dimensionKey: String, quantity: Int) = calculatorViewModel.setTrappeQuantity(dimensionKey, quantity)
    fun clearTrappeLines() = calculatorViewModel.clearTrappeLines()

    // Quotes actions
    fun saveEstimate(
        clientName: String,
        clientPhone: String,
        location: String,
        notes: String,
        onSaved: () -> Unit
    ) {
        val summary = calculationSummary.value ?: return
        devisViewModel.saveEstimate(summary, clientName, clientPhone, location, notes, onSaved)
    }

    fun deleteEstimate(id: Long) = devisViewModel.deleteEstimate(id)

    fun generateWhatsAppText(summary: CalculationSummary, clientName: String = "", location: String = ""): String {
        return devisViewModel.generateWhatsAppText(summary, userSettings.value, selectedLanguage.value, clientName, location)
    }

    fun shareWhatsApp(context: Context, text: String, phone: String = "") {
        devisViewModel.shareWhatsApp(context, text, phone)
    }
}
