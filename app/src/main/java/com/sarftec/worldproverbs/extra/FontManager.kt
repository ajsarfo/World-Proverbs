package com.sarftec.worldproverbs.extra

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarftec.worldproverbs.R
import com.sarftec.worldproverbs.databinding.LayoutFontCardBinding
import com.sarftec.worldproverbs.databinding.LayoutTextViewStylingBinding
import kotlinx.coroutines.CoroutineScope

class FontManager(
    private val coroutineScope: CoroutineScope,
    layout: LayoutTextViewStylingBinding,
    private vararg val textViews: TextView
) {

    init {
        layout.readersettingsFontRvId.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            context.assets.list("fonts")?.toList()?.let { fonts ->
                adapter = FontItemAdapter(coroutineScope, fonts) { typeface ->
                   textViews.forEach {
                       it.typeface = typeface
                   }
                }
            }
        }
    }

    class FontItemAdapter(
        coroutineScope: CoroutineScope,
        fonts: List<String>,
        configure: (Typeface?) -> Unit
    ) : ItemAdapter<String, Typeface?>(coroutineScope, fonts, configure) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ItemViewHolder<String, Typeface?> {
            val binding = LayoutFontCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return FontViewHolder(binding, itemBox).also { it.init() }
        }

        inner class FontViewHolder(
            private val binding: LayoutFontCardBinding,
            fontBox: ItemBox<Typeface?>
        ) : ItemViewHolder<String, Typeface?>(fontBox, binding.root) {

            override fun init() {
                binding.fontLayout.setOnClickListener {
                    itemBox.listener(binding.fontText.typeface)
                    clicked(bindingAdapterPosition)
                }
                super.init()
            }

            override fun bind(font: String) {
                val typeface = Typeface.createFromAsset(
                    binding.root.context.assets,
                    "fonts/$font"
                )
                binding.fontText.typeface = typeface
            }

            override fun switch(isOn: Boolean) {
                if (isOn) {
                    binding.fontText.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.white)
                    )
                    binding.fontLayout.background = ContextCompat.getDrawable(
                        binding.root.context, R.drawable.font_card_shape
                    )
                } else {
                    binding.fontText.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.reader_text_color)
                    )
                    binding.fontLayout.background = null
                }
            }
        }
    }
}