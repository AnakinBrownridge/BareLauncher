package com.anakinbrownridge.barelauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anakinbrownridge.barelauncher.ui.theme.BareLauncherTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BareLauncherTheme {
                BareLauncherHomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BareLauncherHomeScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("bare_prefs", Context.MODE_PRIVATE)
    var pinnedPackages by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        pinnedPackages = prefs.getString("pinned_apps", "")
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        GlanceNow()

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.32f)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.welcome_title), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.welcome_subtitle))
                }
            }

            PinnedAppsGrid(pinnedPackages = pinnedPackages, onLaunch = { pkg ->
                launchApp(context, pkg)
            })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                val intent = Intent(context, HomeSettingsActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(imageVector = Icons.Filled.Wallpaper, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.settings))
            }

            FloatingActionButton(onClick = { /* open app drawer - placeholder */ }) {
                Icon(imageVector = Icons.Filled.Apps, contentDescription = null)
            }
        }
    }
}

@Composable
private fun GlanceNow() {
    val today = LocalDate.now()
    val weekday = today.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
    val dateString = today.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.glance_now), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "$weekday, $dateString", style = MaterialTheme.typography.bodyLarge, fontSize = 18.sp)
    }
}

@Composable
private fun PinnedAppsGrid(pinnedPackages: List<String>, onLaunch: (String) -> Unit) {
    val context = LocalContext.current
    val pm = context.packageManager

    if (pinnedPackages.isEmpty()) {
        Text(stringResource(R.string.no_pinned_apps), style = MaterialTheme.typography.bodyMedium)
        return
    }

    val gridState = rememberLazyGridState()

    androidx.compose.foundation.lazy.grid.LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = gridState,
        modifier = Modifier.height(92.dp)
    ) {
        items(pinnedPackages) { pkg ->
            val appInfo = try { pm.getApplicationInfo(pkg, 0) } catch (e: Exception) { null }
            val label = appInfo?.let { pm.getApplicationLabel(it).toString() } ?: pkg.substringAfterLast('.')
            val iconDrawable = try { pm.getApplicationIcon(pkg) } catch (e: Exception) { null }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onLaunch(pkg) }
            ) {
                val bmp = iconDrawable?.let { drawableToBitmap(it) }
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = label,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Filled.Apps, contentDescription = null)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

private fun launchApp(context: Context, packageName: String) {
    val pm = context.packageManager
    val intent = pm.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "App not installed: $packageName", Toast.LENGTH_SHORT).show()
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
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
}
