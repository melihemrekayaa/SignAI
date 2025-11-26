package com.signai.android.onboarding

import com.signai.signature.PersonalityTrait
import com.signai.signature.SignatureStyle

data class OnboardingUiState(
    val fullName: String = "",
    val nickname: String = "",
    val occupation: String = "",
    val style: SignatureStyle = SignatureStyle.MODERN,
    val trait: PersonalityTrait = PersonalityTrait.CREATIVE,
    val isGenerating: Boolean = false,
    val aiPrompt: String? = null,
    val aiInstructions: String? = null,
    val errorMessage: String? = null
)
