package com.sarftec.worldproverbs.extra

import android.graphics.Color
import android.view.View
import com.sarftec.worldproverbs.R
import com.sarftec.worldproverbs.databinding.LayoutTextViewStylingBinding
import kotlin.math.roundToInt

class OpacityManager(
    private val layout: LayoutTextViewStylingBinding,
    private val view: View
) {

    private val blackOverlayArrays = view
        .context
        .resources
        .getStringArray(R.array.black_overlay_array)
        .map {
            Color.parseColor(it)
        }

    private var iterator = 6

    private val increment = (100f / blackOverlayArrays.size.toFloat()).roundToInt()

    init {
        setColor(iterator)
        layout.apply {
            readerBgOpacityIncreaseId.setOnClickListener {
                previousColor()
            }
            readerBgOpacityDecreaseId.setOnClickListener {
                nextColor()
            }
        }
    }

    private fun setColor(iterator: Int) {
        val customIterator = iterator + 1
        val value = if(customIterator * increment > 100) 100
        else if(iterator == 0) 0
        else customIterator * increment
        view.setBackgroundColor(blackOverlayArrays[iterator])
        layout.readersettingsOpacityValText.text = "$value%"
    }

    private fun nextColor() {
        iterator++
        if(iterator >= blackOverlayArrays.size) {
            iterator = blackOverlayArrays.size - 1
        }
        setColor(iterator)
    }

    private fun previousColor() {
        iterator--
        if(iterator < 0) {
            iterator = 0
        }
        setColor(iterator)
    }
}