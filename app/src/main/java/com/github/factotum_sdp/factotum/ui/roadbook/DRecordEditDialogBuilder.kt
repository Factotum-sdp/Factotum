package com.github.factotum_sdp.factotum.ui.roadbook

import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.MultiAutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.github.factotum_sdp.factotum.models.DestinationRecord.Companion.parseActions
import com.github.factotum_sdp.factotum.models.DestinationRecord.Companion.parseTimestamp
import com.github.factotum_sdp.factotum.models.DestinationRecord.Companion.parseWaitTimeOrRate
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar

private const val PADDING_AMOUNT = 32

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
    MaterialAlertDialogBuilder(
        ContextThemeWrapper(context, R.style.Theme_Factotum_Dialog)) {

    private val clientIDView: AutoCompleteTextView
    private val timestampView: EditText
    private val waitingTimeView: EditText
    private val rateView: EditText
    private val actionsView: MultiAutoCompleteTextView
    private val notesView: EditText
    private val dialogView: View

    init {
        val inflater = host.requireActivity().layoutInflater
        dialogView = inflater.inflate(R.layout.edit_record_custom_dialog, null)
        dialogView.setPadding(PADDING_AMOUNT)
        setTitle(R.string.create_new_record)
        setCancelable(false)
        setView(dialogView)

        clientIDView = dialogView.findViewById(R.id.autoCompleteClientID)
        timestampView = dialogView.findViewById(R.id.editTextTimestamp)
        waitingTimeView = dialogView.findViewById(R.id.editTextWaitingTime)
        rateView = dialogView.findViewById(R.id.editTextRate)
        actionsView = dialogView.findViewById(R.id.multiAutoCompleteActions)
        notesView = dialogView.findViewById(R.id.editTextNotes)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun create(): AlertDialog {
        setClientIDsAdapter()
        setTimestampTimePicker()
        setActionsAdapter()
        setClientIDFieldCheck()

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
            withUserEntryErrorManagement {
                rbViewModel.addRecord(
                    checkClientID(clientIDView.text.toString()),
                    parseTimestamp(timestampView.text.toString()),
                    parseWaitTimeOrRate(waitingTimeView.text.toString()),
                    parseWaitTimeOrRate(rateView.text.toString()),
                    parseActions(actionsView.text.toString()),
                    notesView.text.toString()
                )
                setSnackBar(host.getString(R.string.snap_text_record_added), 700)
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
            var recHasChanged: Boolean
            withUserEntryErrorManagement {
                recHasChanged =
                    rbViewModel.editRecordAt(
                        position,
                        checkClientID(clientIDView.text.toString()),
                        parseTimestamp(timestampView.text.toString()),
                        parseWaitTimeOrRate(waitingTimeView.text.toString()),
                        parseWaitTimeOrRate(rateView.text.toString()),
                        parseActions(actionsView.text.toString()),
                        notesView.text.toString()
                    )
                if (recHasChanged)
                    setSnackBar(context.getString(R.string.edit_confirmed_snap_label), 700)
            }
            rbRecyclerView.adapter!!.notifyItemChanged(position)
        })
        return this
    }

    private fun withUserEntryErrorManagement(toExecute: () -> Unit) {
        try { // On positive button : Try to edit the record
            toExecute()
        } catch (e: InvalidClientIDException) {
            setSnackBar(context.getString(R.string.invalid_client_id_snack_bar), 1400)
        } catch (e: Exception) {
            setSnackBar(host.getString(R.string.edit_rejected_snap_label), 1400)
        }
    }

    private fun checkClientID(userEntry: String): String {
        val possibleClientID = userEntry.trim()
        if(!isValidClientID(possibleClientID))
            throw InvalidClientIDException()
        return possibleClientID
    }

    class InvalidClientIDException: Exception("Invalid Client ID")

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
        clientIDView.threshold = 1
        contactsViewModel.contacts.observe(host.viewLifecycleOwner) { it ->
            val clientIDsAdapter = ArrayAdapter(
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
                val picker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                    .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                    .setTitleText("Select Time")
                    .build()

                picker.addOnPositiveButtonClickListener {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, picker.hour)
                    cal.set(Calendar.MINUTE, picker.minute)

                    timestampView.setText(SimpleDateFormat.getTimeInstance().format(cal.time))
                }

                picker.addOnNegativeButtonClickListener {
                    timestampView.setText("") // Empty string converted to null for the timestamp ViewModel data
                }
                picker.show(host.parentFragmentManager, "MATERIAL_TIME_PICKER")
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

    private fun setClientIDFieldCheck() {
        clientIDView.validator = object : AutoCompleteTextView.Validator {
            override fun isValid(text: CharSequence): Boolean {
                val possibleClientID = text.toString().trim()
                return isValidClientID(possibleClientID)
            }

            override fun fixText(invalidText: CharSequence): CharSequence {
                // If .isValid() returns false then the code comes here
                return context.getString(R.string.invalid_client_id_text)
            }
        }
    }

    private fun isValidClientID(possibleClientID: String): Boolean {
        val currentContacts = contactsViewModel.contacts.value ?: emptyList()
        return currentContacts.any { c -> c.username == possibleClientID }
    }
}