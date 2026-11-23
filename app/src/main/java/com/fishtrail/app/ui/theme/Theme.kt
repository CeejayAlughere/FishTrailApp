package com.fishtrail.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
    outlineVariant = OutlineVariantLight,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    surfaceTint = OceanTealLight,
    inverseSurface = SurfaceDark,
    inverseOnSurface = OnSurfaceDark,
    inversePrimary = OceanTealDark,
    scrim = Color.Black,
    primaryFixed = OceanTealContainerLight,
    primaryFixedDim = OceanTealDark,
    onPrimaryFixed = OnOceanTealDark,
    onPrimaryFixedVariant = OnOceanTealContainerLight,
    secondaryFixed = MarineBlueContainerLight,
    secondaryFixedDim = MarineBlueDark,
    onSecondaryFixed = OnMarineBlueDark,
    onSecondaryFixedVariant = OnMarineBlueContainerLight,
    tertiaryFixed = ForestGreenContainerLight,
    tertiaryFixedDim = ForestGreenDark,
    onTertiaryFixed = OnForestGreenDark,
    onTertiaryFixedVariant = OnForestGreenContainerLight,
)

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
    outlineVariant = OutlineVariantDark,
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    surfaceTint = OceanTealDark,
    inverseSurface = SurfaceLight,
    inverseOnSurface = OnSurfaceLight,
    inversePrimary = OceanTealLight,
    scrim = Color.Black,
    primaryFixed = OceanTealContainerLight,
    primaryFixedDim = OceanTealDark,
    onPrimaryFixed = OnOceanTealDark,
    onPrimaryFixedVariant = OnOceanTealContainerLight,
    secondaryFixed = MarineBlueContainerLight,
    secondaryFixedDim = MarineBlueDark,
    onSecondaryFixed = OnMarineBlueDark,
    onSecondaryFixedVariant = OnMarineBlueContainerLight,
    tertiaryFixed = ForestGreenContainerLight,
    tertiaryFixedDim = ForestGreenDark,
    onTertiaryFixed = OnForestGreenDark,
    onTertiaryFixedVariant = OnForestGreenContainerLight,
)

@Composable
fun FishTrailTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        shapes = FishTrailShapes,
        content = content
    )
}
