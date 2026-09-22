package com.fishtrail.app.map

import android.Manifest
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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

    /*
     * --------------------------------------------------
     * LOCATION MANAGER
     * --------------------------------------------------
     */

    val locationManager = remember {
        LocationManager(context)
    }

    /*
     * --------------------------------------------------
     * SAVED CATCHES
     * --------------------------------------------------
     *
     * These are the catches belonging to the currently
     * logged-in user.
     */

    val catches by catchViewModel.catches.collectAsState()

    /*
     * --------------------------------------------------
     * CURRENT GPS LOCATION
     * --------------------------------------------------
     */

    var latitude by remember {
        mutableStateOf<Double?>(null)
    }

    var longitude by remember {
        mutableStateOf<Double?>(null)
    }

    /*
     * --------------------------------------------------
     * TEMPORARY MANUAL MAP SELECTION
     * --------------------------------------------------
     */

    var selectedLatitude by remember {
        mutableStateOf<Double?>(null)
    }

    var selectedLongitude by remember {
        mutableStateOf<Double?>(null)
    }

    /*
     * --------------------------------------------------
     * PERMISSION STATE
     * --------------------------------------------------
     */

    var locationPermissionGranted by remember {
        mutableStateOf(
            locationManager.hasLocationPermission()
        )
    }

    /*
     * --------------------------------------------------
     * MAP LOADED STATE
     * --------------------------------------------------
     *
     * Google Maps Compose recommends waiting until the
     * map has finished loading before performing camera
     * operations.
     */

    var mapLoaded by remember {
        mutableStateOf(false)
    }

    /*
     * Prevent repeated automatic camera movement.
     */

    var cameraCenteredOnLocation by remember {
        mutableStateOf(false)
    }

    /*
     * --------------------------------------------------
     * LOAD USER CATCHES
     * --------------------------------------------------
     */

    LaunchedEffect(username) {

        if (username.isNotBlank()) {

            catchViewModel.loadCatches(username)
        }
    }

    /*
     * --------------------------------------------------
     * LOCATION PERMISSION REQUEST
     * --------------------------------------------------
     */

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true

            val coarseLocationGranted =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] == true

            locationPermissionGranted =
                fineLocationGranted || coarseLocationGranted

            if (locationPermissionGranted) {

                locationManager.getCurrentLocation(

                    onLocationReceived = { lat, lon ->

                        latitude = lat
                        longitude = lon
                    },

                    onError = { error ->

                        Log.e(
                            "FishTrailLocation",
                            "Location request failed after permission grant: $error"
                        )
                    }
                )
            }
        }

    /*
     * --------------------------------------------------
     * INITIAL CAMERA POSITION
     * --------------------------------------------------
     *
     * Johannesburg is the fallback position until the
     * device location becomes available.
     */

    val cameraPositionState =
        rememberCameraPositionState {

            position = CameraPosition.fromLatLngZoom(
                LatLng(
                    -26.2041,
                    28.0473
                ),
                10f
            )
        }

    /*
     * --------------------------------------------------
     * INITIAL LOCATION CHECK
     * --------------------------------------------------
     *
     * If permission was already granted when the screen
     * opened, request the current location.
     */

    LaunchedEffect(locationPermissionGranted) {

        if (locationPermissionGranted) {

            locationManager.getCurrentLocation(

                onLocationReceived = { lat, lon ->

                    latitude = lat
                    longitude = lon
                },

                onError = { error ->

                    Log.e(
                        "FishTrailLocation",
                        "Could not get current location: $error"
                    )
                }
            )
        }
    }

    /*
     * --------------------------------------------------
     * CENTER CAMERA ON CURRENT LOCATION
     * --------------------------------------------------
     *
     * This only happens:
     *
     * 1. After the map has loaded.
     * 2. When a GPS location exists.
     * 3. When the user is NOT selecting a catch location.
     * 4. Once per MapScreen instance.
     */

    LaunchedEffect(
        mapLoaded,
        latitude,
        longitude,
        selectingLocationForCatch
    ) {

        if (
            mapLoaded &&
            latitude != null &&
            longitude != null &&
            !selectingLocationForCatch &&
            !cameraCenteredOnLocation
        ) {

            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(
                    LatLng(
                        latitude!!,
                        longitude!!
                    ),
                    15f
                )

            cameraCenteredOnLocation = true
        }
    }

    /*
     * --------------------------------------------------
     * MAP UI
     * --------------------------------------------------
     */

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        GoogleMap(

            modifier = Modifier.fillMaxSize(),

            cameraPositionState = cameraPositionState,

            /*
             * The map only displays the My Location layer
             * when the app actually has location permission.
             */
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionGranted
            ),

            /*
             * Standard map controls.
             */
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                rotationGesturesEnabled = true,
                tiltGesturesEnabled = true,
                myLocationButtonEnabled = locationPermissionGranted
            ),

            /*
             * Mark the map as ready only after Google Maps
             * has completely rendered the map.
             */
            onMapLoaded = {

                mapLoaded = true

                Log.d(
                    "FishTrailMap",
                    "Google Map loaded successfully."
                )
            },

            /*
             * --------------------------------------------------
             * MAP TAP HANDLING
             * --------------------------------------------------
             */

            onMapClick = { latLng ->

                val tappedLatitude =
                    latLng.latitude

                val tappedLongitude =
                    latLng.longitude

                /*
                 * --------------------------------------------------
                 * CATCH LOCATION SELECTION MODE
                 * --------------------------------------------------
                 */

                if (selectingLocationForCatch) {

                    /*
                     * Coordinates are captured immediately.
                     *
                     * We always have a safe fallback name so
                     * reverse geocoding cannot prevent navigation.
                     */

                    coroutineScope.launch {

                        var locationName =
                            "Selected Map Location"

                        /*
                         * Give Geocoder a short amount of time.
                         *
                         * If Geocoder is unavailable, slow, or fails,
                         * the timeout returns null and the fallback name
                         * is used.
                         */
                        val geocodedName =
                            withTimeoutOrNull(1500L) {

                                try {

                                    withContext(Dispatchers.IO) {

                                        @Suppress("DEPRECATION")
                                        val geocoder =
                                            Geocoder(context)

                                        @Suppress("DEPRECATION")
                                        val addresses =
                                            geocoder.getFromLocation(
                                                tappedLatitude,
                                                tappedLongitude,
                                                1
                                            )

                                        val address =
                                            addresses
                                                ?.firstOrNull()

                                        when {

                                            !address?.locality.isNullOrBlank() ->
                                                address?.locality

                                            !address?.subAdminArea.isNullOrBlank() ->
                                                address?.subAdminArea

                                            !address?.adminArea.isNullOrBlank() ->
                                                address?.adminArea

                                            !address?.getAddressLine(0).isNullOrBlank() ->
                                                address?.getAddressLine(0)

                                            else ->
                                                null
                                        }
                                    }

                                } catch (e: Exception) {

                                    Log.e(
                                        "FishTrailGeocoder",
                                        "Reverse geocoding failed.",
                                        e
                                    )

                                    null
                                }
                            }

                        if (!geocodedName.isNullOrBlank()) {

                            locationName =
                                geocodedName
                        }

                        /*
                         * ALWAYS return the selected location.
                         *
                         * Even if Geocoder fails, Catch Log receives
                         * the coordinates and fallback location name.
                         */
                        onLocationSelected(
                            tappedLatitude,
                            tappedLongitude,
                            locationName
                        )
                    }

                } else {

                    /*
                     * --------------------------------------------------
                     * NORMAL MAP MODE
                     * --------------------------------------------------
                     *
                     * Do NOT navigate anywhere.
                     *
                     * Simply place a temporary marker where the user
                     * tapped the map.
                     */

                    selectedLatitude =
                        tappedLatitude

                    selectedLongitude =
                        tappedLongitude
                }
            }

        ) {

            /*
             * --------------------------------------------------
             * CURRENT LOCATION MARKER
             * --------------------------------------------------
             */

            if (
                latitude != null &&
                longitude != null
            ) {

                val currentLocation =
                    LatLng(
                        latitude!!,
                        longitude!!
                    )

                Marker(
                    state = remember(
                        latitude,
                        longitude
                    ) {
                        MarkerState(
                            position = currentLocation
                        )
                    },
                    title = "My Location",
                    snippet = "Current device location"
                )
            }

            /*
             * --------------------------------------------------
             * MANUALLY SELECTED MARKER
             * --------------------------------------------------
             */

            if (
                selectedLatitude != null &&
                selectedLongitude != null
            ) {

                val selectedLocation =
                    LatLng(
                        selectedLatitude!!,
                        selectedLongitude!!
                    )

                Marker(
                    state = remember(
                        selectedLatitude,
                        selectedLongitude
                    ) {
                        MarkerState(
                            position = selectedLocation
                        )
                    },
                    title = "Selected Fishing Location",
                    snippet = "Location selected manually"
                )
            }

            /*
             * --------------------------------------------------
             * SAVED CATCH MARKERS
             * --------------------------------------------------
             */

            catches.forEach { fishingCatch ->

                val catchLatitude =
                    fishingCatch.latitude

                val catchLongitude =
                    fishingCatch.longitude

                if (
                    catchLatitude != null &&
                    catchLongitude != null
                ) {

                    val catchLocation =
                        LatLng(
                            catchLatitude,
                            catchLongitude
                        )

                    Marker(
                        state = remember(
                            fishingCatch.id,
                            catchLatitude,
                            catchLongitude
                        ) {
                            MarkerState(
                                position = catchLocation
                            )
                        },
                        title = fishingCatch.species,
                        snippet = buildString {

                            append(
                                "${fishingCatch.weight} kg"
                            )

                            append(" • ")

                            append(
                                "${fishingCatch.length} cm"
                            )

                            if (
                                fishingCatch.locationName
                                    .isNotBlank()
                            ) {

                                append(" • ")

                                append(
                                    fishingCatch.locationName
                                )
                            }
                        }
                    )
                }
            }
        }

        /*
         * --------------------------------------------------
         * CATCH LOCATION SELECTION INSTRUCTIONS
         * --------------------------------------------------
         */

        if (selectingLocationForCatch) {

            Button(
                onClick = {
                    /*
                     * Selection is completed by tapping the map.
                     *
                     * Back navigation remains controlled by
                     * FishTrailNavigation.
                     */
                },
                enabled = false,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {

                Text(
                    text =
                        "Tap the map to choose a fishing location"
                )
            }
        }

        /*
         * --------------------------------------------------
         * LOCATION PERMISSION BUTTON
         * --------------------------------------------------
         */

        if (
            !locationPermissionGranted &&
            !selectingLocationForCatch
        ) {

            Button(
                onClick = {

                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },

                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {

                Text("Allow Location")
            }
        }

        /*
         * --------------------------------------------------
         * CLEAR MANUAL LOCATION
         * --------------------------------------------------
         */

        if (
            selectedLatitude != null &&
            selectedLongitude != null &&
            !selectingLocationForCatch
        ) {

            Button(
                onClick = {

                    selectedLatitude = null
                    selectedLongitude = null
                },

                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {

                Text("Clear Selected Location")
            }
        }
    }
}
