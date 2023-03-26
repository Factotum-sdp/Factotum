package com.github.factotum_sdp.factotum.ui.roadbook

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class DRecordAlertDialogBuilder(context: Context?,
                                private val host: Fragment,
                                private val rbViewModel: RoadBookViewModel,
                                private val rbRecyclerView: RecyclerView) : AlertDialog.Builder(context) {

    private val clientIDView: AutoCompleteTextView
    private val timestampView: EditText
    private val waitingTimeView: EditText
    private val rateView: EditText
    private val actionsView: MultiAutoCompleteTextView
    private val notesView: EditText

    init {
        val inflater = host.requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.edit_record_custom_dialog, null)
        setCancelable(false)
        setView(dialogView)

        clientIDView = dialogView.findViewById(R.id.autoCompleteClientID)
        timestampView = dialogView.findViewById(R.id.editTextTimestamp)
        waitingTimeView = dialogView.findViewById(R.id.editTextWaitingTime)
        rateView = dialogView.findViewById(R.id.editTextRate)
        actionsView = dialogView.findViewById(R.id.multiAutoCompleteActions)
        notesView = dialogView.findViewById(R.id.editTextNotes)
        //todo Need to set notesView after added to a DestinationRecord
    }

    override fun create(): AlertDialog {
        setClientIDsAdapter()
        setTimestampTimePicker()
        setActionsAdapter()

        return super.create()
    }

    fun forNewRecordEdition(): DRecordAlertDialogBuilder {
        setViewModelUpdates({ _, _ ->
            // On negative button do nothing
        }, { _, _ ->
            // On positive button :
            try {
                rbViewModel.addRecord(
                    clientIDView.text.toString(),
                    timestampFromString(timestampView.text.toString()),
                    waitingTimeView.text.toString().toInt(),
                    rateView.text.toString().toInt(),
                    actionsFromString(actionsView.text.toString())
                )
                Snackbar
                    .make(host.requireView(), host.getString(R.string.snap_text_record_added), 700)
                    .setAction("Action", null).show()
            } catch(e: java.lang.Exception) {
                Snackbar
                    .make(host.requireView(), "Wrong format. Edition canceled", 1400)
                    .setAction("Action", null).show()
            }
        })
        return this
    }

    fun forExistingRecordEdition(viewHolder: ViewHolder): DRecordAlertDialogBuilder {
        val position = viewHolder.absoluteAdapterPosition
        val rec = rbViewModel.recordsListState.value!![position]

        bindRecordDataToEditFields(rec)

        setViewModelUpdates({ _, _ ->
            // On negative button : Update the screen, no changes to back-end
            rbRecyclerView.adapter!!.notifyItemChanged(position)
        },{ _, _ ->
            // On positive button : Try to edit the record
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
        })
        return this
    }

    private fun bindRecordDataToEditFields(rec: DestinationRecord) {
        clientIDView.setText(rec.clientID)

        val dateFormat = rec.timeStamp?.let { SimpleDateFormat.getTimeInstance().format(it) } ?: ""
        timestampView.setText(dateFormat)

        waitingTimeView.setText(rec.waitingTime.toString())
        rateView.setText(rec.rate.toString())
        actionsView.setText(rec.actions.toString().removePrefix("[").removeSuffix("]"))
    }

    private fun setViewModelUpdates(onNegativeButton: DialogInterface.OnClickListener,
                                    onPositiveButton: DialogInterface.OnClickListener) {
        setNegativeButton(host.getString(R.string.editDialogCancelB), onNegativeButton)
        setPositiveButton(host.getString(R.string.editDialogUpdateB), onPositiveButton)
    }

    // Here we will need to get the clients IDs through a ViewModel instance
    // initiated in the mainActivity and representing all the clients
    private fun setClientIDsAdapter() {
        var lsClientIDs = DestinationRecords.RECORDS.map { it.clientID }.toSet()
        lsClientIDs = lsClientIDs.plus(DestinationRecords.RECORD_TO_ADD.clientID)
        val clientIDsAdapter = ArrayAdapter(host.requireContext(),
                                            R.layout.pop_auto_complete_client_id,
                                                    lsClientIDs.toList())
        clientIDView.setAdapter(clientIDsAdapter)
        clientIDView.threshold = 1
    }

    private fun setTimestampTimePicker() {
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
    }

    private fun setActionsAdapter() {
        val actionsAdapter = ArrayAdapter(host.requireContext(),
                                            R.layout.pop_auto_complete_action,
                                                DestinationRecord.Action.values())
        actionsView.setAdapter(actionsAdapter)
        actionsView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        actionsView.threshold = 1
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