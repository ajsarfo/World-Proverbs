package com.sarftec.worldproverbs.activity

import androidx.activity.viewModels
import com.sarftec.worldproverbs.viewmodel.FavoriteViewModel

class FavoriteActivity : BaseListActivity() {

    override val viewModel by viewModels<FavoriteViewModel>()

    override val adapterHasFixedSize = true
}