package com.signai.android

import android.app.Application
import com.signai.android.di.androidModule
import com.signai.di.commonModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

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