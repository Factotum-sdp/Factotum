package com.github.factotum_sdp.factotum.ui.directory

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By.clazz
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.Contact
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import junit.framework.TestCase.assertEquals
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
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        onView(withId(R.id.add_contact_button)).perform(click())
    }

    @Test
    fun hasAllTheFields(){
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        onView((withId(R.id.contact_image_creation))).check(matches(isDisplayed()))
        onView(withId(R.id.roles_spinner)).check(matches(isDisplayed()))
        val fields = Contact::class.java.declaredFields
        var nbFields = 0
        for (param in fields){
            if (param.isSynthetic) continue
            nbFields++
        }
        val nbEditText = device.findObjects(clazz(EditText::class.java.name)).size
        // image already present
        assertEquals(nbFields-3, nbEditText)
    }

    @Test
    fun buttonTextIsCorrect(){
        onView(withId(R.id.create_contact)).check(matches(ViewMatchers.withText("Create Contact")))
    }

    @Test
    fun hasRoles(){
        onView(withId(R.id.roles_spinner)).check(matches(isDisplayed()))
    }

    @Test
    fun allFieldsAreEditable(){
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val fields = Contact::class.java.declaredFields
        for (param in fields){
            if (param.isSynthetic) continue
            val editText = device.findObject(clazz(EditText::class.java.name))
            editText.text = "test"
            assertEquals("test", editText.text)
        }
    }

    @Test
    fun canCreateContact(){
        onView(withId(R.id.create_contact)).perform(click())
        //check if recycle view in contacts has 6 items
        onView(withId(R.id.contacts_recycler_view))
            .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
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

        val addressEditText = onView(withId(R.id.contactCreationAddress))
        addressEditText.perform(replaceText("123 Main St"))

        val phoneEditText = onView(withId(R.id.contactCreationPhoneNumber))
        phoneEditText.perform(replaceText("555-555-1234"))

        val notesEditText = onView(withId(R.id.contactCreationNotes))
        notesEditText.perform(replaceText("This is a test note."))

        onView(withId(R.id.create_contact)).perform(click())
        onView(withId(R.id.contacts_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(nbContacts, click()))

        onView(withId(R.id.contact_name)).check(matches(ViewMatchers.withText("John")))
        onView(withId(R.id.contact_surname)).check(matches(ViewMatchers.withText("Doe")))
        onView(withId(R.id.contact_address)).check(matches(ViewMatchers.withText("123 Main St")))
        onView(withId(R.id.contact_phone)).check(matches(ViewMatchers.withText("555-555-1234")))
        onView(withId(R.id.contact_details)).check(matches(ViewMatchers.withText("This is a test note.")))
    }

}