package com.github.factotum_sdp.factotum.ui.directory

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.github.factotum_sdp.factotum.model.Contact
import com.github.factotum_sdp.factotum.repositories.ContactsRepository
import com.google.firebase.database.FirebaseDatabase

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("contacts", Context.MODE_PRIVATE)
    private val repository = ContactsRepository(sharedPreferences)

    fun setDatabase(database: FirebaseDatabase) {
        repository.setDatabase(database)
    }

    val contacts: LiveData<List<Contact>> = repository.getContacts().asLiveData()

    fun getSavedContacts(): List<Contact> {
        return repository.getCachedContacts()
    }

    fun saveContact(contact: Contact) {
        repository.saveContact(contact)
    }

    fun deleteContact(contact: Contact) {
        repository.deleteContact(contact)
    }
}

