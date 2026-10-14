package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

    BoardScaffold(
        title = language.tr("Profil / Mon Compte", "Profile / My Account", "حسابي الشخصي", "حسابي الشخصي"),
        tag = "profile_screen",
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CloudAccountCard(viewModel, language, onSignIn, onCreateAccount, onAdminAccess)
            }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KonstrivoNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = KonstrivoGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = language.tr("Avantages PRO", "PRO Benefits", "ميزات PRO", "ميزات PRO"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = KonstrivoGold
                            )
                        }

                        val rows = listOf(
                            Triple(language.tr("Devis illimités", "Unlimited quotes", "عروض أسعار غير محدودة", "عروض بلا حدود"), false, isAdmin),
                            Triple(language.tr("Catalogue complet", "Full catalog", "الكتالوج الكامل", "الكتالوج الكامل"), true, true),
                            Triple(language.tr("Support prioritaire", "Priority support", "دعم أولوية خاصة", "دعم خاص"), false, isAdmin)
                        )

                        rows.forEach { (label, free, pro) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "FREE (${if (free) "✓" else "—"})  •  PRO (${if (pro) "✓" else "—"})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = KonstrivoGold.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onUpgrade,
                            colors = ButtonDefaults.buttonColors(containerColor = KonstrivoGold, contentColor = KonstrivoNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = language.tr("Passer à la version PRO", "Upgrade to PRO", "الترقية إلى النسخة الاحترافية", "طور لـ PRO"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                BoardRowCard(
                    BoardRow(
                        titleFr = "Paramètres", titleEn = "Settings", titleAr = "الإعدادات", titleDz = "الإعدادات",
                        subFr = "Langue, pays, profil artisan", subEn = "Language, country, contractor profile", subAr = "اللغة، البلد، بيانات الحرفي", subDz = "اللغة والبلد وبيانات الحرفي",
                        icon = Icons.Default.Settings, accent = KonstrivoSlate, tag = "profile_settings",
                        onClick = { viewModel.selectTab(AppTab.SETTINGS) }
                    ),
                    viewModel
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = KonstrivoGold, modifier = Modifier.size(16.dp))
                    Text(
                        text = "  KONSTRIVO PRO • Construction & Bâtiment",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CloudAccountCard(
    viewModel: MainViewModel,
    language: com.example.data.model.AppLanguage,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    onAdminAccess: () -> Unit
) {
    val context = LocalContext.current
    val cloudAuth by viewModel.cloudAuthState.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = language.tr("Compte KONSTRIVO", "KONSTRIVO Account", "حساب KONSTRIVO", "حساب KONSTRIVO"),
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
                            text = language.tr(
                                "Restauration de la session...",
                                "Restoring session...",
                                "جارٍ استرجاع الجلسة...",
                                "جارٍ استرجاع الجلسة..."
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                cloudAuth.session == null -> {
                    Text(
                        text = language.tr(
                            "Connectez-vous pour synchroniser vos projets avec l'espace web.",
                            "Sign in to sync your projects with the web portal.",
                            "سجّل الدخول لمزامنة مشاريعك مع البوابة الإلكترونية.",
                            "سجّل الدخول لمزامنة مشاريعك عبر الموقع."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onSignIn,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("profile_sign_in")
                        ) {
                            Text(language.tr("Se connecter", "Sign in", "تسجيل الدخول", "دخول"), fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onCreateAccount,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("profile_create_account")
                        ) {
                            Text(language.tr("Créer un compte", "Create account", "إنشاء حساب", "حساب جديد"), fontWeight = FontWeight.Bold)
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
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(11.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.fullName.ifBlank { session.email },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = " ${session.email}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (!cloudAuth.isVerified) {
                        Text(
                            text = language.tr(
                                "Session locale active (hors ligne).",
                                "Active local session (offline).",
                                "جلسة محلية نشطة (بدون إنترنت).",
                                "جلسة محلية نشطة."
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("profile_admin_access")
                        ) {
                            Text(
                                text = language.tr("Tableau de bord admin", "Admin dashboard", "لوحة التحكم", "لوحة التحكم"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.signOutCloudSession(context) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("profile_sign_out")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = " " + language.tr("Se déconnecter", "Sign out", "تسجيل الخروج", "خروج"),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}