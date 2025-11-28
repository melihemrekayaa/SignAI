package com.signai.android.di

import com.signai.android.SignAIOnboardingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    viewModel { SignAIOnboardingViewModel() }
}
