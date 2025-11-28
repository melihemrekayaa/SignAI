package com.signai.android

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StyleOption(
    val id: String,
    val title: String,
    val subtitle: String
)

data class TraitOption(
    val id: String,
    val label: String
)

data class SignAIOnboardingUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 4, // şimdilik 4 ekran
    val styleOptions: List<StyleOption> = defaultStyleOptions(),
    val selectedStyleIndex: Int = 0,
    val traitOptions: List<TraitOption> = defaultTraitOptions(),
    val selectedTraitIds: Set<String> = emptySet()
)

private fun defaultStyleOptions(): List<StyleOption> = listOf(
    StyleOption(
        id = "formal",
        title = "Formality",
        subtitle = "Clean, professional and polished"
    ),
    StyleOption(
        id = "casual",
        title = "Casual",
        subtitle = "Relaxed and approachable"
    ),
    StyleOption(
        id = "expressive",
        title = "Expressive",
        subtitle = "Playful, bold and unique"
    )
)

private fun defaultTraitOptions(): List<TraitOption> = listOf(
    TraitOption("bold", "Bold"),
    TraitOption("elegant", "Elegant"),
    TraitOption("minimalist", "Minimalist"),
    TraitOption("creative", "Creative"),
    TraitOption("formal", "Formal"),
    TraitOption("playful", "Playful")
)

class SignAIOnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignAIOnboardingUiState())
    val uiState: StateFlow<SignAIOnboardingUiState> = _uiState.asStateFlow()

    fun selectStyle(index: Int) {
        _uiState.update { state ->
            state.copy(
                selectedStyleIndex = index.coerceIn(
                    0,
                    (state.styleOptions.size - 1).coerceAtLeast(0)
                )
            )
        }
    }

    fun toggleTrait(traitId: String) {
        _uiState.update { state ->
            val current = state.selectedTraitIds
            val new = if (current.contains(traitId)) {
                current - traitId
            } else {
                // Max 3 trait
                if (current.size >= 3) current else current + traitId
            }
            state.copy(selectedTraitIds = new)
        }
    }

    fun next() {
        _uiState.update { state ->
            if (state.currentStep >= state.totalSteps) state
            else state.copy(currentStep = state.currentStep + 1)
        }
    }

    fun back() {
        _uiState.update { state ->
            if (state.currentStep <= 1) state
            else state.copy(currentStep = state.currentStep - 1)
        }
    }

    fun skip() {
        // Şimdilik skip de bir sonraki stepe geçsin
        next()
    }

    fun goToStep(step: Int) {
        _uiState.update { state ->
            state.copy(
                currentStep = step.coerceIn(1, state.totalSteps)
            )
        }
    }
}
