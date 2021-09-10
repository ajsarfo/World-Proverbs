package com.sarftec.worldproverbs.extra

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarftec.worldproverbs.R
import com.sarftec.worldproverbs.databinding.LayoutColorCardBinding
import com.sarftec.worldproverbs.databinding.LayoutTextViewStylingBinding
import kotlinx.coroutines.CoroutineScope
import kotlin.math.floor
import kotlin.math.sqrt

class ColorManager(
    private val coroutineScope: CoroutineScope,
    layout: LayoutTextViewStylingBinding,
    private vararg val textViews: TextView
) {

    init {
        layout.readersettingsThemeRvId.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = ColorItemAdapter(
                coroutineScope,
                context.resources.getStringArray(R.array.reader_colors).map {
                    Color.parseColor(it)
                }
            ) {
                if (it != null) {
                    textViews.forEach { textView ->
                        textView.setTextColor(it)
                    }
                }
            }
        }
    }

    class ColorItemAdapter(
        coroutineScope: CoroutineScope,
        items: List<Int>,
        listener: (Int?) -> Unit
    ) : ItemAdapter<Int, Int?>(coroutineScope, items, listener) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ItemViewHolder<Int, Int?> {
            val binding = LayoutColorCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ColorItemViewHolder(binding, itemBox).also { it.init() }
        }

        inner class ColorItemViewHolder(
            private val binding: LayoutColorCardBinding,
            colorBox: ItemBox<Int?>
        ) : ItemViewHolder<Int, Int?>(colorBox, binding.root) {

            override fun init() {
                binding.colorView.setOnClickListener {
                    //call listener callback with value in item box here
                    binding.colorView.background?.let {
                        itemBox.listener((it as ColorDrawable).color)
                    }
                    clicked(bindingAdapterPosition)
                }
                super.init()
            }

            override fun bind(item: Int) {
                binding.colorView.setBackgroundColor(item)
            }

            override fun switch(isOn: Boolean) {
                binding.colorCheck.visibility = if (isOn) View.VISIBLE else View.GONE
            }
        }
    }
}