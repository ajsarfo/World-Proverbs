package com.sarftec.worldproverbs.binding

import androidx.databinding.BaseObservable
import com.sarftec.worldproverbs.Dependency
import com.sarftec.worldproverbs.model.Selection

class SelectionWrapper(
    val dependency: Dependency,
    val selection: Selection,
    val onClick: () -> Unit
) : BaseObservable() {

    val title = selection.text.replace(" ", "\n").plus("\nProverbs")
}