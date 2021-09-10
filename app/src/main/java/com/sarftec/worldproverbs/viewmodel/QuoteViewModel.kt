package com.sarftec.worldproverbs.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.*
import com.sarftec.worldproverbs.activity.BaseActivity
import com.sarftec.worldproverbs.data.repository.Repository
import com.sarftec.worldproverbs.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuoteResult(
    val quotes: List<Item.Quote>,
    val first: Item.Quote,
    val imageUri: Uri?,
    val position: Int
)

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val repository: Repository,
    private val stateHandle: SavedStateHandle
) : ViewModel(), SavableInterface {

    private val _quotes = MutableLiveData<QuoteResult>()
    val quotes: LiveData<QuoteResult>
    get() = _quotes

    private var contentBundle: Bundle?
        get() = stateHandle.get("bundle")
        set(value) = stateHandle.set("bundle", value)

    fun setBundle(bundle: Bundle?) {
        if (contentBundle != null) return
        contentBundle = bundle
    }

    override fun save(quote: Item.Quote) {
        viewModelScope.launch {
            repository.updateQuote(quote.id, quote.favorite)
        }
    }

    fun getQuote(position: Int) : Item.Quote? {
        return _quotes.value?.quotes?.get(position)
    }

    fun fetch() {
        viewModelScope.launch {
            contentBundle?.let { bundle ->
                val result: List<Item.Quote> = when(bundle.getString(BaseActivity.CATEGORY_TYPE)) {
                    BaseActivity.CATEGORY_FAVORITE -> {
                        repository.favorites()
                    }
                    BaseActivity.CATEGORY_REGULAR -> {
                        repository.quotes(bundle.getInt(BaseActivity.SELECTION_ID))
                    }
                    BaseActivity.CATEGORY_QUOTE_OF_THE_DAY -> {
                       listOf(repository.quoteOfTheDay())
                    }
                    BaseActivity.CATEGORY_TOP -> {
                        repository.topQuotes()
                    }
                    else -> {
                        emptyList()
                    }
                }.map {
                    it.run { Item.Quote(id, message, author, favorite) }
                }.shuffled()
                bundle.getInt(BaseActivity.QUOTE_AUTHOR_ID).takeIf { it > 0 }?.let { id ->
                    result.indexOfFirst { it.id == id }
                }?.let { position ->
                    _quotes.value = QuoteResult(
                        result,
                        result[position].run { Item.Quote(id, message, author, favorite) },
                        bundle.getString(BaseActivity.QUOTE_IMAGE_URI)?.let { uri ->
                            Uri.parse(uri)
                        },
                        position
                    )
                }
            }
        }
    }
}