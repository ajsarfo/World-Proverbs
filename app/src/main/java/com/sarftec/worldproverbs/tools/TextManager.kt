package com.sarftec.worldproverbs.tools

import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.sarftec.worldproverbs.model.Item

class TextManager(
    val activity: Activity,
    val message: TextView,
    val items: List<Item.Quote>,
    val position: Int,
    val onItemSelected: (Item.Quote) -> Unit
) {

    private val dimension: Point = Point()

    val iterator = CustomListIterator(items, position)

    init {
        message.text = items[position].message
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.windowManager.currentWindowMetrics.bounds.let {
                dimension.x = it.width()
                dimension.y = it.height()
            }
        }
        else {
            activity.windowManager.defaultDisplay.getSize(dimension)
        }
    }

    fun next() {
        if (!iterator.hasNext()) return
        iterator.next().let {
            setText(message, "${it.message}\n\n_${it.author}")
            onItemSelected(it)
        }
        rightAnimate()
    }

    fun previous() {
        if (!iterator.hasPrevious()) return
        iterator.previous().let {
            setText(message, "${it.message}\n\n_${it.author}")
            onItemSelected(it)
        }
        leftAnimate()
    }

    private fun leftAnimate() {
        animateView(message, -dimension.x.toFloat())
    }

    private fun rightAnimate() {
        animateView(message, dimension.x.toFloat())
    }

    private fun setText(view: TextView, text: String) {
        view.alpha = 0f
        view.text = text
    }

    private fun animateView(view: View, value: Float) {
        view.alpha = 1f
        ObjectAnimator.ofFloat(view, "translationX", value, 0f).apply {
            interpolator = LinearInterpolator()
            duration = 300
            start()
        }
    }
}