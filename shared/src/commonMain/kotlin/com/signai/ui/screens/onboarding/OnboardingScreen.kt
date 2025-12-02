package com.signai.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.signai.ui.components.PrimaryButton
import com.signai.ui.screens.onboarding.steps.PersonalityIntroStep
import com.signai.ui.screens.onboarding.steps.PersonalityReviewStep
import com.signai.ui.screens.onboarding.steps.StyleSelectionStep
import com.signai.ui.screens.onboarding.steps.TraitSelectionStep
import org.koin.compose.koinInject

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()

    // DÜZELTME 1: Surface ile tüm ekranı arka plan rengine boyuyoruz (Çentik dahil)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Temadaki koyu renk (Navy900)
    ) {
        Scaffold(
            // DÜZELTME 2: Sistem barlarının (Notch, Home Indicator) altına girmeyi yönetiyoruz
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp) // Ekstra üst boşluk
                ) {
                    // Üst Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        if (state.currentStep > 1) {
                            IconButton(
                                onClick = { viewModel.back() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(32.dp))
                        }

                        Text(
                            text = "Step ${state.currentStep} of ${state.totalSteps}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.size(32.dp))
                    }

                    Spacer(Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { state.currentStep.toFloat() / state.totalSteps.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        color = MaterialTheme.colorScheme.primary,
                        strokeCap = StrokeCap.Round,
                    )
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        // Alt çizgiye (Home Indicator) çok yapışmaması için padding
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                ) {
                    PrimaryButton(
                        text = if (state.currentStep == state.totalSteps) "Generate Signature" else "Next",
                        onClick = {
                            if (state.currentStep == state.totalSteps) {
                                onFinished()
                            } else {
                                viewModel.next()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.skip() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Skip for now")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "OnboardingSteps"
                ) { targetStep ->
                    when (targetStep) {
                        1 -> StyleSelectionStep(state) { viewModel.selectStyle(it) }
                        2 -> TraitSelectionStep(state) { viewModel.toggleTrait(it) }
                        3 -> PersonalityIntroStep()
                        4 -> PersonalityReviewStep(state) { viewModel.goToStep(it) }
                        else -> Box(Modifier.fillMaxSize()) { Text("Unknown Step") }
                    }
                }
            }
        }
    }
}