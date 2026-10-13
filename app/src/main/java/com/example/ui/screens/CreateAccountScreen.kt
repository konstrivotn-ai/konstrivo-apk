package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.remote.CloudAuthClient
import com.example.data.model.AppLanguage
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoGold
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Creates accounts through the website's existing Express API. Local calculator
 * and Room data remain available offline; cloud account creation requires internet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf("") }

    // Match ref_top_03 fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }

    var attempted by remember { mutableStateOf(false) }

    val nameOk = firstName.trim().length >= 2 && lastName.trim().length >= 2
    val phoneOk = phone.trim().length >= 6
    val companyOk = company.trim().length >= 2
    val emailOk = email.trim().contains('@') && email.trim().contains('.')
    val pwdOk = password.length >= 8
    val termsOk = acceptedTerms

    val canSubmit = nameOk && phoneOk && companyOk && emailOk && pwdOk && termsOk

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("create_account_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_create_back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = language.tr("Créer un compte", "Create account", "إنشاء حساب", "إنشاء حساب"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = language.tr(
                            "Espace PRO • Offline-first",
                            "PRO workspace • Offline-first",
                            "مساحة PRO • تعمل بدون إنترنت",
                            "مساحة PRO • تعمل بدون إنترنت"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /* Superseded by the reference layout: form fields sit directly on the navy background.
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
            */
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text(language.tr("Prénom", "First name", "الاسم", "الاسم")) },
                        singleLine = true,
                        isError = attempted && firstName.trim().length < 2,
                        modifier = Modifier.fillMaxWidth().testTag("signup_first_name")
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text(language.tr("Nom", "Last name", "اللقب", "اللقب")) },
                        singleLine = true,
                        isError = attempted && lastName.trim().length < 2,
                        modifier = Modifier.fillMaxWidth().testTag("signup_last_name")
                    )
                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        label = { Text(language.tr("Entreprise", "Company", "الشركة", "الشركة")) },
                        singleLine = true,
                        isError = attempted && !companyOk,
                        modifier = Modifier.fillMaxWidth().testTag("signup_company")
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        label = { Text(language.tr("Téléphone", "Phone", "رقم الهاتف", "رقم الهاتف")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = attempted && !phoneOk,
                        modifier = Modifier.fillMaxWidth().testTag("signup_phone")
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        label = { Text(language.tr("Email", "Email", "البريد الإلكتروني", "البريد الإلكتروني")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = attempted && !emailOk,
                        modifier = Modifier.fillMaxWidth().testTag("signup_email")
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        label = { Text(language.tr("Mot de passe", "Password", "كلمة المرور", "كلمة المرور")) },
                        singleLine = true,
                        isError = attempted && !pwdOk,
                        modifier = Modifier.fillMaxWidth().testTag("signup_password")
                    )

                    // Terms line as shown in ref: checkbox + small text.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = acceptedTerms,
                            onCheckedChange = { acceptedTerms = it },
                            modifier = Modifier.testTag("signup_terms")
                        )
                        Text(
                            text = language.tr(
                                fr = "J'accepte les conditions d'utilisation",
                                en = "I accept the terms of service",
                                ar = "أوافق على شروط الاستخدام",
                                dz = "نوافق على شروط الاستعمال"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            attempted = true
                            submitError = ""
                            if (canSubmit && !loading) {
                                scope.launch {
                                    loading = true
                                    val result = withContext(Dispatchers.IO) {
                                        CloudAuthClient.register(
                                            context = context,
                                            fullName = "${firstName.trim()} ${lastName.trim()}",
                                            email = email,
                                            password = password,
                                            phone = phone,
                                            companyName = company
                                        )
                                    }
                                    loading = false
                                    result.onSuccess { onSignIn() }.onFailure {
                                        submitError = it.message ?: "Unable to create account. Check your connection and try again."
                                    }
                                }
                            }
                        },
                        enabled = canSubmit && !loading,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_signup_submit"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = language.tr("Créer mon compte", "Create account", "إنشاء حساب", "إنشاء حساب"),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    TextButton(
                        onClick = onSignIn,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .testTag("btn_signup_goto_login")
                    ) {
                        Text(
                            text = language.tr(
                                fr = "Vous avez déjà un compte ? Se connecter",
                                en = "Already have an account? Sign in",
                                ar = "عندك حساب؟ تسجيل الدخول",
                                dz = "عندك compte؟ تسجيل الدخول"
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            /* Stray Card closer — the form Card wrapper was removed (reference layout).
            }
            */

            if (submitError.isNotBlank()) {
                Text(submitError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Explain online accounts while preserving offline use.
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = KonstrivoGold)
                        Text(
                            text = language.tr(
                                fr = "Compte KONSTRIVO en ligne",
                                en = "KONSTRIVO online account",
                                ar = "حساب KONSTRIVO على الإنترنت",
                                dz = "حساب KONSTRIVO على الإنترنت"
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = language.tr(
                            fr = "Le compte est créé sur le serveur du site. Le calculateur et les données locales restent disponibles hors connexion.",
                            en = "Your account is created on the website server. The calculator and local data remain available offline.",
                            ar = "يُنشأ الحساب على خادم الموقع، وتبقى الحاسبة والبيانات المحلية متاحة دون إنترنت.",
                            dz = "الحساب يتعمل على سيرفر الموقع، والحاسبة والمعطيات المحلية يبقاو يخدمو بلا إنترنت."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                    if (attempted && !canSubmit) {
                        Text(
                            text = language.tr(
                                fr = "Vérifiez vos informations (nom, entreprise, téléphone, email, mot de passe, conditions).",
                                en = "Please verify your information (name, company, phone, email, password, terms).",
                                ar = "يرجى التحقق من البيانات (الاسم، الشركة، الهاتف، البريد، كلمة المرور، الشروط).",
                                dz = "ثبت معلوماتك (الاسم، الشركة، الهاتف، الإيميل، كلمة السر، الشروط)."
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
