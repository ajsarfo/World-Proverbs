package com.sarftec.worldproverbs.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarftec.worldproverbs.data.model.Quote
import com.sarftec.worldproverbs.data.repository.Repository
import com.sarftec.worldproverbs.image.ImageStore
import com.sarftec.worldproverbs.model.Item
import com.sarftec.worldproverbs.model.Selection
import com.sarftec.worldproverbs.tools.ColorPallet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Note: The view model assumes the first category of queried categories has an id of zero, and thus
 * represents the Top Proverbs.
 *
 * Note: Filtering of categories for main activity is performed somewhere inside the viewmodel and
 * is heavily dependent on the aforementioned assumption!!!
 */


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val imageStore: ImageStore,
    @ApplicationContext context: Context
) : ViewModel() {

    private val colorPallet by lazy {
        ColorPallet(context)
    }

    private val _quotes = MutableLiveData<List<Item.Quote>>()
    val quotes: LiveData<List<Item.Quote>>
        get() = _quotes

    private val _selection = MutableLiveData<List<Selection>>()
    val selection: LiveData<List<Selection>>
        get() = _selection

    private val _todayPair = MutableLiveData<Pair<Quote, Uri>>()
    val todayPair: LiveData<Pair<Quote, Uri>>
        get() = _todayPair

    fun fetch() {
        viewModelScope.launch {
            getSliderQuotes()
            getSelections()
            getTodayPair()
        }
    }

    private suspend fun getSliderQuotes() {
        _quotes.value = repository.random(SLIDER_INDICATOR_SIZE)
            .map { quote ->
                quote.run {
                    Item.Quote(id, message, author, favorite)
                }
            }
    }

    private suspend fun getSelections() {
        _selection.value = repository
            .categories()
            .filter { it.id != 0 } /* Filtering done here assumes category with zero id is Top Proverb*/
            .map {
                Selection(it.id, it.category, colorPallet.nextColor())
            }
    }

    private suspend fun getTodayPair() {
        val quoteToday = repository.quoteOfTheDay()
        _todayPair.value = quoteToday to imageStore.getTodayImage()
    }

    companion object {
        const val SLIDER_INDICATOR_SIZE = 5
    }
}