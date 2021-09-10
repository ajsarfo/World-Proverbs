package com.sarftec.worldproverbs

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import com.google.android.material.textview.MaterialTextView
import com.sarftec.worldproverbs.databinding.LayoutDummyCaptureFrameBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


fun View.toBitmap(
    activity: AppCompatActivity,
    callback: (Bitmap) -> Unit
) {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    callback(bitmap)
}

/*
Note: The image should exist inside the cache directory
 */
fun Context.shareImage(imageName: String) {
    val adjustedUri =
        FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            File(cacheDir, imageName)
        )
    startActivity(
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, adjustedUri)
            data = adjustedUri
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    )
}

fun Context.viewInGallery(imageUri: Uri) {
    val adjustedUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                imageUri.toString().substringAfterLast(Environment.DIRECTORY_PICTURES)
            )
        )
    } else {
        imageUri
    }

    startActivity(
        Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(adjustedUri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    )
}

fun Context.savePicture(callback: (uri: Uri?, outputStream: OutputStream?) -> Unit) {
    val appName = getString(R.string.app_name).replace(" ", "_")
    val imageName = "${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null
    var imageUri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val directory = "${Environment.DIRECTORY_PICTURES}/$appName"
        contentResolver?.let {
            val contentValue = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
            }
            imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
            fos = imageUri?.let { contentResolver.openOutputStream(it) }
        }
    } else {
        val directory =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/$appName")
        if (!directory.exists()) directory.mkdirs()
        val image = File(directory, imageName)
        imageUri = Uri.fromFile(image)
        fos = FileOutputStream(image)
        //Scanning for file in pictures
        imageUri?.let {
            sendBroadcast(
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                    data = it
                }
            )
        }
    }
    callback(imageUri, fos)
    fos?.close()
}

fun Context.saveBitmapToGallery(bitmap: Bitmap): Uri? {
    var imageUri: Uri? = null
    savePicture { uri, outputStream ->
        imageUri = uri
        outputStream?.let {
            val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            if (compressed) it.flush()
        }
    }
    return imageUri
}