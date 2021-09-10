package com.sarftec.worldproverbs.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarftec.worldproverbs.Dependency
import com.sarftec.worldproverbs.binding.TopSlider
import com.sarftec.worldproverbs.databinding.LayoutSlideCardBinding
import com.sarftec.worldproverbs.model.Item

class TopSliderAdapter(
    private val dependency: Dependency,
    private var items: List<Item.Quote> = emptyList()
) : RecyclerView.Adapter<TopSliderAdapter.TopSliderViewHolder>() {

    private val sliderContainer = dependency.imageStore.imageContainer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopSliderViewHolder {
        val binding = LayoutSlideCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TopSliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopSliderViewHolder, position: Int) {
        holder.bind(
            dependency,
            items[position].message,
            sliderContainer.randomUri()
        )
    }

    override fun getItemCount(): Int = items.size

    fun submitData(items: List<Item.Quote>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class TopSliderViewHolder(private val binding: LayoutSlideCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dependency: Dependency, text: String, image: Uri) {
            binding.binding = TopSlider(dependency, text, image).also {
                it.init()
            }
            binding.executePendingBindings()
        }
    }
}