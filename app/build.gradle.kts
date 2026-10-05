android {
    // Compile using stable API 34 tools
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anakinbrownridge.barelauncher"
        minSdk = 29     // Supports Android 10 and newer
        targetSdk = 31  // Matches her phone's Android 12 OS natively

        versionCode = 4
        versionName = "26.10-alpha1"

        // Your secret neighborhood easter egg remains intact!
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
        // Aligns with Kotlin 1.9.24 for stable compilation
        kotlinCompilerExtensionVersion = "1.5.14" 
    }
}

dependencies {
    // 🌟 Lightweight Haze version that compiles on API 34 without performance lag
    implementation("dev.chrisbanes.haze:haze:0.6.0")

    // Core Android libraries optimized to keep the APK file size incredibly tiny
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Stable Jetpack Compose libraries that won't lag her phone's RAM
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.ui:ui-graphics:1.6.3")
    implementation("androidx.compose.material3:material3:1.2.1")
}
