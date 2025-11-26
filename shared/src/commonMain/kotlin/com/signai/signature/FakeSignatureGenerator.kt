package com.signai.signature

class FakeSignatureGenerator(
    private val promptBuilder: SignaturePromptBuilder
) : SignatureGenerator {

    override suspend fun generate(profile: SignatureProfile): SignatureResult {
        val prompt = promptBuilder.buildPrompt(profile)

        val instructions = buildString {
            appendLine("1) ${profile.fullName} adını tek bir akıcı çizgide yaz.")
            profile.nickname?.let {
                appendLine("2) İmzanda '$it' kısmını daha büyük ve belirgin yap.")
            }
            appendLine("3) Soyad kısmının son harfini uzat ve altını hafifçe çiz.")
            appendLine("4) Çizgiyi hafif yukarı doğru kıvır; bu daha enerjik bir hava verir.")
            appendLine("5) Tüm imzayı tek nefeste, kalemi kaldırmadan atmaya çalış.")
        }

        return SignatureResult(
            prompt = prompt,
            instructions = instructions
        )
    }
}
