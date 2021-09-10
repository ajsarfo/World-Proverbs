package com.sarftec.worldproverbs.activity

import android.os.Bundle
import android.view.LayoutInflater
import com.sarftec.worldproverbs.binding.AboutItem
import com.sarftec.worldproverbs.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity() {

    private val binding by lazy {
        ActivityAboutBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.aboutToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.binding = AboutItem(dependency)
        binding.executePendingBindings()
    }
}