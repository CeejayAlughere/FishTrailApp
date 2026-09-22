package com.fishtrail.app.catchlog

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fishtrail.app.LocationManager
import com.fishtrail.app.viewmodel.CatchViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CatchLogScreen(
    catchViewModel: CatchViewModel,
    username: String,
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    selectedLocationName: String,
    onSelectLocationOnMap: () -> Unit,
    onLocationUsed: () -> Unit
) {

    val context = LocalContext.current

    val locationManager = remember {
        LocationManager(context)
    }

    var species by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var bait by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }

    var date by remember {
        mutableStateOf(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(Date())
        )
    }

    var time by remember {
        mutableStateOf(
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())
        )
    }

    var latitude by remember {
        mutableStateOf<Double?>(null)
    }

    var longitude by remember {
        mutableStateOf<Double?>(null)
    }

    var message by remember {
        mutableStateOf("")
    }

    var isSuccess by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        selectedLatitude,
        selectedLongitude,
        selectedLocationName
    ) {

        if (
            selectedLatitude != null &&
            selectedLongitude != null
        ) {

            latitude = selectedLatitude
            longitude = selectedLongitude

            if (selectedLocationName.isNotBlank()) {
                locationName = selectedLocationName
            }

            message = "Fishing location selected from map."
            isSuccess = true

            onLocationUsed()
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationGranted || coarseLocationGranted) {

                locationManager.getCurrentLocation(
                    onLocationReceived = { lat, lon ->

                        latitude = lat
                        longitude = lon

                        message = "GPS location captured."
                        isSuccess = true
                    },
                    onError = { error ->

                        message = error
                        isSuccess = false
                    }
                )

            } else {

                message = "Location permission was denied."
                isSuccess = false
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 24.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = "Log a Catch",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Record the details of your fishing catch.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        OutlinedTextField(
            value = species,
            onValueChange = {
                species = it
            },
            label = {
                Text("Fish Species")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = weight,
            onValueChange = {
                weight = it
            },
            label = {
                Text("Weight (kg)")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = length,
            onValueChange = {
                length = it
            },
            label = {
                Text("Length (cm)")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bait,
            onValueChange = {
                bait = it
            },
            label = {
                Text("Bait / Lure")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = {
                date = it
            },
            label = {
                Text("Catch Date")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = time,
            onValueChange = {
                time = it
            },
            label = {
                Text("Catch Time")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = locationName,
            onValueChange = {
                locationName = it
            },
            label = {
                Text("Location Name")
            },
            placeholder = {
                Text("e.g. Vaal River")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                if (locationManager.hasLocationPermission()) {

                    locationManager.getCurrentLocation(
                        onLocationReceived = { lat, lon ->

                            latitude = lat
                            longitude = lon

                            message = "GPS location captured."
                            isSuccess = true
                        },
                        onError = { error ->

                            message = error
                            isSuccess = false
                        }
                    )

                } else {

                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Current Location")
        }

        if (latitude != null && longitude != null) {

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "GPS Coordinates",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Latitude: $latitude"
                )

                Text(
                    text = "Longitude: $longitude"
                )
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = {
                notes = it
            },
            label = {
                Text("Notes")
            },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        if (message.isNotEmpty()) {

            Text(
                text = message,
                color = if (isSuccess) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {

                when {

                    species.isBlank() -> {

                        message = "Please enter the fish species."
                        isSuccess = false
                    }

                    weight.isBlank() -> {

                        message = "Please enter the weight."
                        isSuccess = false
                    }

                    length.isBlank() -> {

                        message = "Please enter the length."
                        isSuccess = false
                    }

                    date.isBlank() -> {

                        message = "Please enter the catch date."
                        isSuccess = false
                    }

                    time.isBlank() -> {

                        message = "Please enter the catch time."
                        isSuccess = false
                    }

                    else -> {

                        val weightValue = weight.toDoubleOrNull()
                        val lengthValue = length.toDoubleOrNull()

                        if (weightValue == null || lengthValue == null) {

                            message =
                                "Weight and length must be valid numbers."

                            isSuccess = false

                        } else {

                            catchViewModel.saveCatch(
                                username = username,
                                species = species,
                                weight = weightValue,
                                length = lengthValue,
                                bait = bait,
                                notes = notes,
                                date = date,
                                time = time,
                                locationName = locationName,
                                latitude = latitude,
                                longitude = longitude
                            ) { success, resultMessage ->

                                message = resultMessage
                                isSuccess = success

                                if (success) {

                                    species = ""
                                    weight = ""
                                    length = ""
                                    bait = ""
                                    notes = ""
                                    locationName = ""

                                    latitude = null
                                    longitude = null

                                    date = SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                    ).format(Date())

                                    time = SimpleDateFormat(
                                        "HH:mm",
                                        Locale.getDefault()
                                    ).format(Date())
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Catch")
        }
    }
}