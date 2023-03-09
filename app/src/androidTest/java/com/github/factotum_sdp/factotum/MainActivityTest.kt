package com.github.factotum_sdp.factotum

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import org.hamcrest.Matchers.not

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    //=============================================================
    // Entry view checks :
    //=============================================================
    @Test
    fun fabIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
    }

    @Test
    fun roadBookIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.list)).check(matches(isDisplayed()))
    }

    @Test
    fun appBarIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.app_bar_main)).check(matches(isDisplayed()))
    }

    @Test
    fun toolBarIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        //onView(withId(R.id.app_bar_main)).check(matches(isDisplayed()))
    }

    @Test
    fun drawerLayoutIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()))
        //onView(withId(R.id.app_bar_main)).check(matches(isDisplayed()))
    }


    //=============================================================
    // drawerMenu Menu Navigation :
    //=============================================================

    @Test
    fun drawerMenuOpensCorrectly() {
        onView(withId(R.id.drawer_layout))
            .check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open())
            .check(matches(DrawerMatchers.isOpen()))
    }

    @Test
    fun clickOnPictureMenuItemLeadsToCorrectFragment() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.pictureFragment))
            .perform(click())
        //onView(withText(R.string.menu_picture)).perform(click()) //access by string
        onView(withId(R.id.fragment_picture_directors_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))) //with menu closed
    }

    @Test
    fun clickOnDirectoryMenuItemLeadsToCorrectFragment() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        //onView(withText(R.string.menu_picture)).perform(click()) //access by string
        onView(withId(R.id.fragment_directory_directors_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))) //with menu closed
    }

    @Test
    fun clickOnRoadBookMenuItemStaysToCorrectFragment() {
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())
        //onView(withText(R.string.menu_picture)).perform(click()) //access by string
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT))) //with menu closed
    }

    @Test
    fun navigateThroughDrawerMenuWorks() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        //onView(withText(R.string.menu_picture)).perform(click()) //access by string
        onView(withId(R.id.fragment_directory_directors_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))

        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())
        //onView(withText(R.string.menu_picture)).perform(click()) //access by string
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
    }

    //=============================================================
    // drawerMenu Menu Navigation :
    //=============================================================

    @Test
    fun startingRecordsAreDisplayed() {
        for (record in DestinationRecords.RECORDS)
            onView(withText(record.destName)).check(matches(isDisplayed()))
    }

    @Test
    fun pressingFabCreatesNewRecord() {
        onView(withId(R.id.fab)).perform(click())
        onView(withText(DestinationRecords.RECORD_TO_ADD.destName)).check(matches(isDisplayed()))
    }

    @Test
    fun listScrollingWorks() {
        onView(withText(DestinationRecords.RECORDS[0].destName)).check(matches(isDisplayed()))
        for (i in 0 .. 17)
            onView(withId(R.id.fab)).perform(click())
        //val recyclerView: RecyclerView = withId(R.id.list) as RecyclerView
        onView(withId(R.id.list)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                 20
            )
        )
        onView(withText(DestinationRecords.RECORDS[0].destName)).check(matches(isDisplayed()))
    }

    //test avec navigateUp



}