plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.signai.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.signai.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // HATA ÇÖZÜMÜ:
    // 'projects.shared' yerine 'project(":shared")' kullanıyoruz.
    // Bu yöntem her zaman çalışır ve derleme hatasını giderir.
    implementation(project(":shared"))

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)

    // Material Icons Extended (Manuel eklenmiş, sorun yok)
    implementation("androidx.compose.material:material-icons-extended:1.7.5")

    // Lifecycle / MVVM
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)

    // Coroutines
    implementation(libs.coroutines.android)

    // DI (Koin)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Image loading
    implementation(libs.coil.compose)

    // Logging
    implementation(libs.timber)
    implementation(libs.napier)

    debugImplementation(libs.compose.ui.tooling)
}