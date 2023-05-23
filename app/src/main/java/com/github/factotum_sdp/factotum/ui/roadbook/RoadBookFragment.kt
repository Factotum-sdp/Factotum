package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.ItemTouchHelper.*
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.models.Contact
import com.github.factotum_sdp.factotum.models.DestinationRecord.Action.DELIVER
import com.github.factotum_sdp.factotum.models.DestinationRecord.Action.PICK
import com.github.factotum_sdp.factotum.models.RoadBookPreferences
import com.github.factotum_sdp.factotum.models.Shift
import com.github.factotum_sdp.factotum.preferencesDataStore
import com.github.factotum_sdp.factotum.repositories.RoadBookPreferencesRepository
import com.github.factotum_sdp.factotum.repositories.RoadBookRepository
import com.github.factotum_sdp.factotum.repositories.ShiftRepository
import com.github.factotum_sdp.factotum.repositories.ShiftRepository.Companion.DELIVERY_LOG_DB_PATH
import com.github.factotum_sdp.factotum.roadBookDataStore
import com.github.factotum_sdp.factotum.shiftDataStore
import com.github.factotum_sdp.factotum.ui.bag.BagViewModel
import com.github.factotum_sdp.factotum.ui.bag.PackCreationDialogBuilder
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.settings.SettingsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

private const val ON_DESTINATION_RADIUS = 15.0
private const val NO_USER_FOR_DB_PATH = "no_user"

/**
 * A fragment representing a RoadBook which is a list of DestinationRecord
 */
class RoadBookFragment : Fragment(), MenuProvider {

    private lateinit var rbRecyclerView: RecyclerView
    private val settings: SettingsViewModel by activityViewModels()
    private val rbViewModel: RoadBookViewModel by activityViewModels {
        RoadBookViewModel.RoadBookViewModelFactory(
            RoadBookRepository(
                FirebaseInstance.getDatabase().reference.child(ROADBOOK_DB_PATH),
                userViewModel.loggedInUser.value?.name ?: NO_USER_FOR_DB_PATH,
                requireContext().roadBookDataStore
            ),
            ShiftRepository(
                FirebaseInstance.getDatabase().reference.child(DELIVERY_LOG_DB_PATH),
                requireContext().shiftDataStore
            ),
        )
    }

    // Following fields are loaded in onCreateMenu()
    private lateinit var fragMenu: Menu
    private lateinit var dragAndDropButton: MenuItem
    private lateinit var swipeLeftButton: MenuItem
    private lateinit var swipeRightButton: MenuItem
    private lateinit var touchClickButton: MenuItem
    private lateinit var showArchivedButton: MenuItem

    private var usePreferences = false
    private val userViewModel: UserViewModel by activityViewModels()
    private val contactsViewModel : ContactsViewModel by activityViewModels()
    private val bagViewModel: BagViewModel by activityViewModels()

