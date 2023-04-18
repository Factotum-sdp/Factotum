package com.github.factotum_sdp.factotum.ui.directory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class DirectoryFragment : Fragment() {

    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ContactsRecyclerAdapter
    private lateinit var viewModel: ContactsViewModel
    private lateinit var emptyContactsMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = MainActivity.getDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    @SuppressLint("NotifyDataSetChanged") //We are updating the entire list at launch so we are not worried about wasted performance
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ContactsViewModel::class.java]
        viewModel.setDatabase(db)
        adapter = ContactsRecyclerAdapter()
        adapter.updateContacts(viewModel.getContacts().value ?: emptyList())

        viewModel.getContacts().observe(viewLifecycleOwner) { contacts ->
            adapter.updateContacts(contacts)
        }

        val createContactButton = view.findViewById<FloatingActionButton>(R.id.add_contact_button)
        createContactButton.setOnClickListener{
            it.findNavController().navigate(R.id.action_directoryFragment_to_contactCreationFragment)
        }

        val recycler = view.findViewById <RecyclerView>(R.id.contacts_recycler_view) // connect the recycler view to the layout
        val searchView = view.findViewById <SearchView>(R.id.contacts_search_view) // connect the search view to the layout
        emptyContactsMessage = view.findViewById(R.id.empty_contacts_message)

        //the recycler is just the way we chose to represent the list of contacts
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DirectoryFragment.adapter
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the RecyclerView when the text changes
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    override fun onPause() {
        super.onPause()
    }
}