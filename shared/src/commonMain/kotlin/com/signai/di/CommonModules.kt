package com.signai.di

import com.signai.data.repository.PollinationsSignatureRepository
import com.signai.domain.repository.SignatureRepository
import com.signai.features.signature.creation.CreateSignatureViewModel
import com.signai.ui.screens.onboarding.OnboardingViewModel
import org.koin.dsl.module

val commonModule = module {
    // 1. Repository'yi oluştur (Singleton)
    single<SignatureRepository> { PollinationsSignatureRepository() }

    // 2. ViewModel'i oluştur (Factory: Her istendiğinde yeni üretilir)
    factory { CreateSignatureViewModel(get())
        OnboardingViewModel()}

}