package com.sarftec.worldproverbs.binding

import android.net.Uri
import com.sarftec.worldproverbs.activity.BaseListActivity
import com.sarftec.worldproverbs.adapter.QuoteHolder
import com.sarftec.worldproverbs.model.Item

class QuoteItem(
    quote: Item.Quote,
    imageUri: Uri?,
    private val container: QuoteHolder,
    showProverbLocation: Boolean
) : BaseQuoteItem(container, quote, showProverbLocation) {

    init {
        val uri = imageUri ?: container.dependency.imageStore.imageContainer.randomUri()
        loadImage(uri)
    }

    override fun onImageChange() {
        super.onImageChange()
        loadImage(container.dependency.imageStore.imageContainer.randomUri())
    }

    override fun onFavorite() {
        super.onFavorite()
        BaseListActivity.itemQuoteListHolder[quote.id] = quote.favorite
    }
}