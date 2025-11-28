package com.signai.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import com.signai.android.create.CreateSignatureScreen
import com.signai.android.onboarding.OnboardingScreen
import com.signai.android.ui.feature.landing.LandingScreen
import org.koin.androidx.compose.koinViewModel

// Root screens
private enum class RootDestination {
    Landing,
    Onboarding,
    CreateSignature
}

@Composable
fun SignAIAppRoot() {
    // Onboarding VM
    val onboardingViewModel: SignAIOnboardingViewModel = koinViewModel()

    // ✅ Saveable ama sadece Int saklıyoruz
    var currentDestinationIndex by rememberSaveable {
        mutableIntStateOf(RootDestination.Landing.ordinal)
    }
    val currentDestination = RootDestination.entries[currentDestinationIndex]

    when (currentDestination) {
        RootDestination.Landing -> {
            LandingScreen(
                onCreateSignatureClick = {
                    currentDestinationIndex = RootDestination.Onboarding.ordinal
                }
            )
        }

        RootDestination.Onboarding -> {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onFinished = {
                    currentDestinationIndex = RootDestination.CreateSignature.ordinal
                }
            )
        }

        RootDestination.CreateSignature -> {
            CreateSignatureScreen(
                onBackClick = {
                    // İstersen buradan tekrar Landing'e dönebilirsin
                    currentDestinationIndex = RootDestination.Landing.ordinal
                },
                onOpenSettingsClick = { /* TODO later */ },
                onGenerateClick = { /* TODO later - real AI call */ }
            )
        }
    }
}