    private var timestampedIDs: Map<String, Date> = mapOf()
    private lateinit var currentContacts: List<Contact>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_roadbook, container, false)
        val adapter = RoadBookViewAdapter(setOnDRecordClickListener())

        // Observe the RoadBook ViewModel, to detect data changes
        // and update the displayed RecyclerView accordingly
        rbViewModel.recordsListState.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        rbViewModel.timestampedRecords.observe(viewLifecycleOwner) {
            handleTimestampChange(it)
        }

        // Set events that triggers change in the RoadBook ViewModel
        setRoadBookEvents(rbViewModel, view)

        // Set up the RoadBook RecyclerView
        rbRecyclerView = view.findViewById(R.id.list)
        rbRecyclerView.layoutManager = LinearLayoutManager(context)
        rbRecyclerView.adapter = adapter
        contactsViewModel.contacts.observe(viewLifecycleOwner) {
            currentContacts = it
        }

        setLiveLocationEvent()

        return view
    }

    private fun handleTimestampChange(newTimestampedID: Map<String, Date>) {
        if(timestampedIDs.size == newTimestampedID.size) {
            val changedIDs = timestampedIDs.filter { newTimestampedID.getValue(it.key) != it.value }
                changedIDs.forEach {
                bagViewModel.adjustTimestampOf(it.value, it.key)
            }
        } else if(timestampedIDs.size < newTimestampedID.size) {
            val newIDs = newTimestampedID.keys.subtract(timestampedIDs.keys)
            newIDs.forEach {
                handleNewTimestampedRecord(it,newTimestampedID.getValue(it))
            }
        } else { // some timestamp has been removed
            val removedIDs = timestampedIDs.keys.subtract(newTimestampedID.keys)
            removedIDs.forEach {
                bagViewModel.removedDestinationRecord(it)
            }
        }

        timestampedIDs = newTimestampedID
    }

    private fun handleNewTimestampedRecord(destID: String, timestamp: Date) {
        val record = rbViewModel.recordsListState.value?.getDestinationRecordFromID(destID)
        record?.actions?.forEach {
            if(it == DELIVER) {
                bagViewModel.arrivedOnDestinationRecord(destID, record.clientID, timestamp)
            }
            if(it == PICK) {
                PackCreationDialogBuilder(
                    requireContext(),
                    this,
                    destID,
                    record.clientID,
                    timestamp,
                    bagViewModel,
                    contactsViewModel
                ).show()
            }
        }
    }
    override fun onPause() {
        rbViewModel.backUp()
        saveButtonStates()
        super.onPause()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setLiveLocationEvent() {
        userViewModel.locationTrackingHandler.setOnLocationUpdate { currentLocation ->
            rbViewModel.nextDestination()?.let {
                val destination = clientLocation(it.clientID)
                destination?.let { dest ->
                    if(onDestinationPlace(currentLocation, dest)) {
                        val cal = Calendar.getInstance()
                        rbViewModel.timeStampARecord(cal.time, it)
                    }
                }
            }
        }
    }

    private fun onDestinationPlace(current: Location, destination: Location): Boolean {
        val distance = current.distanceTo(destination)
        return distance <= ON_DESTINATION_RADIUS // Remove constant
    }

    private fun clientLocation(clientID: String): Location? {
        val contact = currentContacts.firstOrNull {
            it.username == clientID
        } ?: return null
        return contact.getLocation()
    }

    private fun setOnDRecordClickListener(): (String) -> View.OnClickListener {
        return {
            View.OnClickListener { v ->
                if (isTouchClickEnabled()) {
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
        // Add record on floating "addition" button click
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            DRecordEditDialogBuilder(
                context, requireParentFragment(),
                rbViewModel, rbRecyclerView, contactsViewModel
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

        dragAndDropButton = menu.findItem(R.id.rbDragDrop)
        swipeLeftButton = menu.findItem(R.id.rbSwipeLDeletion)
        swipeRightButton = menu.findItem(R.id.rbSwipeREdition)
        touchClickButton = menu.findItem(R.id.rbTouchClick)
        showArchivedButton = menu.findItem(R.id.showArchived)

        val dataStore = requireContext().preferencesDataStore
        rbViewModel.setPreferencesRepository(RoadBookPreferencesRepository(dataStore))
        loadDefaultPreferencesButtonState()

        settings.settingsLiveData.observe(viewLifecycleOwner) {
            usePreferences = it.useRoadBookPreferences
        }
        rbViewModel.initialPreferences().observe(viewLifecycleOwner) {
            if(usePreferences) {
                loadPreferencesButtonState(it)
            }
            showOrHideArchived(isShowArchivedEnabled())
        }

        // Events and displays according to the preferences state
        setPrefButtonsOnClickListeners(dragAndDropButton, swipeLeftButton, swipeRightButton, touchClickButton)
        showArchivedButton.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            showOrHideArchived(isShowArchivedEnabled())
            true
        }
        setLiveLocationSwitch(fragMenu)
        setRefreshButtonListener(fragMenu)
        setBagButtonListener(fragMenu)
        setEndShiftButtonListener(fragMenu)

        // Only at menu initialization
        val itemTouchHelper = ItemTouchHelper(newItemTHCallBack())
        itemTouchHelper.attachToRecyclerView(rbRecyclerView)
    }

    private fun loadDefaultPreferencesButtonState() {
        val defaultPreferences = RoadBookPreferences(
            enableReordering = true,
            enableArchivingAndDeletion = true,
            enableEdition = true,
            enableDetailsAccess = false,
            showArchived = false
        )
        loadPreferencesButtonState(defaultPreferences)
    }

    private fun loadPreferencesButtonState(preferences: RoadBookPreferences) {
        dragAndDropButton.isChecked = preferences.enableReordering
        swipeLeftButton.isChecked = preferences.enableArchivingAndDeletion
        swipeRightButton.isChecked = preferences.enableEdition
        touchClickButton.isChecked = preferences.enableDetailsAccess
        showArchivedButton.isChecked = preferences.showArchived
    }

    private fun setPrefButtonsOnClickListeners(vararg preferenceButtons: MenuItem) {
        preferenceButtons.forEach { button ->
            button.setOnMenuItemClickListener {
                it.isChecked = !it.isChecked
                true
            }
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
            it.isChecked = userViewModel.locationTrackingHandler.isTrackingEnabled.value
        }
        locationSwitch!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                userViewModel.locationTrackingHandler.startLocationService(requireContext(), requireActivity())
            else if (lifecycle.currentState == Lifecycle.State.RESUMED && this.isVisible) {
                userViewModel.locationTrackingHandler.stopLocationService(requireContext(), requireActivity())
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

    private fun setBagButtonListener(menu: Menu) {
        val bagButton = menu.findItem(R.id.bag_button)
        bagButton.setOnMenuItemClickListener {
            this.findNavController().navigate(R.id.bagFragment)
            true
        }
    }

    private fun setEndShiftButtonListener(fragMenu: Menu) {
        val endShiftButton = fragMenu.findItem(R.id.finish_shift)
        endShiftButton.setOnMenuItemClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage(R.string.finish_shift_alert_question)
                .setPositiveButton(R.string.end_shift) { _, _ ->
                    rbViewModel.recordsListState.value?.let { recordList ->
                        userViewModel.loggedInUser.value?.let { user->
                            val currentShift = Shift(
                            Date(),
                            user,
                            recordList
                        )
                            rbViewModel.logShift(currentShift)
                            Toast.makeText(
                                requireContext(),
                                R.string.shift_ended,
                                Toast.LENGTH_SHORT
                            ).show() } ?: Log.w("RoadBookFragment", "User is null, cannot log shift")
                    } ?: Log.w("RoadBookFragment", "Record list is null, cannot log shift")
                }
                .setNegativeButton(R.string.decline_end_shift) { dialog, _ ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.setTitle(R.string.end_shift_dialog_title)
            alert.show()
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

            override fun getContactsViewModel(): ContactsViewModel {
                return contactsViewModel
            }

            override fun getDragDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int { // Set IDLE setting to disable drag up or down detection
                if (!isDragAndDropEnabled())
                    return ACTION_STATE_IDLE
                return super.getDragDirs(recyclerView, viewHolder)
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                var swipeFlags = ACTION_STATE_SWIPE
                if (isSwipeLeftEnabled()) swipeFlags = swipeFlags or LEFT
                if (isSwipeRightEnabled()) swipeFlags = swipeFlags or RIGHT

                return swipeFlags
            }
        }
        return itemTHCallback
    }

    private fun isDragAndDropEnabled(): Boolean {
        return dragAndDropButton.isChecked
    }
    private fun isTouchClickEnabled(): Boolean {
        return touchClickButton.isChecked
    }
    private fun isSwipeLeftEnabled(): Boolean {
        return swipeLeftButton.isChecked
    }
    private fun isSwipeRightEnabled(): Boolean {
        return swipeRightButton.isChecked
    }
    private fun isShowArchivedEnabled(): Boolean {
        return showArchivedButton.isChecked
    }

    companion object {
        const val ROADBOOK_DB_PATH = "Sheet-shift" //change to "Sheet-shift2" for manual testing
        const val DEST_ID_NAV_ARG_KEY = "destID"
    }

    /** Only use that access for testing purpose */
    fun getRBViewModelForTest(): RoadBookViewModel {
        return rbViewModel
    }

    override fun onDestroy() {
        userViewModel.locationTrackingHandler.stopLocationService(requireContext(), requireActivity())
        super.onDestroy()
    }

    override fun onDestroyView() {
        saveButtonStates()
        super.onDestroyView()
    }

    private fun saveButtonStates() {
        val currentPreferences = RoadBookPreferences(
            enableReordering = isDragAndDropEnabled(),
            enableArchivingAndDeletion = isSwipeLeftEnabled(),
            enableEdition = isSwipeRightEnabled(),
            enableDetailsAccess = isTouchClickEnabled(),
            showArchived = isShowArchivedEnabled()
        )
        rbViewModel.updateRoadBookPreferences(currentPreferences)
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
