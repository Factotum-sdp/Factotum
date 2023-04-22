package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.ItemTouchHelper.*
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * A fragment representing a RoadBook which is a list of DestinationRecord
 */
class RoadBookFragment : Fragment(), MenuProvider {

    private lateinit var rbRecyclerView: RecyclerView
    private lateinit var fragMenu: Menu
    private val rbViewModel: RoadBookViewModel by activityViewModels() {
        RoadBookViewModel.RoadBookViewModelFactory(
            MainActivity.getDatabase().reference.child(ROADBOOK_DB_PATH)
        )
    }

    // Checked OptionMenu States with default values
    // overridden by the device saved SharedPreference
    private var isSLEnabled = true
    private var isSREnabled = true
    private var isDDropEnabled = true
    private var isTClickEnabled = true
    private var isShowArchivedEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_roadbook, container, false)
        val adapter = RoadBookViewAdapter(setOnDRecordClickListener())

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
        saveRadioButtonState(SWIPE_R_SHARED_KEY, R.id.rbSwipeREdition)
        saveRadioButtonState(SWIPE_L_SHARED_KEY, R.id.rbSwipeLDeletion)
        saveRadioButtonState(DRAG_N_DROP_SHARED_KEY, R.id.rbDragDrop)
        saveRadioButtonState(TOUCH_CLICK_SHARED_KEY, R.id.rbTouchClick)
        saveRadioButtonState(SHOW_ARCHIVED_KEY, R.id.showArchived)
        super.onPause()
    }

    private fun setOnDRecordClickListener(): (String) -> View.OnClickListener {
        return {
            View.OnClickListener { v ->
                if(isTClickEnabled) {
                    v
                        ?.findNavController()
                        ?.navigate(R.id.action_roadBookFragment_to_DRecordDetailsFragment,
                            Bundle().apply {
                                putString(DEST_ID_NAV_ARG_KEY, it)
                            }
                        )
                }
            }
        }
    }

    private fun setRoadBookEvents(rbViewModel: RoadBookViewModel, view: View) {
        // Add record on positive floating button click
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            DRecordEditDialogBuilder(context, requireParentFragment(),
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
        val rbSA = menu.findItem(R.id.showArchived)

        // fetch saved States
        fetchRadioButtonState(DRAG_N_DROP_SHARED_KEY, rbDD)
        fetchRadioButtonState(SWIPE_L_SHARED_KEY, rbSL)
        fetchRadioButtonState(SWIPE_R_SHARED_KEY, rbSR)
        fetchRadioButtonState(TOUCH_CLICK_SHARED_KEY, rbTC)
        fetchRadioButtonState(SHOW_ARCHIVED_KEY, rbSA)

        // init globals to saved preference state
        isDDropEnabled = rbDD.isChecked
        isSLEnabled = rbSL.isChecked
        isSREnabled = rbSR.isChecked
        isTClickEnabled = rbTC.isChecked
        isShowArchivedEnabled = rbSA.isChecked

        showOrHideArchived(isShowArchivedEnabled)
        setRBonClickListeners(rbDD, rbSL, rbSR, rbTC, rbSA)

        // Only at menu initialization
        val itemTouchHelper = ItemTouchHelper(newItemTHCallBack())
        itemTouchHelper.attachToRecyclerView(rbRecyclerView)
    }

    private fun setRBonClickListeners(rbDD: MenuItem, rbSL: MenuItem,
                                      rbSR: MenuItem, rbTC: MenuItem, rbSA: MenuItem) {
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
        rbSA.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            isShowArchivedEnabled = !isShowArchivedEnabled
            showOrHideArchived(isShowArchivedEnabled)
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
        // enable or disable touch features proposed by RoadBookTHCallback()
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
        private const val SHOW_ARCHIVED_KEY = "ShowArchived"
        private const val TOUCH_CLICK_SHARED_KEY = "TouchClickButton"
        const val DEST_ID_NAV_ARG_KEY = "destID"
    }

    /** Only use that access for testing purpose */
    fun getRBViewModelForTest(): RoadBookViewModel {
        return rbViewModel
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

    @SuppressLint("NotifyDataSetChanged")
    private fun showOrHideArchived(isShowArchEnabled: Boolean) {
        if (isShowArchEnabled)
            rbViewModel.showArchivedRecords()
        else
            rbViewModel.hideArchivedRecords()
        rbRecyclerView.adapter!!.notifyDataSetChanged()
    }
}