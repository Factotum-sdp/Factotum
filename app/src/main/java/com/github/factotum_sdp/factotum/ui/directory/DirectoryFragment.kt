package com.github.factotum_sdp.factotum.ui.directory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DirectoryFragment : Fragment() {

    private val mainScope = MainScope()
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = MainActivity.getDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ContactsList.init(db)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    @SuppressLint("NotifyDataSetChanged") //We are updating the entire list at launch so we are not worried about wasted performance
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById <RecyclerView>(R.id.contacts_recycler_view) // connect the recycler view to the layout
        val searchView = view.findViewById <SearchView>(R.id.contacts_search_view) // connect the search view to the layout
        //the recycler is just the way we chose to represent the list of contacts
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ContactsRecyclerAdapter()
        }

        // Load contacts from local storage
        ContactsList.loadContactsLocally(requireContext())

        // Sync contacts from Firebase when connected to the internet
        mainScope.launch {
            ContactsList.syncContactsFromFirebase(requireContext())
            recycler.adapter?.notifyDataSetChanged()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // You can handle the search submission here if needed
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the RecyclerView when the text changes
                ContactsRecyclerAdapter().filter.filter(newText)
                return true
            }
        })
    }

    override fun onPause() {
        super.onPause()

        // Save contacts to local storage when the app is paused
        ContactsList.saveContactsLocally(requireContext())
    }
}