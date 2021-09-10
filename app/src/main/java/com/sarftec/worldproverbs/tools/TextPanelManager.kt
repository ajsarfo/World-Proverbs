package com.sarftec.worldproverbs.tools

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sarftec.worldproverbs.R
import com.sarftec.worldproverbs.databinding.LayoutTextViewStylingBinding
import com.sarftec.worldproverbs.extra.*
import kotlinx.coroutines.CoroutineScope

class TextPanelManager(
    val coroutineScope: CoroutineScope,
    private val layout: LayoutTextViewStylingBinding,
    background: View,
    vararg textViews: TextView
) {

    private val headers = listOf(
        layout.readerHeaderSize to layout.readerTextsizeSublayoutId,
        layout.readerHeaderColor to layout.readerTextcolorSublayoutId,
        layout.readerHeaderFont to layout.readerTextfontSublayoutId,
        layout.readerHeaderAlignment to layout.readerTextAlignemntSublayoutId,
        layout.readerHeaderBgOpacity to layout.readerBgopacitySublayoutId
    )

    private var current = headers.first()

    private val selectedTextColor by lazy {
        ContextCompat.getColor(
            layout.root.context,
            R.color.reader_text_highlightcolor
        )
    }

    private val unselectedTextColor by lazy {
        ContextCompat.getColor(
            layout.root.context,
            R.color.reader_text_color
        )
    }

    init {
        headers.forEach { pair ->
            pair.first.setOnClickListener {
                (it as TextView).setTextColor(selectedTextColor)
                current.first.setTextColor(unselectedTextColor)
                switchLayout(pair)
                current = pair
            }
        }

        layout.readerCloseButton.setOnClickListener {
            layout.layoutExtraPanel.visibility = View.GONE
        }

        SizeManager(layout, *textViews)
        AlignmentManager(layout, *textViews)
        FontManager(coroutineScope, layout, *textViews)
        ColorManager(coroutineScope, layout, *textViews)
        OpacityManager(layout, background)
    }

    private fun switchLayout(pair: Pair<TextView, LinearLayout>) {
        current.second.visibility = View.GONE
        pair.second.visibility = View.VISIBLE
    }

    fun show() {
        layout.layoutExtraPanel.visibility = View.VISIBLE
    }
}