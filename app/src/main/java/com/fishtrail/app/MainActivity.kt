package com.fishtrail.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fishtrail.app.settings.AppPreferences
import com.fishtrail.app.ui.theme.FishTrailTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    private var darkModeEnabled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

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
                object :
                    android.animation.AnimatorListenerAdapter() {

                    override fun onAnimationEnd(
                        animation: android.animation.Animator
                    ) {
                        splashScreenView.remove()
                    }
                }
            )

            animatorSet.start()
        }

        if (!Places.isInitialized()) {

            Places.initializeWithNewPlacesApiEnabled(
                applicationContext,
                getString(R.string.google_maps_key)
            )
        }

        /*
         * Load the saved Dark Mode preference once when
         * the application starts.
         */
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

                        /*
                         * Save the user's preference.
                         */
                        AppPreferences.setDarkModeEnabled(
                            applicationContext,
                            enabled
                        )

                        /*
                         * Update the Compose state.
                         *
                         * This causes FishTrailTheme to recompose
                         * and the entire application theme changes.
                         */
                        darkModeEnabled = enabled
                    }
                )
            }
        }
    }
}