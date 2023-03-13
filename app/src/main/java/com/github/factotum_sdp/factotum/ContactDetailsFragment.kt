package com.github.factotum_sdp.factotum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.factotum_sdp.factotum.contacts_content.ContactsList

class ContactDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = inflater.inflate(R.layout.contact_with_details, container, false)
        val button = mainView.findViewById<Button>(R.id.button)
        button.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_contactDetailsFragment_to_contactsFragment)}
        setContactDetails(mainView, ContactsList.ITEMS[arguments?.getInt("id")!!])

        return mainView
    }

    private fun setContactDetails(view: View, contact: ContactsList.Contact) {
        val contactName = view.findViewById<TextView>(R.id.contact_name)
        val contactRole = view.findViewById<TextView>(R.id.contact_role)
        val contactImage = view.findViewById<ImageView>(R.id.contact_image)
        val contactPhone = view.findViewById<TextView>(R.id.contact_phone)
        val contactAddress = view.findViewById<TextView>(R.id.contact_address)
        val contactDetails = view.findViewById<TextView>(R.id.contact_details)

        contactName.text = contact.name
        contactRole.text = contact.role
        contactImage.setImageResource(contact.profile_pic_id)
        contactPhone.text = contact.phone
        contactAddress.text = contact.address
        contactDetails.text = contact.details
    }
}