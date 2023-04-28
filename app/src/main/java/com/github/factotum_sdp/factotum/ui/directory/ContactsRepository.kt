package com.github.factotum_sdp.factotum.ui.directory

import android.content.SharedPreferences
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ContactsRepository(
    private val sharedPreferences: SharedPreferences
) {

    private val database = Firebase.database
    private var firebaseContactsRef: DatabaseReference = database.reference.child("contacts")

    fun setDatabase(database: FirebaseDatabase) {
        firebaseContactsRef = database.reference.child("contacts")
    }

    fun saveContactToSharedPreferences(contact: Contact) =
        sharedPreferences.edit().putString(contact.username, Gson().toJson(contact)).apply()

    fun saveContact(contact: Contact) {
        saveContactToSharedPreferences(contact)
        firebaseContactsRef.child(contact.username).setValue(contact)
    }

    fun setContacts(contacts: List<Contact>) {
        for (contact in contacts) {
            saveContactToSharedPreferences(contact)
        }
        firebaseContactsRef.setValue(contacts)
    }


    fun getContact(contactId: String): Contact? {
        val contactJson = sharedPreferences.getString(contactId, null)
        return if (contactJson != null) {
            Gson().fromJson(contactJson, Contact::class.java)
        } else {
            null
        }
    }

    fun deleteContact(contact: Contact) {
        sharedPreferences.edit().remove(contact.username).apply()
        firebaseContactsRef.child(contact.username).removeValue()
    }

    fun getContacts(): Flow<List<Contact>> {
        return callbackFlow {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val contactsList = mutableListOf<Contact>()
                    for (contactSnapshot in snapshot.children) {
                        val contact = contactSnapshot.getValue(Contact::class.java)
                        contact?.let {
                            contactsList.add(it)
                            saveContactToSharedPreferences(it)
                        }
                    }
                    trySend(contactsList).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }

            firebaseContactsRef.addValueEventListener(valueEventListener)

            awaitClose { firebaseContactsRef.removeEventListener(valueEventListener) }
        }
    }

    fun getCachedContacts(): List<Contact> {
        val contactsList = mutableListOf<Contact>()
        for (contactId in sharedPreferences.all.keys) {
            val contactJson = sharedPreferences.getString(contactId, null)
            if (contactJson != null) {
                val contact = Gson().fromJson(contactJson, Contact::class.java)
                contactsList.add(contact)
            }
        }
        return contactsList

    }
}
