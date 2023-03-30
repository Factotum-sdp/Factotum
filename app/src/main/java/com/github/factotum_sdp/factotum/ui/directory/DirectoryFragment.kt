package com.github.factotum_sdp.factotum.ui.directory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R

class DirectoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById <RecyclerView>(R.id.contacts_recycler_view) // connect the recycler view to the layout
        val searchView = view.findViewById <SearchView>(R.id.contacts_search_view) // connect the search view to the layout
        //the recycler is just the way we chose to represent the list of contacts
        recycler.apply {
            layoutManager = LinearLayoutManager(context)    //recycler needs a layout manager and an adapter which we set here
            adapter = ContactsRecyclerAdapter()
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
}