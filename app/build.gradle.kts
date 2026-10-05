plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // 🌟 ADD THIS EXACT LINE AT THE TOP OF THE ANDROID BLOCK:
    namespace = "com.anakinbrownridge.barelauncher"

    compileSdk = 34

    defaultConfig {
        applicationId = "com.anakinbrownridge.barelauncher"
        minSdk = 29     
        targetSdk = 31  // Native target for her phone's Android 12 environment

        versionCode = 4
        versionName = "26.10-alpha1"

        // Saskatoon neighborhood easter egg safely tucked away!
        buildConfigField("String", "CODENAME", "\"CronkiteAlpha1\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" 
    }
}

dependencies {
    // Optimized, lightweight assets for her 2020 hardware layout
    implementation("dev.chrisbanes.haze:haze:0.6.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.ui:ui-graphics:1.6.3")
    implementation("androidx.compose.material3:material3:1.2.1")
}
