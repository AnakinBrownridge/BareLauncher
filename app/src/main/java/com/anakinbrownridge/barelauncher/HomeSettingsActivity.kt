package com.anakinbrownridge.barelauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.anakinbrownridge.barelauncher.ui.theme.BareLauncherTheme

class HomeSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BareLauncherTheme {
                SettingsScreen(onBack = { finish() })
            }
        }
    }
}
