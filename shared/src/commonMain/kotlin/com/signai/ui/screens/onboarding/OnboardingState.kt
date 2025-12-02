package com.signai.ui.screens.onboarding

data class OnboardingState(
    val currentStep: Int = 1,
    val totalSteps: Int = 4,

    // Seçilen Değerler
    val selectedStyleIndex: Int = 0,
    val selectedTraitIds: Set<String> = emptySet(),

    // Sabit Seçenekler (UI'da gösterilecek listeler)
    val styleOptions: List<StyleOption> = listOf(
        StyleOption("professional", "Professional"),
        StyleOption("creative", "Creative"),
        StyleOption("minimalist", "Minimalist"),
        StyleOption("signature", "Signature")
    ),
    val traitOptions: List<TraitOption> = listOf(
        TraitOption("bold", "Bold"),
        TraitOption("elegant", "Elegant"),
        TraitOption("playful", "Playful"),
        TraitOption("formal", "Formal"),
        TraitOption("modern", "Modern"),
        TraitOption("classic", "Classic")
    )
)

// Yardımcı Sınıflar
data class StyleOption(val id: String, val title: String)
data class TraitOption(val id: String, val label: String)