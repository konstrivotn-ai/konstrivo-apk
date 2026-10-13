package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.components.designsystem.KonstrivoBottomBar
import com.example.ui.components.designsystem.KonstrivoCenterFab
import com.example.ui.components.designsystem.KonstrivoNavItem
import com.example.ui.components.designsystem.KonstrivoTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.components.designsystem.tr

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Match the repository's approved dark-navy / gold screen reference consistently,
            // independent of the device's system appearance setting.
            MyApplicationTheme(darkTheme = true) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val language by viewModel.selectedLanguage.collectAsState()
    val country by viewModel.selectedCountry.collectAsState()
    val selectedTradeForDetails by viewModel.selectedTradeForDetails.collectAsState()
    val showSplash by viewModel.showSplash.collectAsState()
    val showLogin by viewModel.showLoginScreen.collectAsState()
    val showCreateAccount by viewModel.showCreateAccountScreen.collectAsState()
    val showMaterialsCatalog by viewModel.showMaterialsCatalog.collectAsState()
    val showAdmin by viewModel.showAdminDashboard.collectAsState()
    val showServices by viewModel.showServices.collectAsState()
    val showTools by viewModel.showTools.collectAsState()
    val showDocs by viewModel.showDocs.collectAsState()
    val showNotifications by viewModel.showNotifications.collectAsState()
    val showProfile by viewModel.showProfile.collectAsState()
    val showSubscription by viewModel.showSubscription.collectAsState()
    val projectDetailsId by viewModel.showProjectDetailsForEstimate.collectAsState()
    val showMaterialCalc by viewModel.showMaterialCalc.collectAsState()

    val currentYear = remember { java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) }
    var showLangMenu by remember { mutableStateOf(false) }

    // PHASE 1 — RTL: Arabic & Derja render right-to-left; French & English stay LTR.
    // Applied once at the app root so every screen, bar, list and card inherits it.
    val layoutDirection = when (language) {
        AppLanguage.ARABIC, AppLanguage.DERJA -> LayoutDirection.Rtl
        AppLanguage.FRENCH, AppLanguage.ENGLISH -> LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    if (showSplash) {
        // Timed splash; no network calls.
        SplashScreen(
            language = language,
            onFinished = { viewModel.dismissSplash() }
        )
    } else if (showLogin) {
        androidx.activity.compose.BackHandler {
            viewModel.closeLoginScreen()
        }
        LoginScreen(
            viewModel = viewModel,
            onBack = { viewModel.closeLoginScreen() },
            onLoginSuccess = {
                viewModel.closeLoginScreen()
                viewModel.openAdminDashboard()
            },
            onUserLoginSuccess = { viewModel.closeLoginScreen() },
            onCreateAccount = {
                viewModel.closeLoginScreen()
                viewModel.openCreateAccountScreen()
            }
        )
    } else if (showCreateAccount) {
        androidx.activity.compose.BackHandler {
            viewModel.closeCreateAccountScreen()
        }
        CreateAccountScreen(
            viewModel = viewModel,
            onBack = { viewModel.closeCreateAccountScreen() },
            onSignIn = {
                viewModel.closeCreateAccountScreen()
                viewModel.openLoginScreen()
            }
        )
    } else if (showMaterialsCatalog) {
        androidx.activity.compose.BackHandler {
            viewModel.closeMaterialsCatalog()
        }
        MaterialsCatalogScreen(
            viewModel = viewModel,
            onBack = { viewModel.closeMaterialsCatalog() }
        )
    } else if (showAdmin) {
        androidx.activity.compose.BackHandler {
            viewModel.closeAdminDashboard()
        }
        AdminDashboardScreen(
            viewModel = viewModel,
            onLogout = { viewModel.closeAdminDashboard() }
        )
    } else if (showServices) {
        androidx.activity.compose.BackHandler { viewModel.closeServices() }
        ServicesScreen(viewModel = viewModel, onBack = { viewModel.closeServices() })
    } else if (showTools) {
        androidx.activity.compose.BackHandler { viewModel.closeTools() }
        ConstructionToolsScreen(viewModel = viewModel, onBack = { viewModel.closeTools() })
    } else if (showDocs) {
        androidx.activity.compose.BackHandler { viewModel.closeDocs() }
        TechnicalDocsScreen(viewModel = viewModel, onBack = { viewModel.closeDocs() })
    } else if (showNotifications) {
        androidx.activity.compose.BackHandler { viewModel.closeNotifications() }
        NotificationsScreen(viewModel = viewModel, onBack = { viewModel.closeNotifications() })
    } else if (showProfile) {
        androidx.activity.compose.BackHandler { viewModel.closeProfile() }
        ProfileScreen(
            viewModel = viewModel,
            onBack = { viewModel.closeProfile() },
            onUpgrade = { viewModel.closeProfile(); viewModel.openSubscription() },
        )
    } else if (showSubscription) {
        androidx.activity.compose.BackHandler { viewModel.closeSubscription() }
        SubscriptionScreen(viewModel = viewModel, onBack = { viewModel.closeSubscription() })
    } else if (projectDetailsId != null) {
        androidx.activity.compose.BackHandler { viewModel.closeProjectDetails() }
        ProjectDetailsScreen(estimateId = projectDetailsId!!, viewModel = viewModel, onBack = { viewModel.closeProjectDetails() })
    } else if (showMaterialCalc) {
        androidx.activity.compose.BackHandler { viewModel.closeMaterialCalc() }
        MaterialPriceCalculatorScreen(viewModel = viewModel, onBack = { viewModel.closeMaterialCalc() })
    } else if (selectedTradeForDetails != null) {
        androidx.activity.compose.BackHandler {
            viewModel.closeTradeDetails()
        }
        TradeDetailsScreen(
            trade = selectedTradeForDetails!!,
            language = language,
            country = country,
            onBack = { viewModel.closeTradeDetails() },
            onNavigateToCalculator = { calcType ->
                val category = selectedTradeForDetails?.category
                viewModel.closeTradeDetails()
                viewModel.navigateToCalculator(category, calcType)
            }
        )
    } else {
        // PHASE 5 — Consistent back behavior: system back returns to Home from any
        // tab instead of leaving the app. From Home the default (exit) applies.
        androidx.activity.compose.BackHandler(enabled = currentTab != AppTab.HOME) {
            viewModel.selectTab(AppTab.HOME)
        }
        Scaffold(
        topBar = {
            // The Home screen owns its branded header; avoid stacking a second global bar.
            if (currentTab != AppTab.HOME) {
            // PHASE 2.1 — Branded navy shell top bar (Konstrivo identity).
            val tabTitle = when (language) {
                AppLanguage.DERJA -> currentTab.titleDerja
                AppLanguage.ARABIC -> currentTab.titleAr
                AppLanguage.FRENCH -> currentTab.titleFr
                AppLanguage.ENGLISH -> currentTab.titleEn
            }
            KonstrivoTopBar(
                title = tabTitle,
                contextLine = "${country.currencyCode} (${country.currencySymbol}) • $currentYear",
                onBrandClick = { viewModel.selectTab(AppTab.HOME) },
                actions = {
                    // Language Switcher Chip / Menu
                    Box {
                        Surface(
                            onClick = { showLangMenu = true },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.14f),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .heightIn(min = 48.dp)
                                .testTag("top_language_selector")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Language, contentDescription = language.tr("Langue","Langue","اللغة","اللغة"), tint = Color.White, modifier = Modifier.size(16.dp))
                                Text(
                                    text = when (language) {
                                        AppLanguage.FRENCH -> "FR"
                                        AppLanguage.ARABIC -> "عربي"
                                        AppLanguage.ENGLISH -> "EN"
                                        AppLanguage.DERJA -> "درجة"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = showLangMenu,
                            onDismissRequest = { showLangMenu = false }
                        ) {
                            AppLanguage.entries.forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = lang.displayName,
                                                fontWeight = if (lang == language) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Text(
                                                text = lang.subTitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    },
                                    leadingIcon = {
                                        if (lang == language) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        showLangMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Account entry point → existing Settings / Owner login area (Phase E).
                    // Uses the existing tab navigation; no new auth system.
                    IconButton(
                        onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                        modifier = Modifier.testTag("top_account_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = language.tr("Compte","Compte","الحساب","الحساب"),
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            )
            }
        },
        bottomBar = {
            val primaryTabs = listOf(
                AppTab.HOME,
                AppTab.CALCULATOR,
                AppTab.PRICES,
                AppTab.ESTIMATES,
                AppTab.PROJECTS,
                AppTab.MORE
            )

            // PHASE 2.1 — Branded bottom bar via the design system component.
            val navItems = primaryTabs.map { tab ->
                KonstrivoNavItem(
                    key = tab.name,
                    label = when (language) {
                        AppLanguage.DERJA -> tab.titleDerja
                        AppLanguage.ARABIC -> tab.titleAr
                        AppLanguage.FRENCH -> tab.titleFr
                        AppLanguage.ENGLISH -> tab.titleEn
                    },
                    icon = when (tab) {
                        AppTab.HOME -> Icons.Default.Home
                        AppTab.CALCULATOR -> Icons.Default.Calculate
                        AppTab.ESTIMATES -> Icons.Default.Description
                        AppTab.PRICES -> Icons.Default.MonetizationOn
                        AppTab.PROJECTS -> Icons.Default.Apartment
                        AppTab.MORE -> Icons.Default.MoreHoriz
                        AppTab.GUIDE -> Icons.AutoMirrored.Filled.MenuBook
                        AppTab.SETTINGS -> Icons.Default.Settings
                    }
                )
            }

            KonstrivoBottomBar(
                items = navItems,
                isSelected = { item ->
                    // MORE is a hub; treat nested tabs as selected state in the bar.
                    item.key == currentTab.name ||
                            (item.key == "MORE" && (currentTab == AppTab.GUIDE || currentTab == AppTab.SETTINGS))
                },
                onSelect = { item ->
                    viewModel.selectTab(AppTab.valueOf(item.key))
                },
                showFabSpacer = true
            )
        },
        floatingActionButton = {
            // Collage shows a center '+' FAB across main sections.
            // Safe behavior: route to Calculator (no new business logic).
            KonstrivoCenterFab(onClick = { viewModel.navigateToCalculator() })
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> HomeScreen(viewModel = viewModel)
                AppTab.CALCULATOR -> CalculatorScreen(viewModel = viewModel)
                AppTab.ESTIMATES -> ReportsDevisScreen(viewModel = viewModel)
                AppTab.MORE -> MoreScreen(viewModel = viewModel)
                AppTab.PRICES -> MarketPricesScreen(viewModel = viewModel)
                AppTab.GUIDE -> ChantierGuideScreen(viewModel = viewModel)
                AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                AppTab.PROJECTS -> ProjectsScreen(viewModel = viewModel)
            }
        }
    }
    }
}
}
