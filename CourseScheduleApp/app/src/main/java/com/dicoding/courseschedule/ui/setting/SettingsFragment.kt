package com.dicoding.courseschedule.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.notification.DailyReminder

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //TODO 10 : Update theme based on value in ListPreference
        val prefListThemes = findPreference<ListPreference>(getString(R.string.pref_key_dark))
        prefListThemes?.setOnPreferenceChangeListener { _, value ->
            when (value) {
                getString(R.string.pref_dark_on) -> updateTheme(AppCompatDelegate.MODE_NIGHT_YES)
                getString(R.string.pref_dark_off) -> updateTheme(AppCompatDelegate.MODE_NIGHT_NO)
                else -> updateTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        //TODO 11 : Schedule and cancel notification in DailyReminder based on SwitchPreference
        val reminder = DailyReminder()
        val notification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
        notification?.setOnPreferenceChangeListener { _, value ->
            val isEnabled = value as Boolean
            if (isEnabled) {
                reminder.setDailyReminder(requireContext())
            } else {
                reminder.cancelAlarm(requireContext())
            }
            true
        }
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }
}