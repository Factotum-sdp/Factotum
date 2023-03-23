package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.DestinationRecord
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
    private lateinit var rbRecyclerView: RecyclerView

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
        rbRecyclerView = view.findViewById(R.id.list)
        rbRecyclerView.layoutManager = LinearLayoutManager(context)
        rbRecyclerView.adapter = adapter

        // Set ItemTouchHelper Callback to manage elaborate screen touch events
        val itemTouchHelper = ItemTouchHelper(itemTHCallback)
        itemTouchHelper.attachToRecyclerView(rbRecyclerView)

        return view
    }

    private val itemTHCallback =
        object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT or ItemTouchHelper.ACTION_STATE_SWIPE ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                try {
                    val fromPosition: Int = viewHolder.absoluteAdapterPosition
                    val toPosition: Int = target.absoluteAdapterPosition

                    recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

                    return true
                } catch (e: java.lang.Exception){
                    return false
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when(direction){
                    ItemTouchHelper.RIGHT -> {
                        val recordVH = viewHolder as RoadBookViewAdapter.RecordViewHolder
                        val position = recordVH.absoluteAdapterPosition
                        val rec = rbViewModel.recordsListState.value!![position]

                        val editdestId = EditText(context)
                        editdestId.setText(rec.destID)

                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Change destination ID :")
                        builder.setCancelable(true)
                        builder.setView(editdestId)
                        builder.setNegativeButton("cancel") { dialog, which ->
                            rbRecyclerView.adapter!!.notifyItemChanged(position) // Update the screen, no changes to back-end
                        }
                        builder.setPositiveButton("update") { dialog, which ->
                            rbViewModel.editRecord(
                                position,
                                DestinationRecord(
                                    editdestId.text.toString(),
                                    rec.timeStamp,
                                    rec.waitingTime,
                                    rec.rate,
                                    rec.actions
                                )
                            )
                        }
                        builder.show()
                    }
                }
            }

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
        val m = menu.add("Edit RB")
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