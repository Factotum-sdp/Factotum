package com.github.factotum_sdp.factotum.ui.directory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DirectoryFragment : Fragment() {

    private val mainScope = MainScope()
    private val db = Firebase.database

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
        val recycler = view.findViewById<RecyclerView>(R.id.contacts_recycler_view)

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
    }

    override fun onPause() {
        super.onPause()

        // Save contacts to local storage when the app is paused
        ContactsList.saveContactsLocally(requireContext())
    }
}