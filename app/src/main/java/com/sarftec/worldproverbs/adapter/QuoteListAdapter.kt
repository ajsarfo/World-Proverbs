package com.sarftec.worldproverbs.adapter

import android.animation.ObjectAnimator
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.worldproverbs.*
import com.sarftec.worldproverbs.binding.QuoteItemList
import com.sarftec.worldproverbs.databinding.LayoutContentHolderBinding
import com.sarftec.worldproverbs.databinding.LayoutListItemBinding
import com.sarftec.worldproverbs.databinding.LayoutSeparatorItemBinding
import com.sarftec.worldproverbs.image.ImageStore
import com.sarftec.worldproverbs.model.Item
import com.sarftec.worldproverbs.tools.PermissionHandler
import com.sarftec.worldproverbs.viewmodel.SavableInterface
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuoteListAdapter(
    private val dependency: Dependency,
    private val permissionHandler: PermissionHandler,
    viewModel: SavableInterface,
    onClick: (Item.Quote, Uri) -> Unit
) : PagingDataAdapter<Item, QuoteListAdapter.ListItemViewHolder>(ListItemUtil) {

    private val container = AdapterContainer(
        dependency,
        ImageCache(dependency.imageStore.imageContainer),
        viewModel,
    ) { item, uri ->
        dependency.vibrate()
        onClick(item, uri)
    }

    private var showProverbLocation = false

    init {
        dependency.coroutineScope.launch {
            showProverbLocation = dependency.context.readSettings(SHOW_AUTHOR, true).first()
        }
    }

    private fun View.startAnimation() {
        ObjectAnimator.ofFloat(this, "translationY", 250f, 0f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 100
            duration = 300
            start()
        }
    }

    override fun onBindViewHolder(viewHolder: ListItemViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Item.Quote -> viewHolder.listItemBinding?.let {
                viewHolder.holder.apply {
                    removeAllViews()
                    addView(it.root, 0)
                }
                it.binding = QuoteItemList(container, position, item, showProverbLocation) {
                    dependency.vibrate()
                    permissionHandler.requestReadWrite {
                        it.captureFrame.toBitmap(
                            permissionHandler.activity
                        ) { bitmap ->
                            it.captureFrame.context.let { context ->
                                context.saveBitmapToGallery(bitmap)?.let { imageUri ->
                                    context.toast("Saved to gallery")
                                    context.viewInGallery(imageUri)
                                }
                            }
                        }
                    }
                }.also { binding ->
                    binding.init()
                }
                it.apply {
                    message.startAnimation()
                }
                it.executePendingBindings()
            }
            is Item.Separator ->
                viewHolder.separatorItemBinding?.let {
                     it.separatorHolder.apply {
                         removeAllViews()
                         addView(item.nativeAd, 0)
                     }
                     viewHolder.holder.apply {
                         removeAllViews()
                         addView(it.root, 0)
                     }
                }
            else -> {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val parentView = LayoutContentHolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return if (viewType == LIST_ITEM) {
            val binding = LayoutListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ListItemViewHolder(holder = parentView.root, listItemBinding = binding)
        } else {
            val binding = LayoutSeparatorItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ListItemViewHolder(holder = parentView.root, separatorItemBinding = binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Quote -> LIST_ITEM
            is Item.Separator -> SEPARATOR_ITEM
            else -> LIST_ITEM
        }
    }


    /***********************List of Helper Classes for the adapter and viewholder*************/
    inner class ListItemViewHolder(
        val holder: LinearLayout,
        val listItemBinding: LayoutListItemBinding? = null,
        val separatorItemBinding: LayoutSeparatorItemBinding? = null
    ) : RecyclerView.ViewHolder(holder)

    inner class AdapterContainer(
        dependency: Dependency,
        val imageCache: ImageCache,
        viewModel: SavableInterface,
        val onClick: (Item.Quote, Uri) -> Unit
    ) : QuoteHolder(dependency, viewModel)

    inner class ImageCache(private val imageContainer: ImageStore.ImageContainer) {

        private val cache = hashMapOf<Int, Uri>()

        fun getImage(position: Int): Uri {
            return cache.getOrPut(position) {
                imageContainer.randomUri()
            }
        }

        fun change(position: Int): Uri {
            return imageContainer.randomUri().also {
                cache[position] = it
            }
        }
    }

    object ListItemUtil : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == oldItem.id
        }
    }

    companion object {
        const val SEPARATOR_ITEM = 0
        const val LIST_ITEM = 1
    }
}