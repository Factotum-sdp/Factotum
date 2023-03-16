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
import com.github.factotum_sdp.factotum.contacts_content.ContactsList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactDetailsFragmentTest {
    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun goToContactDetails() {
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
    fun buttonIsClickable() {
        onView(withId(R.id.button))
            .perform(click())
    }

    @Test
    fun correctInfoIsDisplayed() {
        onView(withId(R.id.contact_name))
            .check(matches(withText(ContactsList.ITEMS[0].name)))
        onView(withId(R.id.contact_phone))
            .check(matches(withText(ContactsList.ITEMS[0].phone)))
        onView(withId(R.id.contact_role))
            .check(matches(withText(ContactsList.ITEMS[0].role)))
        onView(withId(R.id.contact_address))
            .check(matches(withText(ContactsList.ITEMS[0].address)))
        if (ContactsList.ITEMS[0].details != null) {
            onView(withId(R.id.contact_details))
                .check(matches(withText(ContactsList.ITEMS[0].details)))
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

}