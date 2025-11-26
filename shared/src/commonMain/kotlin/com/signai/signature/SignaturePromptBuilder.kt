package com.signai.signature

class SignaturePromptBuilder {

    fun buildPrompt(profile: SignatureProfile): String {
        val base = "You are a professional calligrapher. " +
                "Design a unique, aesthetically pleasing handwritten signature.\n"

        val info = buildString {
            appendLine("Full name: ${profile.fullName}")
            profile.nickname?.takeIf { it.isNotBlank() }?.let {
                appendLine("Nickname / short form: $it")
            }
            profile.occupation?.takeIf { it.isNotBlank() }?.let {
                appendLine("Occupation: $it")
            }
            appendLine("Dominant hand: ${profile.dominantHand}")
            appendLine("Preferred style: ${profile.style.name.lowercase()}")
            appendLine("Personality: ${profile.trait.name.lowercase()}")
            appendLine()
            appendLine("Return a short step-by-step description of how to draw this signature.")
        }

        return base + info
    }
}
