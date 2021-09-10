package com.sarftec.worldproverbs.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.timepicker.MaterialTimePicker
import com.sarftec.worldproverbs.*
import com.sarftec.worldproverbs.databinding.ActivitySettingsBinding
import com.sarftec.worldproverbs.notification.AlarmMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class SettingsActivity : BaseActivity() {

    private val binding by lazy {
        ActivitySettingsBinding.inflate(
            LayoutInflater.from(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .commit()
        }
        binding.settingsToolbar.setNavigationOnClickListener {
            dependency.vibrate()
            onBackPressed()
        }
    }

    //Settings Fragment is here!!!!!
    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {

        @Inject
        lateinit var alarmMaker: AlarmMaker

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            findPreference<SwitchPreference>("vibrate")?.let { switch ->
                switch.setOnPreferenceClickListener {
                    customVibrate()
                    lifecycleScope.launch {
                        requireContext().editSettings(SHOULD_VIBRATE, switch.isChecked)
                    }
                    true
                }
            }

            findPreference<SwitchPreference>("show_author_name")?.let { switch ->
                switch.setOnPreferenceClickListener {
                    customVibrate()
                    lifecycleScope.launch {
                        requireContext().editSettings(SHOW_AUTHOR, switch.isChecked)
                    }
                    true
                }
            }

            lifecycleScope.launch {
                findPreference<SwitchPreference>("night_mode")?.let {
                    it.isChecked = requireContext().readSettings(
                        IS_DARK_MODE, false
                    ).first()
                }
            }

            findPreference<SwitchPreference>("night_mode")?.let { switch ->
                switch.setOnPreferenceClickListener {
                    customVibrate()
                    lifecycleScope.launch {
                        requireContext().editSettings(IS_DARK_MODE, switch.isChecked)
                        AppCompatDelegate.setDefaultNightMode(
                            if (switch.isChecked) AppCompatDelegate.MODE_NIGHT_YES
                            else AppCompatDelegate.MODE_NIGHT_NO
                        )
                        requireActivity().recreate()
                    }
                    true
                }
            }

            findPreference<SwitchPreference>("enable_notifications")?.let { switch ->
                switch.setOnPreferenceClickListener {
                    customVibrate()
                    lifecycleScope.launch {
                        requireContext().editSettings(SHOW_NOTIFICATIONS, switch.isChecked)
                    }
                    true
                }
            }

            findPreference<Preference>("reminder_time")?.let { preference ->
                preference.setOnPreferenceClickListener { _ ->
                    customVibrate()
                    val picker = Calendar.getInstance().let {
                        MaterialTimePicker
                            .Builder()
                            //  .setTheme(R.style.MaterialDialogTheme)
                            .setHour(it.get(Calendar.HOUR_OF_DAY))
                            .setMinute(it.get(Calendar.MINUTE))
                            .setTitleText("Set Notification Time")
                            .build()
                    }
                    picker.addOnPositiveButtonClickListener {
                        val totalMinutes = 60 * picker.hour + picker.minute
                        lifecycleScope.launch {
                            preference.summary = convertMinuteToString(totalMinutes)
                            requireContext().editSettings(REMINDER_TIMER, totalMinutes)
                            alarmMaker.startAlarm()
                        }
                    }
                    picker.show(requireActivity().supportFragmentManager, picker.toString())
                    true
                }
            }
        }

        private fun customVibrate() {
            lifecycleScope.launch {
                requireContext().let {
                    if (it.readSettings(SHOULD_VIBRATE, true).first()) it.vibrate()
                }
            }
        }

        private fun convertMinuteToString(totalMinutes: Int): String {
            val hours = totalMinutes.div(60)
            val initial = if (hours >= 12) "PM" else "AM"
            val minutes = totalMinutes.rem(60)
            return "${hours.rem(12)} : ${String.format("%02d", minutes)} $initial"
        }
    }
}