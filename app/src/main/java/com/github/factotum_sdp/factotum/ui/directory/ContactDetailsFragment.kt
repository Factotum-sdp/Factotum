package com.github.factotum_sdp.factotum.ui.directory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact

class ContactDetailsFragment : Fragment() {
    private lateinit var currentContact: Contact

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contact_with_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactsViewModel = //retrieve list of contacts
            ViewModelProvider(requireActivity())[ContactsViewModel::class.java]

        currentContact = contactsViewModel.contacts.value?.get(arguments?.getInt("id")!!) ?: Contact()

        setContactDetails(view, currentContact) //set contact details

        initialiseAllButtons(view, contactsViewModel)
    }

    // links contact details to the layout
    private fun setContactDetails(view: View, contact: Contact) {
        val contactName = view.findViewById<TextView>(R.id.contact_name)
        val contactSurname = view.findViewById<TextView>(R.id.contact_surname)
        val contactRole = view.findViewById<TextView>(R.id.contact_role)
        val contactImage = view.findViewById<ImageView>(R.id.contact_image)
        val contactPhone = view.findViewById<TextView>(R.id.contact_phone)
        val contactAddress = view.findViewById<TextView>(R.id.contact_address)
        val contactDetails = view.findViewById<TextView>(R.id.contact_details)

        contactName.text = contact.name
        contactSurname.text = contact.surname
        contactRole.text = contact.role
        contactImage.setImageResource(contact.profile_pic_id)
        contactPhone.text = contact.phone
        contactAddress.text = contact.address
        contactDetails.text = contact.details
    }

    private fun initialiseAllButtons(view: View, contactsViewModel: ContactsViewModel) {

        val returnToContactsButton = view.findViewById<Button>(R.id.button) // connect the button to the layout
        returnToContactsButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_contactDetailsFragment2_to_directoryFragment)
        } // go back to the list of contacts when the button is clicked

        val updateContactButton = view.findViewById<Button>(R.id.button_modify_contact)
        updateContactButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", arguments?.getInt("id")!!)
            it.findNavController()
                .navigate(R.id.action_contactDetailsFragment2_to_contactCreationFragment, bundle)
        }

        val deleteContactButton = view.findViewById<Button>(R.id.button_delete_contact)
        deleteContactButton.setOnClickListener {
            contactsViewModel.deleteContact(currentContact)
            it.findNavController()
                .navigate(R.id.action_contactDetailsFragment2_to_directoryFragment)
        }

    }
}