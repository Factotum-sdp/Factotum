package com.github.factotum_sdp.factotum

import android.view.Gravity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//Later when non-root fragment will exists : add test for navigateUp
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    //========================================================================================
    // Entry view checks :
    //========================================================================================

    @Test
    fun appBarIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.app_bar_main)).check(matches(isDisplayed()))
    }

    @Test
    fun toolBarIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun drawerLayoutIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()))
    }


    //========================================================================================
    // Drawer Menu Navigation :
    //========================================================================================
    @Test
    fun drawerMenuOpensCorrectly() {
        onView(withId(R.id.drawer_layout))
            .check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open())
            .check(matches(DrawerMatchers.isOpen()))
    }
    private fun clickOnAMenuItemLeadsCorrectly(menuItemId: Int, fragment_parent_id: Int) {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(menuItemId))
            .perform(click())
        onView(withId(fragment_parent_id)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
    }
    @Test
    fun clickOnPictureMenuItemLeadsToCorrectFragment() {
        clickOnAMenuItemLeadsCorrectly(R.id.pictureFragment, R.id.fragment_picture_directors_parent)
    }

    @Test
    fun clickOnMapsMenuItemLeadsToCorrectFragment() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.mapsFragment))
            .perform(click())
        //temp hard-coded string bug to fetch the fragment parent id
        onView(withText("This is the maps Fragment")).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
    }

    @Test
    fun clickOnDirectoryMenuItemLeadsToCorrectFragment() {
        clickOnAMenuItemLeadsCorrectly(R.id.directoryFragment, R.id.fragment_directory_directors_parent)
    }

    @Test
    fun clickOnRoadBookMenuItemStaysToCorrectFragment() {
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
        clickOnAMenuItemLeadsCorrectly(R.id.roadBookFragment, R.id.fragment_roadbook_directors_parent)
    }

    @Test
    fun navigateThroughDrawerMenuWorks() {
        clickOnAMenuItemLeadsCorrectly(R.id.directoryFragment, R.id.fragment_directory_directors_parent)
        clickOnAMenuItemLeadsCorrectly(R.id.roadBookFragment, R.id.fragment_roadbook_directors_parent)
    }

    @Test
    fun actionSettingsIsAccessible() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.action_settings)).perform(click())
    }

}