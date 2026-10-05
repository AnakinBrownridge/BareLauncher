package com.anakinbrownridge.barelauncher

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class SettingsPage(@StringRes val labelRes: Int) {
    AdvancedAppearance(R.string.advanced_appearance),
    Appearance(R.string.appearance),
    About(R.string.about),
    Licenses(R.string.licenses),
    PhoneInfo(R.string.phone_info)
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
            TopAppBarWithSave(
                title = stringResource(R.string.home_settings_title),
                onBack = onBack,
                onSave = {
                    // Save handled by inner pages where relevant (picker saves immediately), keep for future use
                }
            )

            TabRow(selectedTabIndex = selectedIndex) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        text = { Text(stringResource(page.labelRes)) }
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
private fun ToggleRow(label: String, isChecked: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Checkbox(checked = isChecked, onCheckedChange = {})
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
private fun AdvancedAppearanceSettings() {
    SettingsCard(title = stringResource(R.string.advanced_appearance)) {
        ToggleRow("Use blur and shadows", true)
        ToggleRow("Adaptive colors", true)
        ToggleRow("Legacy app icons fallback", true)
    }
}

@Composable
private fun AppearanceSettings() {
    SettingsCard(title = stringResource(R.string.appearance)) {
        ToggleRow("Dark mode", true)
        ToggleRow("Show widgets", true)
        ToggleRow("Floating drawer", true)

        Spacer(modifier = Modifier.height(12.dp))
        AppPickerSection()
    }
}

@Composable
private fun AboutSettings() {
    SettingsCard(title = stringResource(R.string.about)) {
        InfoRow("BareLauncher")
        InfoRow("Version", "1.0.0")
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
    val context = LocalContext.current
    var sysverText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        sysverText = readSysverFromResources(context)
    }

    SettingsCard(title = stringResource(R.string.phone_info)) {
        if (sysverText.isNullOrBlank()) {
            InfoRow("System info", stringResource(R.string.system_info_unavailable))
        } else {
            val lines = sysverText!!.trim().lines()
            lines.forEach { line -> InfoRow(line) }
        }
    }
}

@Composable
private fun AppPickerSection() {
    val context = LocalContext.current
    val pm = context.packageManager
    val prefs = context.getSharedPreferences("bare_prefs", Context.MODE_PRIVATE)

    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val existing = prefs.getString("pinned_apps", "")
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
        selected = existing.toSet()

        installedApps = loadLaunchableApps(pm)
        loading = false
    }

    SettingsCard(title = stringResource(R.string.manage_pinned_apps)) {
        if (loading) {
            Text(stringResource(R.string.loading_apps), style = MaterialTheme.typography.bodyLarge)
        } else {
            Column {
                Text(stringResource(R.string.tap_toggle_pin), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(installedApps) { app ->
                        val isSelected = selected.contains(app.packageName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val bmp = app.icon?.let { drawableToBitmap(it) }
                                if (bmp != null) {
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = app.label,
                                        modifier = Modifier.size(40.dp)
                                    )
                                } else {
                                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)) {
                                        Text(" ", modifier = Modifier.size(40.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.padding(8.dp))
                                Text(app.label, style = MaterialTheme.typography.bodyLarge)
                            }

                            Checkbox(checked = isSelected, onCheckedChange = { checked ->
                                selected = if (checked) selected + app.packageName else selected - app.packageName
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        val csv = selected.joinToString(",")
                        prefs.edit().putString("pinned_apps", csv).apply()
                    }) {
                        Text(stringResource(R.string.save_pinned_apps))
                    }
                }
            }
        }
    }
}

private data class AppInfo(val packageName: String, val label: String, val icon: Drawable?)

private suspend fun loadLaunchableApps(pm: PackageManager): List<AppInfo> {
    return withContext(Dispatchers.IO) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply { addCategory(android.content.Intent.CATEGORY_LAUNCHER) }
            val list = pm.queryIntentActivities(intent, 0)
            val apps = list.map { ri ->
                val info = ri.activityInfo
                AppInfo(info.packageName, info.loadLabel(pm).toString(), info.loadIcon(pm))
            }.sortedBy { it.label.lowercase() }
            apps
        } catch (e: Exception) {
            emptyList()
        }
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap? {
    try {
        if (drawable is BitmapDrawable) {
            drawable.bitmap?.let { return it }
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 72
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 72

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    } catch (e: Exception) {
        return null
    }
}

private suspend fun readSysverFromResources(context: Context): String? {
    return withContext(Dispatchers.IO) {
        try {
            val rawId = context.resources.getIdentifier("sysver", "raw", context.packageName)
            if (rawId != 0) {
                context.resources.openRawResource(rawId).bufferedReader().use { it.readText() }
            } else {
                try {
                    context.assets.open("sysver.sh").bufferedReader().use { it.readText() }
                } catch (e: Exception) { null }
            }
        } catch (e: Exception) { null }
    }
}
