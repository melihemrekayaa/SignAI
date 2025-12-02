package com.signai.features.signature.creation

import com.signai.domain.model.PersonalityTrait
import com.signai.domain.model.SignatureStyle

data class CreateSignatureState(
    val name: String = "",
    val selectedStyle: SignatureStyle? = null, // Örn: Professional, Artistic
    val selectedTraits: Set<PersonalityTrait> = emptySet(), // Örn: Bold, Elegant (Max 3)
    val generatedImageUrl: String? = null, // Sonuç URL'i
    val isLoading: Boolean = false,
    val error: String? = null
) {
    // Tasarımda "Next" veya "Generate" butonunun aktif olma kuralı
    val canGenerate: Boolean
        get() = name.isNotBlank() && selectedStyle != null
}