package com.fishtrail.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OceanTealDark,
    onPrimary = OnOceanTealDark,
    primaryContainer = OceanTealContainerDark,
    onPrimaryContainer = OnOceanTealContainerDark,
    secondary = MarineBlueDark,
    onSecondary = OnMarineBlueDark,
    secondaryContainer = MarineBlueContainerDark,
    onSecondaryContainer = OnMarineBlueContainerDark,
    tertiary = ForestGreenDark,
    onTertiary = OnForestGreenDark,
    tertiaryContainer = ForestGreenContainerDark,
    onTertiaryContainer = OnForestGreenContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = OceanTealLight,
    onPrimary = OnOceanTealLight,
    primaryContainer = OceanTealContainerLight,
    onPrimaryContainer = OnOceanTealContainerLight,
    secondary = MarineBlueLight,
    onSecondary = OnMarineBlueLight,
    secondaryContainer = MarineBlueContainerLight,
    onSecondaryContainer = OnMarineBlueContainerLight,
    tertiary = ForestGreenLight,
    onTertiary = OnForestGreenLight,
    tertiaryContainer = ForestGreenContainerLight,
    onTertiaryContainer = OnForestGreenContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

@Composable
fun FishTrailTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to maintain branded nautical aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
