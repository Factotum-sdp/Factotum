package com.github.factotum_sdp.factotum.ui.roadbook

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

/**
 * That Class represent a DialogBuilder specifically designed to build a custom
 * AlertDialog containing all the fields needed to edit a DestinationRecord.
 *
 * He has mainly two possibles usages :
 * - The forNewRecordEdition() : To be call for building a Dialog with all fields empty
 * - The forExistingRecordEdition : To be call for building a Dialog with the fields filled with the current RoadBookViewModel data
 *
 * Finally calling the inherited show() method will call create() and make the new custom concrete AlertDialog
 *
 * @param context: Context? The Context wherein this DialogBuilder is instantiated
 * @param host: Fragment The Fragment which will hold the AlertDialog
 * @param rbViewModel: RoadBookViewModel The RoadBookViewModel storing the observable data of each DestinationRecord
 * @param rbRecyclerView: RecyclerView The RecyclerView as optimization to notify the screen representation when there is no edit changes,
 * thus avoiding posting any useless value in the RoadBookViewModel
 *
 * @constructor : Constructs the DRecordAlertDialogBuilder
 */
class DRecordEditDialogBuilder(
    context: Context?,
    private val host: Fragment,
    private val rbViewModel: RoadBookViewModel,
    private val rbRecyclerView: RecyclerView,
    private val contactsViewModel: ContactsViewModel
) :
    AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Holo_Dialog)) {

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
    }

    override fun create(): AlertDialog {
        setClientIDsAdapter()
        setTimestampTimePicker()
        setActionsAdapter()

        return super.create()
    }

    /**
     * Call it to build a custom AlertDialog with all the fields empty
     * On edit validation, the RoadBookViewModel of this class will be notified by a
     * RoadBookViewModel.addRecord() call
     */
    fun forNewRecordEdition(): DRecordEditDialogBuilder {
        setViewModelUpdates({ _, _ ->
            // On negative button do nothing
        }, { _, _ ->
            // On positive button :
            try {
                rbViewModel.addRecord(
                    clientIDView.text.toString(),
                    timestampFromString(timestampView.text.toString()),
                    waitTimeOrRateFromString(waitingTimeView.text.toString()),
                    waitTimeOrRateFromString(rateView.text.toString()),
                    actionsFromString(actionsView.text.toString()),
                    notesView.text.toString()
                )
                setSnackBar(host.getString(R.string.snap_text_record_added), 700)
            } catch (e: java.lang.Exception) {
                setSnackBar(host.getString(R.string.edit_rejected_snap_label), 1400)
            }
        })
        return this
    }

    /**
     * Call it to build a custom AlertDialog with the fields filled with the current data of the viewHolder
     * passed in argument.
     * On edit validation, the RoadBookViewModel of this class will be notified by a
     * RoadBookViewModel.edit() call
     * On edition format errors or when the content did not change, only the RecyclerView is notified
     * to swipe back the viewHolder of the DestinationRecord.
     *
     * @param viewHolder: ViewHolder The ViewHolder holding the starting DestinationRecord data
     */
    fun forExistingRecordEdition(viewHolder: ViewHolder): DRecordEditDialogBuilder {
        val position = viewHolder.absoluteAdapterPosition
        val rec = rbViewModel.recordsListState.value!![position]
        bindRecordDataToEditFields(rec)

        setViewModelUpdates({ _, _ ->
            // On negative button : Update the screen, no changes to back-end
            rbRecyclerView.adapter!!.notifyItemChanged(position)
        }, { _, _ ->
            val recHasChanged: Boolean
            try { // On positive button : Try to edit the record
                recHasChanged =
                    rbViewModel.editRecordAt(
                        position,
                        clientIDView.text.toString().trim(),
                        timestampFromString(timestampView.text.toString()),
                        waitTimeOrRateFromString(waitingTimeView.text.toString()),
                        waitTimeOrRateFromString(rateView.text.toString()),
                        actionsFromString(actionsView.text.toString()),
                        notesView.text.toString()
                    )
                if (recHasChanged)
                    setSnackBar(context.getString(R.string.edit_confirmed_snap_label), 700)
            } catch (e: java.lang.Exception) {
                setSnackBar(host.getString(R.string.edit_rejected_snap_label), 1400)
            }
            rbRecyclerView.adapter!!.notifyItemChanged(position)
        })
        return this
    }

    private fun setSnackBar(content: String, duration: Int) {
        Snackbar
            .make(host.requireView(), content, duration)
            .setAction("Action", null).show()
    }

    private fun bindRecordDataToEditFields(rec: DestinationRecord) {
        clientIDView.setText(rec.clientID)

        val dateFormat = rec.timeStamp?.let { SimpleDateFormat.getTimeInstance().format(it) } ?: ""
        timestampView.setText(dateFormat)

        waitingTimeView.setText(rec.waitingTime.toString())
        rateView.setText(rec.rate.toString())
        actionsView.setText(rec.actions.toString().removePrefix("[").removeSuffix("]"))
        notesView.setText(rec.notes)
    }

    private fun setViewModelUpdates(
        onNegativeButton: DialogInterface.OnClickListener,
        onPositiveButton: DialogInterface.OnClickListener
    ) {
        setNegativeButton(host.getString(R.string.edit_dialog_cancel_b), onNegativeButton)
        setPositiveButton(host.getString(R.string.edit_dialog_update_b), onPositiveButton)
    }

    // Here we will need to get the clients IDs through a ViewModel instance
    // initiated in the mainActivity and representing all the clients
    private fun setClientIDsAdapter() {
        val lsClientIDs : List<Contact> = emptyList()
        var clientIDsAdapter = ArrayAdapter(
            host.requireContext(),
            R.layout.pop_auto_complete_client_id,
            lsClientIDs.map { it.username }
        )
        clientIDView.setAdapter(clientIDsAdapter)
        clientIDView.threshold = 1

        contactsViewModel.contacts.observe(host.viewLifecycleOwner) { it ->
            clientIDsAdapter = ArrayAdapter(
                host.requireContext(),
                R.layout.pop_auto_complete_client_id,
                it.map { it.username }
            )
            clientIDView.setAdapter(clientIDsAdapter)
        }
    }

    // A TimePicker Dialog to set the timestamp EditText field
    private fun setTimestampTimePicker() {
        val focusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val tp = TimePickerDialog(
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
                    false
                )
                tp.setButton(DialogInterface.BUTTON_NEUTRAL, ERASE_B_LABEL) { _, _ ->
                    timestampView.setText("") // Empty string converted to null for the timestamp ViewModel data
                }
                tp.show()
            }
        }
        timestampView.onFocusChangeListener = focusChangeListener
    }

    private fun setActionsAdapter() {
        val actionsAdapter = ArrayAdapter(
            host.requireContext(),
            R.layout.pop_auto_complete_action,
            DestinationRecord.Action.values()
        )
        actionsView.setAdapter(actionsAdapter)
        actionsView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        actionsView.threshold = 1
    }

    private fun waitTimeOrRateFromString(userEntry: String): Int {
        if (userEntry.isEmpty())
            return 0
        return userEntry.toInt()
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

    companion object {
        private const val ERASE_B_LABEL = "Erase"
    }
}