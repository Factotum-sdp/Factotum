package com.github.factotum_sdp.factotum.ui.directory

import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By.clazz
import androidx.test.uiautomator.By.textContains
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.localisation.Location
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactsCreationTest {


    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun goToContacts() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
    }

    @Test
    fun addButtonExists(){
        onView(withId(R.id.add_contact_button)).check(matches(isDisplayed()))

    }

    @Test
    fun addButtonOpensContactCreation(){
        onView(withId(R.id.add_contact_button)).perform(click())
        onView(withId(R.id.contact_creation_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun contactCreationHasAllTheFields(){
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        onView(withId(R.id.add_contact_button)).perform(click())
        onView((withId(R.id.contact_image_creation))).check(matches(isDisplayed()))
        onView((withId(R.id.contactCreationAddress))).check(matches(isDisplayed()))
        onView(withId(R.id.roles_spinner)).check(matches(isDisplayed()))
        val fields = ContactsList.Contact::class.java.declaredFields
        var nbFields = 0
        for (param in fields){
            if (param.isSynthetic) continue
            nbFields++
        }
        val nbEditText = device.findObjects(clazz(EditText::class.java.name)).size
        // image already present
        // searchView already present
        assertEquals(nbFields-2, nbEditText)
    }

    @Test
    fun writeInAddressFieldMakesDropDown(){
        onView(withId(R.id.add_contact_button)).perform(click())
        val city = "Lausanne"
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(ViewActions.typeText(city)).perform(
            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
        )
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val nbLausanne = device.findObjects(textContains(city)).size
        assertEquals(2, nbLausanne)
    }

    @Test
    fun selectSuggestionWritesAddress(){
        onView(withId(R.id.add_contact_button)).perform(click())
        val city = "Lausanne"
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(ViewActions.typeText(city)).perform(
            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
        )
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.findObject(textContains(city)).click()
        val lausanneResult = Location.geocoderQuery(city, getApplicationContext())
        val address = device.findObject(textContains(city)).text
        assertEquals(lausanneResult!![0].getAddressLine(0).toString(), address)
    }

}