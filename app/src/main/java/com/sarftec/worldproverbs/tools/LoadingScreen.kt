package com.sarftec.worldproverbs.tools

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.sarftec.worldproverbs.databinding.LayoutLoadingScreenBinding

class LoadingScreen(context: Context) : AlertDialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setView(
            LayoutLoadingScreenBinding.inflate(
                LayoutInflater.from(context)
            ).root
        )
        setCancelable(false)
    }
}