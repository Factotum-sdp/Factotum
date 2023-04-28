package com.github.factotum_sdp.factotum.ui.directory

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.google.firebase.database.FirebaseDatabase

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("contacts", Context.MODE_PRIVATE)
    private val repository = ContactsRepository(sharedPreferences)

    fun setDatabase(database: FirebaseDatabase) {
        repository.setDatabase(database)
    }

    val contacts: LiveData<List<Contact>> = repository.getContacts().asLiveData()

    fun saveNewIDContact(
        role: String,
        name: String,
        surname: String,
        image: Int,
        addressName: String?,
        latitude: Double?,
        longitude: Double?,
        phone: String,
        details: String = ""
    ) {
        repository.saveNewIDContact(role, name, surname, image, addressName ,latitude, longitude, phone, details)
    }

    fun updateContact(contact: Contact) {
        repository.saveContact(contact)
    }

    fun deleteContact(contact: Contact) {
        repository.deleteContact(contact)
    }
}

