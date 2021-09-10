package com.sarftec.worldproverbs.extra

import android.widget.TextView
import com.sarftec.worldproverbs.databinding.LayoutTextViewStylingBinding

class SizeManager(
    layout: LayoutTextViewStylingBinding,
    private vararg val textViews: TextView
) {

    private val counter = Counter(16, 48, 24)

    init {
        val textCounter = layout.readersettingsTextSize
        textCounter.text = counter.getValue().toString()
        textViews.forEach {
            it.textSize = counter.getValue().toFloat()
        }
        layout.readersettingsSmallerText.setOnClickListener { _ ->
            counter.decrement()
            textCounter.text = counter.getValue().toString()
            textViews.forEach {
                it.textSize = counter.getValue().toFloat()
            }
        }

        layout.readersettingsLargerText.setOnClickListener { _ ->
            counter.increment()
            textCounter.text = counter.getValue().toString()
            textViews.forEach {
                it.textSize = counter.getValue().toFloat()
            }
        }
    }

    private class Counter(private var min: Int, private var max: Int, private var start: Int) {

        fun getValue(): Int = start

        fun increment() {
            if (start >= max) return
            start++
        }

        fun decrement() {
            if (start <= min) return
            start--
        }
    }
}