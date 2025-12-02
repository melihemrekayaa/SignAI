package com.signai.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel : ViewModel() {

    // UI'ın dinleyeceği veri akışı
    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    // Bir sonraki adıma geç
    fun next() {
        if (_uiState.value.currentStep < _uiState.value.totalSteps) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    // Bir önceki adıma dön
    fun back() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    // Adımları atla (Direkt sona git)
    fun skip() {
        _uiState.update { it.copy(currentStep = it.totalSteps) }
    }

    // Spesifik bir adıma git (Düzenleme için)
    fun goToStep(step: Int) {
        _uiState.update { it.copy(currentStep = step) }
    }

    // Stil Seçimi
    fun selectStyle(index: Int) {
        _uiState.update { it.copy(selectedStyleIndex = index) }
    }

    // Özellik (Trait) Seçimi (Max 3 tane)
    fun toggleTrait(id: String) {
        _uiState.update { state ->
            val current = state.selectedTraitIds.toMutableSet()
            if (current.contains(id)) {
                current.remove(id) // Varsa çıkar
            } else {
                if (current.size < 3) {
                    current.add(id) // Yoksa ve limit dolmadıysa ekle
                }
            }
            state.copy(selectedTraitIds = current)
        }
    }
}