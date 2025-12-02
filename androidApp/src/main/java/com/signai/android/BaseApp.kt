package com.signai.android

import android.app.Application
import com.signai.di.commonModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Loglama kütüphanesini başlat
        Napier.base(DebugAntilog())

        // Dependency Injection (Koin) Başlat
        // Burası uygulamanın kalbidir. Tek bir kez burada çalışır.
        startKoin {
            // Android Context'i (Erişim yetkisi) Koin'e veriyoruz
            androidContext(this@BaseApp)

            // Modülleri yüklüyoruz (ViewModel, Repository vs.)
            modules(commonModule)
        }
    }
}