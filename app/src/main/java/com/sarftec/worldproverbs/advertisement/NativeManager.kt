package com.sarftec.worldproverbs.advertisement

import android.app.Activity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream

class NativeManager (
    private val activity: Activity,
    private val networkManager: NetworkManager
) {

    private val container = Container()

    private val adCounter = listOf(
        AdCountManager(listOf(2, 3, 2, 2, 3).shuffled())
    ).random()

    fun start() {
        if(networkManager.isNetworkAvailable()) createNativeAd()
    }

    fun destroy() {
        container.destroy()
    }

    fun getNativeAd(): NativeAdViewContentStream? {
        if(!networkManager.isNetworkAvailable() || !adCounter.canShow()) return null
        return container.getAd() ?: kotlin.run {
            createNativeAd()
            container.getAd()
        }
    }

        fun preloadAds() {
            if(!networkManager.isNetworkAvailable() || !adCounter.canShow()) return
            createNativeAd()
        }

    //Called by back pressure
    private fun createNativeAd() {
        Appodeal.getNativeAds(5).takeIf { it.isNotEmpty() }?.let {
            it.forEach { nativeAd ->
                container.appendAd(NativeAdViewContentStream(activity, nativeAd))
            }
            Appodeal.cache(activity, Appodeal.NATIVE, it.size)
        }
    }

    private class Container {
        private var ads: ArrayDeque<NativeAdViewContentStream?> = ArrayDeque()

        fun getAd(): NativeAdViewContentStream? {
            return ads.removeFirstOrNull()
        }

        fun appendAd(view: NativeAdViewContentStream?) = ads.addLast(view)

        fun destroy() {
            ads.forEach { it?.destroy() }
        }
    }
}