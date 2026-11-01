package com.anakinbrownridge.barelauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.anakinbrownridge.barelauncher.ui.theme.BareLauncherTheme

class HomeSettingsActivity : ComponentActivity() {
    private var settings by mutableStateOf(LauncherSettings())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = LauncherSettings.load(this)
        setContent {
            // The theme reads live settings, so dark mode / accent changes apply instantly here
            BareLauncherTheme(
                darkTheme = settings.darkMode,
                dynamicColor = settings.adaptiveColors,
                accent = settings.accentOrNull()
            ) {
                SettingsScreen(
                    settings = settings,
                    onSettingsChange = { updated ->
                        settings = updated
                        LauncherSettings.save(this@HomeSettingsActivity, updated)
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}
