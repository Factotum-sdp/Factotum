package com.github.factotum_sdp.factotum.utils

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.getDatabase
import kotlinx.coroutines.CompletableDeferred
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class ContactsUtils {

    companion object {
        //fake data --> to be replaced with connection to database
        private val randomContacts = mutableListOf<Contact>()
        private val randomNames = listOf("John", "Jane", "Joe")
        private var randomSurnames = listOf("Smith", "Jones", "Williams")
        private val roles = listOf("Boss", "Courier", "Client")
        private val randomAddresses =
            listOf("123 Fake Street", "456 Fake Street", "789 Fake Street")
        private val randomPhones = listOf("123456789", "987654321", "123987456")
        private val randomDetails = listOf("I am a boss", "I am a courier", "I am a client")

        private const val image = R.drawable.contact_image

        private fun createContact(position: Int): Contact {
            return Contact(
                username = "0$position",
                role = roles[position % roles.size],
                name = randomNames[position % randomNames.size],
                surname = randomSurnames[position % randomSurnames.size],
                profile_pic_id = image,
                address = randomAddresses[position % randomAddresses.size],
                phone = randomPhones[position % randomPhones.size],
                details = randomDetails[position % randomDetails.size]
            )
        }

        private fun createRandomContacts(count: Int) {
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

            for (contact in randomContacts) {
                getDatabase().getReference("contacts").child(contact.username).setValue(contact)
                    .addOnSuccessListener {
                        deferred.complete(Unit)
                    }
                    .addOnFailureListener { exception ->
                        deferred.completeExceptionally(exception)
                    }
            }

            deferred.await()
        }

        fun emptyFirebaseDatabase() {
            getDatabase().reference.child("contacts").removeValue()
        }

        fun withHolderContactName(name: String): Matcher<RecyclerView.ViewHolder> {
            return object : TypeSafeMatcher<RecyclerView.ViewHolder>() {
                var isFirstMatch = true

                override fun describeTo(description: Description) {
                    description.appendText("RecyclerView holder with contact name: $name")
                }

                override fun matchesSafely(item: RecyclerView.ViewHolder): Boolean {
                    val holderName =
                        item.itemView.findViewById<TextView>(R.id.contact_surname_and_name).text.toString()
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