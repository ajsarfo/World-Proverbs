package com.sarftec.worldproverbs.extra

import android.graphics.Paint
import android.view.Gravity
import android.widget.TextView
import com.sarftec.worldproverbs.databinding.LayoutTextViewStylingBinding

class AlignmentManager(
    private val layout: LayoutTextViewStylingBinding,
    private vararg val textViews: TextView
) {

    private var isUnderline = false
    private var isAllCaps = false

    init {
        layout.readerTextAlignemntLeftId.setOnClickListener { _->
            textViews.forEach {
                it.gravity = Gravity.START
            }
        }
        layout.readerTextAlignemntRightId.setOnClickListener { _->
            textViews.forEach {
                it.gravity = Gravity.END
            }
        }
        layout.readerTextAlignemntCenterId.setOnClickListener { _->
            textViews.forEach {
                it.gravity = Gravity.CENTER
            }
        }
        layout.readerTextAlignemntAllcapsId.setOnClickListener { _->
            isAllCaps = !isAllCaps
            textViews.forEach {
                it.isAllCaps = isAllCaps
            }
        }
        layout.readerTextAlignemntUnderlineId.setOnClickListener { _->
            isUnderline = !isUnderline
            textViews.forEach {
                it.paintFlags = if (isUnderline) Paint.UNDERLINE_TEXT_FLAG
                else it.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }
        }
    }

}