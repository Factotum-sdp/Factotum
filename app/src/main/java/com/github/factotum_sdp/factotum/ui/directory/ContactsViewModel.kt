package com.github.factotum_sdp.factotum.ui.directory

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private var database = FirebaseDatabase.getInstance()
    private var databaseRef = database.reference.child("contacts")
    private val contactsPreferences: SharedPreferences = application.getSharedPreferences("contacts", Context.MODE_PRIVATE)

    fun setDatabase(database: FirebaseDatabase) {
        this.database = database
        databaseRef = database.reference.child("contacts")
    }

    fun getContacts(): LiveData<List<Contact>> {
        val contacts: MutableLiveData<List<Contact>> = MutableLiveData()
        val contactsJson = contactsPreferences.getString("contacts", null)
        if (contactsJson != null) {
            val contactList = Gson().fromJson<List<Contact>>(contactsJson, object : TypeToken<List<Contact>>() {}.type)
            contacts.value = contactList
        }
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contactList = mutableListOf<Contact>()
                for (contactSnapshot in snapshot.children) {
                    val contact = contactSnapshot.getValue(Contact::class.java)
                    contact?.let { contactList.add(it) }
                }
                contacts.value = contactList
                saveContacts(contactList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error...
            }
        })
        return contacts
    }

    private fun saveContacts(contacts: List<Contact>) {
        val contactsJson = Gson().toJson(contacts)
        contactsPreferences.edit().putString("contacts", contactsJson).apply()
    }


}