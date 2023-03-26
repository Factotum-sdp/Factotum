package com.github.factotum_sdp.factotum.placeholder

import android.content.Context
import android.content.SharedPreferences
import com.github.factotum_sdp.factotum.ui.directory.ContactsDataSource
import com.github.factotum_sdp.factotum.ui.directory.FirebaseContactsDataSource
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred

val database = Firebase.database
private const val CONTACTS_PREFS = "contacts_prefs"
private const val CONTACTS_KEY = "contacts_key"

/**
 * Class representing a list of contacts.
 */
object ContactsList {

    val contacts = mutableListOf<Contact>()
    private lateinit var dataSource: ContactsDataSource

    fun init(dataSource: ContactsDataSource = FirebaseContactsDataSource()) {
        this.dataSource = dataSource
    }

    /*
    //fake data --> to be replaced with connection to database
    private val randomNames = listOf("John Smith", "Jane Doe", "Bob Builder")
    private val roles = listOf("Boss", "Courier", "Client")
    private val randomAddresses = listOf("123 Fake Street", "456 Fake Street", "789 Fake Street")
    private val randomPhones = listOf("123456789", "987654321", "123987456")
    private val randomDetails = listOf("I am a boss", "I am a courier", "I am a client")

     */

    //private const val image = R.drawable.contact_image


    //Trivial method for now but will be useful when connecting to database
    private fun addItem(item: Contact) {
        contacts.add(item)
    }

    /*
    private fun createContact(position: Int): Contact {
        return Contact(
            "$position",
            roles[position % roles.size], randomNames[position % randomNames.size], image,
                randomAddresses[position % randomAddresses.size],
                randomPhones[position % randomPhones.size],
                randomDetails[position % randomDetails.size])
    }
     */

    /**
     * Saves the contacts list to a local storage.
     */
    fun saveContactsLocally(context: Context) {
        // Get an instance of SharedPreferences with a specific name (CONTACTS_PREFS) and mode (private)
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE)

        // Get an editor for SharedPreferences to make changes
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Create a Gson object to convert the contacts list into a JSON string
        val gson = Gson()

        // Serialize the contacts list into a JSON string
        val jsonContacts = gson.toJson(contacts)

        // Save the JSON string into SharedPreferences with the key CONTACTS_KEY
        editor.putString(CONTACTS_KEY, jsonContacts)

        // Apply the changes to SharedPreferences
        editor.apply()
    }


    /**
     * Loads the contacts list from local storage.
     */
    fun loadContactsLocally(context: Context) {
        // Get an instance of SharedPreferences with the specific name (CONTACTS_PREFS) and mode (private)
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE)

        // Create a Gson object to convert the JSON string back to the contacts list
        val gson = Gson()

        // Retrieve the JSON string from SharedPreferences using the key CONTACTS_KEY
        val jsonContacts = sharedPreferences.getString(CONTACTS_KEY, null)

        // Check if the JSON string is not null (meaning there are contacts saved in SharedPreferences)
        if (jsonContacts != null) {
            // Define the type for deserialization (List<Contact>)
            val type = object : TypeToken<List<Contact>>() {}.type

            // Clear the current contacts list
            contacts.clear()

            // Deserialize the JSON string and add the contacts to the list
            contacts.addAll(gson.fromJson(jsonContacts, type))
        }
    }

    /**
     * Synchronizes the contacts list with Firebase Realtime Database.
     */
    suspend fun syncContactsFromFirebase(context: Context) {
        // Get a reference to the database node
        val contactsRef = dataSource.getContactsReference()

        // Create a CompletableDeferred object to wait for the completion of the onDataChange method
        val deferred = CompletableDeferred<Unit>()

        // Add a ValueEventListener for a single read from Firebase Realtime Database
        contactsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the current contacts list
                contacts.clear()

                // Iterate through each child (contact) in dataSnapshot
                for (contactSnapshot in dataSnapshot.children) {
                    // Deserialize the contactSnapshot into a Contact object
                    val contact = contactSnapshot.getValue(Contact::class.java)

                    // Check if the contact is not null and add it to the contacts list
                    if (contact != null) {
                        contacts.add(contact)
                    }
                }

                // Save the updated contacts list to local storage
                saveContactsLocally(context)

                // Mark the CompletableDeferred as complete
                deferred.complete(Unit)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Mark the CompletableDeferred as failed with an exception
                deferred.completeExceptionally(databaseError.toException())
            }
        })

        // Wait for the completion of the onDataChange method
        deferred.await()
    }


    /*
    /**
     * Saves the contacts to the realtime database.
     */
    suspend fun saveContactsToRealtimeDatabase() {
        val deferred = CompletableDeferred<Unit>()

        db.child("contacts").setValue(contacts)
            .addOnSuccessListener {
                deferred.complete(Unit)
            }
            .addOnFailureListener { exception ->
                deferred.completeExceptionally(exception)
            }

        deferred.await()
    }       */




    /**
     * A data class representing a contact.
     */
    data class Contact(
        val id: String,
        val role: String,
        val name: String,
        val profile_pic_id: Int,
        val address: String,
        val phone: String,
        val details: String? = null)
    {
        constructor() : this("", "", "", 0, "", "", null)
    }
}