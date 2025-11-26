package com.signai.di

import com.signai.signature.FakeSignatureGenerator
import com.signai.signature.SignatureGenerator
import com.signai.signature.SignaturePromptBuilder
import org.koin.core.module.Module
import org.koin.dsl.module
import io.github.aakira.napier.Napier

// İleride repository, use case, http client vs. buraya gelecek.
val commonModule: Module = module {

    // Örnek: global logger
    single {
        SignaturePromptBuilder()
    }

    single {
        Napier
    }
    single<SignatureGenerator> { FakeSignatureGenerator(get()) }



    // Örnek: Buraya ileride HttpClient, Repository, UseCase vs. ekleyeceğiz
    // single { HttpClientFactory().create() }
}
