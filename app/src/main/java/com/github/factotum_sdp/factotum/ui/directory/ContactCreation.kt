package com.github.factotum_sdp.factotum.ui.directory

import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CursorAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.localisation.Location
import com.github.factotum_sdp.factotum.databinding.FragmentContactCreationBinding
import kotlinx.coroutines.launch

/**
 * A simple ContactCreation fragment.
 */
class ContactCreation : Fragment() {

    // Should not stay like that and instead roles should use roles from future ENUM
    val ROLES = listOf("Boss", "Courier", "Client")
    private var _binding: FragmentContactCreationBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeSpinner(view)

        val cursor = initializeAddressSearch()
        setAddressSearchTextListener(cursor)
        setAddressSearchSuggestions()

    }

    private fun initializeSpinner(view: View){
        val spinner: Spinner = view.findViewById(R.id.roles_spinner)
        //Initializes the spinner for the roles
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
    }

    private fun setAddressSearchTextListener(cursorAdapter: SimpleCursorAdapter) {
        binding.contactCreationAddress.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
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

    private fun setAddressSearchSuggestions(){
        binding.contactCreationAddress.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = binding.contactCreationAddress.suggestionsAdapter.getItem(position) as Cursor
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
}
