package com.fishtrail.app.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fishtrail.app.LocationManager
import com.fishtrail.app.viewmodel.AuthViewModel
import androidx.compose.material3.OutlinedTextField

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    username: String,
    darkModeEnabled: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current

    val initialEmail = remember { authViewModel.getCurrentUserEmail() }
    val initialDisplayName = remember { authViewModel.getCurrentUserDisplayName() }

    var userFullName by remember { mutableStateOf(initialDisplayName) }
    var userEmail by remember { mutableStateOf(initialEmail) }

    var editedFullName by remember { mutableStateOf(initialDisplayName) }
    var editedEmail by remember { mutableStateOf(initialEmail) }

    var isEditingProfile by remember { mutableStateOf(false) }
    var isSavingProfile by remember { mutableStateOf(false) }

    var profileMessage by remember { mutableStateOf("") }
    var profileMessageIsError by remember { mutableStateOf(false) }

    var emailVerificationPending by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(username) {
        val currentEmail = authViewModel.getCurrentUserEmail()
        val currentDisplayName = authViewModel.getCurrentUserDisplayName()

        if (username.isNotBlank()) {
            authViewModel.getUserByUsername(username) { user ->
                if (user != null) {
                    userFullName = user.fullName.ifBlank { currentDisplayName }
                    userEmail = user.email.ifBlank { currentEmail }

                    editedFullName = userFullName
                    editedEmail = userEmail
                } else {
                    if (currentEmail.isNotBlank()) userEmail = currentEmail
                    if (currentDisplayName.isNotBlank()) userFullName = currentDisplayName
                    editedFullName = userFullName
                    editedEmail = userEmail
                }
            }
        } else {
            if (currentEmail.isNotBlank()) userEmail = currentEmail
            if (currentDisplayName.isNotBlank()) userFullName = currentDisplayName
            editedFullName = userFullName
            editedEmail = userEmail
        }
    }

    val locationManager = remember {
        LocationManager(context)
    }

    var locationPermissionGranted by remember {
        mutableStateOf(locationManager.hasLocationPermission())
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        locationPermissionGranted = locationManager.hasLocationPermission()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Manage your account & preferences",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // =========================================================
        // ACCOUNT DETAILS CARD
        // =========================================================

        fun continueWithEmailUpdate() {
            val cleanEmail = editedEmail.trim().lowercase()

            if (cleanEmail != userEmail.trim().lowercase()) {

                authViewModel.updateEmail(cleanEmail) { success, message ->

                    isSavingProfile = false
                    profileMessage = message
                    profileMessageIsError = !success

                    if (success) {
                        emailVerificationPending = true
                        isEditingProfile = false
                    }
                }

            } else {
                isSavingProfile = false
                isEditingProfile = false
                profileMessage = "Your profile details have been updated successfully."
                profileMessageIsError = false
            }
        }

        fun saveProfileChanges() {
            val cleanName = editedFullName.trim()
            val cleanEmail = editedEmail.trim().lowercase()

            if (cleanName.isBlank()) {
                profileMessage = "Please enter your full name."
                profileMessageIsError = true
                return
            }

            if (cleanName.length < 2) {
                profileMessage = "Your name must contain at least 2 characters."
                profileMessageIsError = true
                return
            }

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(cleanEmail)
                    .matches()
            ) {
                profileMessage = "Please enter a valid email address."
                profileMessageIsError = true
                return
            }

            isSavingProfile = true
            profileMessage = ""

            if (cleanName != userFullName.trim()) {

                authViewModel.updateFullName(cleanName) { success, message ->

                    if (success) {
                        userFullName = cleanName
                        continueWithEmailUpdate()
                    } else {
                        isSavingProfile = false
                        profileMessage = message
                        profileMessageIsError = true
                    }
                }

            } else {
                continueWithEmailUpdate()
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // Account Details Heading
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Account Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!isEditingProfile) {
                        TextButton(
                            onClick = {
                                editedFullName = userFullName
                                editedEmail = userEmail
                                profileMessage = ""
                                isEditingProfile = true
                            }
                        ) {
                            Text("Edit")
                        }
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // FULL NAME
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Badge,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Full Name",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isEditingProfile) {
                        OutlinedTextField(
                            value = editedFullName,
                            onValueChange = {
                                editedFullName = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("Enter full name") },
                            enabled = !isSavingProfile
                        )
                    } else {
                        Text(
                            text = userFullName.ifBlank {
                                "Name not available"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // EMAIL ADDRESS
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Email Address",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isEditingProfile) {
                        OutlinedTextField(
                            value = editedEmail,
                            onValueChange = {
                                editedEmail = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("Enter email address") },
                            enabled = !isSavingProfile
                        )
                    } else {
                        Text(
                            text = userEmail.ifBlank {
                                "Email not available"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // USERNAME - READ ONLY
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Username",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = username,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                // SAVE AND CANCEL BUTTONS
                if (isEditingProfile) {

                    Button(
                        onClick = {
                            saveProfileChanges()
                        },
                        enabled = !isSavingProfile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isSavingProfile) {
                                "Saving..."
                            } else {
                                "Save Changes"
                            }
                        )
                    }

                    TextButton(
                        onClick = {
                            editedFullName = userFullName
                            editedEmail = userEmail
                            isEditingProfile = false
                            profileMessage = ""
                        },
                        enabled = !isSavingProfile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }

                // SUCCESS AND ERROR MESSAGES
                if (profileMessage.isNotBlank()) {
                    Text(
                        text = profileMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (profileMessageIsError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }

                // EMAIL VERIFICATION CONFIRMATION
                if (emailVerificationPending) {

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Text(
                        text = "Check your new email address and complete the Firebase verification process.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            isSavingProfile = true

                            authViewModel.refreshEmailAfterVerification {
                                    success, message, updatedEmail ->

                                isSavingProfile = false
                                profileMessage = message
                                profileMessageIsError = !success

                                if (success && updatedEmail != null) {
                                    userEmail = updatedEmail
                                    editedEmail = updatedEmail
                                    emailVerificationPending = false
                                }
                            }
                        },
                        enabled = !isSavingProfile,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("I've Verified My Email")
                    }
                }
            }
        }

        // App Preferences Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "App Preferences",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (darkModeEnabled) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Dark Theme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (darkModeEnabled) "Dark mode enabled" else "Light mode enabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { enabled ->
                            onDarkModeChanged(enabled)
                        }
                    )
                }
            }
        }

        // Location Permissions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Location Permissions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (locationPermissionGranted) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (locationPermissionGranted) "Location Access: Granted" else "Location Access: Not Granted",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (locationPermissionGranted) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Text(
                    text = if (locationPermissionGranted) {
                        "FishTrail can utilize device location for Catch Logging and Fishing Spot Maps."
                    } else {
                        "Allow location permission to unlock GPS location logging and map features."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!locationPermissionGranted) {
                    Button(
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Location Permission")
                    }

                    TextButton(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            ).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open System Settings")
                    }
                }
            }
        }

        // About FishTrail Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About FishTrail",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "FishTrail Fishing Logbook & Companion",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Version 1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Logout CTA Button
        Button(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
