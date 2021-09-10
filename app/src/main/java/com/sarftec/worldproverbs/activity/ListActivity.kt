package com.sarftec.worldproverbs.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.appodeal.ads.Appodeal
import com.sarftec.worldproverbs.adapter.QuoteListAdapter
import com.sarftec.worldproverbs.viewmodel.ListViewModel

class ListActivity : BaseListActivity() {

    override val viewModel by viewModels<ListViewModel>()

    override val adapterHasFixedSize = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Appodeal.cache(this, Appodeal.NATIVE, 5)
        viewModel.createNativeManager(this)
    }

    override fun onDestroy() {
        viewModel.destroyNativeManager()
        super.onDestroy()
    }
}