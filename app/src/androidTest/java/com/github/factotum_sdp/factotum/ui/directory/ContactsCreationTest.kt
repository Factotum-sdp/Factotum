package com.github.factotum_sdp.factotum.ui.directory

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By.*
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.Location
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import junit.framework.TestCase.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactsCreationTest {


    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    companion object {
        private const val nbContacts = 5

        @BeforeClass
        @JvmStatic
        fun firebaseSetup() {
            initFirebase()
        }
    }

    @Before
    fun setUp() {
        ContactsUtils.emptyFirebaseDatabase()

        runBlocking {
            ContactsUtils.populateDatabase(nbContacts)
        }
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        onView(withId(R.id.add_contact_button)).perform(click())
    }

    @Test
    fun hasAllTheFields() {
        onView((withId(R.id.contact_image_creation))).check(matches(isDisplayed()))
        onView((withId(R.id.contactCreationAddress))).check(matches(isDisplayed()))
        onView(withId(R.id.roles_spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextName)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextSurname)).check(matches(isDisplayed()))
        onView(withId(R.id.contactCreationPhoneNumber)).check(matches(isDisplayed()))
        onView(withId(R.id.contactCreationNotes)).check(matches(isDisplayed()))
    }

    @Test
    fun buttonTextIsCorrect() {
        onView(withId(R.id.create_contact)).check(matches(withText("Create Contact")))
    }

    @Test
    fun hasRoles() {
        onView(withId(R.id.roles_spinner)).check(matches(isDisplayed()))
    }

    @Test
    fun allFieldsAreEditable() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val fields = Contact::class.java.declaredFields
        for (param in fields) {
            if (param.isSynthetic) continue
            val editText = device.findObject(clazz(EditText::class.java.name))
            editText.text = "test"
            assertEquals("test", editText.text)
        }
    }

    @Test
    fun canCreateContact() {
        onView(withId(R.id.create_contact)).perform(click())
        //check if recycle view in contacts has 6 items
        onView(withId(R.id.contacts_recycler_view))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }
                val recyclerView = view as RecyclerView
                val adapter = recyclerView.adapter
                assert(adapter?.itemCount == nbContacts + 1)
            }
    }


    @Test
    fun createdContactHasCorrectValue() {
        val nameEditText = onView(withId(R.id.editTextName))
        nameEditText.perform(replaceText("John"))

        val surnameEditText = onView(withId(R.id.editTextSurname))
        surnameEditText.perform(replaceText("Doe"))

        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(
            typeText("123 Main St")
        )
        closeSoftKeyboard()

        val phoneEditText = onView(withId(R.id.contactCreationPhoneNumber))
        phoneEditText.perform(replaceText("555-555-1234"))

        val notesEditText = onView(withId(R.id.contactCreationNotes))
        notesEditText.perform(replaceText("This is a test note."))

        onView(withId(R.id.create_contact)).perform(click())
        onView(withId(R.id.contacts_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    nbContacts,
                    click()
                )
            )

        onView(withId(R.id.contact_name)).check(matches(withText("John")))
        onView(withId(R.id.contact_surname)).check(matches(withText("Doe")))
        onView(withId(R.id.contact_address)).check(matches(withText("123 Main St")))
        onView(withId(R.id.contact_phone)).check(matches(withText("555-555-1234")))
        onView(withId(R.id.contact_details)).check(matches(withText("This is a test note.")))
    }


    @Test
    fun writeInAddressFieldMakesDropDown() {
        val city = "Lausanne"
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText(city.dropLast(2)))
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val result = device.wait(Until.hasObject(textContains(city)), 5000)
        assertTrue(result)
    }


    @Test
    fun selectSuggestionWritesAddress() {
        val city = "Lausanne"
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText(city))
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val lausanneResult =
            Location.geocoderQuery(city, getApplicationContext())!![0].getAddressLine(0)
        val address = device.findObject(text(city))
        val result = device.wait(Until.hasObject(text(lausanneResult)), 5000)
        assertTrue(result)
        device.findObject(text(lausanneResult)).click()
        val addressChanged = address.wait(Until.textMatches(lausanneResult), 5000)
        assertTrue(addressChanged)
    }

}