package com.sarftec.worldproverbs.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    var bundle: Bundle?
        get() = stateHandle.get("bundle")
        set(value) = stateHandle.set("bundle", value)

    var imageUri: Uri? = null

    fun getImageName(): String? {
        return bundle?.getString("imageName")
    }
}