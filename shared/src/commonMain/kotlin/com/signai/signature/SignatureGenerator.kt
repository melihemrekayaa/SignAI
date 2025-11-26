package com.signai.signature

interface SignatureGenerator {
    suspend fun generate(profile: SignatureProfile): SignatureResult
}
