package com.signai.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signai.android.onboarding.OnboardingUiState
import com.signai.signature.PersonalityTrait
import com.signai.signature.SignatureGenerator
import com.signai.signature.SignatureProfile
import com.signai.signature.SignatureStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignAIOnboardingViewModel(
    private val generator: SignatureGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    // 🔹 Text field değişim fonksiyonları
    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value)
    }

    fun onNicknameChange(value: String) {
        _uiState.value = _uiState.value.copy(nickname = value)
    }

    fun onOccupationChange(value: String) {
        _uiState.value = _uiState.value.copy(occupation = value)
    }

    // 🔹 Style / trait seçimleri (şimdilik UI’da kullanmasak da dursun)
    fun onStyleSelected(style: SignatureStyle) {
        _uiState.value = _uiState.value.copy(style = style)
    }

    fun onTraitSelected(trait: PersonalityTrait) {
        _uiState.value = _uiState.value.copy(trait = trait)
    }

    // 🔹 İmza üret
    fun onGenerateClicked() {
        val state = _uiState.value
        if (state.fullName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Lütfen ad soyad gir.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isGenerating = true,
                errorMessage = null
            )

            try {
                val profile = SignatureProfile(
                    fullName = state.fullName.trim(),
                    nickname = state.nickname.trim().ifBlank { null },
                    occupation = state.occupation.trim().ifBlank { null },
                    style = state.style,
                    trait = state.trait
                )

                val result = generator.generate(profile)

                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    aiPrompt = result.prompt,
                    aiInstructions = result.instructions
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = "İmza üretiminde bir hata oluştu."
                )
            }
        }
    }

    fun consumeError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}