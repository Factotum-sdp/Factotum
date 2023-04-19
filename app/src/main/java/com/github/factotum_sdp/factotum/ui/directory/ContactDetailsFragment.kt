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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = inflater.inflate(R.layout.contact_with_details, container, false)
        val button = mainView.findViewById<Button>(R.id.button) // connect the button to the layout
        button.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_contactDetailsFragment2_to_directoryFragment)} // go back to the list of contacts when the button is clicked
        val contactsViewModel =
            ViewModelProvider(requireActivity())[ContactsViewModel::class.java] // get the contacts view model
        setContactDetails(mainView,
            contactsViewModel.contacts.value?.get(arguments?.getInt("id")!!)!!
        ) //links the contact details to the layout

        val updateContactButton = mainView.findViewById<Button>(R.id.button_modify_contact)
        updateContactButton.setOnClickListener { view ->
            val bundle = Bundle()
            bundle.putInt("id", arguments?.getInt("id")!!)
            view.findNavController().navigate(R.id.action_contactDetailsFragment2_to_contactCreationFragment, bundle)
        }

        val deleteContactButton = mainView.findViewById<Button>(R.id.button_delete_contact)
        deleteContactButton.setOnClickListener { view ->
            contactsViewModel.deleteContact(contactsViewModel.contacts.value?.get(arguments?.getInt("id")!!)!!)
            view.findNavController().navigate(R.id.action_contactDetailsFragment2_to_directoryFragment)
        }

        return mainView
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
}