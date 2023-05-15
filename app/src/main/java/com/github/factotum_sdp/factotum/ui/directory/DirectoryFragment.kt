package com.github.factotum_sdp.factotum.ui.directory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.models.Role
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DirectoryFragment : Fragment() {

    private lateinit var adapter: ContactsRecyclerAdapter
    private val viewModel: ContactsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var emptyContactsMessage: TextView

    companion object {
        const val USERNAME_NAV_KEY = "username"
        const val IS_SUB_FRAGMENT_NAV_KEY = "isSubFragment"
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


        adapter = ContactsRecyclerAdapter()
        adapter.updateContacts(viewModel.contacts.value ?: emptyList())

        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            adapter.updateContacts(contacts)
        }

        val createContactButton = view.findViewById<FloatingActionButton>(R.id.add_contact_button)
        if (userViewModel.loggedInUser.value?.role == Role.COURIER) {
            createContactButton.visibility = View.GONE
        }
        createContactButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_directoryFragment_to_contactCreationFragment)
        }

        val recycler =
            view.findViewById<RecyclerView>(R.id.contacts_recycler_view) // connect the recycler view to the layout
        val searchView =
            view.findViewById<SearchView>(R.id.contacts_search_view) // connect the search view to the layout

        //the recycler is just the way we chose to represent the list of contacts
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DirectoryFragment.adapter
        }

        emptyContactsMessage = view.findViewById(R.id.empty_contacts_message)

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                emptyContactsMessage.visibility =
                    if (adapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

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
}