package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.ui.components.designsystem.KonstrivoBrandMark
import com.example.ui.components.designsystem.tr

/**
 * Customer-facing sign-in screen. Kept separate from LoginScreen, which is
 * exclusively for owner/admin access. Online customer authentication is not
 * enabled until the project's auth provider is configured.
 */
@Composable
fun UserLoginScreen(
    language: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var attempted by remember { mutableStateOf(false) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_user_login_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            KonstrivoBrandMark(size = 38.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "KONSTRIVO PRO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = language.tr("Connexion", "Sign in", "تسجيل الدخول", "تسجيل الدخول"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = { languageMenuExpanded = true }) {
                    Text(
                        when (language) {
                            AppLanguage.FRENCH -> "FR"
                            AppLanguage.ENGLISH -> "EN"
                            AppLanguage.ARABIC -> "AR"
                            AppLanguage.DERJA -> "DZ"
                        }
                    )
                }
                DropdownMenu(
                    expanded = languageMenuExpanded,
                    onDismissRequest = { languageMenuExpanded = false }
                ) {
                    AppLanguage.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (option) {
                                        AppLanguage.FRENCH -> "Français"
                                        AppLanguage.ENGLISH -> "English"
                                        AppLanguage.ARABIC -> "العربية"
                                        AppLanguage.DERJA -> "تونسي"
                                    }
                                )
                            },
                            onClick = {
                                onLanguageSelected(option)
                                languageMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = language.tr("Ravi de vous revoir", "Welcome back", "مرحبًا بعودتك", "عسلامة من جديد"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = language.tr(
                        "Connectez-vous à votre compte KONSTRIVO",
                        "Sign in to your KONSTRIVO account",
                        "سجّل الدخول إلى حسابك KONSTRIVO",
                        "ادخل لحسابك KONSTRIVO"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; attempted = false },
                    modifier = Modifier.fillMaxWidth().testTag("field_user_email"),
                    label = { Text(language.tr("البريد الإلكتروني", "Email address", "البريد الإلكتروني", "الإيميل")) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; attempted = false },
                    modifier = Modifier.fillMaxWidth().testTag("field_user_password"),
                    label = { Text(language.tr("كلمة المرور", "Password", "كلمة المرور", "كلمة السر")) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                Button(
                    onClick = { attempted = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_user_login")
                ) {
                    Text(language.tr("تسجيل الدخول", "Sign in", "تسجيل الدخول", "ادخل"))
                }
                if (attempted) {
                    Text(
                        text = if (email.isBlank() || password.isBlank()) {
                            language.tr(
                                "يرجى إدخال البريد الإلكتروني وكلمة المرور.",
                                "Enter your email and password.",
                                "يرجى إدخال البريد الإلكتروني وكلمة المرور.",
                                "عمّر الإيميل وكلمة السر."
                            )
                        } else {
                            language.tr(
                                "تسجيل الدخول عبر الإنترنت غير مفعّل بعد. يلزم إعداد خدمة المصادقة.",
                                "Online sign-in is not enabled yet. The authentication provider must be configured.",
                                "تسجيل الدخول عبر الإنترنت غير مفعّل بعد. يلزم إعداد خدمة المصادقة.",
                                "الدخول بالإنترنت مازال موش مفعّل، يلزم إعداد خدمة الحسابات."
                            )
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                TextButton(
                    onClick = onCreateAccount,
                    modifier = Modifier.fillMaxWidth().testTag("btn_user_create_account")
                ) {
                    Text(language.tr("إنشاء حساب جديد", "Create an account", "إنشاء حساب جديد", "اعمل حساب جديد"))
                }
            }
        }
    }
}
