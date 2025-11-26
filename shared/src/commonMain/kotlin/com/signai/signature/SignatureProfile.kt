package com.signai.signature

data class SignatureProfile(
    val fullName: String,
    val nickname: String? = null,
    val occupation: String? = null,
    val dominantHand: String = "right", // şimdilik sabit, sonra UI’dan alırız
    val style: SignatureStyle = SignatureStyle.MODERN,
    val trait: PersonalityTrait = PersonalityTrait.CREATIVE
)