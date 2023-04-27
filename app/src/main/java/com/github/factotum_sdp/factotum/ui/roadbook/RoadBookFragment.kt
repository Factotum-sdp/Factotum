package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.ItemTouchHelper.*
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.RoadBookPreferences
import com.github.factotum_sdp.factotum.repositories.RoadBookPreferencesRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

private const val ROADBOOK_PREFERENCES_NAME = "factotum_preferences"
private val Context.dataStore by preferencesDataStore(
    name = ROADBOOK_PREFERENCES_NAME
)

/**
 * A fragment representing a RoadBook which is a list of DestinationRecord
 */
class RoadBookFragment : Fragment(), MenuProvider {

    private lateinit var rbRecyclerView: RecyclerView
    private lateinit var fragMenu: Menu

    private val rbViewModel: RoadBookViewModel by activityViewModels {
        RoadBookViewModel.RoadBookViewModelFactory(
            MainActivity.getDatabase().reference.child(ROADBOOK_DB_PATH)
        )
    }
    private val locationTrackingHandler: LocationTrackingHandler = LocationTrackingHandler()

    // Checked OptionMenu States with unused init values
    // overridden by the device saved SharedPreference or defaults values in onCreateMenu()
    private var isSLEnabled = true
    private var isSREnabled = true
    private var isDDropEnabled = true
    private var isTClickEnabled = false
    private var isShowArchivedEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_roadbook, container, false)
        val adapter = RoadBookViewAdapter(setOnDRecordClickListener())

        // Observe the roadbook ViewModel, to detect data changes
        // and update the displayed RecyclerView accordingly
        rbViewModel.recordsListState.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // Set events that triggers change in the roadbook ViewModel
        setRoadBookEvents(rbViewModel, view)

        // Set up the RoadBook RecyclerView
        rbRecyclerView = view.findViewById(R.id.list)
        rbRecyclerView.layoutManager = LinearLayoutManager(context)
        rbRecyclerView.adapter = adapter

        locationTrackingHandler.setOnLocationUpdate {
            val cal = Calendar.getInstance()
            rbViewModel.timestampNextDestinationRecord(cal.time)
        }

        return view
    }

    override fun onPause() {
        rbViewModel.backUp()
        saveButtonStates()
        super.onPause()
    }

    private fun setOnDRecordClickListener(): (String) -> View.OnClickListener {
        return {
            View.OnClickListener { v ->
                if (isTClickEnabled) {
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
            DRecordEditDialogBuilder(
                context, requireParentFragment(),
                rbViewModel, rbRecyclerView
            )
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

        val dragAndDropButton = menu.findItem(R.id.rbDragDrop)
        val swipeLeftButton = menu.findItem(R.id.rbSwipeLDeletion)
        val swipeRightButton = menu.findItem(R.id.rbSwipeREdition)
        val touchClickButton = menu.findItem(R.id.rbTouchClick)
        val showArchivedButton = menu.findItem(R.id.showArchived)

        val dStore = requireContext().dataStore // todo set here global setting option of using Roadbook Preferences
        rbViewModel.setPreferencesRepository(RoadBookPreferencesRepository(dStore))
        val initialPreferences = RoadBookPreferences( // or defaults
            enableReordering = true,
            enableArchivingAndDeletion = true,
            enableEdition = true,
            enableDetailsAccess = false,
            showArchived = false
        )
        dragAndDropButton.isChecked = initialPreferences.enableReordering
        swipeLeftButton.isChecked = initialPreferences.enableArchivingAndDeletion
        swipeRightButton.isChecked = initialPreferences.enableEdition
        touchClickButton.isChecked = initialPreferences.enableDetailsAccess
        showArchivedButton.isChecked = initialPreferences.showArchived
        isDDropEnabled = dragAndDropButton.isChecked
        isSLEnabled = swipeLeftButton.isChecked
        isSREnabled = swipeRightButton.isChecked
        isTClickEnabled = touchClickButton.isChecked
        isShowArchivedEnabled = showArchivedButton.isChecked

        rbViewModel.initialPreferences().observe(viewLifecycleOwner) {
            dragAndDropButton.isChecked = it.enableReordering
            swipeLeftButton.isChecked = it.enableArchivingAndDeletion
            swipeRightButton.isChecked = it.enableEdition
            touchClickButton.isChecked = it.enableDetailsAccess
            showArchivedButton.isChecked = it.showArchived
            isDDropEnabled = dragAndDropButton.isChecked
            isSLEnabled = swipeLeftButton.isChecked
            isSREnabled = swipeRightButton.isChecked
            isTClickEnabled = touchClickButton.isChecked
            isShowArchivedEnabled = showArchivedButton.isChecked
        }


        showOrHideArchived(isShowArchivedEnabled)
        setRBonClickListeners(dragAndDropButton, swipeLeftButton, swipeRightButton, touchClickButton, showArchivedButton)
        setLiveLocationSwitch(fragMenu)
        setRefreshButtonListener(fragMenu)

        // Only at menu initialization
        val itemTouchHelper = ItemTouchHelper(newItemTHCallBack())
        itemTouchHelper.attachToRecyclerView(rbRecyclerView)
    }

    private fun setRBonClickListeners(
        rbDD: MenuItem, rbSL: MenuItem, rbSR: MenuItem,
        rbTC: MenuItem, rbSA: MenuItem
    ) {
        rbDD.setOnMenuItemClickListener {
            isDDropEnabled = !isDDropEnabled
            it.isChecked = isDDropEnabled
            true
        }
        rbSL.setOnMenuItemClickListener {
            isSLEnabled = !isSLEnabled
            it.isChecked = isSLEnabled
            true
        }
        rbSR.setOnMenuItemClickListener {
            isSREnabled = !isSREnabled
            it.isChecked = isSREnabled
            true
        }
        rbTC.setOnMenuItemClickListener {
            isTClickEnabled = !isTClickEnabled
            it.isChecked = isTClickEnabled
            true
        }
        rbSA.setOnMenuItemClickListener {
            isShowArchivedEnabled = !isShowArchivedEnabled
            it.isChecked = isShowArchivedEnabled
            showOrHideArchived(it.isChecked)
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

    private fun setLiveLocationSwitch(menu: Menu) {
        val locationMenu = menu.findItem(R.id.location_switch)
        val locationSwitch =
            locationMenu.actionView!!.findViewById<SwitchCompat>(R.id.menu_item_switch)
        locationSwitch?.let {// Load current state to set the switch item
            it.isChecked = locationTrackingHandler.isTrackingEnabled()
        }
        locationSwitch!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                locationTrackingHandler.startLocationService(requireContext(), requireActivity())
            else if (lifecycle.currentState == Lifecycle.State.RESUMED && this.isVisible) {
                locationTrackingHandler.stopLocationService(requireContext(), requireActivity())
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRefreshButtonListener(menu: Menu) {
        val refreshButton = menu.findItem(R.id.refresh_button)
        refreshButton.setOnMenuItemClickListener {
            rbRecyclerView.adapter?.notifyDataSetChanged()
            true
        }
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

    companion object {
        private const val ROADBOOK_DB_PATH = "Sheet-shift"
        const val DEST_ID_NAV_ARG_KEY = "destID"
    }

    /** Only use that access for testing purpose */
    fun getRBViewModelForTest(): RoadBookViewModel {
        return rbViewModel
    }

    override fun onDestroy() {
        locationTrackingHandler.stopLocationService(requireContext(), requireActivity())
        super.onDestroy()
    }

    override fun onDestroyView() {
        saveButtonStates()
        super.onDestroyView()
    }

    private fun saveButtonStates() {
        rbViewModel.updateReordering(isDDropEnabled)
        rbViewModel.updateDeletionOrArchiving(isSLEnabled)
        rbViewModel.updateEdition(isSREnabled)
        rbViewModel.updateDetailsAccess(isTClickEnabled)
        rbViewModel.updateShowArchived(isShowArchivedEnabled)
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