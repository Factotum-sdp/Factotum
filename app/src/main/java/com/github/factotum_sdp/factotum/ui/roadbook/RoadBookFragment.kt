package com.github.factotum_sdp.factotum.ui.roadbook

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * A fragment representing a RoadBook which is a list of DestinationRecord
 */
class RoadBookFragment : Fragment(), MenuProvider {

    private lateinit var rbViewModel: RoadBookViewModel
    private lateinit var rbRecyclerView: RecyclerView
    private lateinit var fragMenu: Menu

    // Checked OptionMenu States with default values
    // overridden by the device saved SharedPreference
    private var isSLEnabled = true
    private var isSREnabled = true
    private var isDDropEnabled = true
    private var isTClickEnabled = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_roadbook, container, false)
        val adapter = RoadBookViewAdapter(setOnDRecordClickListener())
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

        return view
    }

    override fun onPause() {
        rbViewModel.backUp()
        super.onPause()
    }

    private fun setOnDRecordClickListener(): View.OnClickListener {
        return View.OnClickListener { v ->
            if(isTClickEnabled) {
                v
                    ?.findNavController()
                    ?.navigate(R.id.action_roadBookFragment_to_DRecordDetailsFragment)
            }
        }
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
        fragMenu = menu

        val rbDD = menu.findItem(R.id.rbDragDrop)
        val rbSL = menu.findItem(R.id.rbSwipeLDeletion)
        val rbSR = menu.findItem(R.id.rbSwipeREdition)
        val rbTC = menu.findItem(R.id.rbTouchClick)

        // fetch saved States
        fetchRadioButtonState(DRAG_N_DROP_SHARED_KEY, rbDD)
        fetchRadioButtonState(SWIPE_L_SHARED_KEY, rbSL)
        fetchRadioButtonState(SWIPE_R_SHARED_KEY, rbSR)
        fetchRadioButtonState(TOUCH_CLICK_SHARED_KEY, rbTC)

        // init globals to saved preference state
        isDDropEnabled = rbDD.isChecked
        isSLEnabled = rbSL.isChecked
        isSREnabled = rbSR.isChecked
        isTClickEnabled = rbTC.isChecked

        setRBonClickListeners(rbDD, rbSL, rbSR, rbTC)

        // Only at menu initialization
        val itemTouchHelper = ItemTouchHelper(newItemTHCallBack())
        itemTouchHelper.attachToRecyclerView(rbRecyclerView)
    }

    private fun setRBonClickListeners(rbDD: MenuItem, rbSL: MenuItem,
                                      rbSR: MenuItem, rbTC: MenuItem) {
        rbDD.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            isDDropEnabled = !isDDropEnabled
            true
        }
        rbSL.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            isSLEnabled = !isSLEnabled
            true
        }
        rbSR.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            isSREnabled = !isSREnabled
            true
        }
        rbTC.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            isTClickEnabled = !isTClickEnabled
            true
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
    private fun newItemTHCallBack(): Callback {

        // Overriding the getDragDirs and getSwipeDirs() to
        // manage touch features proposed by RoadBookTHCallback()
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
                if (!isDDropEnabled) // setting IDLE setting to disable drag up or down detection
                    return ACTION_STATE_IDLE
                return super.getDragDirs(recyclerView, viewHolder)
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                var swipeFlags = ACTION_STATE_SWIPE
                if (isSLEnabled) swipeFlags = swipeFlags or LEFT
                if (isSREnabled) swipeFlags = swipeFlags or RIGHT

                return swipeFlags
            }

        }
        return itemTHCallback
    }

    companion object{
        private const val ROADBOOK_DB_PATH: String = "Sheet-shift"
        private const val SWIPE_L_SHARED_KEY = "SwipeLeftButton"
        private const val SWIPE_R_SHARED_KEY = "SwipeRightButton"
        private const val DRAG_N_DROP_SHARED_KEY = "DragNDropButton"
        private const val TOUCH_CLICK_SHARED_KEY = "TouchClickButton"
    }

    /** Only use that access for testing purpose */
    fun getRBViewModelForTest(): RoadBookViewModel {
        return rbViewModel
    }

    override fun onDestroyView() {
        saveRadioButtonState(SWIPE_R_SHARED_KEY, R.id.rbSwipeREdition)
        saveRadioButtonState(SWIPE_L_SHARED_KEY, R.id.rbSwipeLDeletion)
        saveRadioButtonState(DRAG_N_DROP_SHARED_KEY, R.id.rbDragDrop)
        saveRadioButtonState(TOUCH_CLICK_SHARED_KEY, R.id.rbTouchClick)
        super.onDestroyView()
    }

    private fun fetchRadioButtonState(sharedKey: String, radioButton: MenuItem) {
        val sp = requireActivity().getSharedPreferences(sharedKey ,Context.MODE_PRIVATE)
        val savedState = sp.getBoolean(sharedKey, true)
        radioButton.setChecked(savedState)
    }
    private fun saveRadioButtonState(sharedKey: String, radioButtonId: Int) {
        val sp = requireActivity().getSharedPreferences(sharedKey,Context.MODE_PRIVATE)
        val edit = sp.edit()
        val radioButton = fragMenu.findItem(radioButtonId)
        radioButton?.let {
            edit.putBoolean(sharedKey, radioButton.isChecked)
            edit.apply()
        }
    }
}