package com.github.factotum_sdp.factotum.ui.directory

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@RunWith(AndroidJUnit4::class)
class ContactsListOnlineTest {
    private lateinit var database: FirebaseDatabase

    @Before
    fun setUp() {

        database = Firebase.database
        //database.useEmulator("10.0.2.2", 9000)

        emptyFirebaseDatabase(database)

        ContactsList.init(database)

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

    @After
    fun tearDown() {
        emptyFirebaseDatabase(database)
    }

}
