package com.signai.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = TextPrimary,
    background = Navy900,
    onBackground = TextPrimary,
    surface = Navy800,
    onSurface = TextPrimary,
    secondary = AccentBlue,
    onSecondary = TextPrimary,
    error = ErrorRed,
    onError = TextPrimary
)

private val SignatureShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun SignatureTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = SignatureTypography,
        shapes = SignatureShapes,
        content = content
    )
}
