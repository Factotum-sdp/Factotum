package com.github.factotum_sdp.factotum.ui.directory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.Contact
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.models.Route
import com.github.factotum_sdp.factotum.ui.maps.MapsViewModel
import com.github.factotum_sdp.factotum.ui.maps.RouteFragment

class ContactDetailsFragment : Fragment() {
    private var currentContact: Contact? = null
    private var isSubFragment = false

    private val contactsViewModel: ContactsViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contact_with_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentContact =
            contactsViewModel.contacts.value?.find { it.username == arguments?.getString("username") }

        isSubFragment = arguments?.getBoolean("isSubFragment") ?: false


        if (currentContact == null) {
            hideAllViews(view)
        } else {
            setContactDetails(view, currentContact!!) //set contact details
            initialiseAllButtons(view, contactsViewModel)
        }
    }

    private fun hideAllViews(view: View) {
        view.findViewById<ViewGroup>(R.id.contact_details_fragment)?.apply {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.visibility = View.GONE
            }
        }
        view.findViewById<TextView>(R.id.contact_not_found_text).visibility = View.VISIBLE
    }


    // links contact details to the layout
    @SuppressLint("SetTextI18n")
    private fun setContactDetails(view: View, contact: Contact) {
        val contactUsername = view.findViewById<TextView>(R.id.contact_username)
        val contactName = view.findViewById<TextView>(R.id.contact_name)
        val contactSurname = view.findViewById<TextView>(R.id.contact_surname)
        val contactRole = view.findViewById<TextView>(R.id.contact_role)
        val contactImage = view.findViewById<ImageView>(R.id.contact_image)
        val contactPhone = view.findViewById<TextView>(R.id.contact_phone)
        val contactAddress = view.findViewById<TextView>(R.id.contact_address)
        val contactDetails = view.findViewById<TextView>(R.id.contact_details)

        contactUsername.text = "@" + contact.username
        contactName.text = contact.name
        contactSurname.text = contact.surname
        contactRole.text = contact.role
        contactImage.setImageResource(contact.profile_pic_id)
        contactPhone.text = contact.phone
        contactAddress.text = contact.addressName
        contactDetails.text = contact.details
    }

    private fun initialiseAllButtons(view: View, contactsViewModel: ContactsViewModel) {

        val updateContactButton = view.findViewById<Button>(R.id.button_modify_contact)
        updateContactButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("username", currentContact!!.username)
            if (isSubFragment)
                it.findNavController()
                    .navigate(R.id.action_dRecordDetailsFragment_to_contactCreationFragment, bundle)
            else
                it.findNavController()
                    .navigate(
                        R.id.action_contactDetailsFragment2_to_contactCreationFragment,
                        bundle
                    )
        }

        val deleteContactButton = view.findViewById<Button>(R.id.button_delete_contact)
        deleteContactButton.setOnClickListener {
            contactsViewModel.deleteContact(currentContact!!)
            it.findNavController()
                .navigate(R.id.action_contactDetailsFragment2_to_directoryFragment)
        }
        if (userViewModel.loggedInUser.value?.role != Role.BOSS) {
            deleteContactButton.visibility = View.GONE
        }

        if (isSubFragment) {
            updateContactButton.visibility = View.GONE
            deleteContactButton.visibility = View.GONE
        }

        view.findViewById<Button>(R.id.run_button).setOnClickListener {
            val uri =
                Uri.parse("google.navigation:q=${currentContact!!.latitude},${currentContact!!.longitude}&mode=b")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage(RouteFragment.MAPS_PKG)
            requireContext().startActivity(intent)
        }

        view.findViewById<Button>(R.id.show_all_button).setOnClickListener {
            if (currentContact!!.latitude == null || currentContact!!.longitude == null) {
                return@setOnClickListener
            }
            mapsViewModel.addRoute(Route(0.0, 0.0, currentContact!!.latitude!!, currentContact!!.longitude!!))
            if (isSubFragment) {
                it.findNavController().navigate(R.id.action_dRecordDetailsFragment_to_MapsFragment)
            } else {
                it.findNavController().navigate(R.id.action_contactDetailsFragment2_to_MapsFragment)
            }
        }
    }
}