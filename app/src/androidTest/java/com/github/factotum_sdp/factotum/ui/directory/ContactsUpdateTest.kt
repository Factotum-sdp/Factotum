package com.github.factotum_sdp.factotum.ui.directory

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.model.AddressCoordinates
import com.github.factotum_sdp.factotum.model.Contact
import com.github.factotum_sdp.factotum.utils.ContactsUtils.Companion.createRandomContacts
import com.github.factotum_sdp.factotum.utils.ContactsUtils.Companion.randomContacts
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.getDatabase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class ContactsUpdateTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    companion object {
        private var nbContacts = 5
        private lateinit var currContact: Contact

        @BeforeClass
        @JvmStatic
        fun setUpFirebase() {
            initFirebase()
            createRandomContacts(1)
            currContact = randomContacts[0]
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

        @BeforeClass
        @JvmStatic
        fun dismissANRSystemDialog() {
            val device = UiDevice.getInstance(getInstrumentation())
            val waitButton = device.findObject(UiSelector().textContains("wait"))
            if (waitButton.exists()) {
                waitButton.click()
            }
        }
    }


    @Before
    fun setUp() {
        GeneralUtils.injectBossAsLoggedInUser(activityRule)
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        onView(withText("@" + currContact.username))
            .perform(click())
        onView(withId(R.id.button_modify_contact)).perform(click())
    }

    @Test
    fun hasAllTheFields() {
        onView((withId(R.id.contactCreationAddress))).check(matches(isDisplayed()))
        onView(withId(R.id.roles_spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextName)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextSurname)).check(matches(isDisplayed()))
        onView(withId(R.id.contactCreationPhoneNumber)).check(matches(isDisplayed()))
        onView(withId(R.id.contactCreationNotes)).check(matches(isDisplayed()))
    }

    @Test
    fun buttonTextIsCorrect() {
        onView(withId(R.id.confirm_form))
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
        onView(withId(R.id.confirm_form)).perform(click())
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
        nameEditText.check(matches(withText(currContact.name)))

        val surnameEditText = onView(withId(R.id.editTextSurname))
        surnameEditText.check(matches(withText(currContact.surname)))

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val addressEditText = device.findObject(By.text(currContact.addressName.toString()))
        assert(addressEditText != null)

        val phoneEditText = onView(withId(R.id.contactCreationPhoneNumber))
        phoneEditText.check(matches(withText(currContact.phone)))

        val notesEditText = onView(withId(R.id.contactCreationNotes))
        notesEditText.check(matches(withText(currContact.details)))

        val roleSpinner = onView(withId(R.id.roles_spinner))
        roleSpinner.check(matches(withSpinnerText(equalToIgnoringCase(currContact.role))))
    }

    @Test
    fun updatedContactHasCorrectValue() {
        val nameEditText = onView(withId(R.id.editTextName))
        nameEditText.perform(ViewActions.replaceText("John"))

        val surnameEditText = onView(withId(R.id.editTextSurname))
        surnameEditText.perform(ViewActions.replaceText("Doe"))

        val usernameEditText = onView(withId(R.id.editTextUsername))
        usernameEditText.perform(ViewActions.replaceText("johndoe"))

        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(
            clearText(),
            typeText("123 Main St\n")
        )
        closeSoftKeyboard()

        val phoneEditText = onView(withId(R.id.contactCreationPhoneNumber))
        phoneEditText.perform(ViewActions.replaceText("555-555-1234"))

        val notesEditText = onView(withId(R.id.contactCreationNotes))
        notesEditText.perform(ViewActions.replaceText("This is a test note."))

        onView(withId(R.id.confirm_form)).perform(click())

        onView(withText("@johndoe")).perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.contact_name))
            .check(matches(withText("John")))
        onView(withId(R.id.contact_surname))
            .check(matches(withText("Doe")))
        onView(withId(R.id.contact_username))
            .check(matches(withText("@johndoe")))
        val address = AddressCoordinates("123 Main St", getApplicationContext())
        onView(withId(R.id.contact_address))
            .check(matches(withText(address?.addressName)))
        onView(withId(R.id.contact_phone))
            .check(matches(withText("555-555-1234")))
        onView(withId(R.id.contact_details))
            .check(matches(withText("This is a test note.")))
        //reset the original contact value
        getDatabase().reference.child("contacts").child("johndoe").removeValue()
        getDatabase().reference.child("contacts").child(currContact.username).setValue(currContact)
    }
}
