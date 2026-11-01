package com.anakinbrownridge.barelauncher

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppEntry(val packageName: String, val label: String, val icon: ImageBitmap?)

private const val ICON_PX = 128

private fun iconOf(block: () -> Drawable?): ImageBitmap? =
    try {
        block()?.toBitmap(ICON_PX, ICON_PX)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }

/** Every app that has a launcher entry, sorted by name. Runs on a background thread. */
suspend fun loadLaunchableApps(context: Context): List<AppEntry> = withContext(Dispatchers.IO) {
    val pm = context.packageManager
    try {
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        pm.queryIntentActivities(intent, 0)
            .map { ri ->
                val info = ri.activityInfo
                AppEntry(info.packageName, info.loadLabel(pm).toString(), iconOf { info.loadIcon(pm) })
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    } catch (e: Exception) {
        emptyList()
    }
}

/** Looks up specific packages (used for pinned apps). Uninstalled ones are skipped. */
suspend fun loadAppsByPackage(context: Context, packages: List<String>): List<AppEntry> =
    withContext(Dispatchers.IO) {
        val pm = context.packageManager
        packages.mapNotNull { pkg ->
            try {
                val info = pm.getApplicationInfo(pkg, 0)
                AppEntry(pkg, pm.getApplicationLabel(info).toString(), iconOf { pm.getApplicationIcon(pkg) })
            } catch (e: Exception) {
                null
            }
        }
    }

fun launchApp(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "App not installed: $packageName", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun AppIcon(app: AppEntry, circle: Boolean, size: Dp) {
    val bmp = app.icon
    if (bmp != null) {
        val base = Modifier.size(size)
        Image(
            bitmap = bmp,
            contentDescription = app.label,
            modifier = if (circle) base.clip(CircleShape) else base,
            contentScale = if (circle) ContentScale.Crop else ContentScale.Fit
        )
    } else {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
            modifier = Modifier.size(size)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.Apps, contentDescription = null)
            }
        }
    }
}
