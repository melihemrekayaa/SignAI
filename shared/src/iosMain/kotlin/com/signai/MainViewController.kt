package com.signai

// import App <-- Bunu sildik, çünkü aynı paketteler (com.signai)
import androidx.compose.ui.window.ComposeUIViewController
import com.signai.di.commonModule
import org.koin.core.context.startKoin

// 1. UI Köprüsü
fun MainViewController() = ComposeUIViewController {
    App()
}

// 2. Koin Başlatıcı
fun doInitKoin() {
    startKoin {
        modules(commonModule)
    }
}