pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral() // <-- BU SATIR ÇOK ÖNEMLİ
        // Eğer varsa "maven { url = uri("https://..." ) }" satırları kalabilir
    }
}

rootProject.name = "SignAI"
include(":androidApp")
include(":shared")