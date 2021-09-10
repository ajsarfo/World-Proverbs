package com.sarftec.worldproverbs.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.appodeal.ads.Appodeal
import com.sarftec.worldproverbs.R
import com.sarftec.worldproverbs.adapter.QuoteListAdapter
import com.sarftec.worldproverbs.advertisement.InterstitialManager
import com.sarftec.worldproverbs.databinding.ActivityListBinding
import com.sarftec.worldproverbs.tools.LoadingScreen
import com.sarftec.worldproverbs.tools.PermissionHandler
import com.sarftec.worldproverbs.viewmodel.BaseListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseListActivity : BaseActivity() {

    protected abstract val viewModel: BaseListViewModel

    protected abstract val adapterHasFixedSize: Boolean

    protected val binding by lazy {
        ActivityListBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    private val permissionHandler by lazy {
        PermissionHandler(this)
    }

    private val interstitialManager by lazy {
        InterstitialManager(
            this,
            networkManager,
            listOf(3, 2, 4, 3)
        )
    }

    private val listAdapter by lazy {
        QuoteListAdapter(dependency, permissionHandler, viewModel) { item, uri ->
           interstitialManager.showAd {
               navigateTo(
                   QuoteActivity::class.java,
                   bundle = viewModel.navigationBundle(item)?.also {
                       it.putString(QUOTE_IMAGE_URI, uri.toString())
                   }
               )
           }
        }
    }

    private val dialog by lazy {
        LoadingScreen(this)
    }

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
        lifecycleScope.launch {
            reConfigureFavorites()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //Show banner
        Appodeal.setBannerViewId(R.id.main_banner)
        lifecycleScope.launchWhenCreated {
            dialog.show()
            delay(500)
            dialog.dismiss()
        }
        savedInstanceState ?: kotlin.run {
            viewModel.setBundle(intent.getBundleExtra(ACTIVITY_BUNDLE))
        }
        binding.listToolbar.apply {
            title = viewModel.getToolbarTitle()
            setNavigationOnClickListener {
                dependency.vibrate()
                onBackPressed()
            }
        }
        with(binding.listRecyclerView) {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(this@BaseListActivity)
            setHasFixedSize(adapterHasFixedSize)
        }
        lifecycleScope.launchWhenCreated {
            viewModel.getPagingFlow()?.collect {
                listAdapter.submitData(it)
            }
        }
    }

    suspend fun reConfigureFavorites() {
        binding.listRecyclerView.apply {
            itemQuoteListHolder.forEach { (key, value) ->
                viewModel.retrieveQuotes()?.let point@{ items ->
                    items.indexOfFirst { quote ->
                        quote.id == key
                    }.let { index ->
                        val holder = findViewHolderForAdapterPosition(index) ?: return@point
                        (holder as QuoteListAdapter.ListItemViewHolder).listItemBinding?.let {
                            it.binding?.apply {
                                quote.favorite = value
                                setFavoriteIcon()
                            }
                        }
                    }
                }
            }
            itemQuoteListHolder.clear()
        }
    }

    companion object {
        val itemQuoteListHolder = hashMapOf<Int, Boolean>()
    }
}