package com.fishtrail.app.catchlog

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
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
    val locationManager = remember { LocationManager(context) }

    var species by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var bait by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }

    var date by remember {
        mutableStateOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )
    }

    var time by remember {
        mutableStateOf(
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )
    }

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLatitude, selectedLongitude, selectedLocationName) {
        if (selectedLatitude != null && selectedLongitude != null) {
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            locationManager.getCurrentLocation(
                onLocationReceived = { lat, lon ->
                    latitude = lat
                    longitude = lon
                    message = "GPS location captured successfully."
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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Title Section
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
                    imageVector = Icons.Filled.SetMeal,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Log a Catch",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Record details of your catch for your logbook",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 1: Fish Details Card
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
                Text(
                    text = "Fish Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = species,
                    onValueChange = {
                        species = it
                        message = ""
                    },
                    label = { Text("Fish Species") },
                    placeholder = { Text("e.g. Bass, Trout, Catfish") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.SetMeal,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = {
                            weight = it
                            message = ""
                        },
                        label = { Text("Weight (kg)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Scale,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = length,
                        onValueChange = {
                            length = it
                            message = ""
                        },
                        label = { Text("Length (cm)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Straighten,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = bait,
                    onValueChange = {
                        bait = it
                        message = ""
                    },
                    label = { Text("Bait / Lure") },
                    placeholder = { Text("e.g. Worm, Spinnerbait, Jig") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Section 2: Date & Time Card
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
                Text(
                    text = "Catch Date & Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = {
                            date = it
                            message = ""
                        },
                        label = { Text("Date (dd/mm/yyyy)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = time,
                        onValueChange = {
                            time = it
                            message = ""
                        },
                        label = { Text("Time (hh:mm)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section 3: Fishing Location & GPS Card
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
                Text(
                    text = "Fishing Location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = locationName,
                    onValueChange = {
                        locationName = it
                        message = ""
                    },
                    label = { Text("Location Name") },
                    placeholder = { Text("e.g. Vaal River / Lake Tahoe") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GpsFixed,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Get GPS")
                    }

                    OutlinedButton(
                        onClick = onSelectLocationOnMap,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Map Pin")
                    }
                }

                if (latitude != null && longitude != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "GPS Coordinates Attached",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Lat: %.6f, Lon: %.6f".format(latitude, longitude),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Notes Card
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
                Text(
                    text = "Additional Notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                        message = ""
                    },
                    label = { Text("Notes & Weather Conditions") },
                    placeholder = { Text("Water clarity, weather conditions, tactics used...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Animated Status / Error Banner
        AnimatedVisibility(
            visible = message.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Filled.CheckCircleOutline else Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Save Catch Action CTA
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
                            message = "Weight and length must be valid numbers."
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

                                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                }
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save Catch",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
