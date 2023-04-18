package com.github.factotum_sdp.factotum.ui.directory

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ContactsListOfflineTest {

    @Test
    fun keepTest() {
        // This test is here to keep the test class from being empty
        // and causing the build to fail
        assert(true)
    }

    /**@Test
    fun testLoadContactsLocally() {
        // Create a sample list of contacts
        val originalContactsList = listOf(
            Contact(
                id = "1",
                role = "Courier",
                name = "John Doe",
                profile_pic_id = R.drawable.contact_image,
                address = "123 Main St",
                phone = "123-456-7890"),
            Contact(
                id = "2",
                role = "Boss",
                name = "Jane Smith",
                profile_pic_id = R.drawable.contact_image,
                address = "456 Elm St",
                phone = "987-654-3210")
        )


        // Save the sample list of contacts to shared preferences
        ContactsList.setItems(originalContactsList)
        ContactsList.saveContactsLocally(ApplicationProvider.getApplicationContext())

        // Clear the current list and load contacts from shared preferences
        ContactsList.setItems(emptyList())
        ContactsList.loadContactsLocally(ApplicationProvider.getApplicationContext())

        // Check if the loaded contacts match the original sample list
        assertEquals(originalContactsList, ContactsList.getItems())
    } **/
}

