package com.example.baseapp.android

import android.app.Application
import com.example.baseapp.di.commonModule
import com.example.baseapp.android.di.androidModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        startKoin {

            modules(
                commonModule,
                androidModule
            )
        }
    }
}

