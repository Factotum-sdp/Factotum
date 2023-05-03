package com.github.factotum_sdp.factotum.ui.directory

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.getDatabase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DirectoryFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        private var nbContacts = 0

        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            initFirebase()
            nbContacts = getDatabase().reference.child("contacts").get().run {
                addOnSuccessListener {
                    nbContacts = it.childrenCount.toInt()
                }
                addOnFailureListener {
                    fail("Could not get the number of contacts in the database")
                }
                nbContacts
            }
        }
    }

    @Before
    fun setUp() {
        GeneralUtils.injectBossAsLoggedInUser(testRule)
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
    }

    @Test
    fun recyclerViewIsCorrectlyDisplayed() {
        onView(withId(R.id.contacts_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun goesToContactDetails() {
        val device = UiDevice.getInstance(getInstrumentation())
        onView(withId(R.id.contacts_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
        val contactName = device.findObject(UiSelector().descriptionContains("All contact Info"))
        assertTrue(contactName.exists())
    }

    @Test
    fun addButtonExists() {
        onView(withId(R.id.add_contact_button)).check(matches(isDisplayed()))

    }

    @Test
    fun addButtonOpensContactCreation() {
        onView(withId(R.id.add_contact_button)).perform(click())
        onView(withId(R.id.contact_creation_fragment)).check(matches(isDisplayed()))
    }


    @Test
    fun allContactsCanBeClickedOn() {
        for (i in 0 until nbContacts) {
            onView(withId(R.id.contacts_recycler_view))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        i,
                        click()
                    )
                )
            onView(withId(R.id.contact_details_fragment)).check(matches(isDisplayed()))
            pressBack()
        }
    }


    @Test
    fun correctContactsShownWithMatchingQuery() {
        // Type a search query in the search view and close the soft keyboard
        onView(withId(R.id.contacts_search_view))
            .perform(typeText("John"), closeSoftKeyboard())

        // Check if the expected contact is visible in the RecyclerView
        onView(withId(R.id.contacts_recycler_view))
            .perform(scrollToHolder(ContactsUtils.withHolderContactName("Smith  John")))
    }

    @Test
    fun incorrectQueryShowsNoMatchingQueryMessage() {
        onView(withId(R.id.empty_contacts_message)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        // Type a search query in the search view and close the soft keyboard
        onView(withId(R.id.contacts_search_view))
            .perform(typeText("urpioeqwjlfdaff"), closeSoftKeyboard())

        // Check if the expected contact is visible in the RecyclerView
        onView(withId(R.id.empty_contacts_message)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

}
