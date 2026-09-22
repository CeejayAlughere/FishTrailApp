package com.fishtrail.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationManager(
    private val context: Context
) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("MissingPermission")
    fun getCurrentLocation(
        onLocationReceived: (Double, Double) -> Unit,
        onError: (String) -> Unit
    ) {

        if (!hasLocationPermission()) {
            onError("Location permission has not been granted.")
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    onLocationReceived(
                        location.latitude,
                        location.longitude
                    )

                } else {

                    onError("Unable to determine your current location.")
                }
            }
            .addOnFailureListener {

                onError("Unable to get your current location.")
            }
    }
}