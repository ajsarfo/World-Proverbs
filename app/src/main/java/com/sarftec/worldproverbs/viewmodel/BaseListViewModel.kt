package com.sarftec.worldproverbs.viewmodel

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.sarftec.worldproverbs.advertisement.NativeManager
import com.sarftec.worldproverbs.advertisement.NetworkManager
import com.sarftec.worldproverbs.data.repository.Repository
import com.sarftec.worldproverbs.model.Item
import com.sarftec.worldproverbs.tools.ListWindowCreator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseListViewModel (
    protected val repository: Repository,
    private val stateHandle: SavedStateHandle
) : ViewModel(), SavableInterface {

    protected var quotes: List<Item.Quote>? = null

    protected var contentBundle: Bundle?
    get() = stateHandle.get("bundle")
    set(value) = stateHandle.set("bundle", value)


    protected abstract val enableSeparators: Boolean

    private var separatorId: Int = -1

    private var nativeManager: NativeManager? = null

    @Inject
    lateinit var networkManager: NetworkManager

    override fun save(quote: Item.Quote) {
        viewModelScope.launch {
            repository.updateQuote(quote.id, quote.favorite)
        }
    }

    fun setBundle(bundle: Bundle?) {
        if (contentBundle != null) return
        contentBundle = bundle
    }

    fun createNativeManager(activity: Activity) {
        if(nativeManager != null) return
        nativeManager = NativeManager(activity, networkManager).apply {
            preloadAds()
        }
    }

    fun destroyNativeManager() {
        nativeManager?.destroy()
    }

    suspend fun getPagingFlow() : Flow<PagingData<Item>>? {
        return retrieveQuotes()?.let {
            Pager(PagingConfig(10)) {
                ListModelSource(ListWindowCreator(it, 10).createWindowedList())
            }.flow
                .map { pagingData ->
                    pagingData.insertSeparators { first: Item?, second: Item? ->
                        if (!enableSeparators || first == null || second == null) null
                        else if(first is Item.Separator && second is Item.Separator) null
                        else  nativeManager!!.getNativeAd()?.let {
                            Log.v("TAG", "Creating native ad")
                            Item.Separator(--separatorId, it)
                        }
                    }
                }
                .cachedIn(viewModelScope)
        }
    }

    abstract fun getToolbarTitle() : String?

    abstract fun navigationBundle(quote: Item.Quote) : Bundle?

    abstract suspend fun retrieveQuotes() : List<Item.Quote>?

    private class ListModelSource(
        private val windowedModels: List<List<Item>>
    ) : PagingSource<Int, Item>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
            val key = params.key ?: 0
            return LoadResult.Page(
                prevKey = if (key == 0) null else key - 1,
                nextKey = if (key == windowedModels.size - 1) null else key + 1,
                data = windowedModels[key]
            )
        }

        override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
            return state.anchorPosition
        }
    }
}