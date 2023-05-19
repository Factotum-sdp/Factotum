package com.github.factotum_sdp.factotum.ui.bag

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.dialog.ClientIDViewValidation
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import java.util.Date

/**
 * That Class represent a DialogBuilder specifically designed to build a custom
 * Dialog for some Package creation.
 *
 * Calling the inherited show() method will call create() and make the new custom concrete AlertDialog desired
 *
 * @param context: Context The Context wherein this DialogBuilder is instantiated
 * @param host: Fragment The Fragment which will hold the AlertDialog
 * @param fromDestID: String The DestinationRecord ID of the pack to create sender
 * @param fromClientID: String The clientID of the pack to create sender
 * @param takenAt: Date The time the pack to create has been taken
 * @param bagViewModel: BagViewModel
 * @param contactsViewModel: ContactViewModel The ContactsViewModel needed for ClientIDViewValidation interface
 *
 * @constructor : Constructs the PackCreationDialogBuilder
 */
class PackCreationDialogBuilder(
    context: Context,
    override val host: Fragment,
    private val fromDestID: String,
    private val fromClientID: String,
    private val takenAt: Date,
    private val bagViewModel: BagViewModel,
    override val contactsViewModel: ContactsViewModel
) :
    AlertDialog.Builder(context),
    ClientIDViewValidation {

    private val nameView: EditText
    private val recipientTextInput: AutoCompleteTextView
    private val notesView: EditText

    init {
        val inflater = host.requireActivity().layoutInflater
        val dialogView: View = inflater.inflate(R.layout.package_creation_custom_dialog, null)

        nameView = dialogView.findViewById(R.id.editTextPackageName)
        recipientTextInput = dialogView.findViewById(R.id.autoCompleteRecipientClientID)
        notesView = dialogView.findViewById(R.id.editTextPackageNotes)

        setTitle(DIALOG_TITLE_PREFIX + fromClientID)
        setIcon(R.drawable.pack_icon)
        setCancelable(false)
        setView(dialogView)
    }

    override fun clientIDInputView(): AutoCompleteTextView {
        return recipientTextInput
    }

    override fun create(): AlertDialog {
        setClientIDsAdapter()
        setClientIDFieldCheck()

        setPositiveButton(context.getString(R.string.confirm_label_pack_creation_dialog)) { _, _ ->
            bagViewModel.newPackage(
                fromDestID,
                takenAt,
                nameView.text.toString(),
                fromClientID,
                recipientTextInput.text.toString().trim(),
                notesView.text.toString()
            )
        }

        return super.create()
    }

    companion object {
        const val DIALOG_TITLE_PREFIX = "New package from"
    }
}