package com.sarftec.worldproverbs.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.appodeal.ads.Appodeal
import com.sarftec.worldproverbs.R
import com.sarftec.worldproverbs.adapter.ImageAdapter
import com.sarftec.worldproverbs.databinding.ActivityImageBinding
import com.sarftec.worldproverbs.toast
import com.sarftec.worldproverbs.tools.ImageHandler
import com.sarftec.worldproverbs.tools.LoadingScreen
import com.sarftec.worldproverbs.tools.PermissionHandler
import com.sarftec.worldproverbs.tools.ScrimDialog
import kotlinx.coroutines.delay

class ImageActivity : BaseActivity() {

    private val binding by lazy {
        ActivityImageBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    private lateinit var imageHandler: ImageHandler

    private val imageAdapter by lazy {
        ImageAdapter(
            dependency,
            imageHandler,
            imageStore.placeholderContainer.images() +
                    imageStore.imageContainer.images()
        ) { action, uri ->
            dependency.vibrate()
            val intent = Intent()
            intent.putExtra(QuoteActivity.IMAGE_URI, uri.toString())
            intent.putExtra(QuoteActivity.IMAGE_ACTION, action)
            setResult(Activity.RESULT_OK, intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private val scrimDialog by lazy {
        LoadingScreen(this)
    }

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //Show banner
        Appodeal.setBannerViewId(R.id.main_banner)
        lifecycleScope.launchWhenCreated {
            scrimDialog.show()
            delay(1000)
            scrimDialog.dismiss()
        }
        imageHandler = ImageHandler(this, PermissionHandler(this))
        binding.imageToolbar.setNavigationOnClickListener {
            dependency.vibrate()
            onBackPressed()
        }
        binding.imageRecycler.apply {
            layoutManager = GridLayoutManager(this@ImageActivity, 3)
            adapter = imageAdapter
            setHasFixedSize(true)
        }
    }
}