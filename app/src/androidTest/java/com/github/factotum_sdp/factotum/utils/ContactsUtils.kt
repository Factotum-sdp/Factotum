package com.github.factotum_sdp.factotum.utils

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class ContactsUtils {

    companion object {
        //fake data --> to be replaced with connection to database
        private val randomContacts = mutableListOf<Contact>()
        private var emulatorSet : Boolean = false
        private var database : FirebaseDatabase = Firebase.database
        private val randomNames = listOf("John Smith", "Jane Doe", "Bob Builder")
        private val roles = listOf("Boss", "Courier", "Client")
        private val randomAddresses = listOf("123 Fake Street", "456 Fake Street", "789 Fake Street")
        private val randomPhones = listOf("123456789", "987654321", "123987456")
        private val randomDetails = listOf("I am a boss", "I am a courier", "I am a client")

        private const val image = R.drawable.contact_image

        fun setEmulatorGet() : FirebaseDatabase {
            if (!emulatorSet) {
                database.useEmulator("10.0.2.2", 9000)
                emulatorSet = true
            }
            return database
        }

        private fun createContact(position: Int): Contact {
            return Contact(
                "$position",
                roles[position % roles.size], randomNames[position % randomNames.size], image,
                randomAddresses[position % randomAddresses.size],
                randomPhones[position % randomPhones.size],
                randomDetails[position % randomDetails.size])
        }

        private fun createRandomContacts(count : Int) {
            randomContacts.clear()
            for (i in randomContacts.size until count) {
                randomContacts.add(createContact(i))
            }
        }

        fun getContacts(): List<Contact> {
            return randomContacts
        }

        /**
         * Populates the database with random contacts.
         */
        suspend fun populateDatabase(count: Int = 5) {
            val deferred = CompletableDeferred<Unit>()

            createRandomContacts(count)

            database.getReference("contacts").setValue(randomContacts)
                .addOnSuccessListener {
                    deferred.complete(Unit)
                }
                .addOnFailureListener { exception ->
                    deferred.completeExceptionally(exception)
                }

            deferred.await()
        }

        fun emptyFirebaseDatabase() {
            database.reference.child("contacts").removeValue()
        }

        fun withHolderContactName(name: String): Matcher<RecyclerView.ViewHolder> {
            return object : TypeSafeMatcher<RecyclerView.ViewHolder>() {
                var isFirstMatch = true

                override fun describeTo(description: Description) {
                    description.appendText("RecyclerView holder with contact name: $name")
                }

                override fun matchesSafely(item: RecyclerView.ViewHolder): Boolean {
                    val holderName =
                        item.itemView.findViewById<TextView>(R.id.contact_name).text.toString()
                    if (holderName == name && isFirstMatch) {
                        isFirstMatch = false
                        return true
                    }
                    return false
                }
            }
        }
    }
}