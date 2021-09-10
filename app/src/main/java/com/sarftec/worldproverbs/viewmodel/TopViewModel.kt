package com.sarftec.worldproverbs.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.sarftec.worldproverbs.activity.BaseActivity
import com.sarftec.worldproverbs.data.repository.Repository
import com.sarftec.worldproverbs.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TopViewModel @Inject constructor(
    repository: Repository,
    stateHandle: SavedStateHandle
) : BaseListViewModel(repository, stateHandle) {

    override val enableSeparators = true

    override fun getToolbarTitle(): String {
        return "Top Proverbs"
    }

    override fun navigationBundle(quote: Item.Quote): Bundle?  {
        return Bundle().apply {
            putInt(BaseActivity.QUOTE_AUTHOR_ID, quote.id)
            putString(BaseActivity.CATEGORY_TYPE, BaseActivity.CATEGORY_TOP)
        }
    }

    override suspend fun retrieveQuotes(): List<Item.Quote> {
        return repository.topQuotes().shuffled().map { quote ->
            quote.run {
                Item.Quote(id, message, author, favorite)
            }
        }
    }
}