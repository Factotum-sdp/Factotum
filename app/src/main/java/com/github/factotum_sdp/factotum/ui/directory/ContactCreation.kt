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

/**
 * A simple ContactCreation fragment.
 */
class ContactCreation : Fragment() {

    // Should not stay like that and instead roles should use roles from future ENUM
    private val ROLES = listOf("Boss", "Courier", "Client")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_creation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = view.findViewById(R.id.roles_spinner)
        // Initializes the spinner for the roles
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ROLES
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        val button = view.findViewById<Button>(R.id.create_contact)
        button.setOnClickListener {
            val viewModel = ViewModelProvider(requireActivity())[ContactsViewModel::class.java]
            viewModel.setDatabase(MainActivity.getDatabase())

            val name = view.findViewById<EditText>(R.id.editTextName).text.toString()
            val surname = view.findViewById<EditText>(R.id.editTextSurname).text.toString()
            val address = view.findViewById<EditText>(R.id.contactCreationAddress).text.toString()
            val phoneNumber = view.findViewById<EditText>(R.id.contactCreationPhoneNumber).text.toString()
            val details = view.findViewById<EditText>(R.id.contactCreationNotes).text.toString()

            viewModel.saveNewIDContact(spinner.selectedItem.toString(), "$name $surname", R.drawable.contact_image, address, phoneNumber, details)
            it.findNavController().navigate(R.id.action_contactCreation_to_directoryFragment)
        }
    }
}
