package com.github.factotum_sdp.factotum.contacts_content

import kotlin.math.min

/**
 * Class representing a list of contacts.
 */
object ContactsList {

    /**
     * An array of sample contacts.
     */
    val ITEMS: MutableList<Contact> = ArrayList()

    /**
     * A map of sample contacts, by ID.
     */
    private val ITEM_MAP: MutableMap<String, Contact> = HashMap()

    private val randomNames = listOf("John", "Jane", "Bob", "Alice", "Tom", "Mary", "Peter", "Kate", "Jack", "Sarah")
    private val randomSurnames = listOf("Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson")

    private const val COUNT = 10

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createContact(i))
        }
    }

    private fun addItem(item: Contact) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    private fun createContact(position: Int): Contact {
        val pos = min(position, randomNames.size-1)
        return Contact(position.toString(), randomNames[pos], randomSurnames[pos], "../../../../../../res/drawable/contact_image.png")
    }

    /**
     * A data class representing a contact.
     */
    data class Contact(val id: String, val name: String, val surname: String, val profile_pic: String? = null) {
        override fun toString(): String = "$name $surname"
    }
}