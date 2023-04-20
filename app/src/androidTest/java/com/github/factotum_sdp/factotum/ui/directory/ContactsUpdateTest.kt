package com.github.factotum_sdp.factotum.ui.directory

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
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
        fun setUpDatabase() {
            val database = ContactsUtils.setEmulatorGet()
            MainActivity.setDatabase(database)
        }
    }

    @Before
    fun setUp() {
        ContactsUtils.emptyFirebaseDatabase()

        runBlocking {
            ContactsUtils.populateDatabase(nbContacts)
        }
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        Espresso.onView(ViewMatchers.withId(R.id.directoryFragment))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.contacts_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                ViewActions.click()))
        Espresso.onView(ViewMatchers.withId(R.id.button_modify_contact)).perform(ViewActions.click())
    }

    @Test
    fun hasAllTheFields(){
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        Espresso.onView((ViewMatchers.withId(R.id.contact_image_creation)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.roles_spinner))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val fields = Contact::class.java.declaredFields
        var nbFields = 0
        for (param in fields){
            if (param.isSynthetic) continue
            nbFields++
        }
        val nbEditText = device.findObjects(By.clazz(EditText::class.java.name)).size
        // image already present
        TestCase.assertEquals(nbFields - 3, nbEditText)
    }

    @Test
    fun buttonTextIsCorrect(){
        Espresso.onView(ViewMatchers.withId(R.id.create_contact))
            .check(ViewAssertions.matches(ViewMatchers.withText("Update Contact")))
    }

    @Test
    fun allFieldsAreEditable(){
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val fields = Contact::class.java.declaredFields
        for (param in fields){
            if (param.isSynthetic) continue
            val editText = device.findObject(By.clazz(EditText::class.java.name))
            editText.text = "test"
            TestCase.assertEquals("test", editText.text)
        }
    }

    @Test
    fun updateDoesntAddOrRemoveContact(){
        Espresso.onView(ViewMatchers.withId(R.id.create_contact)).perform(ViewActions.click())
        //check if recycle view in contacts has 6 items
        Espresso.onView(ViewMatchers.withId(R.id.contacts_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
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
        val nameEditText = Espresso.onView(ViewMatchers.withId(R.id.editTextName))
        nameEditText.check(ViewAssertions.matches(ViewMatchers.withText(ContactsUtils.getContacts()[0].name)))

        val surnameEditText = Espresso.onView(ViewMatchers.withId(R.id.editTextSurname))
        surnameEditText.check(ViewAssertions.matches(ViewMatchers.withText(ContactsUtils.getContacts()[0].surname)))

        val addressEditText = Espresso.onView(ViewMatchers.withId(R.id.contactCreationAddress))
        addressEditText.check(ViewAssertions.matches(ViewMatchers.withText(ContactsUtils.getContacts()[0].address)))

        val phoneEditText = Espresso.onView(ViewMatchers.withId(R.id.contactCreationPhoneNumber))
        phoneEditText.check(ViewAssertions.matches(ViewMatchers.withText(ContactsUtils.getContacts()[0].phone)))

        val notesEditText = Espresso.onView(ViewMatchers.withId(R.id.contactCreationNotes))
        notesEditText.check(ViewAssertions.matches(ViewMatchers.withText(ContactsUtils.getContacts()[0].details)))

        val roleSpinner = Espresso.onView(ViewMatchers.withId(R.id.roles_spinner))
        roleSpinner.check(ViewAssertions.matches(ViewMatchers.withSpinnerText(ContactsUtils.getContacts()[0].role)))
    }

    @Test
    fun updatedContactHasCorrectValue() {
        val nameEditText = Espresso.onView(ViewMatchers.withId(R.id.editTextName))
        nameEditText.perform(ViewActions.replaceText("John"))

        val surnameEditText = Espresso.onView(ViewMatchers.withId(R.id.editTextSurname))
        surnameEditText.perform(ViewActions.replaceText("Doe"))

        val addressEditText = Espresso.onView(ViewMatchers.withId(R.id.contactCreationAddress))
        addressEditText.perform(ViewActions.replaceText("123 Main St"))

        val phoneEditText = Espresso.onView(ViewMatchers.withId(R.id.contactCreationPhoneNumber))
        phoneEditText.perform(ViewActions.replaceText("555-555-1234"))

        val notesEditText = Espresso.onView(ViewMatchers.withId(R.id.contactCreationNotes))
        notesEditText.perform(ViewActions.replaceText("This is a test note."))

        Espresso.onView(ViewMatchers.withId(R.id.create_contact)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.contacts_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                    ViewActions.click()
                ))

        Espresso.onView(ViewMatchers.withId(R.id.contact_name))
            .check(ViewAssertions.matches(ViewMatchers.withText("John")))
        Espresso.onView(ViewMatchers.withId(R.id.contact_surname))
            .check(ViewAssertions.matches(ViewMatchers.withText("Doe")))
        Espresso.onView(ViewMatchers.withId(R.id.contact_address))
            .check(ViewAssertions.matches(ViewMatchers.withText("123 Main St")))
        Espresso.onView(ViewMatchers.withId(R.id.contact_phone))
            .check(ViewAssertions.matches(ViewMatchers.withText("555-555-1234")))
        Espresso.onView(ViewMatchers.withId(R.id.contact_details))
            .check(ViewAssertions.matches(ViewMatchers.withText("This is a test note.")))
    }
}