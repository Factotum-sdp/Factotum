package com.github.factotum_sdp.factotum.ui.directory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact

/**
 * A simple ContactCreation fragment.
 */
class ContactCreation : Fragment() {

    // Should not stay like that and instead roles should use roles from future ENUM
    private val roles = listOf("Boss", "Courier", "Client")
    private var currentContact: Contact? = null
    private val isUpdate: Boolean
        get() = currentContact != null
    private lateinit var viewModel : ContactsViewModel

    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var address: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var details: EditText

    private lateinit var spinner: Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_creation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrievePossibleArguments()

        initaliseRolesSpinner(view)

        setContactFields(view, currentContact)

        initialiseApproveFormButton(view)


    }

    private fun retrievePossibleArguments() {
        viewModel = ViewModelProvider(requireActivity())[ContactsViewModel::class.java]
        viewModel.setDatabase(MainActivity.getDatabase())

        if (arguments?.getInt("id") != null) {
            currentContact = viewModel.contacts.value?.get(arguments?.getInt("id")!!)
        }
    }

    private fun setContactFields(view: View, contact: Contact?) {
        name = view.findViewById(R.id.editTextName)
        surname = view.findViewById(R.id.editTextSurname)
        address = view.findViewById(R.id.contactCreationAddress)
        phoneNumber = view.findViewById(R.id.contactCreationPhoneNumber)
        details = view.findViewById(R.id.contactCreationNotes)

        if (contact != null) {
            spinner.setSelection(roles.indexOf(contact.role))
            name.setText(contact.name)
            surname.setText(contact.surname)
            address.setText(contact.address)
            phoneNumber.setText(contact.phone)
            details.setText(contact.details)
        }
    }

    private fun initaliseRolesSpinner(view : View) {
        spinner = view.findViewById(R.id.roles_spinner)
        // Initializes the spinner for the roles
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            roles
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun initialiseApproveFormButton(view : View) {
        val approveFormButton = view.findViewById<Button>(R.id.create_contact)

        if (isUpdate) {
            approveFormButton.text = getString(R.string.form_button_update)
            approveFormButton.setOnClickListener {
                viewModel.updateContact(
                    Contact(
                        id = currentContact!!.id,
                        role = spinner.selectedItem.toString(),
                        name = name.text.toString(),
                        surname = surname.text.toString(),
                        profile_pic_id = R.drawable.contact_image,
                        address = address.text.toString(),
                        phone = phoneNumber.text.toString(),
                        details = details.text.toString()
                    )
                )
                it.findNavController().navigate(R.id.action_contactCreation_to_directoryFragment)
            }
        } else {
            approveFormButton.text = getString(R.string.form_button_create)
            approveFormButton.setOnClickListener {
                viewModel.saveNewIDContact(
                    role = spinner.selectedItem.toString(),
                    name = name.text.toString(),
                    surname = surname.text.toString(),
                    image = R.drawable.contact_image,
                    address = address.text.toString(),
                    phone = phoneNumber.text.toString(),
                    details = details.text.toString())
                it.findNavController().navigate(R.id.action_contactCreation_to_directoryFragment)
            }
        }
    }
}
