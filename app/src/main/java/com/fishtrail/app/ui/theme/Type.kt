package com.fishtrail.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun style(size: Int, height: Int, weight: FontWeight, tracking: Double) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = height.sp,
    letterSpacing = tracking.sp
)

val Typography = Typography(
    displayLarge = style(56, 64, FontWeight.Medium, -0.5),
    displayMedium = style(44, 52, FontWeight.Medium, -0.4),
    displaySmall = style(36, 44, FontWeight.Medium, -0.3),
    headlineLarge = style(32, 40, FontWeight.Bold, -0.4),
    headlineMedium = style(26, 34, FontWeight.SemiBold, -0.3),
    headlineSmall = style(22, 30, FontWeight.SemiBold, -0.2),
    titleLarge = style(20, 28, FontWeight.SemiBold, -0.1),
    titleMedium = style(16, 24, FontWeight.SemiBold, 0.1),
    titleSmall = style(14, 20, FontWeight.SemiBold, 0.15),
    bodyLarge = style(16, 24, FontWeight.Normal, 0.2),
    bodyMedium = style(14, 21, FontWeight.Normal, 0.2),
    bodySmall = style(12, 18, FontWeight.Normal, 0.25),
    labelLarge = style(14, 20, FontWeight.SemiBold, 0.15),
    labelMedium = style(12, 16, FontWeight.Medium, 0.25),
    labelSmall = style(11, 16, FontWeight.Medium, 0.3)
)
