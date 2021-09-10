package com.sarftec.worldproverbs

import android.content.Context
import com.sarftec.worldproverbs.image.ImageLoader
import com.sarftec.worldproverbs.image.ImageStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Dependency(
    val context: Context,
    val coroutineScope: CoroutineScope,
    val imageLoader: ImageLoader,
    val imageStore: ImageStore
) {
    fun vibrate() {
        coroutineScope.launch {
            if(context.readSettings(SHOULD_VIBRATE, true).first()) context.vibrate()
        }
    }
}