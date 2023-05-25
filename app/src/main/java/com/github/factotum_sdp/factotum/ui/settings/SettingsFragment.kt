package com.github.factotum_sdp.factotum.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.bag.BagViewModel
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * The fragment holding the Application settings UI
 */
class SettingsFragment: Fragment() {

    private val settings: SettingsViewModel by activityViewModels()
    private val roadBookViewModel: RoadBookViewModel by activityViewModels()
    private val bagViewModel: BagViewModel by activityViewModels()

    private lateinit var roadBookPreferences: CheckBox
    private lateinit var loadRoadBookBackUp: CheckBox
    private lateinit var deleteAllRoadBook: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        roadBookPreferences = view.findViewById(R.id.save_roadbook_preferences)
        loadRoadBookBackUp = view.findViewById(R.id.load_roadbook_backup)
        deleteAllRoadBook = view.findViewById(R.id.delete_all_roadbook)

        setSettingItems()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadRoadBookBackUp.isChecked = false
        deleteAllRoadBook.isChecked = false
    }

    private fun setSettingItems() {
        setCheckedSettingBehavior(
            roadBookPreferences,
            settings.settingsLiveData.value?.useRoadBookPreferences ?: false) {
            settings.updateUseRoadBookPreferences(it)
        }
        setCheckedSettingBehavior(loadRoadBookBackUp, false) {
            if(it) {
                bagViewModel.blockPackUpdate()
                roadBookViewModel.fetchBackBackUps()
                bagViewModel.fetchBackBackUp()
                snapMessage(R.string.snap_load_roadbook_backup)
            }
        }
        setCheckedSettingBehavior(deleteAllRoadBook, false) {
            if(it) {
                deleteAllConfirmationDialog({
                    roadBookViewModel.clearAllRecords()
                    snapMessage(R.string.snap_delete_all_roadbook)
                    deleteAllRoadBook.isChecked = false
                }, {
                    deleteAllRoadBook.isChecked = false
                })
            }
        }
    }

    private fun setCheckedSettingBehavior(
        checkBox: CheckBox,
        checkedInitState: Boolean,
        onCheckedChange: (Boolean) -> Unit
    )
    {
        checkBox.isChecked = checkedInitState
        checkBox.setOnCheckedChangeListener{ _, isChecked ->
            onCheckedChange(isChecked)
        }
    }

    private fun deleteAllConfirmationDialog(onConfirm: () -> Unit, onCancel:() -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_all_roadbook_dialog_title))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.positive_label_delete_all_roadbook_dialog)) { _, _ ->
            onConfirm()
        }
        builder.setNegativeButton(getString(R.string.negative_label_delete_all_roadbook_dialog)){ _, _ ->
            onCancel()
        }
        val dial = builder.create()
        dial.window?.attributes?.windowAnimations = R.style.DialogAnimLeftToRight
        dial.show()
    }

    private fun snapMessage(snapMessageID: Int) {
        Snackbar
            .make(requireView(), getString(snapMessageID), 1000)
            .setAction("Action", null).show()
    }
}