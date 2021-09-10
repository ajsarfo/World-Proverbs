package com.sarftec.worldproverbs.adapter

import android.animation.ObjectAnimator
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.worldproverbs.Dependency
import com.sarftec.worldproverbs.activity.QuoteActivity
import com.sarftec.worldproverbs.databinding.LayoutImageItemBinding
import com.sarftec.worldproverbs.image.ImageLoader
import com.sarftec.worldproverbs.tools.ImageHandler

class ImageAdapter(
    private val dependency: Dependency,
    private val handler: ImageHandler,
    private val images: List<Uri>,
    private val onClick: (String, Uri?) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = LayoutImageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(position, images[position])
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(
        private val binding: LayoutImageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var uri: Uri? = null
        private var imagePosition = -1

        init {
            binding.imageCard.setOnClickListener {
                when (imagePosition) {
                    1 -> {
                       /*
                        val file = File(
                            handler.activity.cacheDir,
                            File.createTempFile("cache", ".jpg").name
                        )
                        val uri = FileProvider.getUriForFile(
                            binding.root.context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            file
                        )
                        */
                        handler.performTakePicture { isSuccess, imageUri ->
                            if (isSuccess) onClick(QuoteActivity.IS_IMAGE, imageUri)
                        }

                    }
                    0 -> onClick(QuoteActivity.IS_COLOR, null)
                    else -> onClick(QuoteActivity.IS_IMAGE, uri)
                }
            }
        }

        fun bind(imagePosition: Int, uri: Uri) {
            this.imagePosition = imagePosition
            this.uri = uri
            binding.cup = ImageCup(uri, dependency.imageLoader)
            binding.executePendingBindings()
              /*
              ObjectAnimator.ofFloat(
                  binding.imageCardImage,
                  "translationY",
                  300f,
                  0f
              ).apply {
                  startDelay = 50
                  duration = 300
                  interpolator = AccelerateDecelerateInterpolator()
                  start()
              }
               */
        }
    }

    class ImageCup(val uri: Uri, val imageLoader: ImageLoader)
}