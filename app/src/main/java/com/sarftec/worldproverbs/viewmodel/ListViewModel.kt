package com.sarftec.worldproverbs.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.sarftec.worldproverbs.activity.BaseActivity
import com.sarftec.worldproverbs.activity.BaseActivity.Companion.CATEGORY_REGULAR
import com.sarftec.worldproverbs.activity.BaseActivity.Companion.CATEGORY_TYPE
import com.sarftec.worldproverbs.activity.BaseActivity.Companion.QUOTE_AUTHOR_ID
import com.sarftec.worldproverbs.activity.BaseActivity.Companion.SELECTION_ID
import com.sarftec.worldproverbs.activity.BaseActivity.Companion.SELECTION_TITLE
import com.sarftec.worldproverbs.data.repository.Repository
import com.sarftec.worldproverbs.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    repository: Repository,
    stateHandle: SavedStateHandle
) : BaseListViewModel(repository, stateHandle) {

    override val enableSeparators = true

    override fun getToolbarTitle(): String? {
        return contentBundle?.getString(SELECTION_TITLE)?.let {
            "$it Proverbs"
        }
    }

    override fun navigationBundle(quote: Item.Quote): Bundle {
        return Bundle().apply {
            contentBundle?.let {
                putInt(SELECTION_ID, it.getInt(SELECTION_ID))
                putInt(QUOTE_AUTHOR_ID, quote.id)
                putString(CATEGORY_TYPE, CATEGORY_REGULAR)
            }
        }
    }

    override suspend fun retrieveQuotes(): List<Item.Quote>? {
        return quotes ?: contentBundle?.let {
            quotes = repository.quotes(it.getInt(SELECTION_ID)).map { result ->
                Item.Quote(result.id, result.message, result.author, result.favorite)
            }.shuffled()
            quotes
        }
    }
}