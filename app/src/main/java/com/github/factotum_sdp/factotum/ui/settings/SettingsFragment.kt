package com.github.factotum_sdp.factotum.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookViewModel

/**
 * The fragment holding the Application settings UI
 */
class SettingsFragment: Fragment() {

    private val settings: SettingsViewModel by activityViewModels()
    private val roadBookViewModel: RoadBookViewModel by activityViewModels()
    private lateinit var roadBookPreferences: CheckBox
    private lateinit var loadRoadBookBackUp: CheckBox
    private lateinit var deleteAllRoadBook: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        roadBookPreferences = view.findViewById(R.id.save_roadbook_preferences)
        roadBookPreferences.isChecked = settings.settingsLiveData.value?.useRoadBookPreferences ?: false
        roadBookPreferences.setOnCheckedChangeListener { _, isChecked ->
            settings.updateUseRoadBookPreferences(isChecked)
        }

        loadRoadBookBackUp = view.findViewById(R.id.load_roadbook_backup)
        loadRoadBookBackUp.isChecked = false
        loadRoadBookBackUp.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) roadBookViewModel.fetchBackBackUps()
        }

        deleteAllRoadBook = view.findViewById(R.id.delete_all_roadbook)
        deleteAllRoadBook.isChecked = false
        deleteAllRoadBook.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) roadBookViewModel.clearAllRecords()
        }

        return view
    }
}