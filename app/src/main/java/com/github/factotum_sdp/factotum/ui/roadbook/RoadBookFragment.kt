package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.ActivityMainBinding
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


/**
 * A fragment representing a RoadBook which is a list of DestinationRecord
 */
class RoadBookFragment : Fragment(), MenuProvider {

    private lateinit var rbViewModel: RoadBookViewModel
    private lateinit var viewP : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_roadbook, container, false)
        viewP = view
        val adapter = RoadBookViewAdapter()
        val dbRef = (activity as MainActivity).getDatabaseRef().child(ROADBOOK_DB_PATH)
        val rbFact = RoadBookViewModel.RoadBookViewModelFactory(dbRef)
        rbViewModel = ViewModelProvider(this, rbFact)[RoadBookViewModel::class.java]

        // Observe the roadbook ViewModel, to detect data changes
        // and update the displayed RecyclerView accordingly
        rbViewModel.recordsListState.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }
        // Set events that triggers change in the roadbook ViewModel
        setRoadBookEvents(rbViewModel, view)

        // Set up the RoadBook RecyclerView
        val rbRecyclerView: RecyclerView = view.findViewById(R.id.list)
        rbRecyclerView.layoutManager = LinearLayoutManager(context)
        rbRecyclerView.adapter = adapter

        return view
    }


    override fun onPause() {
        rbViewModel.backUp()
        super.onPause()
    }

    private fun setRoadBookEvents(rbViewModel: RoadBookViewModel, view: View) {
        // Add record on positive floating button click
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            rbViewModel.addRecord(DestinationRecords.RECORD_TO_ADD)
            Snackbar
                .make(it, getString(R.string.snap_text_record_added), 700)
                .setAction("Action", null).show()
        }

        // Delete a record on negative floating button click
        view.findViewById<FloatingActionButton>(R.id.fab_delete).setOnClickListener {
            rbViewModel.deleteLastRecord()
            Snackbar
                .make(it, getString(R.string.snap_text_on_rec_delete), 700)
                .setAction("Action", null).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MenuHost).addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        binding.drawerLayout.open()
        menuInflater.inflate(R.menu.main, menu)
        menu.add("Edit RB")
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Needed to have onSupportNavigateUp() called
        // when clicking on the home button after an onMenuItemSelected() override
        if (menuItem.itemId == android.R.id.home) {
            return false
        }
        return true
    }

    companion object{
        private const val ROADBOOK_DB_PATH: String = "Sheet-shift"
    }
}