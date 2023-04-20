package com.github.factotum_sdp.factotum.ui.directory

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.setEmulatorGet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@RunWith(AndroidJUnit4::class)
class ContactsListOnlineTest {

   @get:Rule
   var testRule = ActivityScenarioRule(
       MainActivity::class.java
   )

    companion object {
        private lateinit var database: FirebaseDatabase
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            database = setEmulatorGet()
            MainActivity.setDatabase(database)
        }

        @AfterClass
        @JvmStatic
        fun emptyDatabase() {
            ContactsUtils.emptyFirebaseDatabase()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testAddContacts() = runTest {
        ContactsUtils.populateDatabase()

        val ref = database.getReference("contacts")
        val dataSnapshot = suspendCoroutine { continuation ->
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

}
