package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


/**
 * A fragment representing a RoadBook which is a list of DestinationRecord
 */
class RoadBookFragment : Fragment(), MenuProvider {

    private lateinit var rbViewModel: RoadBookViewModel
    private lateinit var rbRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_roadbook, container, false)
        val adapter = RoadBookViewAdapter()
        val dbRef = MainActivity.getDatabase().reference.child(ROADBOOK_DB_PATH)
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

        // Set ItemTouchHelper Callback to manage Drag&Drop and SwipeRight edition
        val itemTouchHelper = ItemTouchHelper(itemTHCallback)
        itemTouchHelper.attachToRecyclerView(rbRecyclerView)

        return view
    }
    override fun onPause() {
        rbViewModel.backUp()
        super.onPause()
    }

    private fun setRoadBookEvents(rbViewModel: RoadBookViewModel, view: View) {
        // Add record on positive floating button click
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            DRecordAlertDialogBuilder(context, requireParentFragment(),
                                            rbViewModel, rbRecyclerView)
                .forNewRecordEdition()
                .show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main, menu)
        menu.add(getString(R.string.rb_label_dragdrop))
        menu.add(getString(R.string.rb_label_swipel_deletion))
        menu.add(getString(R.string.rb_label_swiper_edition))
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Needed to have the onSupportNavigateUp() called
        // when clicking on the home button after an onMenuItemSelected() override
        if (menuItem.itemId == android.R.id.home) {
            return false
        }
        return true
    }

    /** ItemTouchHelper Callback for Drag & Drop and Swipe-right edition */
    private val itemTHCallback =
        object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT or ItemTouchHelper.ACTION_STATE_SWIPE ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                try {
                    val fromPosition = viewHolder.absoluteAdapterPosition
                    val toPosition = target.absoluteAdapterPosition

                    // Only the front-end is updated when drag-travelling for a smoother UX
                    recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

                    // Back-end swap job not published here, @see pushSwapsResult() call
                    if(toPosition < fromPosition) {
                        rbViewModel.swapRecords(toPosition, fromPosition - 1)
                    } else {
                        rbViewModel.swapRecords(fromPosition, toPosition - 1)
                    }

                    return true
                } catch (e: java.lang.Exception){
                    return false
                }
            }

            // Hacky move to update the ViewModel only when the Drag&Drop has ended
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                    // Push only if the STATE_IDLE arrives after a Drag and Drop move
                    rbViewModel.pushSwapsResult()
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.RIGHT -> { // Record Edition
                        DRecordAlertDialogBuilder(context, requireParentFragment(),
                                                    rbViewModel, rbRecyclerView)
                            .forExistingRecordEdition(viewHolder)
                            .show()
                    }
                    ItemTouchHelper.LEFT -> { // Record Deletion
                        val position = viewHolder.absoluteAdapterPosition
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(getString(R.string.delete_dialog_title))
                        builder.setCancelable(false)
                        builder.setPositiveButton(getString(R.string.delete_confirm_button_label)) { _, _ ->
                            rbViewModel.deleteRecordAt(position)
                            Snackbar
                                .make(requireView(), getString(R.string.snap_text_on_rec_delete), 700)
                                .setAction("Action", null).show()
                        }
                        builder.setNegativeButton(getString(R.string.delete_cancel_button_label)) { _, _ ->
                            rbRecyclerView.adapter!!.notifyItemChanged(position)
                        }
                        builder.show()
                    }
                }
            }
        }
    companion object{
        private const val ROADBOOK_DB_PATH: String = "Sheet-shift"
    }

    /** Only use that access for testing purpose */
    fun getRBViewModelForTest(): RoadBookViewModel {
        return rbViewModel
    }
}