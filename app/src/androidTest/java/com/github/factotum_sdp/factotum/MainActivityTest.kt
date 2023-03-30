package com.github.factotum_sdp.factotum

import android.Manifest
import android.provider.MediaStore
import android.view.Gravity
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
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.BeforeClass
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

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            val database = Firebase.database
            database.useEmulator("10.0.2.2", 9000)
            MainActivity.setDatabase(database)
            ContactsList.init(database)
            runBlocking { ContactsList.populateDatabase() }
        }
    }

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
    fun clickOnDirectoryMenuItemLeadsToCorrectFragment() {
        clickOnAMenuItemLeadsCorrectly(
            R.id.directoryFragment,
            R.id.fragment_directory_directors_parent
        )
    }

    @Test
    fun clickOnRoadBookMenuItemStaysToCorrectFragment() {
        //onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
        clickOnAMenuItemLeadsCorrectly(
            R.id.roadBookFragment,
            R.id.fragment_roadbook_directors_parent
        )
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
        clickOnAMenuItemLeadsCorrectly(
            R.id.routeFragment,
            R.id.fragment_route_directors_parent
        )
    }

    @Test
    fun clickOnSignOutMenuItemLeadsToCorrectFragment() {
        clickOnAMenuItemLeadsCorrectly(
            R.id.signoutButton,
            R.id.fragment_login_directors_parent
        )
    }

    fun clickOnDisplayProofPictureMenuItemLeadsToCorrectFragment() {
        clickOnAMenuItemLeadsCorrectly(
            R.id.displayFragment,
            R.id.fragment_display_directors_parent
        )
    }

    @Test
    fun navigateThroughDrawerMenuWorks() {
        clickOnAMenuItemLeadsCorrectly(
            R.id.directoryFragment,
            R.id.fragment_directory_directors_parent
        )
        clickOnAMenuItemLeadsCorrectly(
            R.id.roadBookFragment,
            R.id.fragment_roadbook_directors_parent
        )
    }
    
    /*@Test
    fun actionSettingsIsAccessible() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.action_settings)).perform(click())
    }*/

}