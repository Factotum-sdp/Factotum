package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.model.DestinationRecord

/**
 * The Fragment displaying the informations about a specific record in a read-only way
 */
class DRecordInfoFragment(private val record: DestinationRecord) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drecord_info, container, false)
        setEditTexts(record, view)
        return view
    }

    private fun setEditTexts(rec: DestinationRecord, view: View) {
        setEditText(rec.destID, R.id.editTextDestID, view)
        setEditText(rec.clientID, R.id.editTextClientID, view)
        setEditText(
            DestinationRecord.timeStampFormat(rec.timeStamp),
            R.id.editTextTimestampDRecord,
            view
        )
        setEditText(rec.waitingTime.toString(), R.id.editTextWaitingTime, view)
        setEditText(rec.rate.toString(), R.id.editTextRate, view)
        setEditText(DestinationRecord.actionsFormat(rec.actions), R.id.editTextActions, view)
        setEditText(rec.notes, R.id.editTextNotesInfo, view)
    }

    private fun setEditText(format: String, id: Int, view: View) {
        val editText = view.findViewById<EditText>(id)
        editText.setText(format)
    }
}