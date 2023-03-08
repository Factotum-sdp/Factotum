package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.Action
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.databinding.FragmentDirectoryBinding
import com.github.factotum_sdp.factotum.databinding.FragmentRoadbookBinding
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.Collections


/**
 * A fragment representing a list of DestinationRecord
 */
class RoadBookFragment : Fragment() {

    private var columnCount = 1
    private var _binding: FragmentRoadbookBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_roadbook, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list)

        recyclerView.adapter = RoadBookRecyclerViewAdapter(DestinationRecords.RECORDS)
        recyclerView.adapter
        (recyclerView as RecyclerView).layoutManager = LinearLayoutManager(context)
        val divDec: DividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        //recyclerView.addItemDecoration(divDec)

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            Snackbar
                .make(it, "Don't touch ;)", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        /*
        val itemTouch: ItemTouchHelper = ItemTouchHelper(ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Get the position of the items being moved
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition

                recyclerView.adapter =
                    RoadBookRecyclerViewAdapter(recyclerView.adapter.notifyItemInserted(0))

                return true
            }
        })
        recyclerView.
        */
        return view
    }





    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            RoadBookFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}