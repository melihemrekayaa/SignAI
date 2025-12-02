package com.signai

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.signai.ui.screens.creation.CreateSignatureScreen
import com.signai.ui.screens.landing.LandingScreen
import com.signai.ui.screens.onboarding.OnboardingScreen
import com.signai.ui.theme.SignatureTheme
import org.koin.compose.KoinContext

// 1. Ekran Rotalarını (Adreslerini) Tanımlıyoruz
enum class SignAIScreen {
    Landing,
    Onboarding,
    CreateSignature
}

@Composable
fun App() {
    // KRİTİK DÜZELTME: 'KoinApplication' yerine 'KoinContext' kullanıyoruz.
    // Çünkü Android'de BaseApp (veya iOS'ta MainViewController) Koin'i zaten başlattı.
    // Tekrar başlatmaya çalışırsak uygulama çöker (KoinAppAlreadyStartedException).
    // KoinContext, "Mevcut çalışan Koin yapısını kullan" demektir.
    KoinContext {
        SignatureTheme {
            // 2. Navigasyon Kontrolcüsü
            val navController = rememberNavController()

            // 3. NavHost: Hangi adreste hangi ekranın gösterileceğini belirler
            NavHost(
                navController = navController,
                startDestination = SignAIScreen.Landing.name // İlk açılan ekran
            ) {

                // --- LANDING SCREEN (Karşılama) ---
                composable(route = SignAIScreen.Landing.name) {
                    LandingScreen(
                        onCreateSignatureClick = {
                            // Butona basınca Onboarding'e git
                            navController.navigate(SignAIScreen.Onboarding.name)
                        }
                    )
                }

                // --- ONBOARDING SCREEN (Tanıtım/Seçim) ---
                composable(route = SignAIScreen.Onboarding.name) {
                    OnboardingScreen(
                        onFinished = {
                            // Onboarding bitince İmza Oluşturmaya git
                            navController.navigate(SignAIScreen.CreateSignature.name) {
                                // Kullanıcı 'Geri' tuşuna bastığında tekrar Onboarding sorularına dönmesin diye
                                // Landing ekranına kadar olan geçmişi yığından temizliyoruz.
                                popUpTo(SignAIScreen.Landing.name) { inclusive = false }
                            }
                        }
                    )
                }

                // --- CREATE SIGNATURE (İmza Çizme) ---
                composable(route = SignAIScreen.CreateSignature.name) {
                    CreateSignatureScreen(
                        onBackClick = {
                            // Geri tuşuna basınca bir önceki ekrana dön
                            navController.popBackStack()
                        },
                        onGenerateClick = {
                            // Buraya ileride 'Result' ekranına geçişi ekleyeceğiz
                            println("Generate butonuna basıldı! AI tetiklenecek.")
                        },
                        onOpenSettingsClick = {
                            println("Ayarlar tıklandı")
                        }
                    )
                }
            }
        }
    }
}