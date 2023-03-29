package com.github.factotum_sdp.factotum.ui.roadbook

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

        // Set ItemTouchHelper Callback to manage Drag&Drop and SwipeRight edition
        setItemTHCallBack(false, false, false)

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
        val rbDD = menu.findItem(R.id.rbDragDrop)
        val rbSL = menu.findItem(R.id.rbSwipeLDeletion)
        val rbSR = menu.findItem(R.id.rbSwipeREdition)
        rbDD.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            setItemTHCallBack(it.isChecked, rbSL.isChecked, rbSR.isChecked)
            false
        }
        rbSL.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            setItemTHCallBack(rbDD.isChecked, rbSL.isChecked, rbSR.isChecked)
            false
        }
        rbSR.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            setItemTHCallBack(rbDD.isChecked, rbSL.isChecked, rbSR.isChecked)
            false
        }
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Needed to have the onSupportNavigateUp() called
        // when clicking on the home button after an onMenuItemSelected() override
        if (menuItem.itemId == android.R.id.home) {
            return false
        }
        return true
    }
    private fun setItemTHCallBack(isDragNDrop: Boolean, isSwipeLeft: Boolean, isSwipeRight: Boolean) {

        val itemTHCallback = object : RoadBookTHCallback() {
            override fun getRbViewModel(): RoadBookViewModel {
                return rbViewModel
            }
            override fun getHost(): Fragment {
                return requireParentFragment()
            }
            override fun getRecyclerView(): RecyclerView {
                return rbRecyclerView
            }
            override fun getDragDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if(!isDragNDrop)
                    return ItemTouchHelper.ACTION_STATE_IDLE
                return super.getDragDirs(recyclerView, viewHolder)
            }
            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (!isSwipeLeft && !isSwipeRight)
                    return ItemTouchHelper.ACTION_STATE_IDLE
                var swipeDirs = ItemTouchHelper.ACTION_STATE_SWIPE
                if (isSwipeLeft) swipeDirs += ItemTouchHelper.LEFT
                if (isSwipeRight) swipeDirs += ItemTouchHelper.RIGHT
                return swipeDirs
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTHCallback)
        itemTouchHelper.attachToRecyclerView(rbRecyclerView)
    }

    companion object{
        private const val ROADBOOK_DB_PATH: String = "Sheet-shift"
    }

    /** Only use that access for testing purpose */
    fun getRBViewModelForTest(): RoadBookViewModel {
        return rbViewModel
    }
}