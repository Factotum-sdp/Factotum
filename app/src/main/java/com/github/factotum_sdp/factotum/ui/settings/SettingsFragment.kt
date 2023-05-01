package com.github.factotum_sdp.factotum.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.factotum_sdp.factotum.R

/**
 * The fragment holding the Application settings UI
 */
class SettingsFragment: Fragment() {

    private val settings: SettingsViewModel by activityViewModels()
    private lateinit var checkBox: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        checkBox = view.findViewById(R.id.save_roadbook_preferences)
        checkBox.isChecked = settings.settingsLiveData.value?.useRoadBookPreferences ?: false

        // set update event
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            settings.updateUseRoadBookPreferences(isChecked)
        }

        return view
    }
}