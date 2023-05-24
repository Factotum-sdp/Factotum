package com.github.factotum_sdp.factotum.ui.directory

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.models.Contact
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.models.Route
import com.github.factotum_sdp.factotum.ui.directory.DirectoryFragment.Companion.IS_SUB_FRAGMENT_NAV_KEY
import com.github.factotum_sdp.factotum.ui.directory.DirectoryFragment.Companion.USERNAME_NAV_KEY
import com.github.factotum_sdp.factotum.ui.maps.MapsFragment
import com.github.factotum_sdp.factotum.ui.maps.MapsViewModel

class ContactDetailsFragment : Fragment() {
    private lateinit var currentContact: Contact
    private var isSubFragment = false

    private val contactsViewModel: ContactsViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val REQUEST_CALL_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contact_with_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isSubFragment = arguments?.getBoolean(IS_SUB_FRAGMENT_NAV_KEY) ?: false
        initialiseDetails(view, retrieveContact())
    }

    private fun retrieveContact(): Contact? {
        return contactsViewModel.contacts.value?.find {
            it.username == arguments?.getString(
                USERNAME_NAV_KEY
            )
        }
    }

    private fun initialiseDetails(view: View, contact: Contact?) {
        if (contact == null) {
            hideAllViews(view)
        } else {
            currentContact = contact
            setContactDetails(view, currentContact) //set contact details
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
        val contactSuperClient = view.findViewById<TextView>(R.id.managing_client_value)
        val contactRole = view.findViewById<TextView>(R.id.contact_role)
        val contactImage = view.findViewById<ImageView>(R.id.contact_image)
        val contactPhone = view.findViewById<TextView>(R.id.contact_phone)
        val contactAddress = view.findViewById<TextView>(R.id.contact_address)
        val contactDetails = view.findViewById<TextView>(R.id.contact_details)

        if (contact.role == Role.CLIENT.name && !contact.super_client.isNullOrEmpty()) {
            view.findViewById<LinearLayout>(R.id.managing_client_shown).visibility = View.VISIBLE
            contactSuperClient.text = "@" + contact.super_client
        }

        contactPhone.setOnClickListener {
            val phoneNumber = contact.phone
            if (phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                if (ContextCompat.checkSelfPermission(
                        view.context,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        view.context as Activity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        REQUEST_CALL_PERMISSION
                    )
                } else {
                    view.context.startActivity(intent)
                }
            }
        }

        contactUsername.text = "@" + contact.username
        contactName.text = contact.name
        contactSurname.text = contact.surname
        contactRole.text = contact.role
        contactImage.setImageResource(R.mipmap.ic_profile_pic_round)
        contactPhone.text = contact.phone
        contactPhone.paintFlags = contactPhone.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        contactAddress.text = contact.addressName
        contactDetails.text = contact.details
    }

    private fun initialiseAllButtons(view: View, contactsViewModel: ContactsViewModel) {

        val updateContactButton = view.findViewById<Button>(R.id.button_modify_contact)
        updateContactButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(USERNAME_NAV_KEY, currentContact.username)
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
            contactsViewModel.deleteContact(currentContact)
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
            if (currentContact.hasCoordinates()) {
                val uri =
                    Uri.parse("google.navigation:q=${currentContact.latitude},${currentContact.longitude}&mode=b")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage(MapsFragment.MAPS_PKG)
                requireContext().startActivity(intent)
            } else {
                coordinatesError()
            }
        }

        view.findViewById<Button>(R.id.show_all_button).setOnClickListener {
            if (currentContact.hasCoordinates()) {
                mapsViewModel.addRoute(
                    Route(
                        0.0,
                        0.0,
                        currentContact.latitude!!,
                        currentContact.longitude!!
                    )
                )
                if (isSubFragment) {
                    it.findNavController()
                        .navigate(R.id.action_dRecordDetailsFragment_to_MapsFragment)
                } else {
                    it.findNavController()
                        .navigate(R.id.action_contactDetailsFragment2_to_MapsFragment)
                }
            } else {
                coordinatesError()
            }
        }
    }

    private fun coordinatesError() {
        Toast.makeText(
            requireContext(),
            "Contact does not have coordinates",
            Toast.LENGTH_SHORT
        ).show()
    }
}