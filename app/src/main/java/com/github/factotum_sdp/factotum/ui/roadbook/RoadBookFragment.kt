package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


/**
 * A fragment representing a RoadBook which is a list of DestinationRecord
 */
class RoadBookFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_roadbook, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list)
        val adapter = RoadBookViewAdapter()

        val rbViewModel: RoadBookViewModel =
            ViewModelProvider(this)[RoadBookViewModel::class.java]
        rbViewModel.recordsList.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val divDec = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divDec)

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            rbViewModel.addRecord(DestinationRecords.RECORD_TO_ADD)
            Snackbar
                .make(it, getString(R.string.snap_text_record_added), 700)
                .setAction("Action", null).show()
        }

        view.findViewById<FloatingActionButton>(R.id.fab_delete).setOnClickListener {
            rbViewModel.deleteLastRecord()
            Snackbar
                .make(it, getString(R.string.snap_text_on_rec_delete), 700)
                .setAction("Action", null).show()
        }

        return view
    }

}