package com.fishtrail.app.map

import android.Manifest
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fishtrail.app.LocationManager
import com.fishtrail.app.viewmodel.CatchViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun MapScreen(
    catchViewModel: CatchViewModel,
    username: String,
    selectingLocationForCatch: Boolean,
    onLocationSelected: (
        Double,
        Double,
        String
    ) -> Unit,
    onSavedFishingSpotsClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationManager = remember { LocationManager(context) }

    val catches by catchViewModel.catches.collectAsState()

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var selectedLatitude by remember { mutableStateOf<Double?>(null) }
    var selectedLongitude by remember { mutableStateOf<Double?>(null) }

    var locationPermissionGranted by remember {
        mutableStateOf(locationManager.hasLocationPermission())
    }

    var mapLoaded by remember { mutableStateOf(false) }
    var cameraCenteredOnLocation by remember { mutableStateOf(false) }

    LaunchedEffect(username) {
        if (username.isNotBlank()) {
            catchViewModel.loadCatches(username)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        locationPermissionGranted = fineLocationGranted || coarseLocationGranted

        if (locationPermissionGranted) {
            locationManager.getCurrentLocation(
                onLocationReceived = { lat, lon ->
                    latitude = lat
                    longitude = lon
                },
                onError = { error ->
                    Log.e("FishTrailLocation", "Location request failed after permission grant: $error")
                }
            )
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(-26.2041, 28.0473),
            10f
        )
    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            locationManager.getCurrentLocation(
                onLocationReceived = { lat, lon ->
                    latitude = lat
                    longitude = lon
                },
                onError = { error ->
                    Log.e("FishTrailLocation", "Could not get current location: $error")
                }
            )
        }
    }

    LaunchedEffect(mapLoaded, latitude, longitude, selectingLocationForCatch) {
        if (mapLoaded && latitude != null && longitude != null && !selectingLocationForCatch && !cameraCenteredOnLocation) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(latitude!!, longitude!!),
                15f
            )
            cameraCenteredOnLocation = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionGranted
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                rotationGesturesEnabled = true,
                tiltGesturesEnabled = true,
                myLocationButtonEnabled = locationPermissionGranted
            ),
            onMapLoaded = {
                mapLoaded = true
                Log.d("FishTrailMap", "Google Map loaded successfully.")
            },
            onMapClick = { latLng ->
                val tappedLatitude = latLng.latitude
                val tappedLongitude = latLng.longitude

                if (selectingLocationForCatch) {
                    coroutineScope.launch {
                        var locationName = "Selected Map Location"

                        val geocodedName = withTimeoutOrNull(1500L) {
                            try {
                                withContext(Dispatchers.IO) {
                                    @Suppress("DEPRECATION")
                                    val geocoder = Geocoder(context)
                                    @Suppress("DEPRECATION")
                                    val addresses = geocoder.getFromLocation(
                                        tappedLatitude,
                                        tappedLongitude,
                                        1
                                    )
                                    val address = addresses?.firstOrNull()

                                    when {
                                        !address?.locality.isNullOrBlank() -> address?.locality
                                        !address?.subAdminArea.isNullOrBlank() -> address?.subAdminArea
                                        !address?.adminArea.isNullOrBlank() -> address?.adminArea
                                        !address?.getAddressLine(0).isNullOrBlank() -> address?.getAddressLine(0)
                                        else -> null
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("FishTrailGeocoder", "Reverse geocoding failed.", e)
                                null
                            }
                        }

                        if (!geocodedName.isNullOrBlank()) {
                            locationName = geocodedName
                        }

                        onLocationSelected(
                            tappedLatitude,
                            tappedLongitude,
                            locationName
                        )
                    }
                } else {
                    selectedLatitude = tappedLatitude
                    selectedLongitude = tappedLongitude
                }
            }
        ) {
            if (latitude != null && longitude != null) {
                val currentLocation = LatLng(latitude!!, longitude!!)
                Marker(
                    state = remember(latitude, longitude) {
                        MarkerState(position = currentLocation)
                    },
                    title = "My Location",
                    snippet = "Current device location"
                )
            }

            if (selectedLatitude != null && selectedLongitude != null) {
                val selectedLocation = LatLng(selectedLatitude!!, selectedLongitude!!)
                Marker(
                    state = remember(selectedLatitude, selectedLongitude) {
                        MarkerState(position = selectedLocation)
                    },
                    title = "Selected Fishing Location",
                    snippet = "Location selected manually"
                )
            }

            catches.forEach { fishingCatch ->
                val catchLatitude = fishingCatch.latitude
                val catchLongitude = fishingCatch.longitude

                if (catchLatitude != null && catchLongitude != null) {
                    val catchLocation = LatLng(catchLatitude, catchLongitude)
                    Marker(
                        state = remember(
                            fishingCatch.id,
                            catchLatitude,
                            catchLongitude
                        ) {
                            MarkerState(position = catchLocation)
                        },
                        title = fishingCatch.species,
                        snippet = buildString {
                            append("${fishingCatch.weight} kg")
                            append(" • ")
                            append("${fishingCatch.length} cm")
                            if (fishingCatch.locationName.isNotBlank()) {
                                append(" • ")
                                append(fishingCatch.locationName)
                            }
                        }
                    )
                }
            }
        }

        // Top Floating Banner Overlay for Catch Location Selection
        if (selectingLocationForCatch) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pick Catch Location",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Tap any point on the map to select coordinates",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Top-Right Floating Action Button for Saved Fishing Spots
        FloatingActionButton(
            onClick = onSavedFishingSpotsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = "Saved Spots",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Saved Spots",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Clear Selection Floating Button
        if (selectedLatitude != null && selectedLongitude != null && !selectingLocationForCatch) {
            OutlinedButton(
                onClick = {
                    selectedLatitude = null
                    selectedLongitude = null
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear Selection")
            }
        }

        // Bottom Permission Banner Card
        if (!locationPermissionGranted && !selectingLocationForCatch) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Location Permission Required",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Allow location access to view your current position on the fishing map.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Allow Location Access")
                    }
                }
            }
        }
    }
}
