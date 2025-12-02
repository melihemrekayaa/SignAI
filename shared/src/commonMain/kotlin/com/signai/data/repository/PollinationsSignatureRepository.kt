package com.signai.data.repository

import com.signai.domain.model.SignatureRequest
import com.signai.domain.repository.SignatureRepository
import io.ktor.http.encodeURLPathPart

class PollinationsSignatureRepository : SignatureRepository {

    override suspend fun generateSignatureUrl(request: SignatureRequest): String {
        // 1. Prompt Mühendisliği: Kullanıcının seçimlerini cümleye döküyoruz
        val prompt = buildPrompt(request)

        // 2. Pollinations API URL yapısı: https://pollinations.ai/p/{prompt}
        // URL içinde boşluk olamaz, bu yüzden encode ediyoruz.
        // Genişlik ve yükseklik parametreleri de ekleyebiliriz (?width=800&height=400)
        val encodedPrompt = prompt.encodeURLPathPart()

        return "https://pollinations.ai/p/$encodedPrompt?width=1024&height=1024&seed=${(0..10000).random()}&model=flux"
    }

    private fun buildPrompt(request: SignatureRequest): String {
        val traitsDescription = request.traits.joinToString(", ") { it.promptModifier }

        // Flux Modeli için optimize edilmiş prompt şablonu:
        return """
            handwritten signature of the name "${request.name}", 
            isolated on pure white background, 
            black ink, 
            ${request.style.basePrompt}, 
            $traitsDescription, 
            high contrast, vector style, masterpiece, 8k resolution, 
            no watermark, no blur, no extra text
        """.trimIndent().replace("\n", " ")
    }
}