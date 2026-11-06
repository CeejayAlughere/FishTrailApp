package com.fishtrail.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fishtrail.app.notifications.FishTrailFirebaseMessagingService
import com.fishtrail.app.settings.AppPreferences
import com.fishtrail.app.ui.theme.FishTrailTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    private var darkModeEnabled by mutableStateOf(false)

    // Stores a destination requested by an FCM notification.
    private var pendingNotificationRoute by mutableStateOf<String?>(null)

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                Log.d(
                    "FishTrailFCM",
                    "Notification permission granted."
                )
            } else {
                Log.d(
                    "FishTrailFCM",
                    "Notification permission denied."
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Check whether the app was opened through a notification.
        pendingNotificationRoute =
            extractNotificationRoute(intent)

        createNotificationChannel()

        splashScreen.setOnExitAnimationListener { splashScreenView ->

            val iconView = splashScreenView.iconView

            val scaleX =
                android.animation.ObjectAnimator.ofFloat(
                    iconView,
                    android.view.View.SCALE_X,
                    1f,
                    1.4f
                )

            val scaleY =
                android.animation.ObjectAnimator.ofFloat(
                    iconView,
                    android.view.View.SCALE_Y,
                    1f,
                    1.4f
                )

            val fadeOut =
                android.animation.ObjectAnimator.ofFloat(
                    splashScreenView.view,
                    android.view.View.ALPHA,
                    1f,
                    0f
                )

            val animatorSet =
                android.animation.AnimatorSet()

            animatorSet.playTogether(
                scaleX,
                scaleY,
                fadeOut
            )

            animatorSet.duration = 400L

            animatorSet.interpolator =
                android.view.animation.AccelerateDecelerateInterpolator()

            animatorSet.addListener(
                object : android.animation.AnimatorListenerAdapter() {

                    override fun onAnimationEnd(
                        animation: android.animation.Animator
                    ) {
                        splashScreenView.remove()
                    }
                }
            )

            animatorSet.start()
        }

        // Initialise Google Places.
        if (!Places.isInitialized()) {

            Places.initializeWithNewPlacesApiEnabled(
                applicationContext,
                getString(R.string.google_maps_key)
            )
        }

        // Load saved dark mode preference.
        darkModeEnabled =
            AppPreferences.isDarkModeEnabled(
                applicationContext
            )

        setContent {

            FishTrailTheme(
                darkTheme = darkModeEnabled,
                dynamicColor = true
            ) {

                FishTrailNavigation(
                    darkModeEnabled = darkModeEnabled,

                    onDarkModeChanged = { enabled ->

                        AppPreferences.setDarkModeEnabled(
                            applicationContext,
                            enabled
                        )

                        darkModeEnabled = enabled
                    },

                    notificationRoute = pendingNotificationRoute,

                    onNotificationRouteHandled = {
                        pendingNotificationRoute = null
                    }
                )
            }
        }

        requestNotificationPermission()
    }

    // Called when an existing MainActivity receives a new Intent.
    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)

        setIntent(intent)

        pendingNotificationRoute =
            extractNotificationRoute(intent)

        Log.d(
            "FishTrailFCM",
            "New notification intent received."
        )
    }

    // Determine whether this launch came from an FCM notification.
    private fun extractNotificationRoute(
        intent: Intent?
    ): String? {

        val extras = intent?.extras ?: return null

        val explicitRoute =
            extras.getString(
                FishTrailFirebaseMessagingService.EXTRA_NOTIFICATION_ROUTE
            )
                ?: extras.getString("route")
                ?: extras.getString("screen")

        if (!explicitRoute.isNullOrBlank()) {
            return explicitRoute
        }

        // Firebase-generated notifications may not contain our
        // custom route extra, but can contain Firebase metadata.
        val isFirebaseNotification =
            extras.keySet().any { key ->

                key.startsWith("google.") ||
                        key.startsWith("gcm.")
            }

        return if (isFirebaseNotification) {

            FishTrailFirebaseMessagingService
                .DEFAULT_NOTIFICATION_ROUTE

        } else {
            null
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                FishTrailFirebaseMessagingService.CHANNEL_ID,
                FishTrailFirebaseMessagingService.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    "Fishing alerts and updates from FishTrail"
            }

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {
                return
            }

            val preferences = getSharedPreferences(
                "fishtrail_notification_preferences",
                MODE_PRIVATE
            )

            val alreadyRequested =
                preferences.getBoolean(
                    "notification_permission_requested",
                    false
                )

            if (!alreadyRequested) {

                preferences.edit()
                    .putBoolean(
                        "notification_permission_requested",
                        true
                    )
                    .apply()

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }
}