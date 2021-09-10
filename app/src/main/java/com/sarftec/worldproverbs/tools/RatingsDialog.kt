package com.sarftec.worldproverbs.tools

import android.app.AlertDialog
import com.sarftec.worldproverbs.databinding.LayoutRatingsDialogBinding

class RatingsDialog(
    binding: LayoutRatingsDialogBinding
) : AlertDialog(binding.root.context) {

    var onNextTime: (() -> Unit)? = null
    var onReview : (() -> Unit)? = null

    init {
        setCancelable(false)
        setView(binding.root)
        binding.nextTime.setOnClickListener {
            onNextTime?.invoke()
            cancel()
        }
        binding.ok.setOnClickListener {
            onReview?.invoke()
            cancel()
        }
    }
}