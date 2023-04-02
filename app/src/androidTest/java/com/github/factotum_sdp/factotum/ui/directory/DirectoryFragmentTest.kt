package com.github.factotum_sdp.factotum.ui.directory

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
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
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DirectoryFragmentTest {
    private val _waitForAnimation = 500L

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            val database = Firebase.database
            database.useEmulator("10.0.2.2", 9000)
            MainActivity.setDatabase(database)

            ContactsUtils.emptyFirebaseDatabase(database)

            ContactsList.init(database)

            runBlocking {
                ContactsList.populateDatabase()
            }
        }
    }

    @Before
    fun setUp() {

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
    fun allContactsCanBeClickedOn() {
        val device = UiDevice.getInstance(getInstrumentation())
        for (i in 0 until ContactsList.size) {
            onView(withId(R.id.contacts_recycler_view))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        i,
                        click()
                    )
                )
            val contactName =
                device.findObject(UiSelector().descriptionContains("All contact Info"))
            assertTrue(contactName.exists())
            device.pressBack()
            Thread.sleep(_waitForAnimation)
        }
    }

    @Test
    fun correctContactsShownWithMatchingQuery() {
        // Type a search query in the search view and close the soft keyboard
        onView(withId(R.id.contacts_search_view))
            .perform(typeText("John"), closeSoftKeyboard())

        // Check if the expected contact is visible in the RecyclerView
        onView(withId(R.id.contacts_recycler_view))
            .perform(scrollToHolder(ContactsUtils.withHolderContactName("John Smith")))
    }

}