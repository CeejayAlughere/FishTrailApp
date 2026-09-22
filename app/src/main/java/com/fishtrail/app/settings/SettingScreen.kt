package com.fishtrail.app.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fishtrail.app.LocationManager
import com.fishtrail.app.viewmodel.AuthViewModel
import com.google.android.datatransport.BuildConfig

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    username: String,
    darkModeEnabled: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onLogoutClick: () -> Unit
) {

    val context = LocalContext.current

    /*
     * --------------------------------------------------
     * CURRENT USER
     * --------------------------------------------------
     */

    var userFullName by remember {
        mutableStateOf("")
    }

    var userEmail by remember {
        mutableStateOf("")
    }

    /*
     * Load the logged-in user's account information.
     */
    LaunchedEffect(username) {

        if (username.isNotBlank()) {

            authViewModel.getUserByUsername(username) { user ->

                if (user != null) {

                    userFullName = user.fullName
                    userEmail = user.email
                }
            }
        }
    }

    /*
     * --------------------------------------------------
     * LOCATION
     * --------------------------------------------------
     */

    val locationManager = remember {
        LocationManager(context)
    }

    var locationPermissionGranted by remember {
        mutableStateOf(
            locationManager.hasLocationPermission()
        )
    }

    /*
     * Request location permission.
     */
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) {

            locationPermissionGranted =
                locationManager.hasLocationPermission()
        }

    /*
     * --------------------------------------------------
     * SETTINGS SCREEN
     * --------------------------------------------------
     */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        /*
         * --------------------------------------------------
         * HEADER
         * --------------------------------------------------
         */

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Manage your FishTrail account and app preferences.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        /*
         * --------------------------------------------------
         * ACCOUNT
         * --------------------------------------------------
         */

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider()

                Text(
                    text = "Full Name",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = userFullName.ifBlank {
                        "Loading..."
                    },
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = userEmail.ifBlank {
                        "Loading..."
                    },
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Username",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        /*
         * --------------------------------------------------
         * PREFERENCES
         * --------------------------------------------------
         */

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = if (darkModeEnabled) {
                                "Dark theme is enabled."
                            } else {
                                "Light theme is enabled."
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
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

        /*
         * --------------------------------------------------
         * LOCATION
         * --------------------------------------------------
         */

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider()

                Text(
                    text = if (locationPermissionGranted) {
                        "Location permission: Allowed"
                    } else {
                        "Location permission: Not allowed"
                    },
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = if (locationPermissionGranted) {
                        "FishTrail can use your device location when you use location features."
                    } else {
                        "Allow location access to use GPS features in Catch Log and Map."
                    },
                    style = MaterialTheme.typography.bodyMedium
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
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Allow Location")
                    }

                    TextButton(
                        onClick = {

                            val intent =
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                ).apply {

                                    data = Uri.parse(
                                        "package:${context.packageName}"
                                    )
                                }

                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Open App Settings")
                    }
                }
            }
        }

        /*
         * --------------------------------------------------
         * ABOUT
         * --------------------------------------------------
         */

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "About FishTrail",
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider()

                Text(
                    text = "FishTrail Fishing Logbook",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        /*
         * --------------------------------------------------
         * LOGOUT
         * --------------------------------------------------
         */

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Logout")
        }
    }
}