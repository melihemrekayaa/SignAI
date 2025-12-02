package com.signai.domain.model

// Tasarımdaki "Traits" (Kişilik Özellikleri)
enum class PersonalityTrait(val promptModifier: String) {
    BOLD("thick stroke, confident lines, heavy pressure, strong impact"),
    ELEGANT("curvy, smooth flow, sophisticated, calligraphy style"),
    MINIMALIST("clean lines, simple, thin stroke, readable, less details"),
    CREATIVE("artistic, abstract elements, unique flair, expressive"),
    FORMAL("business professional, straight lines, legible, classic"),
    PLAYFUL("bouncy baseline, rounded letters, casual, fun vibe")
}

// Tasarımdaki "Style" (Ana Tarz)
enum class SignatureStyle(val basePrompt: String) {
    PROFESSIONAL("professional signature, business style, clean dark ink"),
    ARTISTIC("artistic autograph, creative flourishes, expressive style"),
    CASUAL("casual handwritten name, relaxed style, quick signage"),
    RETRO("vintage fountain pen style, classic handwriting, intricate details")
}

// Kullanıcının UI'da seçip "Generate"e bastığı an oluşan veri paketi
data class SignatureRequest(
    val name: String,
    val style: SignatureStyle,
    val traits: List<PersonalityTrait> = emptyList()
)