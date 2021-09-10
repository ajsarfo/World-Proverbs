package com.sarftec.worldproverbs.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.appodeal.ads.Appodeal
import com.appodeal.ads.Native
import com.sarftec.worldproverbs.R
import com.sarftec.worldproverbs.adapter.ItemDecorator
import com.sarftec.worldproverbs.adapter.TopSliderAdapter
import com.sarftec.worldproverbs.advertisement.InterstitialManager
import com.sarftec.worldproverbs.binding.SelectionWrapper
import com.sarftec.worldproverbs.binding.Today
import com.sarftec.worldproverbs.databinding.ActivityMainBinding
import com.sarftec.worldproverbs.databinding.LayoutRatingsDialogBinding
import com.sarftec.worldproverbs.moreApps
import com.sarftec.worldproverbs.rateApp
import com.sarftec.worldproverbs.share
import com.sarftec.worldproverbs.tools.RatingsDialog
import com.sarftec.worldproverbs.tools.RatingsManager
import com.sarftec.worldproverbs.tools.SetupDialog
import com.sarftec.worldproverbs.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class MainActivity : BaseActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    private val loadingDialog by lazy {
        SetupDialog(this)
    }

    private val bindingList by lazy {
        arrayOf(
            binding.first,
            binding.second,
            binding.third,
            binding.forth,
            binding.fifth,
            binding.sixth
        )
    }
    private val viewModel by viewModels<MainViewModel>()

    private val topSlideAdapter by lazy {
        TopSliderAdapter(dependency)
    }

    private val ratingsManager by lazy {
        RatingsManager(
            lifecycleScope,
            RatingsDialog(
                LayoutRatingsDialogBinding.inflate(
                    LayoutInflater.from(this@MainActivity),
                    binding.root,
                    false
                )
            )
        )
    }

    private val interstitialManager by lazy {
        InterstitialManager(
            this,
            networkManager,
            listOf(1, 3, 4, 3)
        )
    }

    private var navigationCallback: (() -> Unit)? = null

    //Setup appodeal onResume
    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        /**************Set up appodeal configuration*****************/
        Appodeal.setNativeAdType(Native.NativeAdType.NoVideo)
        Appodeal.setAutoCache(Appodeal.NATIVE, false)
        Appodeal.setBannerViewId(R.id.main_banner)
        Appodeal.initialize(
            this,
            getString(R.string.appodeal_id),
            Appodeal.BANNER_VIEW or Appodeal.INTERSTITIAL or Appodeal.NATIVE
        )
        /*************************************************************/
        loadingDialog.show()
        configureDrawerHeader()
        lifecycleScope.launchWhenCreated {
            val timeElapsed = measureTimeMillis {
                setup()
            }
            val diff = TimeUnit.SECONDS.toMillis(1) - timeElapsed
            if (diff > 0) delay(diff)
            loadingDialog.dismiss()
        }
    }

    override fun onDestroy() {
        imageLoader.destroy()
        super.onDestroy()
    }

    private fun setup() {
        viewModel.fetch()
        configureSliderAdapter()
        configureNavigationDrawer()
        conFigureNavigationView()
        viewModel.quotes.observe(this) {
            topSlideAdapter.submitData(it)
        }
        viewModel.selection.observe(this) { selections ->
            selections.forEachIndexed { index, selection ->
                bindingList[index].binding = SelectionWrapper(dependency, selection) {
                    //Selection Navigation Takes Place Here!
                    dependency.vibrate()
                    val bundle = Bundle().apply {
                        putInt(SELECTION_ID, selection.id)
                        putString(SELECTION_TITLE, selection.text)
                    }
                   interstitialManager.showAd {
                       navigateTo(ListActivity::class.java, bundle = bundle)
                   }
                }
            }
            bindingList.forEach {
                it.executePendingBindings()
            }
        }
        viewModel.todayPair.observe(this) { todayQuotePair ->
            binding.bottomCardLayout.binding = Today(
                dependency,
                todayQuotePair.first,
                todayQuotePair.second
            ).also {
                it.init()
            }
            binding.bottomCardLayout.executePendingBindings()
        }
        lifecycleScope.launchWhenCreated {
            ratingsManager.init()
            while (true) {
                delay(TimeUnit.SECONDS.toMillis(10))
                binding.viewPager.let {
                    it.currentItem = (it.currentItem + 1).mod(MainViewModel.SLIDER_INDICATOR_SIZE)
                }
            }
        }
    }

    private fun configureDrawerHeader() {
        binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.header_image)?.let {
            assets.open(imageStore.launcherImagePath()).use { stream ->
                it.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        }

    }

    private fun configureSliderAdapter() {
        val indicatorPager =
            SliderIndicatorPager(MainViewModel.SLIDER_INDICATOR_SIZE, binding.indicatorLayout)
        with(binding.viewPager) {
            adapter = topSlideAdapter
            addItemDecoration(ItemDecorator(0f, this@MainActivity))
        }
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    indicatorPager.switch(position)
                }
            }
        )

    }

    private fun configureNavigationDrawer() {
        binding.materialToolbar.setNavigationOnClickListener {
            binding.navDrawer.openDrawer(GravityCompat.START)
        }
        binding.navDrawer.addDrawerListener(
            object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                }

                override fun onDrawerOpened(drawerView: View) {
                }

                override fun onDrawerClosed(drawerView: View) {
                    navigationCallback?.invoke()
                    navigationCallback = null
                }

                override fun onDrawerStateChanged(newState: Int) {
                }
            }
        )
    }

    private fun conFigureNavigationView() {
        fun onDrawerAction(callback: () -> Unit) {
            dependency.vibrate()
            navigationCallback = {
                callback.invoke()
            }
            binding.navDrawer.closeDrawer(GravityCompat.START)
        }
        binding.navView.itemIconTintList = null
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    dependency.vibrate()
                    binding.navDrawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.top_proverbs -> {
                    onDrawerAction {
                        interstitialManager.showAd {
                            navigateTo(TopActivity::class.java)
                        }
                    }
                    true
                }
                R.id.proverb_of_the_day -> {
                    val bundle = Bundle().apply {
                        viewModel.todayPair.value?.let { pair ->
                            putInt(QUOTE_AUTHOR_ID, pair.first.id)
                            putString(CATEGORY_TYPE, CATEGORY_QUOTE_OF_THE_DAY)
                        }
                    }
                    onDrawerAction {
                        interstitialManager.showAd {
                            navigateTo(QuoteActivity::class.java, bundle = bundle)
                        }
                    }
                    true
                }
                R.id.favorite -> {
                    onDrawerAction {
                        navigateTo(FavoriteActivity::class.java)
                    }
                    true
                }
                R.id.settings -> {
                    onDrawerAction {
                        navigateTo(SettingsActivity::class.java)
                    }
                    true
                }
                R.id.share -> {
                    onDrawerAction {
                        share(
                            "${getString(R.string.app_share_message)}\n\nhttps://play.google.com/store/apps/details?id=${packageName}",
                            "Share"
                        )
                    }
                    true
                }
                R.id.rate -> {
                    onDrawerAction {
                        rateApp()
                    }
                    true
                }
                R.id.more -> {
                    onDrawerAction {
                        moreApps()
                    }
                    true
                }
                R.id.about -> {
                    onDrawerAction {
                        navigateTo(AboutActivity::class.java)
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.navDrawer.isDrawerOpen(GravityCompat.START))
            binding.navDrawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    private class SliderIndicatorPager(size: Int, parent: LinearLayout) {

        private val indicatorList: List<Indicator>

        private var current: Indicator

        init {
            val list = mutableListOf<Indicator>()
            for (i in 0 until size) list.add(Indicator(parent))
            indicatorList = list
            current = list[0].apply {
                switchOn(true)
            }
        }

        fun switch(position: Int) {
            current.switchOn(false)
            current = indicatorList[position].apply {
                switchOn(true)
            }
        }
    }

    class Indicator(parent: LinearLayout) {

        private val view: ImageView

        init {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            view = ImageView(parent.context).apply {
                setPadding(8, 0, 8, 0)
                parent.addView(this, params)
                setImageResource(R.drawable.slider_unchecked)
            }
        }

        fun switchOn(on: Boolean) {
            view.setImageResource(
                if (on) R.drawable.slider_checked else R.drawable.slider_unchecked
            )
        }
    }
}