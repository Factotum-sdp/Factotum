package com.github.factotum_sdp.factotum.ui.directory

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.setEmulatorGet
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactDetailsFragmentTest {
    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {

            val database = setEmulatorGet()
            MainActivity.setDatabase(database)
        }
    }

    @Before
    fun goToContactDetails() {
        ContactsUtils.emptyFirebaseDatabase()

        runBlocking {
            ContactsUtils.populateDatabase(5)
        }
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        onView(withId(R.id.contacts_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @Test
    fun buttonIsDisplayed() {
        onView(withId(R.id.button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun modifyButtonIsDisplayed() {
        onView(withId(R.id.button_modify_contact))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deleteButtonIsDisplayed() {
        onView(withId(R.id.button_delete_contact))
            .check(matches(isDisplayed()))
    }

    @Test
    fun buttonIsClickable() {
        onView(withId(R.id.button))
            .perform(click())
    }

    @Test
    fun modifyButtonIsClickable() {
        onView(withId(R.id.button_modify_contact))
            .perform(click())
    }

    @Test
    fun deleteButtonIsClickable() {
        onView(withId(R.id.button_delete_contact))
            .perform(click())
    }

    @Test
    fun correctInfoIsDisplayed() {
        onView(withId(R.id.contact_name))
            .check(matches(withText(ContactsUtils.getContacts()[0].name)))
        onView(withId(R.id.contact_surname))
            .check(matches(withText(ContactsUtils.getContacts()[0].surname)))
        onView(withId(R.id.contact_phone))
            .check(matches(withText(ContactsUtils.getContacts()[0].phone)))
        onView(withId(R.id.contact_role))
            .check(matches(withText(ContactsUtils.getContacts()[0].role)))
        onView(withId(R.id.contact_address))
            .check(matches(withText(ContactsUtils.getContacts()[0].address)))
        if (ContactsUtils.getContacts()[0].details != null) {
            onView(withId(R.id.contact_details))
                .check(matches(withText(ContactsUtils.getContacts()[0].details)))
        }
    }

    @Test
    fun correctImageIsDisplayed() {
        onView(withId(R.id.contact_image))
            .check(matches(isDisplayed()))
    }

    @Test
    fun buttonReturnsToContacts() {
        onView(withId(R.id.button))
            .perform(click())
        onView(withId(R.id.contacts_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun modifyButtonGoesToContactEdition() {
        onView(withId(R.id.button_modify_contact))
            .perform(click())
        onView(withId(R.id.contact_creation_fragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deleteButtonReturnsToContacts() {
        onView(withId(R.id.button_delete_contact))
            .perform(click())
        onView(withId(R.id.contacts_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deleteButtonDeletesContact() {
        onView(withId(R.id.button_delete_contact))
            .perform(click())
        onView(withId(R.id.contacts_recycler_view))
            .check(matches(isDisplayed()))
        //check if size of recycler view is 4
        onView(withId(R.id.contacts_recycler_view))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }
                val recyclerView = view as RecyclerView
                val adapter = recyclerView.adapter
                assert(adapter?.itemCount == 4)
            }
    }

}