package com.signai.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.signai.App

// DİKKAT: App fonksiyonunu import etmelisin.
// Eğer App.kt dosyan 'com.signai' paketindeyse:
// Eğer App.kt dosyanı 'composeApp' içinde kök dizine koyduysan import gerekmeyebilir veya paket adı farklı olabilir.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // ARTIK SADECE BUNU ÇAĞIRIYORUZ:
            // Tüm UI kontrolünü ortak koda (App.kt) devrediyoruz.
            App()
        }
    }
}