package com.github.factotum_sdp.factotum.ui.dialog

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel

/**
 * ClientIDViewValidation Interface
 *
 * Implement the necessary helpers function for an AutoCompleteTextView which
 * consist in receiving a clientID as user input.
 *
 * It proposes to set the autoComplete adapter and a ClientID check with the contactsViewModel field state.
 */
interface ClientIDViewValidation {

    val host: Fragment
    val contactsViewModel: ContactsViewModel

    /** @return AutoCompleteTextView */
    fun clientIDInputView(): AutoCompleteTextView

    /**
     * Set the clientIDInputView()'s adapter with the current clientIDs registered in the app
     */
    fun setClientIDsAdapter() {
        clientIDInputView().threshold = 1
        contactsViewModel.contacts.observe(host.viewLifecycleOwner) { it ->
            val clientIDsAdapter = ArrayAdapter(
                host.requireContext(),
                R.layout.pop_auto_complete_client_id,
                it.map { it.username }
            )
            clientIDInputView().setAdapter(clientIDsAdapter)
        }
    }

    /**
     * Set a clientID check on the clientIDInputView()'s text entry
     */
    fun setClientIDFieldCheck() {
        clientIDInputView().validator = object : AutoCompleteTextView.Validator {
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

    /**
     * Whether the "possibleClientID" is currently registered in the app clientIDs
     *
     * @param possibleClientID: String
     * @return Boolean
     */
    fun isValidClientID(possibleClientID: String): Boolean {
        val currentContacts = contactsViewModel.contacts.value ?: emptyList()
        return currentContacts.any { c -> c.username == possibleClientID }
    }
}