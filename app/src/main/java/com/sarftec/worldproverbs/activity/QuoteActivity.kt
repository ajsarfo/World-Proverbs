package com.sarftec.worldproverbs.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.appodeal.ads.Appodeal
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.sarftec.worldproverbs.*
import com.sarftec.worldproverbs.adapter.QuoteHolder
import com.sarftec.worldproverbs.advertisement.InterstitialManager
import com.sarftec.worldproverbs.binding.QuoteItem
import com.sarftec.worldproverbs.databinding.ActivityQuoteBinding
import com.sarftec.worldproverbs.databinding.LayoutTextViewStylingBinding
import com.sarftec.worldproverbs.model.Item
import com.sarftec.worldproverbs.tools.OnSwipeTouchListener
import com.sarftec.worldproverbs.tools.ScrimDialog
import com.sarftec.worldproverbs.tools.TextManager
import com.sarftec.worldproverbs.tools.TextPanelManager
import com.sarftec.worldproverbs.viewmodel.QuoteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class QuoteActivity : BaseActivity(), ColorPickerDialogListener {

    private val viewModel by viewModels<QuoteViewModel>()

    private val activityBinding by lazy {
        ActivityQuoteBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    private val panelBinding by lazy {
        LayoutTextViewStylingBinding.inflate(
            LayoutInflater.from(this),
            activityBinding.panelContainer,
            true
        )
    }

    private val textPanelManager by lazy {
        TextPanelManager(
            lifecycleScope,
            panelBinding,
            activityBinding.quoteImageOverlay,
            activityBinding.message
        )
    }

    private val scrimDialog by lazy {
        ScrimDialog(this)
    }

    private val interstitialManager by lazy {
        InterstitialManager(
            this,
            networkManager,
            listOf(2, 3, 4, 3)
        )
    }

    private var quoteBinding: QuoteItem? = null

    private var textManager: TextManager? = null

    private var showProverbLocation = true

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityBinding.root)
        //Show banner
        Appodeal.setBannerViewId(R.id.main_banner)
        lifecycleScope.launchWhenCreated {
            scrimDialog.show()
            delay(500)
            scrimDialog.dismiss()
        }
        lifecycleScope.launch {
            showProverbLocation = readSettings(SHOW_AUTHOR, true).first()
        }
        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            activityResult.data?.apply {
                getStringExtra(IMAGE_ACTION)?.let {
                    if (it == IS_COLOR) {
                        showColorPicker()
                    } else if (it == IS_IMAGE) {
                        getStringExtra(IMAGE_URI)?.let { uriString ->
                            loadAndSetImage(Uri.parse(uriString))
                        }
                    }
                }
            }
        }
        savedInstanceState ?: kotlin.run {
            viewModel.setBundle(intent.getBundleExtra(ACTIVITY_BUNDLE))
        }
        viewModel.fetch()
        //Setting text panel
        activityBinding.bottomSelections.showPanelLayout.setOnClickListener {
            dependency.vibrate()
            textPanelManager.show()
        }

        activityBinding.bottomSelections.imageChooserLayout.setOnClickListener {
            //toast("Starting quote activity")
            dependency.vibrate()
            interstitialManager.showAd {
                resultLauncher.launch(Intent(this, ImageActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        activityBinding.bottomSelections.wallpaperLayout.setOnClickListener {
            dependency.vibrate()
            val imageName = "temp_image"
            val file = File.createTempFile(imageName, ".jpg", cacheDir)
            file.outputStream().use { outputStream ->
                activityBinding.captureFrame.toBitmap(this) {
                    val compressed = it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    if (compressed) outputStream.flush()
                }
            }
            interstitialManager.showAd {
                navigateTo(
                    PreviewActivity::class.java,
                    bundle = Bundle().apply {
                        putString("imageName", file.name)
                    }
                )
            }
        }

        viewModel.quotes.observe(this) {
            textManager = TextManager(
                this,
                activityBinding.message,
                it.quotes,
                it.position
            ) { item ->
                changeQuote(item)
            }
            quoteBinding = QuoteItem(it.first, it.imageUri, QuoteHolder(dependency, viewModel), showProverbLocation)
            quoteBinding?.let { item ->
                activityBinding.binding = item
                activityBinding.executePendingBindings()
            }
        }

        activityBinding.root.setOnTouchListener(
            object : OnSwipeTouchListener(this@QuoteActivity) {
                override fun onSwipeRight() {
                    textManager?.previous()
                }

                override fun onSwipeLeft() {
                    textManager?.next()
                }
            }
        )
    }

    private fun loadAndSetImage(uri: Uri) {
        lifecycleScope.launch {
            scrimDialog.show()
            delay(500)
            scrimDialog.dismiss()
            imageLoader.loadImage(uri).collect {
                it?.let { bitmap ->
                    activityBinding.captureFrameImage.setImageBitmap(bitmap)
                }
            }
        }
    }
    private fun changeQuote(quote: Item.Quote) {
        quoteBinding?.switchQuote(quote)
        activityBinding.executePendingBindings()
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        activityBinding.captureFrameImage.setImageDrawable(
            ColorDrawable(color)
        )
    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    private fun showColorPicker() {
       lifecycleScope.launch {
           scrimDialog.show()
           delay(500)
           scrimDialog.dismiss()
           ColorPickerDialog
               .newBuilder()
               .show(this@QuoteActivity)
       }
    }

    companion object {
        const val IMAGE_URI = "image_result"
        const val IMAGE_ACTION = "image_action"
        const val IS_COLOR = "is_color"
        const val IS_IMAGE = "is_image"
    }
}