package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.remote.CloudAuthClient
import com.example.data.model.AppLanguage
import com.example.ui.theme.KonstrivoAmber
import com.example.ui.theme.KonstrivoDarkNavy
import com.example.ui.theme.KonstrivoGold
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.ui.components.designsystem.tr

/**
 * STEP 2 — Professional standalone login experience for KONSTRIVO PRO.
 *
 * Pure UI/UX: authentication is delegated verbatim to [MainViewModel.loginAdmin]
 * (PBKDF2 / BuildConfig / fail-closed). No auth architecture change.
 *
 * On success [onLoginSuccess] is invoked, which the host screen wires to open the
 * existing AdminDashboard. No Register / Forgot-password / Google / Firebase / OTP.
 */
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onCloudLoginSuccess: (com.example.data.remote.CloudAuthClient.Session) -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.selectedLanguage.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetLoading by remember { mutableStateOf(false) }
    var resetMessage by remember { mutableStateOf("") }
    var resetError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun submit() {
        if (loading) return
        scope.launch {
            loading = true
            showError = false
            // Preserve the existing local admin gate; normal users authenticate on the website.
            val localAdmin = withContext(Dispatchers.Default) { viewModel.loginAdmin(email.trim(), password) }
            if (localAdmin) {
                loading = false
                onLoginSuccess()
                return@launch
            }
            val result = withContext(Dispatchers.IO) { CloudAuthClient.login(context, email, password) }
            loading = false
            result.onSuccess { session -> onCloudLoginSuccess(session) }.onFailure {
                errorMessage = it.message ?: "Unable to sign in. Check your connection and try again."
                showError = true
            }
        }
    }
Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle construction-blueprint background (light geometric accent).
            BlueprintBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // Back control (start-aligned, RTL-safe).
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        onClick = onBack,
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("btn_login_back")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = language.tr("Retour","Back","رجوع","رجوع"),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = language.tr("Retour","Back","رجوع","رجوع"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Geometric K logo.
                KonstrivoKLogo()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "KONSTRIVO PRO",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = language.tr("Construction & Bâtiment","Construction & Building","البناء والتشييد","البناء والتشييد"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                                            text = language.tr("Connexion sécurisée","Secure sign-in","تسجيل دخول آمن","تسجيل دخول آمن"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                showError = false
                                errorMessage = ""
                            },
                            label = { Text(language.tr("Email","Email","البريد الإلكتروني","البريد الإلكتروني")) },
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            isError = showError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email")
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                showError = false
                                errorMessage = ""
                            },
                            label = { Text(language.tr("Mot de passe","Password","كلمة المرور","كلمة المرور")) },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                            singleLine = true,
                            isError = showError,
                            visualTransformation = if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                        contentDescription = if (showPassword) {
                                                                                    language.tr("Masquer le mot de passe","Hide password","إخفاء كلمة المرور","إخفاء كلمة المرور")
                                        } else {
                                                                                    language.tr("Afficher le mot de passe","Show password","إظهار كلمة المرور","إظهار كلمة المرور")
                                        }
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { submit() }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password")
                        )

                        if (showError) {
                            Text(
                                text = errorMessage.ifBlank {
                                    language.tr(
                                        fr = "Email ou mot de passe incorrect.",
                                        en = "Incorrect email or password.",
                                        ar = "البريد الإلكتروني أو كلمة المرور غير صحيحة.",
                                        dz = "البريد الإلكتروني أو كلمة المرور غير صحيحة."
                                    )
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.testTag("login_error")
                            )
                        }
                        androidx.compose.material3.TextButton(
                            onClick = {
                                resetEmail = email.trim()
                                resetMessage = ""
                                resetError = false
                                showForgotDialog = true
                            },
                            modifier = Modifier.align(Alignment.End).testTag("btn_forgot_password")
                        ) {
                            Text(
                                language.tr("Mot de passe oublié ?", "Forgot password?", "نسيت كلمة المرور؟", "نسيت كلمة السر؟"),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = { submit() },
                            enabled = !loading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_login_submit")
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                                        text = language.tr("Se connecter","Sign in","تسجيل الدخول","تسجيل الدخول"),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onCreateAccount,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KonstrivoGold.copy(alpha = 0.65f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_open_create_account")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = language.tr("Créer un compte", "Create account", "إنشاء حساب", "إنشاء حساب"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = language.tr(
                        fr = "Connexion sécurisée • Compte synchronisé avec le service web",
                        en = "Secure sign-in • Account connected to the web service",
                        ar = "تسجيل دخول آمن • الحساب مربوط بخدمة الموقع",
                        dz = "دخول آمن • الحساب مربوط بخدمة الموقع"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showForgotDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { if (!resetLoading) showForgotDialog = false },
            title = {
                Text(language.tr("Réinitialiser le mot de passe", "Reset password", "إعادة تعيين كلمة المرور", "بدّل كلمة السر"))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(language.tr(
                        "Saisissez votre email. Si le compte existe, un lien de réinitialisation sera envoyé.",
                        "Enter your email. If the account exists, a reset link will be sent.",
                        "اكتب البريد الإلكتروني. إذا الحساب موجود، توصلك رسالة فيها رابط التغيير.",
                        "اكتب الإيميل. كان الحساب موجود، توصلك رسالة فيها رابط تبديل كلمة السر."
                    ))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        singleLine = true,
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth().testTag("forgot_password_email")
                    )
                    if (resetMessage.isNotBlank()) {
                        Text(
                            resetMessage,
                            color = if (resetError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    enabled = !resetLoading && resetEmail.contains("@"),
                    onClick = {
                        resetLoading = true
                        resetMessage = ""
                        scope.launch {
                            val result = withContext(Dispatchers.IO) { CloudAuthClient.requestPasswordReset(resetEmail) }
                            resetLoading = false
                            result.onSuccess {
                                resetError = false
                                resetMessage = language.tr(
                                    "Si cette adresse existe, un lien sera envoyé.",
                                    "If this address exists, a reset link will be sent.",
                                    "إذا البريد موجود، باش يتبعثلك رابط.",
                                    "كان الإيميل موجود، باش يوصلك رابط."
                                )
                            }.onFailure {
                                resetError = true
                                resetMessage = it.message ?: language.tr("Échec de la demande.", "Request failed.", "فشل الطلب.", "الطلب ما نجحش.")
                            }
                        }
                    }
                ) {
                    if (resetLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text(language.tr("Envoyer", "Send", "إرسال", "ابعث"))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showForgotDialog = false },
                    enabled = !resetLoading
                ) { Text(language.tr("Fermer", "Close", "إغلاق", "سكر")) }
            }
        )
    }
}
/**
 * Geometric "K" monogram inspired by construction profiles. Built purely from
 * Compose primitives (Canvas strokes) — no external assets.
 */
@Composable
private fun KonstrivoKLogo(modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()
    Box(
        modifier = modifier
            .size(88.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (dark) KonstrivoNavy else KonstrivoDarkNavy)
            .testTag("login_logo"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(52.dp)) {
            val w = this.size.width
            val stroke = w * 0.16f
            val cap = StrokeCap.Round

            val startX = w * 0.30f
            // Vertical stem.
            drawLine(
                color = KonstrivoAmber,
                start = Offset(startX, w * 0.16f),
                end = Offset(startX, w * 0.84f),
                strokeWidth = stroke,
                cap = cap
            )
            // Upper diagonal arm.
            drawLine(
                color = KonstrivoGold,
                start = Offset(startX, w * 0.42f),
                end = Offset(w * 0.78f, w * 0.14f),
                strokeWidth = stroke,
                cap = cap
            )
            // Lower diagonal arm.
            drawLine(
                color = KonstrivoGold,
                start = Offset(startX, w * 0.52f),
                end = Offset(w * 0.78f, w * 0.86f),
                strokeWidth = stroke,
                cap = cap
            )
        }
    }
}

/**
 * Faint construction-blueprint grid: soft diagonal amber/gold profile lines on a
 * themed background. Subtle, decorative only.
 */
@Composable
private fun BlueprintBackground(modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5f)
        val accentAlpha = if (dark) 0.14f else 0.08f

        // Diagonal building-profile lines (blueprint feel).
        var x = -h
        while (x < w) {
            drawLine(
                color = KonstrivoAmber.copy(alpha = accentAlpha),
                start = Offset(x, 0f),
                end = Offset(x + h, h),
                strokeWidth = stroke.width
            )
            x += 44.dp.toPx()
        }
        // Horizontal build-line accents.
        for (i in 1..4) {
            val y = h * i / 5f
            drawLine(
                color = KonstrivoNavy.copy(alpha = if (dark) 0.12f else 0.05f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = stroke.width
            )
        }
    }
}
