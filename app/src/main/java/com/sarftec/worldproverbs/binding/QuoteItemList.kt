package com.sarftec.worldproverbs.binding

import android.net.Uri
import com.sarftec.worldproverbs.adapter.QuoteListAdapter
import com.sarftec.worldproverbs.model.Item

class QuoteItemList(
    private val container: QuoteListAdapter.AdapterContainer,
    private val position: Int,
    quote: Item.Quote,
    showProverbLocation: Boolean,
    val onCapture: () -> Unit
) : BaseQuoteItem(container, quote, showProverbLocation) {

    private var imageUri: Uri = container.imageCache.getImage(position)
    init {
        setFavoriteIcon()
    }

    fun init() {
        loadImage(imageUri)
    }

    fun onClick() = container.onClick(quote, imageUri)

    override fun onImageChange() {
        super.onImageChange()
        imageUri = container.imageCache.change(position)
        loadImage(imageUri)
    }
}