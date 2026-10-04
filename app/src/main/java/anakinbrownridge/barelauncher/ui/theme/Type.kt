package com.barelauncher

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

class HomeSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BareLauncherTheme {
                HomeSettingsScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}

enum class SettingsPage(val label: String) {
    AdvancedAppearance("Advanced Appearance"),
    Appearance("Appearance"),
    About("About"),
    Licenses("Licenses"),
    PhoneInfo("Phone information")
}

@Composable
fun HomeSettingsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val pages = SettingsPage.entries

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Text(
                text = "Home settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            TabRow(selectedTabIndex = selectedTab) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(page.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (pages[selectedTab]) {
                SettingsPage.AdvancedAppearance -> AdvancedAppearancePage()
                SettingsPage.Appearance -> AppearancePage()
                SettingsPage.About -> AboutPage()
                SettingsPage.Licenses -> LicensesPage()
                SettingsPage.PhoneInfo -> PhoneInfoPage()
            }
        }
    }
}

@Composable
private fun AdvancedAppearancePage() {
    SettingsSection(title = "Advanced appearance") {
        ToggleRow(label = "Use blur and shadows", checked = true)
        ToggleRow(label = "Adaptive colors", checked = true)
        ToggleRow(label = "Legacy app icons fallback", checked = true)
    }
}

@Composable
private fun AppearancePage() {
    SettingsSection(title = "Appearance") {
        ToggleRow(label = "Dark mode", checked = true)
        ToggleRow(label = "Show widgets", checked = true)
        ToggleRow(label = "Floating drawer", checked = true)
    }
}

@Composable
private fun AboutPage() {
    SettingsSection(title = "About") {
        Text("BareLauncher")
        Text("Version: 1.0.0")
        Text("Low-resource Material You launcher")
        Text("Designed for a simple home experience.")
    }
}

@Composable
private fun LicensesPage() {
    SettingsSection(title = "Licenses") {
        Text("Material3 — Apache 2.0")
        Text("Jetpack Compose — Apache 2.0")
        Text("Chris Banes / Haze — Apache 2.0")
    }
}

@Composable
private fun PhoneInfoPage() {
    SettingsSection(title = "Phone information") {
        Text("Model: Pixel 9 Pro")
        Text("Android: 15")
        Text("Battery: 94%")
        Text("Storage: 128 GB")
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(14.dp))
            Divider()
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = null)
    }
}
