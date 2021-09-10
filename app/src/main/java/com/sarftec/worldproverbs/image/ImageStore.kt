package com.sarftec.worldproverbs.image

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val imageContainer by lazy {
        ImageContainer(context, "background")
    }

    val placeholderContainer by lazy {
        ImageContainer(context, "placeholder")
    }

    fun getTodayImage() : Uri {
        val folderName = "today"
        return context
            .assets
            .list(folderName)!!
            .toHashSet()
            .random()
            .toAssetUri(folderName)
    }

    fun launcherImagePath() : String {
        return "launcher/launcher.jpg"
    }

    fun launcherImageUri() : Uri {
        return "launcher.jpg".toAssetUri("launcher")
    }

    class ImageContainer(context: Context, private val folder: String) {
        private val imageList = context
            .assets
            .list(folder)!!
            .toHashSet()

        fun randomUri(): Uri {
            return imageList.random().toAssetUri(folder)
        }

        fun get(name: String) : Uri? {
            return imageList.firstOrNull {
                it.lowercase(Locale.ENGLISH).trim().startsWith(
                    name.lowercase(Locale.ENGLISH).trim()
                )
            }?.toAssetUri(folder)
        }

        fun images() : List<Uri> {
            return imageList.map {
                it.toAssetUri(folder)
            }.toList()
        }
    }
}