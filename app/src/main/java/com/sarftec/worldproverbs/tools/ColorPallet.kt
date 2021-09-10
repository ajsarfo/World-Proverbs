package com.sarftec.worldproverbs.tools

import android.content.Context
import androidx.core.content.ContextCompat
import com.sarftec.worldproverbs.R

class ColorPallet(context: Context) {

    private var iterator: Int = 0

    private val colorList = arrayOf(
        ContextCompat.getColor(context, R.color.color11),
        ContextCompat.getColor(context, R.color.color12),
        ContextCompat.getColor(context, R.color.color13),

        //ContextCompat.getColor(context, R.color.color14),
        //ContextCompat.getColor(context, R.color.color15),
       //ContextCompat.getColor(context, R.color.color16),

        ContextCompat.getColor(context, R.color.color17),
        ContextCompat.getColor(context, R.color.color18),
        ContextCompat.getColor(context, R.color.color27)
    ).also { it.shuffle() }

    fun nextColor(): Int {
        if (iterator == colorList.size) iterator = 0
        return colorList[iterator++]
    }
}