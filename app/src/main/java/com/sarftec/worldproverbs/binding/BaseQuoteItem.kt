package com.sarftec.worldproverbs.binding

import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sarftec.worldproverbs.*
import com.sarftec.worldproverbs.adapter.QuoteHolder
import com.sarftec.worldproverbs.image.ImageHolder
import com.sarftec.worldproverbs.model.Item
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseQuoteItem(
    private val container: QuoteHolder,
    var quote: Item.Quote,
    private var showProverbLocation: Boolean = true
) : BaseObservable() {

    @get:Bindable
    var message: String by bindable("${quote.message}\n\n_${quote.author}", BR.message)

    @get:Bindable
    var image: ImageHolder by bindable(ImageHolder.Empty, BR.image)

    @get:Bindable
    var favoriteIcon: ImageHolder by bindable(ImageHolder.Empty, BR.favoriteIcon)


    init {
        switchQuote(quote)
        setFavoriteIcon()
    }

    fun switchQuote(quote: Item.Quote) {
        this.quote = quote
        message = if(showProverbLocation) "${quote.message}\n\n_${quote.author}" else quote.message
        setFavoriteIcon()
    }

    fun setFavoriteIcon() {
       favoriteIcon = if(quote.favorite) ImageHolder.ImageDrawable(R.drawable.ic_star_filled)
      else ImageHolder.ImageDrawable(R.drawable.ic_star_unfilled)
    }

    protected fun loadImage(uri: Uri) = with(container.dependency) {
        coroutineScope.launch {
            imageLoader.loadImage(uri).collect { bitmap ->
                bitmap?.let {
                    image = ImageHolder.ImageBitmap(it)
                    throw CancellationException()
                }
            }
        }
    }

    fun onCopy() {
        container.dependency.apply {
            vibrate()
            context.apply {
                val text = if(showProverbLocation) "\"${quote.message}\"\n\n_${quote.author}"
                else "\"${quote.message}"
                copy(text, "share")
                toast("Copied to clipboard")
            }
        }
    }

    open fun onFavorite() {
        container.dependency.vibrate()
        quote.favorite = !quote.favorite
        setFavoriteIcon()
        container.viewModel.save(quote)
        container.dependency.context.toast(
            if(quote.favorite) "Added to favorites" else "Removed from favorites"
        )
    }

    open fun onImageChange() {
        container.dependency.vibrate()
    }

    fun onShare() {
        container.dependency.apply {
            vibrate()
            context.apply {
                val text = if(showProverbLocation) "\"${quote.message}\"\n\n_${quote.author}"
                else "\"${quote.message}"
                share(text, "Share")
            }
        }
    }
}