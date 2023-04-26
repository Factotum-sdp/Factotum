package com.github.factotum_sdp.factotum.ui.directory

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class ContactsUpdateTest {
    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    companion object {
        private const val nbContacts = 5

        @BeforeClass
        @JvmStatic
        fun setUpFirebase() {
            initFirebase()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        runTest {
            ContactsUtils.emptyFirebaseDatabase()

            runBlocking {
                ContactsUtils.populateDatabase(nbContacts)
            }
            onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open())
            onView(withId(R.id.directoryFragment))
                .perform(ViewActions.click())
            onView(withId(R.id.contacts_recycler_view))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        ViewActions.click()
                    )
                )
            onView(withId(R.id.button_modify_contact)).perform(ViewActions.click())
        }
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
        onView(withId(R.id.create_contact))
            .check(matches(withText("Update Contact")))
    }

    @Test
    fun allFieldsAreEditable() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val fields = Contact::class.java.declaredFields
        for (param in fields) {
            if (param.isSynthetic) continue
            val editText = device.findObject(By.clazz(EditText::class.java.name))
            editText.text = "test"
            assertEquals("test", editText.text)
        }
    }

    @Test
    fun updateDoesntAddOrRemoveContact() {
        onView(withId(R.id.create_contact)).perform(ViewActions.click())
        //check if recycle view in contacts has 6 items
        onView(withId(R.id.contacts_recycler_view))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }
                val recyclerView = view as RecyclerView
                val adapter = recyclerView.adapter
                assert(adapter?.itemCount == nbContacts)
            }
    }


    @Test
    fun fieldsContainContactValues() {
        val nameEditText = onView(withId(R.id.editTextName))
        nameEditText.check(matches(withText(ContactsUtils.getContacts()[0].name)))

        val surnameEditText = onView(withId(R.id.editTextSurname))
        surnameEditText.check(matches(withText(ContactsUtils.getContacts()[0].surname)))

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val addressEditText = device.findObject(By.text(ContactsUtils.getContacts()[0].address))
        assert(addressEditText != null)

        val phoneEditText = onView(withId(R.id.contactCreationPhoneNumber))
        phoneEditText.check(matches(withText(ContactsUtils.getContacts()[0].phone)))

        val notesEditText = onView(withId(R.id.contactCreationNotes))
        notesEditText.check(matches(withText(ContactsUtils.getContacts()[0].details)))

        val roleSpinner = onView(withId(R.id.roles_spinner))
        roleSpinner.check(matches(withSpinnerText(ContactsUtils.getContacts()[0].role)))
    }

    @Test
    fun updatedContactHasCorrectValue() {
        val nameEditText = onView(withId(R.id.editTextName))
        nameEditText.perform(ViewActions.replaceText("John"))

        val surnameEditText = onView(withId(R.id.editTextSurname))
        surnameEditText.perform(ViewActions.replaceText("Doe"))

        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(
            clearText(),
            typeText("123 Main St")
        )
        closeSoftKeyboard()

        val phoneEditText = onView(withId(R.id.contactCreationPhoneNumber))
        phoneEditText.perform(ViewActions.replaceText("555-555-1234"))

        val notesEditText = onView(withId(R.id.contactCreationNotes))
        notesEditText.perform(ViewActions.replaceText("This is a test note."))

        onView(withId(R.id.create_contact)).perform(ViewActions.click())
        onView(withId(R.id.contacts_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )

        onView(withId(R.id.contact_name))
            .check(matches(withText("John")))
        onView(withId(R.id.contact_surname))
            .check(matches(withText("Doe")))
        onView(withId(R.id.contact_address))
            .check(matches(withText("123 Main St")))
        onView(withId(R.id.contact_phone))
            .check(matches(withText("555-555-1234")))
        onView(withId(R.id.contact_details))
            .check(matches(withText("This is a test note.")))
    }
}