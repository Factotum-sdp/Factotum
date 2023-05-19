package com.github.factotum_sdp.factotum.ui.bag

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookViewModel
import java.util.Date

class PackageCreationDialogBuilder(
    context: Context,
    private val host: Fragment,
    private val fromDestID: String,
    private val fromClientID: String,
    private val takenAt: Date,
    private val bagViewModel: BagViewModel,
    private val contactsViewModel: ContactsViewModel
) :
    AlertDialog.Builder(context) {

    private val nameView: EditText
    private val recipientTextInput: AutoCompleteTextView
    private val notesView: EditText

    init {
        val inflater = host.requireActivity().layoutInflater
        val dialogView: View = inflater.inflate(R.layout.package_creation_custom_dialog, null)

        nameView = dialogView.findViewById(R.id.editTextPackageName)
        recipientTextInput = dialogView.findViewById(R.id.autoCompleteRecipientClientID)
        notesView = dialogView.findViewById(R.id.editTextPackageNotes)

        setTitle("New package from $fromDestID")
        setIcon(R.drawable.pack_icon)
        setCancelable(false)
        setView(dialogView)
    }

    override fun create(): AlertDialog {
        setClientIDsAdapter()
        setClientIDFieldCheck()

        setPositiveButton("Confirm") { _, _ ->
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

    // Here we will need to get the clients IDs through a ViewModel instance
    // initiated in the mainActivity and representing all the clients
    private fun setClientIDsAdapter() {
        recipientTextInput.threshold = 1
        contactsViewModel.contacts.observe(host.viewLifecycleOwner) { it ->
            val clientIDsAdapter = ArrayAdapter(
                host.requireContext(),
                R.layout.pop_auto_complete_client_id,
                it.map { it.username }
            )
            recipientTextInput.setAdapter(clientIDsAdapter)
        }
    }

    private fun setClientIDFieldCheck() {
        recipientTextInput.validator = object : AutoCompleteTextView.Validator {
            override fun isValid(text: CharSequence): Boolean {
                val possibleClientID = text.toString().trim()
                return isValidClientID(possibleClientID)
            }

            override fun fixText(invalidText: CharSequence): CharSequence {
                // If .isValid() returns false then the code comes here
                return host.requireContext().getString(R.string.invalid_client_id_text)
            }
        }
    }

    private fun isValidClientID(possibleClientID: String): Boolean {
        val currentContacts = contactsViewModel.contacts.value ?: emptyList()
        return currentContacts.any { c -> c.username == possibleClientID }
    }
}