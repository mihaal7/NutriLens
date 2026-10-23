buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    // Подключаем KSP (версия должна совпадать с Kotlin)
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}