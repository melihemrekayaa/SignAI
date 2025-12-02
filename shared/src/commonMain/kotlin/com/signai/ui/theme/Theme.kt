package com.signai.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// 1. Renk Şemasını Tanımlıyoruz (Senin Koyu Teman)
private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = TextPrimary,
    background = Navy900,
    onBackground = TextPrimary,
    surface = Navy800,
    onSurface = TextPrimary,
    surfaceVariant = Navy700, // Kart arka planları için
    onSurfaceVariant = TextSecondary,
    secondary = AccentBlue,
    onSecondary = TextPrimary,
    error = ErrorRed,
    onError = TextPrimary,
    outline = BorderSubtle
)

// 2. Şekiller (Yuvarlatılmış Köşeler)
private val SignatureShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun SignatureTheme(
    content: @Composable () -> Unit
) {
    // 3. Yeni Font Ayarlarını Alıyoruz (Önceki adımda yazdığımız)
    val typography = getAppTypography()

    // 4. Hepsini Birleştiriyoruz
    MaterialTheme(
        colorScheme = DarkColorScheme, // <-- RENKLER GERİ GELDİ
        typography = typography,       // <-- YENİ FONT EKLENDİ
        shapes = SignatureShapes,
        content = content
    )
}