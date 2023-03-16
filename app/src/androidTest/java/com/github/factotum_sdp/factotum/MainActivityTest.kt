package com.github.factotum_sdp.factotum

import android.Manifest
import android.provider.MediaStore
import android.view.Gravity
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.ui.login.LoginFragment
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertTrue
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

    @get:Rule
    val permissionsRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    //========================================================================================
    // Entry view checks :
    //========================================================================================

    @Test
    fun loginFragmentIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.fragment_login_directors_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun appBarIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.app_bar_main)).check(matches(isDisplayed()))
    }

    @Test
    fun toolBarIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
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
    fun clickOnMapsMenuItemLeadsToCorrectFragment() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
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
    fun clickOnPictureMenuItemLeadsToCorrectFragmentAnd() {
        Intents.init()
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.pictureFragment))
            .perform(click())
        // Check that is open the camera

        // Create an IntentMatcher to capture the intent that should open the camera app
        val expectedIntent = allOf(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        Thread.sleep(5000)

        // Click on the camera shutter button
        device.executeShellCommand("input keyevent 27")

        // Use Intents.intended() to check that the captured intent matches the expected intent
        Intents.intended(expectedIntent)
        Intents.release()
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
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        onView(withId(R.id.fragment_directory_directors_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
    }

    @Test
    fun clickOnRoadBookMenuItemStaysToCorrectFragment() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
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