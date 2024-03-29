package com.github.factotum_sdp.factotum.ui.directory

import android.Manifest
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until.hasObject
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.model.Contact
import com.github.factotum_sdp.factotum.model.User
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.ui.maps.MapsFragment
import com.github.factotum_sdp.factotum.utils.ContactsUtils.Companion.createRandomContacts
import com.github.factotum_sdp.factotum.utils.ContactsUtils.Companion.randomContacts
import com.github.factotum_sdp.factotum.utils.ContactsUtils.Companion.resetContact
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.logout
import com.github.factotum_sdp.factotum.utils.LocationUtils
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactDetailsFragmentTest {
    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val permission = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    companion object {
        private lateinit var currContact: Contact

        @BeforeClass
        @JvmStatic
        fun setUp() {
            initFirebase()
            createRandomContacts(1)
            currContact = randomContacts[0]
            logout()
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
    fun goToContactDetails() {
        GeneralUtils.injectBossAsLoggedInUser(activityRule)
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        onView(withText("@" + currContact.username))
            .perform(click())
    }

    @Test
    fun modifyButtonIsDisplayed() {
        onView(withId(R.id.button_modify_contact))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deleteButtonIsDisplayedForABoss() {
        onView(withId(R.id.button_delete_contact))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deleteButtonIsNotDisplayedForACourier() {
        val user = UsersPlaceHolder.USER_COURIER
        val loggedInUser = User(user.uid, user.name, user.email, user.role)
        GeneralUtils.injectLoggedInUser(activityRule, loggedInUser)
        onView(withId(R.id.button_delete_contact))
            .check(doesNotExist())
    }

    @Test
    fun deleteButtonIsNotDisplayedForAClient() {
        val user = UsersPlaceHolder.USER_CLIENT
        val loggedInUser = User(user.uid, user.name, user.email, user.role)
        GeneralUtils.injectLoggedInUser(activityRule, loggedInUser)
        onView(withId(R.id.button_delete_contact))
            .check(doesNotExist())
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
        resetContact(currContact)
    }

    @Test
    fun correctInfoIsDisplayed() {
        onView(withId(R.id.contact_name))
            .check(matches(withText(currContact.name)))
        onView(withId(R.id.contact_surname))
            .check(matches(withText(currContact.surname)))
        onView(withId(R.id.contact_phone))
            .check(matches(withText(currContact.phone)))
        onView(withId(R.id.contact_role))
            .check(matches(withText(equalToIgnoringCase(currContact.role))))
        onView(withId(R.id.contact_address))
            .check(matches(withText(currContact.addressName)))
        if (currContact.details != null) {
            onView(withId(R.id.contact_details))
                .check(matches(withText(currContact.details)))
        }
    }

    @Test
    fun correctImageIsDisplayed() {
        onView(withId(R.id.contact_image))
            .check(matches(isDisplayed()))
    }

    @Test
    fun superClientIsntDisplayedWithBossContact() {
        onView(withId(R.id.contact_role))
            .check(matches(withText("Boss")))
        onView(withId(R.id.managing_client_shown))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
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
        resetContact(currContact)
    }

    @Test
    fun deleteButtonDeletesContact() {
        onView(withId(R.id.button_delete_contact))
            .perform(click())

        onView(withText("@${currContact.username}")).check(doesNotExist())
        resetContact(currContact)
    }

    @Test
    fun buttonRunOpensGoogleMaps() {
        Intents.init()
        onView(withId(R.id.run_button)).perform(click())
        if (LocationUtils.hasLocationPopUp()) {
            val device = UiDevice.getInstance(getInstrumentation())
            device.findObject(UiSelector().textContains(LocationUtils.buttonTextAllow)).click()
        }
        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.toPackage(MapsFragment.MAPS_PKG)
            )
        )
        Intents.release()
    }
/*
    @Test
    fun buttonShowDestination() {
        onView(withId(R.id.show_all_button)).perform(click())
        val device = UiDevice.getInstance(getInstrumentation())
        val markers = device.wait(hasObject(By.descContains("Destination")), 5000L)
        assertTrue(markers)
    } */
}