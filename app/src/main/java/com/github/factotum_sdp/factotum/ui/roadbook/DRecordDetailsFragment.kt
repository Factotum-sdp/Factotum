package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.factotum_sdp.factotum.R

class DRecordDetailsFragment: Fragment() {

    private val rbViewModel: RoadBookViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drecord_details, container, false)

        val arg = arguments?.getInt("RecPos") ?: 0
        val list = rbViewModel.recordsListState.value!! //todo better exceptions handling
        val rec = list[arg]
        val textView: EditText = view.findViewById(R.id.editTextTextMultiLine)
        textView.setText(rec.notes)

        return view
    }
}