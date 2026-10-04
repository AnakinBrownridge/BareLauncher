package com.barelauncher

import android.app.WallpaperManager
import android.content.Context
import android.net.Uri

object WallpaperController {
    fun setWallpaper(context: Context, uri: Uri) {
        val wallpaperManager = WallpaperManager.getInstance(context)
        context.contentResolver.openInputStream(uri)?.use { stream ->
            wallpaperManager.setStream(stream)
        }
    }
}
