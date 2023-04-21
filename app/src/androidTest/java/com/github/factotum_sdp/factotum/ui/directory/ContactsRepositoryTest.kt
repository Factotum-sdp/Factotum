package com.github.factotum_sdp.factotum.ui.directory

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.setEmulatorGet
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class ContactsRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: ContactsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val sharedPreferences = context.getSharedPreferences("contacts_test", Context.MODE_PRIVATE)
        repository = ContactsRepository(sharedPreferences)
        repository.setDatabase(setEmulatorGet())
        ContactsUtils.emptyFirebaseDatabase()
    }

    @After
    fun tearDown() {
        val sharedPreferences = context.getSharedPreferences("contacts_test", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun savesContactToSharedPreferences() = runBlocking {
        // Save a contact to the cache
        val contact = Contact("1", "Manager", "John", "Doe", R.drawable.contact_image, "123 Main St", "555-555-1234")
        repository.saveContactToSharedPreferences(contact)

        // Verify that the contact was saved to the cache
        val cachedContacts = repository.getCachedContacts()
        Assert.assertEquals(1, cachedContacts.size)
        Assert.assertEquals(contact, cachedContacts[0])
    }


    @Test
    fun savesContactOnline() = runBlocking {
        val contact = Contact("1", "Manager", "John", "Doe", R.drawable.contact_image, "123 Main St", "555-555-1234")
        val latch = CountDownLatch(1)

        repository.saveContact(contact)

        val contacts = repository.getContacts().first()

        if (contacts.isNotEmpty()) {
            Assert.assertEquals(1, contacts.size)
            Assert.assertEquals(contact, contacts[0])
            latch.countDown()
        }

        if (!withContext(Dispatchers.IO) {
                latch.await(5, TimeUnit.SECONDS)
            }) {
            Assert.fail("Timeout waiting for contacts to update")
        }
    }

}