package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.DestinationRecord

class DRecordDetailsFragment: Fragment() {

    private val rbViewModel: RoadBookViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drecord_details, container, false)
        val arg = arguments?.getString(RoadBookFragment.DEST_ID_NAV_ARG_KEY) ?: "UNKNOWN"
        rbViewModel.recordsListState.value?.let {
            val rec = it.first { d -> d.destID == arg }
            setEditTexts(rec, view)
        }

        return view
    }

    private fun setEditTexts(rec: DestinationRecord, view: View) {
        setEditText(rec.destID, R.id.editTextDestID, view)
        setEditText(rec.clientID, R.id.editTextClientID, view)
        setEditText(DestinationRecord.timeStampFormat(rec.timeStamp), R.id.editTextTimestamp, view)
        setEditText(rec.waitingTime.toString(), R.id.editTextWaitingTime, view)
        setEditText(rec.rate.toString(), R.id.editTextRate, view)
        setEditText(DestinationRecord.actionsFormat(rec.actions), R.id.editTextActions, view)
        setEditText(rec.notes, R.id.editTextNotes, view)
    }
    private fun setEditText(format: String, id: Int, view: View) {
        val editText = view.findViewById<EditText>(id)
        editText.setText(format)
    }
}