package com.sarftec.worldproverbs.model

import com.appodeal.ads.native_ad.views.NativeAdViewContentStream

sealed class Item(
    val id: Int
) {
   class Quote(
       id: Int,
       val message: String,
       val author: String,
       var favorite: Boolean = false
   ) : Item(id)

    class Separator(
        id: Int,
        val nativeAd: NativeAdViewContentStream
    ) : Item(id)
}