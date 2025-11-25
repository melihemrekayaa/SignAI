package com.example.baseapp.android.di

import org.koin.dsl.module

val androidModule = module {
    // Android'e özel bağımlılıklar (DataStore, NotificationHelper, vb.) buraya gelecek

    // Örnek:
    // single { AndroidLogger(androidContext()) }
}
