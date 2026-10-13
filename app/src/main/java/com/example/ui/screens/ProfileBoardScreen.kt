package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.designsystem.KonstrivoRadius
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.theme.KonstrivoSlate
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onUpgrade: () -> Unit,
    onCreateAccount: () -> Unit = {},
    onSignIn: () -> Unit = {},
    onAdminAccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val language by viewModel.selectedLanguage.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    BoardScaffold(title = language.tr("Profile / Mon Compte", "Profile / My Account", "الحساب", "الحساب"), tag = "profile_screen", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { CloudAccountCard(viewModel, language, onSignIn, onCreateAccount, onAdminAccess) }
            item {
                Card(shape = RoundedCornerShape(KonstrivoRadius.Card),
                    colors = CardDefaults.cardColors(containerColor = KonstrivoNavy),
                    modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(language.tr("FREE vs PRO", "FREE vs PRO", "مجاني vs برو", "مجاني vs برو"),
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = KonstrivoGold)
                        val rows = listOf(
                            Triple(language.tr("Devis illimites", "Unlimited quotes", "عروض غير محدودة", "ديفي بلا حدود"), false, isAdmin),
                            Triple(language.tr("Catalogue complet", "Full catalog", "كتالوج كامل", "كتالوج كامل"), true, true),
                            Triple(language.tr("Support prioritaire", "Priority support", "دعم ذو اولوية", "دعم ذو اولوية"), false, isAdmin),
                        )
                        rows.forEach { (label, free, pro) ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                Text("FREE ${if (free) "OK" else "--"}  -  PRO ${if (pro) "OK" else "--"}",
                                    style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                        Button(onClick = onUpgrade,
                            colors = ButtonDefaults.buttonColors(containerColor = KonstrivoGold, contentColor = KonstrivoNavy),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(language.tr("Upgrade to PRO", "Upgrade to PRO", "الترقية الى برو", "الترقية الى برو"))
                        }
                    }
                }
            }
            item {
                BoardRowCard(BoardRow("Parametres", "Settings", "الاعدادات", "الاعدادات",
                    "Langue, pays, entreprise", "Language, country, company", "اللغة والدولة والشركة", "اللغة والبلد والشركة",
                    Icons.Default.Settings, KonstrivoSlate, "profile_settings",
                    onClick = { it.selectTab(AppTab.SETTINGS) }), viewModel)
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = KonstrivoGold)
                    Text("  KONSTRIVO PRO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

/**
 * Shared-account card: shows the website session state (restoring, signed
 * out, signed in) and the supported account actions. The role shown for
 * admin access is the one validated by the backend (GET /users/me) — the
 * locally stored preference value alone is never trusted.
 */
@Composable
private fun CloudAccountCard(
    viewModel: MainViewModel,
    language: com.example.data.model.AppLanguage,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    onAdminAccess: () -> Unit,
) {
    val context = LocalContext.current
    val cloudAuth by viewModel.cloudAuthState.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    Card(
        shape = RoundedCornerShape(KonstrivoRadius.Card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                language.tr("Compte KONSTRIVO", "KONSTRIVO Account", "حساب KONSTRIVO", "حساب KONSTRIVO"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = KonstrivoGold
            )
            when {
                cloudAuth.isRestoring -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = KonstrivoGold)
                        Text(
                            language.tr(
                                "Restauration de la session…",
                                "Restoring session…",
                                "جارٍ استرجاع الجلسة…",
                                "باش نرجعو الجلسة…"
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                cloudAuth.session == null -> {
                    Text(
                        language.tr(
                            "Connectez-vous pour retrouver vos comptes partagés avec le site konstrivo.konstrivo.blitz.cloud.",
                            "Sign in to share your account with the konstrivo.konstrivo.blitz.cloud website.",
                            "سجّل الدخول لمشاركة حسابك مع موقع konstrivo.konstrivo.blitz.cloud.",
                            "ادخل باش تشارك حسابك مع ست konstrivo.konstrivo.blitz.cloud."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onSignIn,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("profile_sign_in")
                        ) {
                            Text(language.tr("Se connecter", "Sign in", "تسجيل الدخول", "ادخل"))
                        }
                        OutlinedButton(
                            onClick = onCreateAccount,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("profile_create_account")
                        ) {
                            Text(language.tr("Créer un compte", "Create account", "إنشاء حساب", "اعمل حساب"))
                        }
                    }
                }

                else -> {
                    val session = cloudAuth.session!!
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = KonstrivoNavy,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text(
                                session.fullName.ifBlank { session.email },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    " " + session.email,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    if (!cloudAuth.isVerified) {
                        Text(
                            language.tr(
                                "Session locale enregistrée — vérification du serveur indisponible pour le moment.",
                                "Saved local session — server verification is unavailable right now.",
                                "جلسة محلية محفوظة — التحقق من الخادم غير متاح حالياً.",
                                "جلسة محلية محفوظة — التحقق من السيرفر ما يجاش حالياً."
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = KonstrivoGold
                        )
                    }
                    if (cloudAuth.isVerified && isAdmin) {
                        Button(
                            onClick = onAdminAccess,
                            colors = ButtonDefaults.buttonColors(containerColor = KonstrivoGold, contentColor = KonstrivoNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("profile_admin_access")
                        ) {
                            Text(language.tr("Tableau de bord admin", "Admin dashboard", "لوحة التحكم", "لوحة التحكم"))
                        }
                    }
                    OutlinedButton(
                        onClick = { viewModel.signOutCloudSession(context) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("profile_sign_out")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(" " + language.tr("Se déconnecter", "Sign out", "تسجيل الخروج", "اخرج"))
                    }
                }
            }
        }
    }
}

