package com.sarftec.worldproverbs.image

import android.net.Uri

fun String.toAssetUri(folder: String) : Uri {
    return Uri.parse("file:///android_asset/$folder/$this")
}