package com.anakinbrownridge.barelauncher

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anakinbrownridge.barelauncher.ui.theme.BareLauncherTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    // Bumped on every resume so pins / settings reload after returning from Settings
    private var refreshTick by mutableIntStateOf(0)
    private var settings by mutableStateOf(LauncherSettings())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = LauncherSettings.load(this)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            BareLauncherTheme(
                darkTheme = settings.darkMode,
                dynamicColor = settings.adaptiveColors,
                accent = settings.accentOrNull()
            ) {
                BareLauncherHomeScreen(settings = settings, refreshKey = refreshTick)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        settings = LauncherSettings.load(this)
        refreshTick++
    }
}

@Composable
fun BareLauncherHomeScreen(settings: LauncherSettings, refreshKey: Int = 0) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("bare_prefs", Context.MODE_PRIVATE)
    val hazeState = remember { HazeState() }

    var pinnedApps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var allApps by remember { mutableStateOf<List<AppEntry>?>(null) }
    var drawerOpen by remember { mutableStateOf(false) }

    LaunchedEffect(refreshKey) {
        val packages = prefs.getString("pinned_apps", "")
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
        pinnedApps = loadAppsByPackage(context, packages)
    }

    // Load the full app list only when the drawer is actually opened
    LaunchedEffect(drawerOpen, refreshKey) {
        if (drawerOpen) allApps = loadLaunchableApps(context)
    }

    BackHandler(enabled = drawerOpen) { drawerOpen = false }

    Box(modifier = Modifier.fillMaxSize()) {
        // Locked wallpaper: always the bundled snowyforest.jpg. This is the Haze "source".
        WallpaperBackground(modifier = Modifier.fillMaxSize().haze(hazeState))

        CompositionLocalProvider(LocalContentColor provides Color.White) {
            HomeContent(
                settings = settings,
                pinnedApps = pinnedApps,
                onOpenDrawer = { drawerOpen = true }
            )

            if (drawerOpen) {
                AppDrawer(
                    apps = allApps,
                    settings = settings,
                    hazeState = hazeState,
                    onLaunch = { pkg ->
                        launchApp(context, pkg)
                        drawerOpen = false
                    },
                    onClose = { drawerOpen = false }
                )
            }
        }
    }
}

@Composable
private fun WallpaperBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val wallpaper by produceState<ImageBitmap?>(initialValue = null) {
        value = withContext(Dispatchers.IO) { decodeWallpaper(context) }
    }

    Box(modifier = modifier.background(Color(0xFF0B1A1F))) {
        wallpaper?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        // Dark scrim so white text stays readable over bright snow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.50f)
                        )
                    )
                )
        )
    }
}

/**
 * Decodes res/drawable/snowyforest.jpg at reduced size. The source is 4267x2400, which would
 * take ~40 MB uncompressed, so we sub-sample it. inScaled = false stops Android from also
 * multiplying it by the screen density (which would be far bigger still).
 */
private fun decodeWallpaper(context: Context): ImageBitmap? = try {
    val res = context.resources
    val bounds = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        inScaled = false
    }
    BitmapFactory.decodeResource(res, R.drawable.snowyforest, bounds)

    val screenH = res.displayMetrics.heightPixels
    var sample = 1
    while (bounds.outHeight / (sample * 2) >= screenH / 2) sample *= 2

    val opts = BitmapFactory.Options().apply {
        inSampleSize = sample
        inScaled = false
    }
    BitmapFactory.decodeResource(res, R.drawable.snowyforest, opts)?.asImageBitmap()
} catch (e: Throwable) {
    null
}

@Composable
private fun HomeContent(
    settings: LauncherSettings,
    pinnedApps: List<AppEntry>,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (settings.showWidgets) GlanceNow() else Spacer(modifier = Modifier.height(1.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (settings.showWidgets) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.35f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.welcome_title), style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.welcome_subtitle))
                    }
                }
            }

            PinnedAppsGrid(
                pinnedApps = pinnedApps,
                circleIcons = settings.legacyIcons,
                onLaunch = { pkg -> launchApp(context, pkg) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                context.startActivity(Intent(context, HomeSettingsActivity::class.java))
            }) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.settings))
            }

            FloatingActionButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Apps, contentDescription = stringResource(R.string.all_apps))
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
private fun PinnedAppsGrid(
    pinnedApps: List<AppEntry>,
    circleIcons: Boolean,
    onLaunch: (String) -> Unit
) {
    if (pinnedApps.isEmpty()) {
        Text(stringResource(R.string.no_pinned_apps), style = MaterialTheme.typography.bodyMedium)
        return
    }

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        modifier = Modifier.height(108.dp)
    ) {
        items(pinnedApps, key = { it.packageName }) { app ->
            AppIconCell(app = app, circleIcons = circleIcons, iconSize = 56, onClick = { onLaunch(app.packageName) })
        }
    }
}

@Composable
private fun AppIconCell(app: AppEntry, circleIcons: Boolean, iconSize: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AppIcon(app = app, circle = circleIcons, size = iconSize.dp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AppDrawer(
    apps: List<AppEntry>?,
    settings: LauncherSettings,
    hazeState: HazeState,
    onLaunch: (String) -> Unit,
    onClose: () -> Unit
) {
    val floating = settings.floatingDrawer
    val shape = if (floating) RoundedCornerShape(28.dp) else RectangleShape
    val noRipple = remember { MutableInteractionSource() }

    // Tapping the dimmed area outside a floating drawer closes it
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = noRipple, indication = null, onClick = onClose)
    ) {
        val placement = if (floating) {
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
        } else {
            Modifier.fillMaxSize()
        }

        val blurred = if (settings.blur) {
            placement.hazeChild(hazeState, shape)   // frosted glass over the wallpaper
        } else {
            placement.clip(shape)
        }

        Column(
            modifier = blurred
                .background(Color.Black.copy(alpha = if (settings.blur) 0.35f else 0.85f), shape)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .then(if (floating) Modifier else Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.all_apps), style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (apps == null) {
                Text(stringResource(R.string.loading_apps), style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 76.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(apps, key = { it.packageName }) { app ->
                        AppIconCell(
                            app = app,
                            circleIcons = settings.legacyIcons,
                            iconSize = 52,
                            onClick = { onLaunch(app.packageName) }
                        )
                    }
                }
            }
        }
    }
}
