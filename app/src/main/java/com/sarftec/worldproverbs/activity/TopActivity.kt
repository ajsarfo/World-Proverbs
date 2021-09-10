package com.sarftec.worldproverbs.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.appodeal.ads.Appodeal
import com.sarftec.worldproverbs.viewmodel.TopViewModel

class TopActivity : BaseListActivity() {

    override val viewModel by viewModels<TopViewModel>()

    override val adapterHasFixedSize = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.createNativeManager(this)
        Appodeal.cache(this, Appodeal.NATIVE, 5)
    }

    override fun onDestroy() {
        viewModel.destroyNativeManager()
        super.onDestroy()
    }
}