package com.sarftec.worldproverbs.activity

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.lifecycle.lifecycleScope
import com.appodeal.ads.Appodeal
import com.sarftec.worldproverbs.*
import com.sarftec.worldproverbs.databinding.ActivityImagePreviewBinding
import com.sarftec.worldproverbs.tools.PermissionHandler
import com.sarftec.worldproverbs.tools.ScrimDialog
import com.sarftec.worldproverbs.viewmodel.PreviewViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class PreviewActivity : BaseActivity() {

    private val binding by lazy {
        ActivityImagePreviewBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    private val viewModel by viewModels<PreviewViewModel>()

    private lateinit var permissionHandler: PermissionHandler

    override fun onResume() {
        super.onResume()
        Appodeal.show(this, Appodeal.BANNER_VIEW)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //Show banner
        Appodeal.setBannerViewId(R.id.main_banner)
        permissionHandler = PermissionHandler(this)
        intent.getBundleExtra(ACTIVITY_BUNDLE)?.let {
            viewModel.bundle = it
        }

        binding.previewToolbar.setNavigationOnClickListener {
            dependency.vibrate()
            onBackPressed()
        }
        viewModel.getImageName()?.let { name ->
            binding.previewImage.setImageURI(
                Uri.fromFile(File(cacheDir, name))
            )
        }
        binding.previewSave.setOnClickListener {
            dependency.vibrate()
            if (viewModel.imageUri != null) return@setOnClickListener
            val imageName = viewModel.getImageName()
            permissionHandler.requestReadWrite {
                savePicture { uri, outputStream ->
                    if (imageName == null || outputStream == null) return@savePicture
                    viewModel.imageUri = uri
                    val file = File(cacheDir, imageName)
                    try {
                        file.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        toast("Image saved to gallery")
                        binding.previewView.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        toast("Error occurred!. Try again")
                    }
                }
            }
        }

        binding.previewShare.setOnClickListener {
            dependency.vibrate()
            viewModel.getImageName()?.let { imageName ->
                //Assumes file exists in the cacheDir
                shareImage(imageName)
            }
        }

        binding.previewApply.setOnClickListener {
            dependency.vibrate()
            showDropDownMenu(it, R.menu.menu_drop_down)
        }

        binding.previewView.visibility = if (viewModel.imageUri == null) View.GONE else View.VISIBLE
        binding.previewView.setOnClickListener {
            dependency.vibrate()
            viewModel.imageUri?.let {
                viewInGallery(it)
            }
        }
    }

    private fun showDropDownMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.home_screen -> {
                    setWallpaper(false)
                    true
                }
                R.id.lock_screen -> {
                    setWallpaper(true)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setWallpaper(isLock: Boolean) {
        WallpaperManager.getInstance(this).apply {
            viewModel.getImageName()?.let { imageName ->
                File(cacheDir, imageName).inputStream().use { stream ->
                    if (!isLock) {
                        setBitmap(BitmapFactory.decodeStream(stream))
                        toast("Image set as wallpaper")
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        setBitmap(
                            BitmapFactory.decodeStream(stream),
                            null,
                            true,
                            WallpaperManager.FLAG_LOCK
                        )
                        toast("Image set as lock screen")
                    }
                }
            }
        }
    }
}