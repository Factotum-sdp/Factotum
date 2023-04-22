package com.github.factotum_sdp.factotum

import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.placeholder.ContactsList
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.ui.login.LoginFragmentTest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.AfterClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val LOGIN_REFRESH_TIME = 3000L
const val DRAWER_REFRESH_TIME = 1000L

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

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
                UsersPlaceHolder.addUserToDb(UsersPlaceHolder.USER_BOSS)
            }
            runBlocking {
                UsersPlaceHolder.addUserToDb(UsersPlaceHolder.USER_COURIER)
            }
            runBlocking {
                UsersPlaceHolder.addUserToDb(UsersPlaceHolder.USER_CLIENT)
            }
            runBlocking {
                UsersPlaceHolder.addAuthUser(UsersPlaceHolder.USER_BOSS)
            }
            runBlocking {
                UsersPlaceHolder.addAuthUser(UsersPlaceHolder.USER_COURIER)
            }
            runBlocking {
                UsersPlaceHolder.addAuthUser(UsersPlaceHolder.USER_CLIENT)
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
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("boss@gmail.com", "123456")
        Thread.sleep(LOGIN_REFRESH_TIME)

        navigateToAndPressBackLeadsToRB(R.id.directoryFragment)
        navigateToAndPressBackLeadsToRB(R.id.displayFragment)
        navigateToAndPressBackLeadsToRB(R.id.routeFragment)
    }

    private fun navigateToAndPressBackLeadsToRB(menuItemId: Int) {
        navigateTo(menuItemId)
        pressBack()
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun pressingBackOnRBFragmentLeadsOutOfTheApp() {
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("boss@gmail.com", "123456")
        Thread.sleep(LOGIN_REFRESH_TIME)
        pressBackUnconditionally()
        val uiDevice = UiDevice.getInstance(getInstrumentation())
        assertFalse(uiDevice.currentPackageName == "com.github.factotum_sdp.factotum")
        assertTrue(uiDevice.isScreenOn)
    }

    @Test
    fun navHeaderDisplaysUserData() {
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("boss@gmail.com", "123456")
        Thread.sleep(LOGIN_REFRESH_TIME)
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withText("boss@gmail.com")).check(matches(isDisplayed()))
        onView(withText("Boss (BOSS)")).check(matches(isDisplayed()))
    }

    @Test
    fun drawerMenuIsCorrectlyDisplayedForBoss() {
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("boss@gmail.com", "123456")
        Thread.sleep(LOGIN_REFRESH_TIME)

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())

        Thread.sleep(DRAWER_REFRESH_TIME)

        // Check that the menu items are displayed
        onView(withText("RoadBook")).check(matches(isDisplayed()))
        onView(withText("Directory")).check(matches(isDisplayed()))
        onView(withText("Maps")).check(matches(isDisplayed()))
        onView(withText("View Proof Pictures")).check(matches(isDisplayed()))
    }

    @Test
    fun drawerMenuIsCorrectlyDisplayedForClient() {
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("client@gmail.com", "123456")
        Thread.sleep(LOGIN_REFRESH_TIME)

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())

        Thread.sleep(DRAWER_REFRESH_TIME)

        // Check that the menu items are displayed
        onView(withText("RoadBook")).check(doesNotExist())
        onView(withText("Directory")).check(doesNotExist())
        onView(withText("Maps")).check(doesNotExist())
        onView(withText("View Proof Pictures")).check(matches(isDisplayed()))
    }

    // Work when executing the scenario manually but emulators issues make it fails in the connectedCheck
    // The second user Helen Bates can't be found.
    private fun navHeaderStillDisplaysCorrectlyAfterLogout() {
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("boss@gmail.com", "123456")
        Thread.sleep(LOGIN_REFRESH_TIME)

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withText("boss@gmail.com")).check(matches(isDisplayed()))
        onView(withText("Boss (BOSS)")).check(matches(isDisplayed()))

        onView(withId(R.id.signoutButton)).perform(click())
        Thread.sleep(LOGIN_REFRESH_TIME)
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("helen.bates@gmail.com", "123456")
        Thread.sleep(LOGIN_REFRESH_TIME)
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withText("helen.bates@gmail.com")).check(matches(isDisplayed()))
        onView(withText("Helen Bates (COURIER)")).check(matches(isDisplayed()))
    }

    @Test
    fun signUpFormWithAllFieldsAndClickAndLogin() {
        onView(withId(R.id.signup)).perform(click())
        onView(withId(R.id.username)).perform(typeText("Name"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("email@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        Espresso.onData(Matchers.anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(1)
            .perform(click())
        onView(withId(R.id.signup)).perform(click())
        FirebaseAuth.AuthStateListener {
            onView(withId(R.id.fragment_login_directors_parent)).check(
                matches(
                    isDisplayed()
                )
            )
        }
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("email@gmail.com", "123456")

        FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                onView(withId(R.id.fragment_login_directors_parent)).check(
                    matches(
                        isDisplayed()
                    )
                )
            }

        }
    }
}