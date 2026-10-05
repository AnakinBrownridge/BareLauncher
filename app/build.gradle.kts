plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // Keeps compilation light and fully compatible with Java 17 and Gradle 8.7
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anakinbrownridge.barelauncher"
        minSdk = 29     // Allows it to run on Android 10 and newer
        targetSdk = 31  // Native target for her phone's Android 12 environment

        versionCode = 4
        versionName = "26.10-alpha1"

        // Your secret Saskatoon neighborhood easter egg stays safely tucked here!
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
    // Highly-optimized, lightweight Haze version that won't stutter on her 2020 hardware
    implementation("dev.chrisbanes.haze:haze:0.6.0")

    // Lightweight core foundations to keep her available storage high
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Core Compose components that keep RAM and battery usage exceptionally low
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.ui:ui-graphics:1.6.3")
    implementation("androidx.compose.material3:material3:1.2.1")
}
