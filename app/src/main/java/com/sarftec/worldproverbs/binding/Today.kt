package com.sarftec.worldproverbs.binding

import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sarftec.worldproverbs.BR
import com.sarftec.worldproverbs.Dependency
import com.sarftec.worldproverbs.bindable
import com.sarftec.worldproverbs.data.model.Quote
import com.sarftec.worldproverbs.image.ImageHolder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class Today(
    private val dependency: Dependency,
    quote: Quote,
    private val imageUri: Uri
) : BaseObservable() {

    @get:Bindable
    var image: ImageHolder by bindable(ImageHolder.Empty, BR.image)

    val text = quote.message

    fun init() = with(dependency) {
        coroutineScope.launch {
            imageLoader.loadImage(imageUri).collect { bitmap ->
                bitmap?.let {
                    image = ImageHolder.ImageBitmap(it)
                    throw CancellationException()
                }
            }
        }
    }
}