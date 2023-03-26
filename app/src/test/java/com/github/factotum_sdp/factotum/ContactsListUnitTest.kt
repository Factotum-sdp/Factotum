package com.github.factotum_sdp.factotum

import android.content.Context
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.github.factotum_sdp.factotum.ui.directory.ContactsDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import org.mockito.ArgumentMatchers.any

@RunWith(MockitoJUnitRunner::class)
class ContactsListUnitTest {

    private val mockDataSource: ContactsDataSource = mock(ContactsDataSource::class.java)

    @Before
    fun setUp() {
        ContactsList.init(mockDataSource)
    }

    @Test
    fun testSyncContactsFromFirebase() {
        // Prepare the mocked data snapshot
        val dataSnapshot = mock(DataSnapshot::class.java)
        val contactSnapshot = mock(DataSnapshot::class.java)
        val contact = ContactsList.Contact("1", "Role", "Name", 0, "Address", "Phone", "Details")

        Mockito.`when`(contactSnapshot.getValue(ContactsList.Contact::class.java)).thenReturn(contact)
        Mockito.`when`(dataSnapshot.children).thenReturn(listOf(contactSnapshot).asIterable())
        Mockito.`when`(mockDataSource.getContactsReference().addListenerForSingleValueEvent(any(
            ValueEventListener::class.java)))
            .thenAnswer {
                (it.arguments[0] as ValueEventListener).onDataChange(dataSnapshot)
            }

        // Test the syncContactsFromFirebase method
        runBlocking {
            ContactsList.syncContactsFromFirebase(mock(Context::class.java))
        }

        // Verify that the local contacts list has been updated
        assertEquals(1, ContactsList.contacts.size)
        assertEquals(contact, ContactsList.contacts[0])
    }
}
