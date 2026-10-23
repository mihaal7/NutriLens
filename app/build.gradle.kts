plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") // Плагин для Room
}

android {
    namespace = "com.example.nutrilens"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.nutrilens"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4" // Подходит для Kotlin 1.9.20
    }
}

dependencies {
    // Основа Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Фоновые задачи (для трекера воды)
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Общее хранилище данных (SharedViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // ИИ Gemini от Google
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // UI (Jetpack Compose)
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Навигация между экранами
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // База данных Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    add("ksp", "androidx.room:room-compiler:$room_version")

    // Тесты
    testImplementation("junit:junit:4.13.2")
}