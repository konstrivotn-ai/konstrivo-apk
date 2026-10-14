package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UserSettingsEntity
import com.example.ui.components.designsystem.tr
import com.example.ui.theme.KonstrivoAmber
import com.example.ui.theme.KonstrivoNavy
import com.example.ui.viewmodel.MainViewModel

@Composable
fun UserDashboardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.selectedLanguage.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    var companyName by remember(userSettings.companyHeader) { mutableStateOf(userSettings.companyHeader) }
    var contractorName by remember(userSettings.contractorName) { mutableStateOf(userSettings.contractorName ?: "") }
    var userPhone by remember(userSettings.contractorPhone) { mutableStateOf(userSettings.contractorPhone ?: "") }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("user_dashboard_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // العنوان العلوي
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = language.tr("Espace Utilisateur • PRO", "User Dashboard • PRO", "لوحة تحكم المستخدم • PRO", "لوحة التحكم • PRO"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = language.tr("Gérez vos informations et votre entreprise", "Manage your profile and company", "إدارة ملفك الشخصي وشركتك", "عصّر معلوماتك وشركتك"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // أيقونة / زر اختيار وتعديل صورة الملف الشخصي
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .background(color = KonstrivoAmber.copy(alpha = 0.15f), shape = CircleShape)
                            .border(2.dp, KonstrivoAmber, CircleShape)
                            .clip(CircleShape)
                            .clickable {
                                Toast.makeText(
                                    context,
                                    language.tr("Sélection de photo bientôt disponible", "Photo selection coming soon", "اختيار الصورة قريباً", "اختيار الصورة قريباً"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        alignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Modifier la photo",
                            tint = KonstrivoAmber,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Text(
                        text = language.tr("Modifier mon Profil", "Edit My Profile", "تعديل ملفي الشخصي", "بدّل ملفي الشخصي"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = contractorName,
                        onValueChange = { contractorName = it },
                        label = { Text(language.tr("Nom et Prénom / Responsable", "Full Name / Manager", "الاسم الكامل / المسؤول", "الاسم واللقب")) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("field_user_name")
                    )

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text(language.tr("Nom de l'Entreprise / Projet", "Company / Project Name", "اسم الشركة أو المشروع", "اسم الشركة")) },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("field_company_name")
                    )

                    OutlinedTextField(
                        value = userPhone,
                        onValueChange = { userPhone = it },
                        label = { Text(language.tr("Numéro de Téléphone", "Phone Number", "رقم الهاتف", "رقم الهاتف")) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("field_user_phone")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            val updatedSettings = userSettings.copy(
                                companyHeader = companyName,
                                contractorName = contractorName,
                                contractorPhone = userPhone
                            )
                            viewModel.updateUserSettings(updatedSettings)
                            Toast.makeText(
                                context,
                                language.tr("Profil mis à jour avec succès", "Profile successfully updated", "تم تحديث الملف الشخصي بنجاح", "تم تحديث بياناتك بنجاح"),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_user_profile"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KonstrivoAmber)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = KonstrivoNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = language.tr("Enregistrer les modifications", "Save changes", "حفظ التغييرات", "حفظ التغييرات"),
                            color = KonstrivoNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}