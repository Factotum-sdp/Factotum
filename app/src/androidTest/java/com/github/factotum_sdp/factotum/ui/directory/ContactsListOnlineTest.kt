package com.github.factotum_sdp.factotum.ui.directory

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@RunWith(AndroidJUnit4::class)
class ContactsListOnlineTest {

    companion object {
        private var database: FirebaseDatabase = Firebase.database

        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            database.useEmulator("10.0.2.2", 9001)
            MainActivity.setDatabase(database)

            ContactsList.init(database)
        }

        @AfterClass
        @JvmStatic
        fun emptyDatabase() {
            ContactsUtils.emptyFirebaseDatabase(database)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testAddContacts() = runTest {
        ContactsList.populateDatabase()

        val ref = database.getReference("contacts")
        val dataSnapshot = suspendCoroutine<DataSnapshot> { continuation ->
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    continuation.resume(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resumeWithException(databaseError.toException())
                }
            })
        }

        assert(dataSnapshot.hasChildren())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testSyncContactsFromFirebase() = runTest {
        // First, populate the database with contacts
        ContactsList.populateDatabase()
        // Then, synchronize the contacts list with Firebase
        ContactsList.syncContactsFromFirebase(ApplicationProvider.getApplicationContext())
        // Now, check if the local contacts list is not empty
        assert(ContactsList.getItems().isNotEmpty())
    }

}
