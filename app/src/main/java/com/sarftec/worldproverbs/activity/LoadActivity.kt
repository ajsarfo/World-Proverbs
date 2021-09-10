package com.sarftec.worldproverbs.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.sarftec.worldproverbs.*
import com.sarftec.worldproverbs.data.DatabaseSetup
import com.sarftec.worldproverbs.databinding.LayoutSetupDialogBinding
import com.sarftec.worldproverbs.tools.SetupDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class LoadActivity : BaseActivity() {

    @Inject
    lateinit var setup: DatabaseSetup

    private val setupLoadingBinding by lazy {
        LayoutSetupDialogBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            savedInstanceState ?: kotlin.run {
                val constant =
                    if (readSettings(IS_DARK_MODE, false).first()) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.setDefaultNightMode(constant)
            }
            if (!setup.isCreated()) {
                showLoadingDialog()
                setup.create()
                delay(1000)
            }
            editSettings(APP_START_COUNT, readSettings(APP_START_COUNT, 1).first() + 1)
            navigateTo(SplashActivity::class.java, true, R.anim.no_anim, R.anim.no_anim)
        }
    }

    private fun showLoadingDialog() {
        object : AlertDialog(this) {}.apply {
            setView(setupLoadingBinding.root)
            show()
        }
    }
}