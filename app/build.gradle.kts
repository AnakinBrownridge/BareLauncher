plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.anakinbrownridge.barelauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anakinbrownridge.barelauncher"
        minSdk = 29     
        targetSdk = 31  // Native target for her phone's Android 12 environment

        versionCode = 4
        versionName = "26.10-alpha2-261101"

        // Your secret neighborhood easter egg is safe here!
        buildConfigField("String", "CODENAME", "\"CronkiteAlpha2\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    } // <-- Closes defaultConfig

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" 
    }
} // <-- This exact bracket MUST be here to close the android block completely!

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // XML theme parent (Theme.Material3.DayNight) comes from this artifact
    implementation("com.google.android.material:material:1.12.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Frosted-glass blur for the app drawer
    implementation("dev.chrisbanes.haze:haze:0.7.3")
}
