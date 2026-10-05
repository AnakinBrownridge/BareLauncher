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

    /**
     * Set the app's default wallpaper to the bundled image named "snowyforest".
     * The method tries multiple resource types (drawable, raw, mipmap, wallpaper) so it
     * will work even if the file is placed in a non-standard resource directory.
     */
    fun setDefaultWallpaper(context: Context) {
        val wallpaperManager = WallpaperManager.getInstance(context)
        val resName = "snowyforest"
        val resTypes = listOf("drawable", "raw", "mipmap", "wallpaper")

        for (type in resTypes) {
            val resId = context.resources.getIdentifier(resName, type, context.packageName)
            if (resId != 0) {
                try {
                    context.resources.openRawResource(resId).use { stream ->
                        wallpaperManager.setStream(stream)
                    }
                    return
                } catch (e: Exception) {
                    // Try the next resource type if this one fails
                }
            }
        }
    }
}
