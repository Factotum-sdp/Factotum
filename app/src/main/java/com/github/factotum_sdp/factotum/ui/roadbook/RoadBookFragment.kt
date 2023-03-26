package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.widget.MultiAutoCompleteTextView.Tokenizer
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


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
            val rec = DestinationRecords.RECORD_TO_ADD
            rbViewModel.addRecord(rec.clientID, rec.timeStamp, rec.waitingTime, rec.rate, rec.actions)
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
        menuInflater.inflate(R.menu.main, menu)
        menu.add(getString(R.string.rbLabelDragDrop))
        menu.add(getString(R.string.rbLabelSwipeEdition))
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
                ItemTouchHelper.RIGHT or ItemTouchHelper.ACTION_STATE_SWIPE ) {

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
                    ItemTouchHelper.RIGHT -> {
                        buildAlertDialog(viewHolder).show()
                    }
                }
            }

            // Dialog for editing a DestinationRecord
            private fun buildAlertDialog(viewHolder: RecyclerView.ViewHolder): AlertDialog.Builder {
                val position = viewHolder.absoluteAdapterPosition
                val rec = rbViewModel.recordsListState.value!![position]

                val builder = AlertDialog.Builder(context)
                builder.setCancelable(false)
                val dialogView = requireActivity().layoutInflater.inflate(R.layout.edit_record_custom_dialog, null)
                builder.setView(dialogView)

                val clientIDView = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteClientID)
                clientIDView.setText(rec.clientID)

                // Here will need to get the clients IDs through a ViewModel instance initiated in the mainActivity and representing all the clients
                var lsClientIDs = DestinationRecords.RECORDS.map { it.clientID }.toSet()
                lsClientIDs = lsClientIDs.plus(DestinationRecords.RECORD_TO_ADD.clientID)
                val clientIDsAdapter = ArrayAdapter(requireContext(), R.layout.pop_auto_complete_client_id, lsClientIDs.toList())
                clientIDView.setAdapter(clientIDsAdapter)
                clientIDView.threshold = 1

                val timestampView = dialogView.findViewById<EditText>(R.id.editTextTimestamp)
                val focusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        TimePickerDialog(
                            context,
                            {   // OnSetListener argument
                                _, hourOfDay, minutes ->
                                val cal = Calendar.getInstance()
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                cal.set(Calendar.MINUTE, minutes)
                                timestampView.setText(SimpleDateFormat.getTimeInstance().format(cal.time))
                            },
                            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                            Calendar.getInstance().get(Calendar.MINUTE),
                            false)
                            .show()
                    }
                }
                timestampView.onFocusChangeListener = focusChangeListener
                val dateFormat = rec.timeStamp?.let { SimpleDateFormat.getTimeInstance().format(it) } ?: ""
                timestampView.setText(dateFormat)

                val waitingTimeView = dialogView.findViewById<EditText>(R.id.editTextWaitingTime)
                waitingTimeView.setText(rec.waitingTime.toString())

                val rateView = dialogView.findViewById<EditText>(R.id.editTextRate)
                rateView.setText(rec.rate.toString())

                val actionsView = dialogView.findViewById<MultiAutoCompleteTextView>(R.id.multiAutoCompleteActions)
                actionsView.setText(rec.actions.toString().removePrefix("[").removeSuffix("]"))
                val actionsAdapter = ArrayAdapter(requireContext(), R.layout.pop_auto_complete_action, DestinationRecord.Action.values())
                actionsView.setAdapter(actionsAdapter)
                actionsView.setTokenizer(CommaTokenizer())
                actionsView.threshold = 1

                val notesView = dialogView.findViewById<EditText>(R.id.editTextNotes)
                //Need to set notesView after added to a DestinationRecord

                builder.setNegativeButton(getString(R.string.editDialogCancelB)) { _, _ ->
                    // Update the screen, no changes to back-end
                    rbRecyclerView.adapter!!.notifyItemChanged(position)
                }
                builder.setPositiveButton(getString(R.string.editDialogUpdateB)) { _, _ ->
                    var recHasChanged = false
                    try {
                        recHasChanged =
                            rbViewModel.editRecord(
                                position,
                                clientIDView.text.toString(),
                                timestampFromString(timestampView.text.toString()),
                                waitingTimeView.text.toString().toInt(),
                                rateView.text.toString().toInt(),
                                actionsFromString(actionsView.text.toString())
                            )
                    } catch(e: java.lang.Exception) {
                        Snackbar
                            .make(viewHolder.itemView, "Wrong format. Edition canceled", 1400)
                            .setAction("Action", null).show()
                    }
                    if (!recHasChanged)
                        rbRecyclerView.adapter!!.notifyItemChanged(position)
                }
                return builder
            }

            private fun timestampFromString(userEntry: String): Date? {
                if (userEntry.isEmpty())
                    return null
                return SimpleDateFormat.getTimeInstance().parse(userEntry)
            }

            private fun actionsFromString(actions: String): List<DestinationRecord.Action> {
                return actions
                    .split(",")
                    .map { DestinationRecord.Action.fromString(it.trim().lowercase()) }
                    .filter { it != DestinationRecord.Action.UNKNOWN }
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