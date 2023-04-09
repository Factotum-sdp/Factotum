package com.github.factotum_sdp.factotum

import android.Manifest
import android.provider.MediaStore
import android.view.Gravity
import androidx.test.espresso.Espresso
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
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.ui.login.LoginFragmentTest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.AfterClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    val permissionsRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            val database = Firebase.database
            val auth = Firebase.auth
            database.useEmulator("10.0.2.2", 9000)
            auth.useEmulator("10.0.2.2", 9099)
            MainActivity.setDatabase(database)
            MainActivity.setAuth(auth)
            ContactsList.init(database)
            runBlocking {
                ContactsList.populateDatabase()
            }

            UsersPlaceHolder.init(database, auth)

            runBlocking {
                UsersPlaceHolder.populateDatabase()
            }
            runBlocking {
                UsersPlaceHolder.addAuthUser(UsersPlaceHolder.USER3)
            }
        }

        @AfterClass
        @JvmStatic
        fun stopAuthEmulator() {
            val auth = Firebase.auth
            auth.signOut()
            MainActivity.setAuth(auth)
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
        navigateTo(menuItemId)
        onView(withId(fragment_parent_id)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
    }

    private fun navigateTo(menuItemId: Int) {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(menuItemId))
            .perform(click())
    }

    @Test
    fun clickOnRoadBookMenuItemStaysToCorrectFragment() {
        clickOnAMenuItemLeadsCorrectly(
            R.id.roadBookFragment,
            R.id.fragment_roadbook_directors_parent
        )
    }

    @Test
    fun clickOnPictureMenuItemLeadsToCorrectFragmentAnd() {
        Intents.init()
        val device = UiDevice.getInstance(getInstrumentation())

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

    @Test
    fun pressingBackOnAMenuFragmentLeadsToRBFragment() {
        // First need to login to trigger the change of navGraph's start fragment
        LoginFragmentTest.fillUserEntryAndGoToRBFragment()
        Thread.sleep(3000)

        navigateToAndPressBackLeadsToRB(R.id.directoryFragment)
        navigateToAndPressBackLeadsToRB(R.id.displayFragment)
        navigateToAndPressBackLeadsToRB(R.id.routeFragment)
    }

    private fun navigateToAndPressBackLeadsToRB(menuItemId: Int) {
        navigateTo(menuItemId)
        Espresso.pressBack()
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun pressingBackOnRBFragmentLeadsOutOfTheApp() {
        LoginFragmentTest.fillUserEntryAndGoToRBFragment()
        Thread.sleep(3000)
        Espresso.pressBackUnconditionally()
        val uiDevice = UiDevice.getInstance(getInstrumentation())
        assertFalse(uiDevice.currentPackageName == "com.github.factotum_sdp.factotum")
        assertTrue(uiDevice.isScreenOn)
    }

    @Test
    fun navHeaderDisplaysUserData() {
        LoginFragmentTest.fillUserEntryAndGoToRBFragment()
        Thread.sleep(3000)

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withText("jane.doe@gmail.com")).check(matches(isDisplayed()))
        onView(withText("Jane Doe (CLIENT)")).check(matches(isDisplayed()))
    }
}