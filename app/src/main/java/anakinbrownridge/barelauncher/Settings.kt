package com.barelauncher

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class SettingsPage(val label: String) {
    AdvancedAppearance("Advanced Appearance"),
    Appearance("Appearance"),
    About("About"),
    Licenses("Licenses"),
    PhoneInfo("Phone information")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: (() -> Unit)? = null
) {
    val pages = SettingsPage.entries
    var selectedIndex by remember { mutableStateOf(0) }

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
        ) {
            CenterAlignedTopAppBar(
                title = { Text("Home settings") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )

            TabRow(selectedTabIndex = selectedIndex) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        text = { Text(page.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (pages[selectedIndex]) {
                SettingsPage.AdvancedAppearance -> AdvancedAppearanceSettings()
                SettingsPage.Appearance -> AppearanceSettings()
                SettingsPage.About -> AboutSettings()
                SettingsPage.Licenses -> LicensesSettings()
                SettingsPage.PhoneInfo -> PhoneInfoSettings()
            }
        }
    }
}

@Composable
private fun AdvancedAppearanceSettings() {
    SettingsCard(title = "Advanced appearance") {
        ToggleRow("Use blur and shadows", true)
        ToggleRow("Adaptive colors", true)
        ToggleRow("Legacy app icons fallback", true)
    }
}

@Composable
private fun AppearanceSettings() {
    SettingsCard(title = "Appearance") {
        ToggleRow("Dark mode", true)
        ToggleRow("Show widgets", true)
        ToggleRow("Floating drawer", true)
    }
}

@Composable
private fun AboutSettings() {
    SettingsCard(title = "About") {
        InfoRow("BareLauncher")
        InfoRow("Version", "1.0.0")
        InfoRow("Theme", "Low-resource Material You")
        InfoRow("Note", "Designed for simple daily use.")
    }
}

@Composable
private fun LicensesSettings() {
    SettingsCard(title = "Licenses") {
        InfoRow("Material3", "Apache 2.0")
        InfoRow("Jetpack Compose", "Apache 2.0")
        InfoRow("Haze", "Apache 2.0")
    }
}

@Composable
private fun PhoneInfoSettings() {
    // Read /res/raw/sysver.sh at runtime and display its contents.
    // The sysver.sh file should be placed at: app/src/main/res/raw/sysver.sh
    val context = LocalContext.current
    var sysverText by remember { mutableStateOf<String?>(null) }

    // Load the file once when this composable enters composition.
    LaunchedEffect(Unit) {
        sysverText = readSysverFromResources(context)
    }

    SettingsCard(title = "Phone information") {
        if (sysverText.isNullOrBlank()) {
            InfoRow("System info", "Unavailable")
        } else {
            // Show entire file as preformatted-ish lines
            val lines = sysverText!!.trim().lines()
            lines.forEach { line ->
                InfoRow(line)
            }
        }
    }
}

/**
 * Attempts to read res/raw/sysver.sh (resource name: sysver, type: raw).
 * Falls back to trying an asset named "sysver.sh" if the raw resource is not present.
 */
private suspend fun readSysverFromResources(context: Context): String? {
    return withContext(Dispatchers.IO) {
        try {
            // Try res/raw/sysver.sh first
            val rawId = context.resources.getIdentifier("sysver", "raw", context.packageName)
            if (rawId != 0) {
                context.resources.openRawResource(rawId).bufferedReader().use { it.readText() }
            } else {
                // Fallback to assets/sysver.sh if present
                try {
                    context.assets.open("sysver.sh").bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(14.dp))
            Divider()
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}

@Composable
private fun InfoRow(
    title: String,
    value: String? = null
) {
    if (value == null) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
