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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.remote.CloudAuthClient
import com.example.ui.components.designsystem.KonstrivoBrandMark
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoAmber
import com.example.ui.theme.KonstrivoGold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserLoginScreen(
    language: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    onCreateAccount: () -> Unit,
    onSignInSuccess: (CloudAuthClient.Session) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var attempted by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }
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
                    Text(language.name)
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
                    text = language.tr("مرحبًا بعودتك", "Welcome back", "مرحبًا بعودتك", "عسلامة من جديد"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = language.tr(
                        "سجّل الدخول إلى حسابك KONSTRIVO",
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
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; attempted = false },
                    modifier = Modifier.fillMaxWidth().testTag("field_user_password"),
                    label = { Text(language.tr("كلمة المرور", "Password", "كلمة المرور", "كلمة السر")) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            attempted = true
                            errorMessage = ""
                        } else {
                            attempted = false
                            errorMessage = ""
                            loading = true
                            scope.launch {
                                val result = withContext(Dispatchers.IO) {
                                    CloudAuthClient.login(context, email.trim(), password)
                                }
                                loading = false
                                result
                                    .onSuccess { session -> onSignInSuccess(session) }
                                    .onFailure { err ->
                                        errorMessage = err.message
                                            ?: language.tr("تعذر تسجيل الدخول. تحقق من الاتصال.", "Unable to sign in. Check your connection.", "تعذر تسجيل الدخول. تحقق من الاتصال.", "تعذر تسجيل الدخول.")
                                    }
                            }
                        }
                    },
                    enabled = !loading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_user_login")
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(language.tr("تسجيل الدخول", "Sign in", "تسجيل الدخول", "ادخل"), fontWeight = FontWeight.Bold)
                    }
                }

                // زر تسجيل الدخول عبر Google / Gmail
                OutlinedButton(
                    onClick = {
                        loading = true
                        scope.launch {
                            // محاكاة أو تفعيل مسار ربط Google Sign-In
                            loading = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KonstrivoGold.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_google_signin")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = KonstrivoAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = language.tr("Connexion avec Google (Gmail)", "Sign in with Google", "التسجيل بـ Google", "التسجيل بـ Google"),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (attempted && (email.isBlank() || password.isBlank())) {
                    Text(
                        text = language.tr(
                            "يرجى إدخال البريد الإلكتروني وكلمة المرور.",
                            "Enter your email and password.",
                            "يرجى إدخال البريد الإلكتروني وكلمة المرور.",
                            "عمّر الإيميل وكلمة السر."
                        ),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(
                    onClick = { showForgotDialog = true },
                    modifier = Modifier.fillMaxWidth().testTag("btn_user_forgot_password")
                ) {
                    Text(language.tr("نسيت كلمة المرور؟", "Forgot password?", "نسيت كلمة المرور؟", "نسيت كلمة السر؟"))
                }

                Spacer(modifier = Modifier.height(2.dp))

                Button(
                    onClick = onCreateAccount,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_user_create_account")
                ) {
                    Text(
                        language.tr("إنشاء حساب جديد", "Create an account", "إنشاء حساب جديد", "اعمل حساب جديد"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showForgotDialog) {
        var resetEmail by remember { mutableStateOf(email) }
        var resetLoading by remember { mutableStateOf(false) }
        var resetMessage by remember { mutableStateOf("") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { if (!resetLoading) showForgotDialog = false },
            title = {
                Text(language.tr("استعادة كلمة المرور", "Password recovery", "استعادة كلمة المرور", "استرجاع كلمة السر"))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        language.tr(
                            "أدخل بريدك الإلكتروني وسنرسل لك رابط إعادة التعيين.",
                            "Enter your email and we will send a reset link.",
                            "أدخل بريدك الإلكتروني وسنرسل لك رابط إعادة التعيين.",
                            "عمّر إيميلك ونبعثلك رابط الاسترجاع."
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it; resetMessage = "" },
                        label = { Text(language.tr("البريد الإلكتروني", "Email", "البريد الإلكتروني", "الإيميل")) },
                        singleLine = true,
                        enabled = !resetLoading,
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (resetMessage.isNotBlank()) {
                        Text(
                            resetMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        resetLoading = true
                        resetMessage = ""
                        scope.launch {
                            val result = withContext(Dispatchers.IO) {
                                CloudAuthClient.requestPasswordReset(resetEmail)
                            }
                            resetLoading = false
                            result.onSuccess {
                                resetMessage = language.tr(
                                    "إذا كان البريد مسجّلًا، فسيصلك رابط إعادة التعيين.",
                                    "If the email is registered, a reset link will be sent.",
                                    "إذا كان البريد مسجّلًا، فسيصلك رابط إعادة التعيين.",
                                    "إلا كان الإيميل مسجّل، باش يجيك رابط الاسترجاع."
                                )
                            }.onFailure { err ->
                                resetMessage = err.message
                                    ?: language.tr("تعذر إرسال رابط الاسترجاع.", "Unable to request a reset link.", "تعذر إرسال رابط الاسترجاع.", "ما نجموش نبعثو الرابط.")
                            }
                        }
                    },
                    enabled = !resetLoading,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (resetLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text(language.tr("إرسال", "Send", "إرسال", "ابعث"))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showForgotDialog = false },
                    enabled = !resetLoading
                ) { Text(language.tr("إغلاق", "Close", "إغلاق", "سكر")) }
            }
        )
    }
}