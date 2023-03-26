package com.github.factotum_sdp.factotum.ui.directory

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseContactsDataSource : ContactsDataSource {
    private val database = FirebaseDatabase.getInstance()

    override fun getContactsReference(): DatabaseReference {
        return database.getReference("contacts")
    }
}

interface ContactsDataSource {
    fun getContactsReference(): DatabaseReference
}