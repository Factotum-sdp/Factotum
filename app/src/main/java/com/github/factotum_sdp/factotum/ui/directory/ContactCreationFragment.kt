package com.github.factotum_sdp.factotum.ui.directory

import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.Role
import com.github.factotum_sdp.factotum.data.localisation.Location
import com.github.factotum_sdp.factotum.databinding.FragmentContactCreationBinding
import com.github.factotum_sdp.factotum.placeholder.Contact
import kotlinx.coroutines.launch

/**
 * A simple ContactCreation fragment.
 */
class ContactCreationFragment : Fragment() {

    // Should not stay like that and instead roles should use roles from future ENUM
    private val roles = Role.values().map { it.name }
    private var currentContact: Contact? = null
    private val isUpdate: Boolean
        get() = currentContact != null
    private lateinit var viewModel: ContactsViewModel
    private var _binding: FragmentContactCreationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var details: EditText

    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrievePossibleArguments()

        val cursor = initializeAddressSearch()
        setAddressSearchTextListener(cursor)
        setAddressSearchSuggestions()

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
        phoneNumber = view.findViewById(R.id.contactCreationPhoneNumber)
        details = view.findViewById(R.id.contactCreationNotes)

        if (contact != null) {
            spinner.setSelection(roles.indexOf(contact.role))
            name.setText(contact.name)
            surname.setText(contact.surname)
            binding.contactCreationAddress.setQuery(contact.address, false)
            phoneNumber.setText(contact.phone)
            details.setText(contact.details)
        }
    }

    private fun initaliseRolesSpinner(view: View) {
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

    private fun setAddressSearchTextListener(cursorAdapter: SimpleCursorAdapter) {
        binding.contactCreationAddress.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.length > 2) {
                    val cursor =
                        MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                    viewLifecycleOwner.lifecycleScope.launch {
                        val result = Location.geocoderQuery(newText, requireContext())
                        result?.forEachIndexed { index, suggestion ->
                            cursor.addRow(arrayOf(index, suggestion.getAddressLine(0).toString()))
                        }
                        cursorAdapter.changeCursor(cursor)
                    }
                }
                return true
            }
        })
    }

    private fun setAddressSearchSuggestions() {
        binding.contactCreationAddress.setOnSuggestionListener(object :
            SearchView.OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor =
                    binding.contactCreationAddress.suggestionsAdapter.getItem(position) as Cursor
                val index = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
                if (index == -1) return true
                val selection =
                    cursor.getString(index)
                binding.contactCreationAddress.setQuery(selection.toString(), false)
                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }
        })
    }

    private fun initializeAddressSearch(): SimpleCursorAdapter {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.searchItemID)
        val cursorAdapter = SimpleCursorAdapter(
            requireContext(),
            R.layout.suggestion_item_layout,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        binding.contactCreationAddress.suggestionsAdapter = cursorAdapter
        return cursorAdapter
    }


    private fun initialiseApproveFormButton(view: View) {
        val approveFormButton = view.findViewById<Button>(R.id.create_contact)
        if (isUpdate) {
            approveFormButton.text = getString(R.string.form_button_update)
            approveFormButton.setOnClickListener {
                val address = validateLocation()
                viewModel.updateContact(
                    Contact(
                        id = currentContact!!.id,
                        role = spinner.selectedItem.toString(),
                        name = name.text.toString(),
                        surname = surname.text.toString(),
                        profile_pic_id = R.drawable.contact_image,
                        address = address?.addressName.toString(),
                        coordinates = address?.coordinates.toString(),
                        phone = phoneNumber.text.toString(),
                        details = details.text.toString()
                    )
                )
                it.findNavController().navigate(R.id.action_contactCreation_to_directoryFragment)
            }
        } else {
            approveFormButton.text = getString(R.string.form_button_create)
            approveFormButton.setOnClickListener {
                val address = validateLocation()
                viewModel.saveNewIDContact(
                    role = spinner.selectedItem.toString(),
                    name = name.text.toString(),
                    surname = surname.text.toString(),
                    image = R.drawable.contact_image,
                    address = address?.addressName.toString(),
                    coordinates = address?.addressName.toString(),
                    phone = phoneNumber.text.toString(),
                    details = details.text.toString()
                )
                it.findNavController().navigate(R.id.action_contactCreation_to_directoryFragment)
            }
        }
    }

    private fun validateLocation(): Location? {
        val addressName = binding.contactCreationAddress.query.toString()
        return Location.createAndStore(addressName, requireContext())
    }
}
