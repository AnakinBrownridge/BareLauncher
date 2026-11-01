package com.anakinbrownridge.barelauncher

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

enum class SettingsPage(@StringRes val labelRes: Int) {
    AdvancedAppearance(R.string.advanced_appearance),
    Appearance(R.string.appearance),
    About(R.string.about),
    Licenses(R.string.licenses),
    PhoneInfo(R.string.phone_info)
}

private val accentPresets = listOf(
    0xFF006061, // teal (default palette)
    0xFF1E88E5, // blue
    0xFF8E24AA, // purple
    0xFFD81B60, // pink
    0xFFE53935, // red
    0xFFFB8C00, // orange
    0xFF43A047, // green
    0xFF6D4C41  // brown
).map { Color(it) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: LauncherSettings,
    onSettingsChange: (LauncherSettings) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val pages = SettingsPage.entries
    var selectedIndex by remember { mutableIntStateOf(0) }

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
            TopAppBarWithSave(
                title = stringResource(R.string.home_settings_title),
                onBack = onBack,
                onSave = {
                    Toast.makeText(context, R.string.settings_autosaved, Toast.LENGTH_SHORT).show()
                }
            )

            ScrollableTabRow(selectedTabIndex = selectedIndex, edgePadding = 8.dp) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        text = { Text(stringResource(page.labelRes)) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                when (pages[selectedIndex]) {
                    SettingsPage.AdvancedAppearance -> AdvancedAppearanceSettings(settings, onSettingsChange)
                    SettingsPage.Appearance -> AppearanceSettings(settings, onSettingsChange)
                    SettingsPage.About -> AboutSettings()
                    SettingsPage.Licenses -> LicensesSettings()
                    SettingsPage.PhoneInfo -> PhoneInfoSettings()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarWithSave(title: String, onBack: (() -> Unit)?, onSave: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            IconButton(onClick = onSave) {
                Icon(imageVector = Icons.Filled.Save, contentDescription = stringResource(R.string.save))
            }
        }
    )
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    description: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            if (description != null) {
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
        }
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun InfoRow(label: String, value: String? = null) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AdvancedAppearanceSettings(
    settings: LauncherSettings,
    onChange: (LauncherSettings) -> Unit
) {
    SettingsCard(title = stringResource(R.string.advanced_appearance)) {
        ToggleRow(
            label = "Use blur and shadows",
            description = "Frosted-glass app drawer",
            isChecked = settings.blur
        ) { onChange(settings.copy(blur = it)) }

        ToggleRow(
            label = "Adaptive colors",
            description = "Match system colors (Android 12+)",
            isChecked = settings.adaptiveColors
        ) { onChange(settings.copy(adaptiveColors = it)) }

        ToggleRow(
            label = "Legacy app icons fallback",
            description = "Show app icons as circles",
            isChecked = settings.legacyIcons
        ) { onChange(settings.copy(legacyIcons = it)) }
    }
}

@Composable
private fun AppearanceSettings(
    settings: LauncherSettings,
    onChange: (LauncherSettings) -> Unit
) {
    SettingsCard(title = stringResource(R.string.appearance)) {
        ToggleRow(label = "Dark mode", isChecked = settings.darkMode) {
            onChange(settings.copy(darkMode = it))
        }
        ToggleRow(label = "Show widgets", description = "Date and welcome card on the home screen", isChecked = settings.showWidgets) {
            onChange(settings.copy(showWidgets = it))
        }
        ToggleRow(label = "Floating drawer", description = "Off = full-screen app drawer", isChecked = settings.floatingDrawer) {
            onChange(settings.copy(floatingDrawer = it))
        }
    }

    SettingsCard(title = stringResource(R.string.accent_colour)) {
        AccentPicker(selected = settings.accent) { onChange(settings.copy(accent = it)) }
    }

    AppPickerSection(circleIcons = settings.legacyIcons)
}

@Composable
private fun AccentPicker(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // "Auto" = no custom accent
        AccentSwatch(color = null, selected = selected == 0) { onSelect(0) }
        accentPresets.forEach { color ->
            AccentSwatch(color = color, selected = selected == color.toArgb()) { onSelect(color.toArgb()) }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(stringResource(R.string.accent_hint), style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun AccentSwatch(color: Color?, selected: Boolean, onClick: () -> Unit) {
    val fill = color ?: MaterialTheme.colorScheme.surfaceVariant
    val tick = if (fill.luminance() > 0.5f) Color.Black else Color.White
    val borderColor = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(fill)
            .border(if (selected) 3.dp else 1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = tick)
        } else if (color == null) {
            Text("A", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun AboutSettings() {
    SettingsCard(title = stringResource(R.string.about)) {
        InfoRow("BareLauncher")
        InfoRow("Version", BuildConfig.VERSION_NAME)
        InfoRow("Theme", "Low-resource Material You")
        InfoRow("Note", "Designed for simple daily use.")
    }
}

@Composable
private fun LicensesSettings() {
    SettingsCard(title = stringResource(R.string.licenses)) {
        InfoRow("Material3", "Apache 2.0")
        InfoRow("Jetpack Compose", "Apache 2.0")
        InfoRow("Haze", "Apache 2.0")
    }
}

@Composable
private fun PhoneInfoSettings() {
    SettingsCard(title = stringResource(R.string.phone_info)) {
        InfoRow("Device", "${Build.MANUFACTURER} ${Build.MODEL}")
        InfoRow("Android", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        InfoRow("Build", Build.DISPLAY)
        InfoRow("Security patch", Build.VERSION.SECURITY_PATCH)
        InfoRow("Launcher", "${BuildConfig.VERSION_NAME} (${BuildConfig.CODENAME})")
    }
}

@Composable
private fun AppPickerSection(circleIcons: Boolean) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("bare_prefs", Context.MODE_PRIVATE)

    var installedApps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val existing = prefs.getString("pinned_apps", "")
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
        selected = existing.toSet()

        installedApps = loadLaunchableApps(context)
        loading = false
    }

    SettingsCard(title = stringResource(R.string.manage_pinned_apps)) {
        if (loading) {
            Text(stringResource(R.string.loading_apps), style = MaterialTheme.typography.bodyLarge)
        } else {
            Column {
                Text(stringResource(R.string.tap_toggle_pin), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))

                // Fixed height: a LazyColumn inside a scrolling page needs bounded size
                LazyColumn(modifier = Modifier.height(360.dp)) {
                    items(installedApps, key = { it.packageName }) { app ->
                        val isSelected = selected.contains(app.packageName)
                        val toggle = { checked: Boolean ->
                            selected = if (checked) selected + app.packageName else selected - app.packageName
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { toggle(!isSelected) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppIcon(app = app, circle = circleIcons, size = 40.dp)
                                Spacer(modifier = Modifier.padding(8.dp))
                                Text(app.label, style = MaterialTheme.typography.bodyLarge)
                            }
                            Checkbox(checked = isSelected, onCheckedChange = toggle)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        prefs.edit().putString("pinned_apps", selected.joinToString(",")).apply()
                        Toast.makeText(context, R.string.pins_saved, Toast.LENGTH_SHORT).show()
                    }) {
                        Text(stringResource(R.string.save_pinned_apps))
                    }
                }
            }
        }
    }
}
