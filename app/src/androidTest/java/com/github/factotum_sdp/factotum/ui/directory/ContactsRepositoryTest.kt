package com.github.factotum_sdp.factotum.ui.directory

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.getDatabase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class ContactsRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: ContactsRepository

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpFirebase() {
            initFirebase()
        }
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val sharedPreferences = context.getSharedPreferences("contacts_test", Context.MODE_PRIVATE)
        repository = ContactsRepository(sharedPreferences)
        repository.setDatabase(getDatabase())
    }

    /*
    @After
    fun tearDown() {
        val sharedPreferences = context.getSharedPreferences("contacts_test", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun savesContactToSharedPreferences() = runTest {
        // Save a contact to the cache
        val contact = Contact(
            "1",
            "Manager",
            "John",
            "Doe",
            R.drawable.contact_image,
            "123 Main St",
            "555-555-1234"
        )

        runBlocking {
            repository.saveContactToSharedPreferences(contact)
        }
        // Verify that the contact was saved to the cache
        val cachedContacts = repository.getCachedContacts()
        Assert.assertEquals(1, cachedContacts.size)
        Assert.assertEquals(contact, cachedContacts[0])
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun savesContactOnline() = runTest {
        val contact = Contact(
            "1",
            "Manager",
            "John",
            "Doe",
            R.drawable.contact_image,
            "123 Main St",
            "555-555-1234"
        )
        val latch = CountDownLatch(1)

        runBlocking {
            repository.saveContact(contact)
        }

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

     */ // Do not empty the databse or work by injection

}
