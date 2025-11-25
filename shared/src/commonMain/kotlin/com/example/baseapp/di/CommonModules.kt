package com.example.baseapp.di

import org.koin.core.module.Module
import org.koin.dsl.module
import io.github.aakira.napier.Napier

// İleride repository, use case, http client vs. buraya gelecek.
val commonModule: Module = module {

    // Örnek: global logger
    single {
        Napier
    }

    // Örnek: Buraya ileride HttpClient, Repository, UseCase vs. ekleyeceğiz
    // single { HttpClientFactory().create() }
}
