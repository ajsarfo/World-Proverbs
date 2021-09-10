package com.sarftec.worldproverbs.binding

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sarftec.worldproverbs.*
import com.sarftec.worldproverbs.image.ImageHolder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.StringBuilder


class AboutItem(
    private val dependency: Dependency
) : BaseObservable() {

    val appVersion = "v1.0"

    @get:Bindable
    var image: ImageHolder by bindable(ImageHolder.Empty, BR.image)

    init {
        with(dependency) {
            coroutineScope.launch {
                imageLoader.loadImage(imageStore.launcherImageUri()).collect { bitmap ->
                    bitmap?.let {
                        image = ImageHolder.ImageBitmap(it)
                        throw CancellationException()
                    }
                }
            }
        }
    }
    fun rate() {
        dependency.apply {
            vibrate()
            context.rateApp()
        }
    }

    fun share() {
        dependency.apply {
            vibrate()
            context.share(
                "${context.getString(R.string.app_share_message)}\n\nhttps://play.google.com/store/apps/details?id=${context.packageName}",
                "Share"
            )
        }
    }

    fun email() {
        val address = "sarftecsolutions111@gmail.com"
        val subject = "Feedback for World Proverbs"

        val body = StringBuilder().apply {
            append("________________________")
            append("\n")
            append("Please keep the following information:")
            append("\n")
            append("________________________")
            append("\n")
            append("App Version: 1.0\n")
            append("OS version: ${Build.VERSION.RELEASE}\n")
            append("Device Brand: ${Build.BRAND}\n")
            append("Device Model: ${Build.MODEL}\n")
            append("Device Manufacturer: ${Build.MANUFACTURER}")
        }.toString()

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.apply {
            data = Uri.fromParts("mailto:", address, null)
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        dependency.context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }
}