package com.signai.domain.repository

import com.signai.domain.model.SignatureRequest

interface SignatureRepository {
    // URL string döner (Pollinations.ai direkt resim URL'i veriyor)
    suspend fun generateSignatureUrl(request: SignatureRequest): String
}