package com.github.factotum_sdp.factotum.ui.directory

import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CursorAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentContactCreationBinding
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance.getDatabase
import com.github.factotum_sdp.factotum.model.AddressCoordinates
import com.github.factotum_sdp.factotum.model.Contact
import com.github.factotum_sdp.factotum.model.Role
import com.github.factotum_sdp.factotum.ui.directory.DirectoryFragment.Companion.USERNAME_NAV_KEY
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

    private val contactsViewModel: ContactsViewModel by activityViewModels()

    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var username: EditText
    private lateinit var managingClientUsername: EditText
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
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_creation)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrievePossibleArguments()

        val cursor = initializeAddressSearch()
        setAddressSearchTextListener(cursor)
        setAddressSearchSuggestions()

        initialiseRolesSpinner(view)

        setContactFields(view, currentContact)

        initialiseApproveFormButton(view)
    }

    private fun retrievePossibleArguments() {
        viewModel = ViewModelProvider(requireActivity())[ContactsViewModel::class.java]
        viewModel.setDatabase(getDatabase())

        currentContact =
            contactsViewModel.contacts.value?.find {
                it.username == arguments?.getString(
                    USERNAME_NAV_KEY
                )
            }

    }

    private fun setContactFields(view: View, contact: Contact?) {
        name = view.findViewById(R.id.editTextName)
        surname = view.findViewById(R.id.editTextSurname)
        username = view.findViewById(R.id.editTextUsername)
        managingClientUsername = view.findViewById(R.id.editTextSuperClientUsername)
        phoneNumber = view.findViewById(R.id.contactCreationPhoneNumber)
        details = view.findViewById(R.id.contactCreationNotes)

        if (contact != null) {
            spinner.setSelection(roles.indexOf(contact.role))
            name.setText(contact.name)
            surname.setText(contact.surname)
            username.setText(contact.username)
            managingClientUsername.setText(contact.super_client)
            binding.contactCreationAddress.setQuery(contact.addressName, false)
            phoneNumber.setText(contact.phone)
            details.setText(contact.details)
        }
    }

    private fun initialiseRolesSpinner(view: View) {
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

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == Role.CLIENT.name) {
                    managingClientUsername.visibility = View.VISIBLE
                } else {
                    managingClientUsername.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

    }

    private fun setAddressSearchTextListener(cursorAdapter: SimpleCursorAdapter) {
        binding.contactCreationAddress.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val cursor =
                        MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                    viewLifecycleOwner.lifecycleScope.launch {
                        val result = AddressCoordinates.geocoderQuery(newText, requireContext())
                        result?.forEachIndexed { index, suggestion ->
                            cursor.addRow(arrayOf(index, suggestion.addressName))
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
        val approveFormButton = view.findViewById<Button>(R.id.confirm_form)
        approveFormButton.text =
            if (isUpdate) getString(R.string.form_button_update) else getString(R.string.form_button_create)
        approveFormButton.setOnClickListener {
            if (username.text.toString().isEmpty()) {
                showErrorToast(R.string.empty_username)
                return@setOnClickListener
            } else if (!isUsernameUnique(username.text.toString()) && currentContact?.username != username.text.toString()) {
                showErrorToast(R.string.username_not_unique)
                return@setOnClickListener
            } else if (managingClientUsername.text.toString()
                    .isNotEmpty() && !isUsernameExistingContact(managingClientUsername.text.toString())
            ) {
                showErrorToast(R.string.super_client_id_not_valid)
                return@setOnClickListener
            } else {
                if (currentContact != null) viewModel.deleteContact(currentContact!!)
                val address = validateLocation()
                viewModel.saveContact(
                    Contact(
                        username = username.text.toString(),
                        role = spinner.selectedItem.toString(),
                        name = name.text.toString(),
                        surname = surname.text.toString(),
                        profile_pic_id = R.mipmap.ic_launcher_round,
                        addressName = address.addressName,
                        latitude = address.coordinates?.latitude,
                        longitude = address.coordinates?.longitude,
                        super_client = if (spinner.selectedItem.toString() == Role.CLIENT.name)
                            managingClientUsername.text.toString() else null,
                        phone = phoneNumber.text.toString(),
                        details = details.text.toString()
                    )
                )
                it.findNavController().navigate(R.id.action_contactCreation_to_directoryFragment)
            }
        }
    }

    private fun isUsernameUnique(username: String): Boolean {
        return viewModel.contacts.value?.find { it.username == username } == null
    }

    private fun isUsernameExistingContact(username: String): Boolean {
        return viewModel.contacts.value?.find { it.username == username && it.role == Role.CLIENT.name } != null
    }

    private fun validateLocation(): AddressCoordinates {
        val addressName = binding.contactCreationAddress.query.toString()
        return AddressCoordinates(addressName, requireContext())
    }

    private fun showErrorToast(resId: Int) {
        Toast.makeText(
            requireContext(),
            getString(resId),
            Toast.LENGTH_SHORT
        ).show()
    }

}