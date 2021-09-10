package com.sarftec.worldproverbs.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageLoader @Inject constructor(
    @ApplicationContext private val context: Context
    ) {

    val coilImageLoader = ImageLoader(context)
    private val imageLoadingScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private suspend fun buildAndLoad(uri: Uri): Bitmap {
        return coilImageLoader.execute(
            ImageRequest.Builder(context)
                .data(uri)
                .allowHardware(false)
                .build()
        ).drawable.let { (it as BitmapDrawable).bitmap }
    }

    fun loadImage(uri: Uri) : StateFlow<Bitmap?> {
        val imageFlow = MutableStateFlow<Bitmap?>(null)
        imageLoadingScope.launch {
            imageFlow.value = buildAndLoad(uri)
        }
        return imageFlow
    }

    fun destroy() {
        imageLoadingScope.coroutineContext.cancelChildren()
    }
}