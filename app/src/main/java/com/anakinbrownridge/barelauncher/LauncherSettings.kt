package com.anakinbrownridge.barelauncher

import android.content.Context
import androidx.compose.ui.graphics.Color

data class LauncherSettings(
    val blur: Boolean = true,
    val adaptiveColors: Boolean = true,
    val legacyIcons: Boolean = true,
    val darkMode: Boolean = true,
    val showWidgets: Boolean = true,
    val floatingDrawer: Boolean = true,
    /** ARGB accent colour, or 0 for "Auto" (system / default colours). */
    val accent: Int = 0
) {
    fun accentOrNull(): Color? = if (accent != 0) Color(accent) else null

    companion object {
        private const val PREFS = "bare_prefs"

        fun load(context: Context): LauncherSettings {
            val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val d = LauncherSettings()
            return LauncherSettings(
                blur = p.getBoolean("blur", d.blur),
                adaptiveColors = p.getBoolean("adaptive_colors", d.adaptiveColors),
                legacyIcons = p.getBoolean("legacy_icons", d.legacyIcons),
                darkMode = p.getBoolean("dark_mode", d.darkMode),
                showWidgets = p.getBoolean("show_widgets", d.showWidgets),
                floatingDrawer = p.getBoolean("floating_drawer", d.floatingDrawer),
                accent = p.getInt("accent", d.accent)
            )
        }

        fun save(context: Context, s: LauncherSettings) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putBoolean("blur", s.blur)
                .putBoolean("adaptive_colors", s.adaptiveColors)
                .putBoolean("legacy_icons", s.legacyIcons)
                .putBoolean("dark_mode", s.darkMode)
                .putBoolean("show_widgets", s.showWidgets)
                .putBoolean("floating_drawer", s.floatingDrawer)
                .putInt("accent", s.accent)
                .apply()
        }
    }
}
