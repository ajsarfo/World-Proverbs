package com.sarftec.worldproverbs

import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
       /*
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder().build()
        )
        */
    }
}