package com.signai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

import signai.shared.generated.resources.Res
import signai.shared.generated.resources.manrope_bold
import signai.shared.generated.resources.manrope_medium
import signai.shared.generated.resources.manrope_regular


// Compose Resources ile Font Ailesi Tanımı
// NOT: Dosya isimlerin (Manrope_Bold vb.) burada 'Res.font.Manrope_Bold' olarak otomatik gelir.
// Eğer kırmızı yanarsa projeyi bir kez 'Build' etmen yeterli.
@Composable
fun getAppFontFamily() = FontFamily(
    Font(Res.font.manrope_regular  , FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.manrope_medium, FontWeight.Medium, FontStyle.Normal),
    Font(Res.font.manrope_bold, FontWeight.Bold, FontStyle.Normal)
)

@Composable
fun getAppTypography(): Typography {
    val appFontFamily = getAppFontFamily()

    return Typography(
        headlineMedium = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        ),
        headlineSmall = TextStyle( // Onboarding başlıkları için
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        titleMedium = TextStyle( // Kart başlıkları için
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        bodyLarge = TextStyle( // Açıklama metinleri için
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        labelLarge = TextStyle( // Buton yazıları için
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Medium, // Butonlar genelde Medium olur
            fontSize = 14.sp
        )
    )
}