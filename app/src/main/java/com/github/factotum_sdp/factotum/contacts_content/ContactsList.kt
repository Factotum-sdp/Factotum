package com.github.factotum_sdp.factotum.contacts_content

import android.os.Parcelable
import com.github.factotum_sdp.factotum.R

/**
 * Class representing a list of contacts.
 */
object ContactsList {

    /**
     * An array of sample contacts.
     */
    val ITEMS: MutableList<Contact> = ArrayList()

    private val randomNames = listOf("John", "Jane", "Bob", "Alice", "Tom", "Mary", "Peter", "Kate", "Jack", "Sarah")
    private val roles = listOf("Boss", "Courier", "Client")
    private val randomAddresses = listOf("123 Fake Street", "456 Fake Street", "789 Fake Street")
    private val randomPhones = listOf("123456789", "987654321", "123987456")

    private const val image = R.drawable.contact_image

    private const val COUNT = 10

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createContact(i))
        }
    }

    private fun addItem(item: Contact) {
        ITEMS.add(item)
    }

    private fun createContact(position: Int): Contact {
        return Contact(roles[position % roles.size], randomNames[position % randomNames.size], image,
            randomAddresses[position % randomAddresses.size], randomPhones[position % randomPhones.size])
    }

    /**
     * A data class representing a contact.
     */
    @kotlinx.parcelize.Parcelize
    data class Contact(val role: String, val name: String, val profile_pic_id: Int, val address: String, val phone: String, val details: String? = null) : Parcelable {
        override fun toString(): String = "$role: $name"
    }
}