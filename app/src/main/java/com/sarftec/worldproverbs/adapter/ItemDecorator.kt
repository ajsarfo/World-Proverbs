package com.sarftec.worldproverbs.adapter

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorator(
    private var padding: Float,
    context: Context
) : RecyclerView.ItemDecoration() {

    init {
        padding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            padding,
            context.resources.displayMetrics
        )
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        if(itemPosition == RecyclerView.NO_POSITION) return
        val adapter = parent.adapter ?: return
        if(itemPosition == adapter.itemCount - 1) outRect.right = padding.toInt()
        if(itemPosition == 0) outRect.left = padding.toInt()
    }
}