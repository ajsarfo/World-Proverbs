package com.sarftec.worldproverbs.tools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity

class ImageHandler(
    val activity: AppCompatActivity,
    val permissionHandler: PermissionHandler
) {

    private var pictureAction: ((Boolean, Uri?) -> Unit)? = null

    private var uri: Uri? = null

    val launcher = activity.registerForActivityResult(PickPicture()) {
        pictureAction?.invoke(it != null, it)
        uri = null
        pictureAction = null
    }

    fun performTakePicture(action: (Boolean, Uri?) -> Unit) {
        this.pictureAction = action
        launcher.launch(null)
    }

    inner class PickPicture : ActivityResultContract<Int, Uri?>() {

        override fun createIntent(context: Context, input: Int?): Intent {
            return Intent(Intent.ACTION_PICK).apply {
                setDataAndType(uri, "image/*")
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK) intent?.data
            else null
        }
    }
}